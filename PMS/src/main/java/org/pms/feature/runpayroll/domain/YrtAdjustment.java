package org.pms.feature.runpayroll.domain;

import lombok.Data;

@Data
public class YrtAdjustment {
    private Long empId;                
    // 사원 ID  
    // 社員ID

    private Integer baseYear;          
    // 기준 연도 (연말정산 대상 연도)  
    // 基準年度（年末調整対象年度）

    private Long refundAmt;            
    // 환급 금액  
    // 還付金額

    private Long additionalTaxAmt;     
    // 추징 금액  
    // 追徴金額
}
