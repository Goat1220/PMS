package org.pms.feature.runpayroll.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.pms.feature.runpayroll.domain.*;
import org.pms.feature.runpayroll.mapper.RunPayrollMapper;

@Service
public class RunPayrollServiceImpl implements RunPayrollService {

    @Autowired
    private RunPayrollMapper mapper;

    @Override
    public List<PayrollRow> getPayrollSummary(PayrollSearchParam param) {
        return mapper.selectPayrollSummary(param);
    }

    @Override
    public List<PayslipItemRow> getPayslipItems(String empNo, String yyyymm, String payType) {
        return mapper.selectPayslipItems(empNo, yyyymm, payType);
    }

    @Override
    public List<PayslipDeductionRow> getPayslipDeductions(String empNo, String yyyymm, String payType) {
        return mapper.selectPayslipDeductions(empNo, yyyymm, payType);
    }
}
