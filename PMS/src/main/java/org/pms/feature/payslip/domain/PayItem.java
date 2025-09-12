package org.pms.feature.payslip.domain;

import lombok.Data;

@Data
public class PayItem {
    private String itemName;
    private String chkPaid;   // 'Y'/'N' ← is_previous alias
    private String chkValid;  // 'Y'/'N' ← is_safety_bonus alias
    private Long   amount;
}