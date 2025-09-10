package org.pms.feature.runpayroll.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PayrollRow {
    private String empNo;   // 사번
    private String empName; // 사원
    private String deptName; // 부서

    private String taxApplyType;              // 세금적용구분
    private BigDecimal taxAdjustRate;         // 세액조정율
    private String projectName;               // 프로젝트
    private String taxCalcExemptYn;           // 세금계산안함
    private String prorateYn;                 // 일할계산여부
    private String settlementReflectYn;       // 정산반영여부
    private String manufTaxExemptYn;          // 생산직비과세대상여부
    private String overseasTaxExemptYn;       // 국외근로비과세대상여부
    private String researcherTaxExemptYn;     // 연구원비과세대상여부
    private BigDecimal incomeTaxReductionRate;// 소득세감면율
    private String personalTaxApplyType;      // 세금적용구분(개인)
    private BigDecimal bonusRate;             // 상여율

    private BigDecimal payTotAmt;   // 지급총액
    private BigDecimal prevPayTotAmt; // 기지급총액
    private BigDecimal dedTotAmt;   // 공제총액
    private BigDecimal netPayAmt;   // 실지급액
    private String retireYn;        // 퇴직여부 (Y/N)
}
