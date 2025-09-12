package org.pms.feature.payslip.domain;

import java.util.List;
import lombok.Data;

@Data
public class PayslipSummary {
    private Long   payslipId;
    private String empNo;
    private String empName;
    private String periodYm;     // apply_yyyymm (YYYY-MM)
    private String payType;
    private Long   grossAmount;  // 지급총액 (item 합계)
    private Long   deductionSum; // 공제총액
    private Long   netAmount;    // payslip.net_pay_amt

    private List<PayItem>       payItems;
    private List<DeductionItem> deductionItems;
}
