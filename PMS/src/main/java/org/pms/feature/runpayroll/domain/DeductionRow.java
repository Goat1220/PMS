package org.pms.feature.runpayroll.domain;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DeductionRow {
    private String deductionName;
    private Long amount;
}
