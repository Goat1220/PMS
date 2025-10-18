package org.pms.feature.voucher.domain;

import java.util.List;
import lombok.Data;

/**
 * 전표처리 요청 DTO  
 * 伝票処理リクエストDTO  
 *
 * - 급여/상여 발생분을 전표로 변환할 때 사용하는 요청 데이터 구조  
 * - 給与・賞与の発生分を伝票に変換する際に使用するリクエストデータ構造  
 */
@Data
public class VoucherProcessRequest {

	/** 지급연월 (예: "2018-08") / 支給年月（例："2018-08"） */
	private String yyyymm;

	/** 급여유형 (예: "SALARY", "BONUS") / 給与タイプ（例："SALARY", "BONUS"） */
	private String payType;

	/** 차변 계정ID (급여비용) — 미지정 시 1001 / 借方勘定ID（給与費用）— 未指定時は1001 */
	private Long wageAccountId;

	/** 대변 계정ID (예수금/공제부채) — 미지정 시 2101 / 貸方勘定ID（預り金・控除負債）— 未指定時は2101 */
	private Long withholdAccountId;

	/** 전표번호(선택) / 伝票番号（任意） */
	private String voucherNo;

	/** 전표 요약/비고(선택) — 기본값은 서비스에서 "PAYVCH-YYYY-MM-PAYTYPE"  
	    伝票サマリー／備考（任意）— デフォルト値はサービス側で "PAYVCH-YYYY-MM-PAYTYPE" */
	private String summaryNote;

	/** 승인여부(선택) — 기본값 'N' / 承認有無（任意）— デフォルトは 'N' */
	private String approvedYn;

	/** 발생 계정ID (예: 발생전표용 계정) / 発生勘定ID（例：発生伝票用勘定） */
	private Long accruedAccountId;

	/** 은행 계정ID (예: 보통예금 등) / 銀行勘定ID（例：普通預金など） */
	private Long bankAccountId;

	/** 지급 포함 여부 (true 시 지급 전표도 포함) / 支給を含めるかどうか（true の場合、支給伝票も含む） */
	private Boolean includePayment;

	/** 기존 전표 정리 여부 (true 시 기존 PAYVCH-YYYY-MM-PAYTYPE#ACCRUAL 삭제)  
	    既存伝票のクリーンアップ（true の場合、既存 PAYVCH-YYYY-MM-PAYTYPE#ACCRUAL を削除） */
	private Boolean cleanupExisting;

	/** 미리보기 대상 사번 목록 (null/빈값 시 전체 대상)  
	    プレビュー対象社員番号リスト（null または空の場合、全社員が対象） */
	private List<String> empNos;
}
