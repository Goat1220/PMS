package org.pms.feature.payslip.domain;

import lombok.Data;

/*
 * [KO] 공제 항목 한 줄(한 개)을 표현하는 VO
 * - 예: 국민연금, 건강보험, 소득세 등
 * - amount는 원(₩) 단위의 정수 금액을 가정
 *
 * [JA] 控除項目を表す VO
 * - 例(れい)：国民年金、健康保険、所得税 など
 * - amount は通貨金額（整数）を想定
 */
@Data
public class DeductionItem {

    // [KO] 공제항목 코드(식별자). 예: "DED_INCOME_TAX"
    // [JA] 控除項目コード（識別子）
    private String itemCode;

    // [KO] 공제항목명. 예: "소득세"
    // [JA] 項目名 	例：所得税
    private String itemName;

    // [KO] 공제 금액(원)
    // [JA] 控除金額
    private Long amount;
}
