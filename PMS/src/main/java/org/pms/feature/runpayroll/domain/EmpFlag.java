package org.pms.feature.runpayroll.domain;
import lombok.Data;

@Data
public class EmpFlag {
    private String empNo;
    private String personalTaxApplyType;
    private String taxAdjustRate;
    private String projectName;
    private String taxCalcExemptYn;
    private String prorateYn;
    private String settlementYn;
    private String nonTaxProdYn;
    private String foreignWorkYn;
    private String researcherYn;
    private String bonusRate;
    private String retiredYn;
}
