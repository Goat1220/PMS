package org.pms.feature.runpayroll.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.runpayroll.domain.*;

public interface RunPayrollMapper {
    List<PayrollRow> selectPayrollSummary(PayrollSearchParam param);

    List<PayslipItemRow> selectPayslipItems(@Param("empNo") String empNo,
                                            @Param("yyyymm") String yyyymm,
                                            @Param("payType") String payType);

    List<PayslipDeductionRow> selectPayslipDeductions(@Param("empNo") String empNo,
                                                      @Param("yyyymm") String yyyymm,
                                                      @Param("payType") String payType);
}
