package org.pms.feature.voucher.domain;

import java.util.List;

import lombok.Data;

/** 전표처리 요청 DTO */
@Data
public class VoucherProcessRequest {
	/** 지급연월 (예: "2018-08") */
	private String yyyymm;

	/** 급여유형 (예: "SALARY", "BONUS") */
	private String payType;

	/** 차변 계정ID (급여비용) - 미지정 시 1001 */
	private Long wageAccountId;

	/** 대변 계정ID (예수금/공제부채) - 미지정 시 2101 */
	private Long withholdAccountId;

	/** 전표번호(선택) */
	private String voucherNo;

	/** 전표 요약/비고(선택) — 기본값은 서비스에서 "PAYVCH-YYYY-MM-PAYTYPE" */
	private String summaryNote;

	/** 승인여부(선택) — 기본 'N' */
	private String approvedYn;

	private Long accruedAccountId;

	private Long bankAccountId;

	private Boolean includePayment;
	
	private Boolean cleanupExisting;

	/** (선택) 이번 미리보기 대상 사번 목록. null/빈값이면 전체 */
	private List<String> empNos;
}
