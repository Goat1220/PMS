package org.pms.feature.runpayroll.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PayslipDeductionRow {
    private String deductionName; // 공제항목
    private BigDecimal amount;    // 금액
}
