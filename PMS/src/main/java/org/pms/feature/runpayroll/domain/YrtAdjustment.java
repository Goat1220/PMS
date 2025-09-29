package org.pms.feature.runpayroll.domain;
import lombok.Data;

@Data
public class YrtAdjustment {
    private Long empId;
    private Integer baseYear;
    private Long refundAmt;          // 환급 금액
    private Long additionalTaxAmt;   // 추징 금액
}
