package org.pms.feature.runpayroll.domain;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class BatchEmpRequest {
    private String yyyymm;      // "YYYY-MM"
    private String payType;     // nullable (확정/해제, 처리 시 사용)
    private List<String> empNos;
    // apply-yrt 용
    private Integer splitMonths;
    // confirm 용
    private Boolean confirm;
}
