// src/main/java/org/pms/feature/runpayroll/domain/PayrollSummaryRow.java
package org.pms.feature.runpayroll.domain;
import lombok.Data;

@Data
public class PayrollSummaryRow {
    private String empNo;
    private String empName;
    private String deptName;

    private String taxApplyType;
    private String taxAdjustRate;
    private String projectName;

    private String taxCalcExemptYn;
    private String prorateYn;

    // XML 별칭에 맞춘 필드명들
    private String settlementReflectYn;     // 기존 settlementYn 과 다름
    private String manufTaxExemptYn;        // 기존 nonTaxProdYn 과 다름
    private String overseasTaxExemptYn;     // 기존 foreignWorkYn 과 다름
    private String researcherTaxExemptYn;   // 기존 researcherYn 과 다름

    private String incomeTaxReductionRate;
    private String personalTaxApplyType;
    private String bonusRate;

    private Long payTotAmt;
    private Long prevPayTotAmt;
    private Long dedTotAmt;
    private Long netPayAmt;

    private String retireYn;
}
