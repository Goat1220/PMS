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
    @Transactional
    public boolean delete(Long yrtId) {
        if (yrtId == null) {
            log.warn("삭제 요청 실패: yrtId 없음");
            return false;
        }

        try {
            int deleted = mapper.deleteByYrtId(yrtId);
            log.info("yrtId={} 삭제 결과 {}건", yrtId, deleted);
            return deleted > 0; // 정상 삭제 시 true
        } catch (Exception e) {
            log.error("삭제 중 예외 발생", e);
            return false; // 예외 발생 시 false 반환 (Controller가 400 응답)
        }
    }

    @Transactional(readOnly = true)
    public InstallmentResponse installment(Long yrtId, int months, String startMonth) {
        if (yrtId == null) {
            throw new IllegalArgumentException("yrtId가 필요합니다.");
        }
        if (months < 2 || months > 12) {
            throw new IllegalArgumentException("분납 개월수는 2~12 범위여야 합니다.");
        }

        ResultTotal rt = query.findResultTotal(yrtId);
        if (rt == null) {
            throw new IllegalStateException("정산 결과가 존재하지 않습니다. 먼저 시뮬레이션을 실행하세요.");
        }

        long nat = rt.getAddNational() == null ? 0L : rt.getAddNational();
        long loc = rt.getAddLocal() == null ? 0L : rt.getAddLocal();

        List<InstallmentEntry> list = new ArrayList<>();
        YearMonth ym;
        try {
            ym = YearMonth.parse(startMonth);
        } catch (Exception e) {
            log.warn("시작월 형식 오류: {}", startMonth);
            ym = YearMonth.now(); // 현재 달로 대체
        }

        long natBase = (months <= 1) ? nat : nat / months;
        long locBase = (months <= 1) ? loc : loc / months;

        for (int i = 0; i < months; i++) {
            long natPay = (i == months - 1) ? (nat - natBase * (months - 1)) : natBase;
            long locPay = (i == months - 1) ? (loc - locBase * (months - 1)) : locBase;
            list.add(new InstallmentEntry(
                    ym.plusMonths(i).toString(), natPay, locPay, natPay + locPay
            ));
        }

        return new InstallmentResponse(list, "균등 분할, 잔액은 마지막 달 가산");
    }

    /* -----------------------------------------------------
     * 내부 보조: 예시 항목 시딩 / 内部補助：例示項目シーディング
     * 현재 DB의 yrt_item(category, item_name)과 동일하게 구성
     * DB 수정 없이 실행 가능하게 조정
     * ----------------------------------------------------- */
    private void seedScenarioItems(Long yrtId) {
        // 【税額控除】子供税額控除 / [세액공제] 자녀세액공제
        mapper.insertItem(yrtId, "税額控除", "子供税額控除", 500_000L, 500_000L);

        // 【税額控除】保険料税額控除 / [세액공제] 보험료세액공제
        mapper.insertItem(yrtId, "税額控除", "保険料税額控除", 300_000L, 300_000L);

        // 【税額控除】教育費税額控除 / [세액공제] 교육비세액공제
        mapper.insertItem(yrtId, "税額控除", "教育費税額控除", 400_000L, 400_000L);

        // 【税額控除】寄付金税額控除 / [세액공제] 기부금세액공제
        mapper.insertItem(yrtId, "税額控除", "寄付金税額控除", 200_000L, 200_000L);
    }


}