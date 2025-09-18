package org.pms.feature.yearendtaxsimulation.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** KO: 분납 응답 DTO */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentResponse {
    private List<InstallmentEntry> schedule;
    private String note;
}
