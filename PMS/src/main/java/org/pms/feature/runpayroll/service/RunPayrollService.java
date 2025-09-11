package org.pms.feature.runpayroll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.runpayroll.domain.*;
import org.pms.feature.runpayroll.mapper.RunPayrollMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RunPayrollService {

    private final RunPayrollMapper mapper;

    @Transactional(readOnly = true)
    public List<PayrollSummaryRow> getSummary(String yyyymm, String payType, String deptCode, String empNo){
        return mapper.selectPayrollSummary(yyyymm, payType, blankToNull(deptCode), blankToNull(empNo));
    }

    @Transactional(readOnly = true)
    public List<EarningItemRow> getItems(String empNo, String yyyymm, String payType){
        return mapper.selectEarningItems(empNo, yyyymm, blankToNull(payType));
    }

    @Transactional(readOnly = true)
    public List<DeductionRow> getDeductions(String empNo, String yyyymm, String payType){
        return mapper.selectDeductions(empNo, yyyymm, blankToNull(payType));
    }

    /** 급상여처리: 현재는 payslip 존재만 보장하고, 후속 로직은 placeholder */
    @Transactional
    public SimpleResult processPayroll(BatchEmpRequest req){
        List<Long> empIds = ensurePayslips(req.getEmpNos(), req.getYyyymm(), req.getPayType());
        int affected = mapper.dummyProcessPayroll(empIds, req.getYyyymm(), req.getPayType());
        return SimpleResult.ok(affected);
    }

    @Transactional
    public SimpleResult recalcTaxes(BatchEmpRequest req){
        List<Long> empIds = ensurePayslips(req.getEmpNos(), req.getYyyymm(), req.getPayType());
        int affected = mapper.dummyRecalcTaxes(empIds, req.getYyyymm(), req.getPayType());
        return SimpleResult.ok(affected);
    }

    @Transactional
    public SimpleResult applyYrt(BatchEmpRequest req){
        // 정산 반영은 payslip 유무와 무관할 수도 있으나, 일단 보장
        List<Long> empIds = toEmpIds(req.getEmpNos());
        int affected = mapper.dummyApplyYrt(empIds, req.getYyyymm(), req.getSplitMonths());
        return SimpleResult.ok(affected);
    }

    @Transactional
    public SimpleResult confirm(BatchEmpRequest req){
        List<Long> empIds = ensurePayslips(req.getEmpNos(), req.getYyyymm(), req.getPayType());
        boolean confirm = Boolean.TRUE.equals(req.getConfirm());
        int affected = mapper.dummyConfirm(empIds, req.getYyyymm(), req.getPayType(), confirm);
        return SimpleResult.ok(affected);
    }

    // ===== helpers =====

    private List<Long> ensurePayslips(List<String> empNos, String yyyymm, String payType){
        List<Long> ids = new ArrayList<>();
        for(String empNo : empNos){
            Long empId = mapper.findEmpIdByEmpNo(empNo);
            if(empId != null){
                mapper.ensurePayslipExists(empId, yyyymm, payType);
                ids.add(empId);
            }
        }
        return ids;
    }

    private List<Long> toEmpIds(List<String> empNos){
        List<Long> ids = new ArrayList<>();
        for(String empNo : empNos){
            Long empId = mapper.findEmpIdByEmpNo(empNo);
            if(empId != null) ids.add(empId);
        }
        return ids;
    }

    private String blankToNull(String s){
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
