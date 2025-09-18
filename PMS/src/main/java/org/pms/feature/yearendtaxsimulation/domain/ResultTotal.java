package org.pms.feature.yearendtaxsimulation.domain;

import lombok.Data;

/** KO: 추가 납부 합계 / JP: 追加納付合計 */
@Data
public class ResultTotal {
    private Long addNational; // 국세
    private Long addLocal;    // 지방세
}
