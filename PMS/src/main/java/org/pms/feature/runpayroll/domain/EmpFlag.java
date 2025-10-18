package org.pms.feature.runpayroll.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EmpFlag {
    private String empNo;                   
    // 행 식별용 (사번)
    // 行識別用（社員番号）

    private BigDecimal taxAdjustRate;       
    // 세액 조정율  
    // 税額調整率

    private String projectName;             
    // 프로젝트명  
    // プロジェクト名

    private String taxCalcExemptYn;         
    // 세금 계산 제외 여부 (Y/N)  
    // 税計算除外（Y/N）

    private String prorateYn;               
    // 일할 계산 여부 (Y/N)  
    // 日割計算（Y/N）

    private String settlementReflectYn;     
    // 정산(연말정산) 반영 여부 (Y/N)  
    // 年末調整反映（Y/N）

    private String manufTaxExemptYn;        
    // 생산직 비과세 여부 (Y/N)  
    // 生産職非課税（Y/N）

    private String overseasTaxExemptYn;     
    // 국외 근로 비과세 여부 (Y/N)  
    // 海外勤務非課税（Y/N）

    private String researcherTaxExemptYn;   
    // 연구원 비과세 여부 (Y/N)  
    // 研究員非課税（Y/N）

    private BigDecimal incomeTaxReductionRate; 
    // 소득세 감면율  
    // 所得税減免率 

    private String personalTaxApplyType;    
    // 세금 적용(개인) 유형  
    // 個人税適用タイプ

    private BigDecimal bonusRate;           
    // 상여율  
    // 賞与率

    private String retiredYn;               
    // 퇴직 여부 (Y/N)  
    // 退職有無（Y/N）
}
