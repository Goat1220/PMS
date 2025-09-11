package org.pms.feature.runpayroll.domain;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EarningItemRow {
    private String itemName;
    private String nonTaxType;     // 스키마에 없어 placeholder (필요 시 컬럼/뷰 확장)
    private String previousYn;     // Y/N  (payslip_item.is_previous)
    private Long amount;           // 금액
}
