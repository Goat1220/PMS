package org.pms.feature.yearendtaxsimulation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** KO: 분납 요청 DTO */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentRequest {
    private Long yrtId;
    private Integer months;    // 2..3
    private String startMonth; // YYYY-MM
}
