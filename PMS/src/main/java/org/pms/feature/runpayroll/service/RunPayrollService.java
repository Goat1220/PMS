package org.pms.feature.runpayroll.service;

import java.util.List;
import org.pms.feature.runpayroll.domain.*;

public interface RunPayrollService {
    List<PayrollRow> getPayrollSummary(PayrollSearchParam param);
    List<PayslipItemRow> getPayslipItems(String empNo, String yyyymm, String payType);
    List<PayslipDeductionRow> getPayslipDeductions(String empNo, String yyyymm, String payType);
}
