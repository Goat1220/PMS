package org.pms.feature.runpayroll.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.runpayroll.domain.*;

import java.util.List;

@Mapper
public interface RunPayrollMapper {

    // ==== Summary ====
    List<PayrollSummaryRow> selectPayrollSummary(
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType,
            @Param("deptCode") String deptCode,
            @Param("empNo") String empNo
    );

    // ==== Items / Deductions (Right panel) ====
    List<EarningItemRow> selectEarningItems(
            @Param("empNo") String empNo,
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType
    );

    List<DeductionRow> selectDeductions(
            @Param("empNo") String empNo,
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType
    );

    // ==== Utility ====
    Long findEmpIdByEmpNo(@Param("empNo") String empNo);

    Integer ensurePayslipExists(
            @Param("empId") Long empId,
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType
    );

    // 아래는 실제 처리 로직 연결 지점 (업무 훅)
    int dummyProcessPayroll(@Param("empIds") List<Long> empIds,
                            @Param("yyyymm") String yyyymm,
                            @Param("payType") String payType);

    int dummyRecalcTaxes(@Param("empIds") List<Long> empIds,
                         @Param("yyyymm") String yyyymm,
                         @Param("payType") String payType);

    int dummyApplyYrt(@Param("empIds") List<Long> empIds,
                      @Param("yyyymm") String yyyymm,
                      @Param("splitMonths") Integer splitMonths);

    int dummyConfirm(@Param("empIds") List<Long> empIds,
                     @Param("yyyymm") String yyyymm,
                     @Param("payType") String payType,
                     @Param("confirm") boolean confirm);
}
