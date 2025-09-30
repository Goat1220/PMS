package org.pms.feature.voucher.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VoucherPreviewRow {
    private Long accountId;       // 계정과목ID
    private String drcrCode;      // 차대구분
    private BigDecimal debitAmt;  // 차변금액
    private BigDecimal creditAmt; // 대변금액
    private Long deptId;          // 발생부서ID
    private String note;          // 적요
    private String payDate;       // 지급일
}
