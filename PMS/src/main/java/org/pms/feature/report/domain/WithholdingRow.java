// WithholdingRow.java
package org.pms.feature.report.domain;
import lombok.Data;

@Data
public class WithholdingRow {
    private String  applyYyyymm;
    private String  incomeType;
    private String  code;
    private Integer headCount;
    private Long    taxTotal;
    private Long    ntWithheld;
    private Long    taxIncome;
    private Long    penaltyTax;
    private Long    taxWithheld;
    private Long    adjRefund;
    private Long    taxNt;
    private Integer no;          // 화면 표시용 순번
}
