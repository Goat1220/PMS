package org.pms.feature.voucher.domain;

import lombok.Data;

@Data
public class VoucherRequest {
    private String yyyymm;     // 적용연월
    private String payType;    // 급여유형 (SALARY/BONUS)
    private String orgUnit;    // 회계단위
    private String jobType;    // 급여작업
    private String empTarget;  // 대상자 범위 (재직/퇴직)
    private String procKind;   // 처리구분 (정상/조정)
}
