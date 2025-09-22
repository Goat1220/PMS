package org.pms.feature.yearendtaxsimulation.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.yearendtaxsimulation.domain.*;
import org.pms.feature.yearendtaxsimulation.mapper.YearendTaxSimMapper;

/* 시뮬레이션 실행/삭제/분납 계산 서비스 / シミュレーション実行・削除・分納計算サービス */
@Service @RequiredArgsConstructor @Slf4j
public class YearendTaxSimCommandService {
    private final YearendTaxSimMapper mapper;              // DB 접근 매퍼 / DBアクセスマッパー
    private final YearendTaxSimQueryService query;         // 조회용 서비스 / 参照用サービス

    /**
     * 시뮬레이션 실행
     * - overwrite=true면 기존 미확정 데이터를 삭제
     * - 헤더를 생성하고 yrtId를 발급받음(selectKey)
     * - 예시용 항목 데이터를 입력
     * - 결과 합계 upsert
     *
     * シミュレーション実行
     * - overwrite=true の場合、既存の未確定データを削除
     * - ヘッダを作成して yrtId を採番（selectKey）
     * - サンプル項目の挿入
     * - 合計結果のアップサート
     */
    @Transactional
    public Long run(String empId, Integer baseYear, Long policyId, String runLabel, boolean overwrite){
        if (overwrite) mapper.deleteUnconfirmedForEmpYear(empId, baseYear); // 기존 미확정 삭제 / 既存未確定を削除

        // 헤더 생성 후 저장 / ヘッダ生成して保存
        SimHeaderMini header = new SimHeaderMini();
        header.setEmpId(empId); header.setBaseYear(baseYear); header.setRunLabel(runLabel);
        mapper.insertHeader(header);
        Long yrtId = header.getYrtId(); // selectKey로 채워진 ID / selectKeyで付与されたID

        // 샘플 데이터 삽입(실제 로직 대체 가능) / サンプルデータ挿入（実ロジックに置換可）
        mapper.insertItem(yrtId, "소득",   "근로소득",     30000000L, 30000000L);
        mapper.insertItem(yrtId, "세액공제", "카드 세액공제", 180000L,    180000L);

        // 결과 합계 upsert / 結果合計のアップサート
        mapper.upsertResultTotal(yrtId, 180000L, 20000L);
        return yrtId;
    }

    /**
     * 시뮬레이션 결과 삭제
     * 実行結果の削除
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
        long nat = rt==null||rt.getAddNational()==null?0L:rt.getAddNational(); // 국세 / 国税
        long loc = rt==null||rt.getAddLocal()==null?0L:rt.getAddLocal();       // 지방세 / 地方税

        List<InstallmentEntry> list = new ArrayList<>();
        YearMonth ym = YearMonth.parse(startMonth); // 시작 연월 파싱 / 開始年月のパース
        long natBase = months<=1? nat : nat / months; // 기본 분할액(국세) / 基本割額（国税）
        long locBase = months<=1? loc : loc / months; // 기본 분할액(지방세) / 基本割額（地方税）

        for(int i=0;i<months;i++){
            // 마지막 달에 잔액 보정 / 最終月で端数調整
            long natPay = (i==months-1)? (nat - natBase*(months-1)) : natBase;
            long locPay = (i==months-1)? (loc - locBase*(months-1)) : locBase;
            list.add(new InstallmentEntry(ym.plusMonths(i).toString(), natPay, locPay, natPay+locPay)); // 한 달분 / 1か月分
        }
        return new InstallmentResponse(list, "균등 분할, 잔액은 마지막 달 가산"); // 説明文 / 説明
    }
}
