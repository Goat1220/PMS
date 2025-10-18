package org.pms.feature.runpayroll.domain;

import lombok.*;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class DeductionRow {
    private String deductionName;  
    // 공제항목 이름  
    // 控除項目名

    private Long amount;           
    // 공제 금액  
    // 控除金額
}
