package org.pms.feature.yearendtaxsimulation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** KO: 분납 스케줄 한 행 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentEntry {
    private String yyyymm;
    private Long national;
    private Long local;
    private Long total;
}
