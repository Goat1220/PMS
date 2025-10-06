package org.pms.feature.voucher.domain;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VoucherLedgerRow {
    // 화면 표시용
	private String displaySeq; 		// 화면 번호
    private String accountName;     // 계정과목
    private String drcrName;        // 차대구분(차변/대변 라벨)
    private BigDecimal debitAmt;    // 차변금액
    private BigDecimal creditAmt;   // 대변금액
    private String occurDeptName;   // 발생부서
    private String occurDate;       // 발생일자 yyyy-MM-dd
    private String payDateStr;      // 지급일 yyyy-MM-dd
    private String summaryNote;     // 적요
    private String voucherNo;       // 전표발행번호(전표내부코드)
    private String approvedYn;      // 승인여부(Y/N)
    private Integer lineSeq;        // 순번
    private String accountCode;     // 계정내부코드
    private String drcrCode;        // 자대구분코드(D/C)
    private String occurDeptCode;   // 발생부서코드
    private String costTypeCode;    // 비용구분코드
    private Integer processFlag;    // 처리구분(예: 승인여부 기반 0/1)
    private String targetCode;      // 전표처리대상자코드(예: source_dept_code 등)
}
