package org.pms.feature.payslip.domain;

import lombok.Data;

/** 좌측 목록 그리드의 한 줄 */
@Data
public class PayslipListRow {
    private Long   payslipId;
    private String payType;
    private String periodYm;       // YYYY-MM
    private Long   grossAmount;    // 지급총액
    private Long   prevPaidAmount; // 기지급액 (is_previous='Y')
    private Long   deductionSum;   // 공제총액
    private Long   netAmount;      // 실지급액
    private String payTypeName; // 한글 라벨(급여/상여/…)

}
