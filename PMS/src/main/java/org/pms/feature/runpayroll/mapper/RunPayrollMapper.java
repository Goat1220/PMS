package org.pms.feature.runpayroll.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.runpayroll.domain.DeductionRow;
import org.pms.feature.runpayroll.domain.EarningItemRow;
import org.pms.feature.runpayroll.domain.EmpFlag;
import org.pms.feature.runpayroll.domain.PayrollSummaryRow;
import org.pms.feature.runpayroll.domain.YrtAdjustment;

import java.util.List;
import java.util.Map;

@Mapper
public interface RunPayrollMapper {

    // ===== 조회 =====
    List<PayrollSummaryRow> selectPayrollSummary(
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType,
            @Param("deptCode") String deptCode,
            @Param("empNo") String empNo);

    List<EarningItemRow> selectEarningItems(
            @Param("empNo") String empNo,
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType);

    List<DeductionRow> selectDeductions(
            @Param("empNo") String empNo,
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType);

    // ===== 공통 유틸 =====
    Long findEmpIdByEmpNo(@Param("empNo") String empNo);

    int ensurePayslipExists(@Param("empId") Long empId,
                            @Param("yyyymm") String yyyymm,
                            @Param("payType") String payType);

    Long ensurePayslip(@Param("empId") Long empId,
                       @Param("yyyymm") String yyyymm,
                       @Param("payType") String payType);

    Long sumEarningItems(@Param("payslipId") Long payslipId);
    Long sumDeductionItems(@Param("payslipId") Long payslipId);

    int updatePayslipNet(@Param("payslipId") Long payslipId,
                         @Param("net") Long net);

    // 요약행 플래그 저장 (emp_tax_profile upsert 등)
    int upsertPayslipFlags(@Param("empId") Long empId,
                           @Param("yyyymm") String yyyymm,
                           @Param("payType") String payType,
                           @Param("f") EmpFlag f);

    // 확정 상태 체크/변경
    int countConfirmedPayslips(@Param("yyyymm") String yyyymm,
                               @Param("payType") String payType,
                               @Param("empNos") List<String> empNos);

    int confirmPayslips(@Param("yyyymm") String yyyymm,
                        @Param("payType") String payType,
                        @Param("empNos") List<String> empNos);
    
    int unconfirmPayslips(
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType,
            @Param("empNos") List<String> empNos
    );

    // ===== 월집계(pay_month_summary) =====
    Map<String, Object> calcMonthRollup(@Param("yyyymm") String yyyymm,
                                        @Param("payType") String payType);

    int upsertMonthlySummaryMonth(@Param("payType") String payType,
                                  @Param("yyyymm") String yyyymm,
                                  @Param("totalPayAmt") Long totalPayAmt,
                                  @Param("prevPaidAmt") Long prevPaidAmt,
                                  @Param("totalDedAmt") Long totalDedAmt,
                                  @Param("netPayAmt") Long netPayAmt);

    // ===== 전표 처리(pay_voucher, pay_voucher_line) =====
    Integer hasVoucherByToken(@Param("token") String token);

    int insertVoucherHeaderByToken(@Param("yyyymm") String yyyymm,
                                   @Param("token") String token);

    Long findVoucherIdByToken(@Param("token") String token);

    int upsertVoucherLinesForPayslip(@Param("payslipId") Long payslipId,
                                     @Param("voucherId") Long voucherId,
                                     @Param("wageAccountId") Long wageAccountId,
                                     @Param("withholdAccountId") Long withholdAccountId);

    // ===== 세금 재처리 =====
    int deleteTaxDeductions(@Param("payslipId") Long payslipId);

    int insertTaxDeduction(@Param("payslipId") Long payslipId,
                           @Param("code") String code,
                           @Param("name") String name,
                           @Param("amount") Long amount);

    // ===== YRT 반영 =====
    YrtAdjustment findLatestYrtAdjustment(@Param("empId") Long empId,
                                          @Param("baseYear") Integer baseYear);

    int insertEarningItem(@Param("payslipId") Long payslipId,
                          @Param("itemName") String itemName,
                          @Param("amount") Long amount);

    int insertDeductionItem(@Param("payslipId") Long payslipId,
                            @Param("code") String code,
                            @Param("name") String name,
                            @Param("amount") Long amount);
    
    int updateEmpRetiredYn(@Param("empId") Long empId,
            @Param("yyyymm") String yyyymm,
            @Param("retiredYn") String retiredYn);
    
    //월별 합계 업서트
    int upsertMonthSummary(@Param("yyyymm") String yyyymm,
                           @Param("payType") String payType);
}
