package org.pms.feature.runpayroll.domain;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PayrollSummaryRow {
    private String empNo;
    private String empName;
    private String deptName;

    private String taxApplyType;            // (연말정산 헤더 등에서 가져옴 - 없으면 null)
    private Double taxAdjustRate;           // emp_tax_profile.tax_adjust_rate
    private String projectName;             // emp_tax_profile.project_name

    private String taxCalcExemptYn;         // 'Y'/'N'
    private String prorateYn;
    private String settlementReflectYn;
    private String manufTaxExemptYn;
    private String overseasTaxExemptYn;
    private String researcherTaxExemptYn;

    private Double incomeTaxReductionRate;  // emp_tax_profile.income_tax_reduction_rate
    private String personalTaxApplyType;    // emp_tax_profile.personal_tax_apply_type
    private Double bonusRate;               // emp_tax_profile.bonus_rate

    private Long payTotAmt;     // 이번달 지급 총액 (이전분 제외)
    private Long prevPayTotAmt; // 기지급(이전분) 총액
    private Long dedTotAmt;     // 공제 총액
    private Long netPayAmt;     // 실지급액 (payslip.net_pay_amt)
    private String retireYn;    // 계산: 해당월 말일 기준 퇴사 여부
}
