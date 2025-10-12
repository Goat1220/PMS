package org.pms.feature.voucher.domain;

import lombok.Data;
import java.util.Date;

@Data
public class PayVoucher {
    private Long voucherId;       // 전표ID (PK)
    private String voucherNo;     // 전표번호
    private Date payDate;         // 지급일
    private String summaryNote;   // 적요(요약)
    private String approvedYn;    // 승인여부 (Y/N)
}
