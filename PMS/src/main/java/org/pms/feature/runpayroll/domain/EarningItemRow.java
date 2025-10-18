package org.pms.feature.runpayroll.domain;

import lombok.*;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class EarningItemRow {
    private String itemName;         
    // 지급항목 이름  
    // 支給項目名

    private String nonTaxType;       
    // 비과세 유형 (스키마에는 없으며 필요 시 컬럼/뷰 확장 가능)  
    // 非課税区分（スキーマには存在せず、必要に応じてカラム/ビューを拡張可能）

    private String previousYn;       
    // 이전 지급 여부 (Y/N, payslip_item.is_previous 참조)  
    // 前回支給区分（Y/N、payslip_item.is_previous 参照）

    private Long amount;             
    // 금액  
    // 金額
}
