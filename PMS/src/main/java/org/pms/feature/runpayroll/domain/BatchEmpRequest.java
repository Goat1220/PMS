package org.pms.feature.runpayroll.domain;

import lombok.*;

import java.util.List;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class BatchEmpRequest {
    private String yyyymm;      
    // 지급연월 (예: "YYYY-MM")
    // 支給年月（例："YYYY-MM"）

    private String payType;     
    // 급여유형 (null 가능, 확정/해제 및 처리 시 사용)
    // 給与区分（null可、確定/解除・処理時に使用）

    private List<String> empNos;
    // 사원번호 목록
    // 社員番号のリスト

    private Integer splitMonths; 
    // 적용개월수 (apply-yrt용)
    // 適用月数（apply-yrt用）

    private Boolean confirm;    
    // 확정 여부 (confirm용)
    // 確定有無（confirm用）
    
    private List<EmpFlag> flags; 
    // 요약행에서 수정된 플래그/입력값 저장용 (선택적)
    // サマリー行で編集したフラグ/入力値を保存するため（任意）
}
