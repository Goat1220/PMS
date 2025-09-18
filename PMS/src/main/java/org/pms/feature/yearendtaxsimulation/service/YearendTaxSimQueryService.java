package org.pms.feature.yearendtaxsimulation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.yearendtaxsimulation.domain.*;
import org.pms.feature.yearendtaxsimulation.mapper.YearendTaxSimMapper;

/** KO: 조회 전용 서비스 / JP: 参照サービス */
@Service @RequiredArgsConstructor @Slf4j
public class YearendTaxSimQueryService {
    private final YearendTaxSimMapper mapper;

    @Transactional(readOnly = true)
    public List<SimItemRow> loadFinalGrid(String empId, Integer baseYear){
        return mapper.selectFinalGrid(empId, baseYear);
    }

    @Transactional(readOnly = true)
    public List<SimItemRow> loadSimGrid(String empId, Integer baseYear, Long yrtId){
        return mapper.selectSimGrid(empId, baseYear, yrtId);
    }

    @Transactional(readOnly = true)
    public SimHeaderMini latestHeader(String empId, Integer baseYear, boolean confirmedOnly){
        return mapper.selectLatestHeader(empId, baseYear, confirmedOnly);
    }

    @Transactional(readOnly = true)
    public ResultTotal findResultTotal(Long yrtId){
        return mapper.selectResultTotal(yrtId);
    }

    /** KO: 세금적용결과(표준/특별) 판정 / JP: 標準vs特別の判定 */
    @Transactional(readOnly = true)
    public String computeTaxApplyResult(String empId, Integer baseYear, Long yrtId) {
        Long creditSum = mapper.selectTaxCreditSum(empId, baseYear, yrtId);
        Long standard  = mapper.selectStandardCredit(baseYear);
        long cs = (creditSum == null ? 0L : creditSum);
        long st = (standard  == null ? 0L : standard);
        if (cs == 0L && st == 0L) return "미판정";
        return (cs > st) ? "특별세액공제" : "표준세액공제";
    }
}
