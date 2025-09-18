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

/** KO: 시뮬/삭제/분납 서비스 / JP: 実行・削除・分納サービス */
@Service @RequiredArgsConstructor @Slf4j
public class YearendTaxSimCommandService {
    private final YearendTaxSimMapper mapper;
    private final YearendTaxSimQueryService query;

    /** KO: 시뮬 실행(테스트용 간이 데이터) / JP: 実行 */
    @Transactional
    public Long run(String empId, Integer baseYear, Long policyId, String runLabel, boolean overwrite){
        if (overwrite) mapper.deleteUnconfirmedForEmpYear(empId, baseYear);

        SimHeaderMini header = new SimHeaderMini();
        header.setEmpId(empId); header.setBaseYear(baseYear); header.setRunLabel(runLabel);
        mapper.insertHeader(header);
        Long yrtId = header.getYrtId();

        // 샘플 데이터 2건
        mapper.insertItem(yrtId, "소득",   "근로소득",     30000000L, 30000000L);
        mapper.insertItem(yrtId, "세액공제", "카드 세액공제", 180000L,    180000L);

        // 분납 테스트용 합계
        mapper.upsertResultTotal(yrtId, 180000L, 20000L);
        return yrtId;
    }

    @Transactional
    public boolean delete(Long yrtId){
        return mapper.deleteByYrtId(yrtId) > 0;
    }

    /** KO: 분납 스케줄 계산 / JP: 分納スケジュール計算 */
    @Transactional(readOnly = true)
    public InstallmentResponse installment(Long yrtId, int months, String startMonth){
        ResultTotal rt = query.findResultTotal(yrtId);
        long nat = rt==null||rt.getAddNational()==null?0L:rt.getAddNational();
        long loc = rt==null||rt.getAddLocal()==null?0L:rt.getAddLocal();

        List<InstallmentEntry> list = new ArrayList<>();
        YearMonth ym = YearMonth.parse(startMonth);
        long natBase = months<=1? nat : nat / months;
        long locBase = months<=1? loc : loc / months;

        for(int i=0;i<months;i++){
            long natPay = (i==months-1)? (nat - natBase*(months-1)) : natBase;
            long locPay = (i==months-1)? (loc - locBase*(months-1)) : locBase;
            list.add(new InstallmentEntry(ym.plusMonths(i).toString(), natPay, locPay, natPay+locPay));
        }
        return new InstallmentResponse(list, "균등 분할, 잔액은 마지막 달 가산");
    }
}
