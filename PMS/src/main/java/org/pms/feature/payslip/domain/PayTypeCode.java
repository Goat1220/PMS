package org.pms.feature.payslip.domain;

import lombok.Data;

/** 급상여종류 드롭다운용 코드 */
@Data
public class PayTypeCode {
    private String code; // pay_type
    private String name; // 표시명
}
