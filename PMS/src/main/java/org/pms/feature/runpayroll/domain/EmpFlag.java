package org.pms.feature.runpayroll.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EmpFlag {
    private String empNo;                   // 행 식별용(사번)

    private BigDecimal taxAdjustRate;       // 세액조정율
    private String projectName;             // 프로젝트명

    private String taxCalcExemptYn;         // 세금계산안함(Y/N)
    private String prorateYn;               // 일할계산(Y/N)
    private String settlementReflectYn;            // 정산반영(Y/N)

    private String manufTaxExemptYn;            // 생산직 비과세(Y/N)
    private String overseasTaxExemptYn;           // 국외근로 비과세(Y/N)
    private String researcherTaxExemptYn;            // 연구원 비과세(Y/N)

    private BigDecimal incomeTaxReductionRate; // 소득세 감면율 
    private String personalTaxApplyType;    // 세금적용(개인) 유형
    private BigDecimal bonusRate;           // 상여율

    private String retiredYn;               // 퇴직여부
}
