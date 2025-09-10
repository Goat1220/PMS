package org.pms.feature.runpayroll.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PayslipItemRow {
    private String itemName;    // 지급항목
    private String nonTaxType;  // 비과세유형(표시용)
    private String previousYn;  // 기지급여부
    private BigDecimal amount;  // 금액
}
