package org.pms.feature.payslip.domain;

import lombok.Data;

/**
 * JP: 明細の「控除項目」と「控除項目コード」を画面表示するためのVO
 * KR: 공제항목명과 공제항목코드를 화면에 표시하기 위한 VO
 */
@Data
public class DeductionItem {
    // JP: 控除項目コード（DB: payslip_deduction.deduction_code）
    // KR: 공제항목코드 (DB: payslip_deduction.deduction_code)
    private String itemCode;

    // JP: 控除項目名（DB: payslip_deduction.deduction_name）
    // KR: 공제항목명 (DB: payslip_deduction.deduction_name)
    private String itemName;

    // JP: 金額（DB: payslip_deduction.amount）
    // KR: 금액 (DB: payslip_deduction.amount)
    private Long amount;
}
