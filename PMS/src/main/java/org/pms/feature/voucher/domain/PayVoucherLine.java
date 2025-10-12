package org.pms.feature.voucher.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PayVoucherLine {
    private Long lineId;        // 전표행ID (PK)
    private Long voucherId;     // 전표ID (FK)
    private Integer lineSeq;    // 행 순번
    private Long accountId;     // 계정과목ID
    private String drcrCode;    // 차대구분(D/C)
    private Long deptId;        // 부서ID
    private Long sourceDeptId;  // 발생원천(선택사항)
    private String costTypeCode;// 비용구분 코드
    private BigDecimal debitAmt;// 차변금액
    private BigDecimal creditAmt;// 대변금액
    private String note;        // 적요
    private String payDate;     // 지급일 (yyyy-MM-dd)
}
