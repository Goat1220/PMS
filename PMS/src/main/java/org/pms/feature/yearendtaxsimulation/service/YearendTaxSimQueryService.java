package org.pms.feature.yearendtaxsimulation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.yearendtaxsimulation.domain.*;
import org.pms.feature.yearendtaxsimulation.mapper.YearendTaxSimMapper;

/* 조회 전용 서비스 / 参照用サービス */
@Service @RequiredArgsConstructor @Slf4j
public class YearendTaxSimQueryService {
    private final YearendTaxSimMapper mapper; // DB 매퍼 / DBマッパー

    @Transactional(readOnly = true)
    public List<SimItemRow> loadFinalGrid(String empId, Integer baseYear){
        // 최종 탭 그리드 조회 / 最終タブのグリッド取得
        return mapper.selectFinalGrid(empId, baseYear);
    }

    @Transactional(readOnly = true)
    public List<SimItemRow> loadSimGrid(String empId, Integer baseYear, Long yrtId){
        // 시뮬 탭 그리드 조회(특정 실행ID 기준) / シミュタブのグリッド取得（実行ID基準）
        return mapper.selectSimGrid(empId, baseYear, yrtId);
    }

    @Transactional(readOnly = true)
    public SimHeaderMini latestHeader(String empId, Integer baseYear, boolean confirmedOnly){
        // 최신 헤더(확정만 여부 선택) / 最新ヘッダ（確定のみ選択可）
        return mapper.selectLatestHeader(empId, baseYear, confirmedOnly);
    }

    @Transactional(readOnly = true)
    public ResultTotal findResultTotal(Long yrtId){
        // 결과 합계(국세/지방세) 조회 / 結果合計（国税/地方税）取得
        return mapper.selectResultTotal(yrtId);
    }

    @Transactional(readOnly = true)
    public String computeTaxApplyResult(String empId, Integer baseYear, Long yrtId) {
        // 세액공제 합계 vs 표준세액공제 비교 / 税額控除合計と標準税額控除の比較
        Long creditSum = mapper.selectTaxCreditSum(empId, baseYear, yrtId); // 세액공제 합계 / 税額控除合計
        Long standard  = mapper.selectStandardCredit(baseYear);             // 표준세액공제 / 標準税額控除
        long cs = (creditSum == null ? 0L : creditSum);
        long st = (standard  == null ? 0L : standard);
        if (cs == 0L && st == 0L) return "미판정";       // 정보 부족 시 미판정 / 情報不足は未判定
        return (cs > st) ? "특별세액공제" : "표준세액공제"; // 비교 결과 반환 / 比較結果を返却
    }
}

