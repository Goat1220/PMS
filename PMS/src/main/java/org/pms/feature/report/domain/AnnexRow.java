package org.pms.feature.report.domain;

import lombok.Data;

/**
 * [부표 행]
 * - 구조는 WithholdingRow와 동일하게 맞춰, 화면 재사용성을 높였습니다.
 * - 굳이 분리 안 하고 WithholdingRow 하나만 써도 되지만
 *   기존 코드 호환을 위해 타입을 남겨둡니다.
 */
@Data
public class AnnexRow {
    private String applyYyyymm;
    private String incomeType;
    private String code;
    private Integer headCount;
    private Long taxTotal;

    private Long ntWithheld;
    private Long taxIncome;
    private Long penaltyTax;
    private Long taxWithheld;
    private Long adjRefund;
    private Long taxNt;

    private Integer no;
}
