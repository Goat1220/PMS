package org.pms.feature.yearendtaxsimulation.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.pms.feature.yearendtaxsimulation.domain.InstallmentEntry;
import org.pms.feature.yearendtaxsimulation.domain.InstallmentResponse;
import org.pms.feature.yearendtaxsimulation.domain.ResultTotal;
import org.pms.feature.yearendtaxsimulation.domain.SimHeaderMini;
import org.pms.feature.yearendtaxsimulation.mapper.YearendTaxSimMapper;

/* 시뮬레이션 실행/삭제/분납 계산 서비스 / シミュレーション実行・削除・分納計算サービス */
@Service
@RequiredArgsConstructor
@Slf4j
public class YearendTaxSimCommandService {

    private final YearendTaxSimMapper mapper;              // DB 접근 매퍼 / DBアクセスマッパー
    private final YearendTaxSimQueryService query;         // 조회용 서비스 / 参照用サービス

    /**
     * 시뮬레이션 실행
     * - overwrite=true면 기존 미확정 데이터를 삭제
     * - 헤더를 생성하고 yrtId를 발급받음(selectKey)
     * - 정식 스키마용 예시 항목(마스터 yrt_item 매핑) 입력
     * - 간이 판정 후 결과 합계 upsert
     *
     * シミュレーション実行
     * - overwrite=true の場合、既存の未確定データを削除
     * - ヘッダを作成して yrtId を採番（selectKey）
     * - 正式スキーマ用のサンプル項目（マスタ yrt_item にマッピング）を挿入
     * - 簡易判定の後、合計結果をアップサート
     */
    @Transactional
    public Long run(String empId, Integer baseYear, Long policyId, String runLabel, boolean overwrite){
        // 0) 방어적 검증 / 防御的検証
        if (empId == null || empId.trim().isEmpty()) {
            throw new IllegalArgumentException("empId is required");
        }
        if (baseYear == null) {
            throw new IllegalArgumentException("baseYear is required");
        }

        // 1) overwrite면 해당 사번+연도 미확정 데이터 정리 / overwrite の場合 未確定データを削除
        if (overwrite) {
            mapper.deleteUnconfirmedForEmpYear(empId, baseYear);
        }

        // 2) 헤더 생성(미확정) / ヘッダ作成（未確定）
        SimHeaderMini header = new SimHeaderMini();
        header.setEmpId(empId);         // 화면·API는 사번 문자열 / 画面・APIは社員番号の文字列
        header.setBaseYear(baseYear);
        header.setRunLabel(runLabel);   // 임시: tax_apply_type에 매핑 / 暫定：tax_apply_typeへマッピング
        mapper.insertHeader(header);    // selectKey로 yrtId 채워짐 / selectKeyでyrtIdを採番
        Long yrtId = header.getYrtId();

        // 3) 예시 항목 시딩(정식 스키마) / 例示項目のシーディング（正式スキーマ）
        //    ※ category/item_name 은 yrt_item_seed.sql 과 정확히 일치해야 함
        seedScenarioItems(yrtId);

        // 4) (임시) 세액적용결과 간이계산 / （暫定）税額適用結果の簡易計算
        long taxCreditSum   = mapper.selectTaxCreditSum(empId, baseYear, yrtId); // 세액공제 합계(예상값 기준)
        long standardCredit = mapper.selectStandardCredit(baseYear);             // ← 연도 인자 넘김

        // 간단 규칙: 초과분을 국세, 지방세는 10% / 簡易ルール：超過分→国税、地方税は10%
        long addNational = Math.max(0, taxCreditSum - standardCredit);
        long addLocal    = Math.round(addNational * 0.1);

        // 5) 결과 합계 upsert / 合計結果のアップサート
        mapper.upsertResultTotal(yrtId, addNational, addLocal);

        // 6) 실행 ID 반환 / 実行IDを返却
        return yrtId;
    }

    /**
     * 시뮬레이션 결과 삭제
     * 実行結果の削除
     * - 미확정(N)인 헤더만 삭제 허용 / 未確定(N)のみ削除可能
     */
    @Transactional
    public boolean delete(Long yrtId){
        return mapper.deleteByYrtId(yrtId) > 0; // 삭제 건수>0이면 true / 削除件数>0ならtrue
    }

    /**
     * 분납 시뮬레이션
     * - 결과 합계(국세/지방세)를 월수로 균등 분할
     * - 나누어떨어지지 않는 잔액은 마지막 달에 더함
     *
     * 分納シミュレーション
     * - 合計（国税/地方税）を月数で均等割
     * - 端数は最終月に加算
     */
    @Transactional(readOnly = true)
    public InstallmentResponse installment(Long yrtId, int months, String startMonth){
        ResultTotal rt = query.findResultTotal(yrtId); // 합계 조회 / 合計取得
        long nat = (rt == null || rt.getAddNational() == null) ? 0L : rt.getAddNational(); // 국세 / 国税
        long loc = (rt == null || rt.getAddLocal()    == null) ? 0L : rt.getAddLocal();    // 지방세 / 地方税

        List<InstallmentEntry> list = new ArrayList<>();
        YearMonth ym = YearMonth.parse(startMonth); // 시작 연월 파싱 / 開始年月のパース

        long natBase = (months <= 1) ? nat : nat / months; // 기본 분할액(국세) / 基本割額（国税）
        long locBase = (months <= 1) ? loc : loc / months; // 기본 분할액(지방세) / 基本割額（地方税）

        for (int i = 0; i < months; i++) {
            // 마지막 달에 잔액 보정 / 最終月で端数調整
            long natPay = (i == months - 1) ? (nat - natBase * (months - 1)) : natBase;
            long locPay = (i == months - 1) ? (loc - locBase * (months - 1)) : locBase;
            list.add(new InstallmentEntry(ym.plusMonths(i).toString(), natPay, locPay, natPay + locPay)); // 한 달분 / 1か月分
        }
        return new InstallmentResponse(list, "균등 분할, 잔액은 마지막 달 가산"); // 설명문 / 説明
    }

    /* -----------------------------------------------------
     * 내부 보조: 예시 항목 시딩 / 内部補助：例示項目シーディング
     *  - 반드시 yrt_item_seed.sql 의 category/item_name 과 동일해야 함
     *  - 例：基本事項/課税勤労所得、税額控除/標準税額控除、税額控除(その他)/月税額
     * ----------------------------------------------------- */
    private void seedScenarioItems(Long yrtId) {
        // [기본사항] 과세근로소득 30,000,000 / 【基本事項】課税勤労所得 30,000,000
        mapper.insertItem(yrtId, "기본사항", "과세근로소득", 30_000_000L, 30_000_000L);

        // [세액공제] 표준세액공제 130,000 / 【税額控除】標準税額控除 130,000
        mapper.insertItem(yrtId, "세액공제", "표준세액공제", 130_000L, 130_000L);

        // [세액공제(기타)] 월세액 1,200,000 (예: 100,000/월) / 【税額控除(その他)】家賃(月額) 1,200,000
        mapper.insertItem(yrtId, "세액공제(기타)", "월세액", 1_200_000L, 1_200_000L);

        // 필요한 경우 여기에 항목을 계속 추가 / 必要に応じてここに項目を追加
        // mapper.insertItem(yrtId, "카테고리", "항목명", 금액, 예상금액);
    }
}

