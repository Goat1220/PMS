package org.pms.feature.voucher.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.voucher.domain.VoucherLedgerRow;

/**
 * 급상여 전표 처리용 Mapper  
 * 給与・賞与伝票処理用マッパー  
 *
 * - MyBatis를 통해 DB의 전표 관련 테이블에 접근  
 * - MyBatisを介してDBの伝票関連テーブルへアクセス  
 */
public interface PayrollVoucherMapper {

	/** 
	 * 전표 원장 데이터 조회  
	 * 伝票元帳データの照会  
	 */
	List<VoucherLedgerRow> selectVoucherLedger(
		@Param("yyyymm") String yyyymm, 
		@Param("payType") String payType
	);

	/** 
	 * 전표 미리보기 (차변/대변 라인)  
	 * 伝票プレビュー（借方／貸方ライン）  
	 */
	List<Map<String, Object>> selectVoucherPreviewLines(
		@Param("yyyymm") String yyyymm,
		@Param("payType") String payType
	);

	/** 
	 * 전표 헤더: 토큰(summary_note) 기준으로 존재하지 않으면 INSERT  
	 * 伝票ヘッダー：トークン（summary_note）基準で存在しない場合INSERT  
	 */
	int insertVoucherIfAbsent(
		@Param("yyyymm") String yyyymm, 
		@Param("token") String token
	);

	/** 
	 * 토큰으로 voucher_id 조회  
	 * トークンでvoucher_idを取得  
	 */
	Long findVoucherIdByToken(@Param("token") String token);

	/** 
	 * 기존 라인 삭제  
	 * 既存ライン削除  
	 */
	int deleteVoucherLines(@Param("voucherId") Long voucherId);

	/** 
	 * 차변 라인 INSERT (부서별 지급 합계)  
	 * 借方ラインINSERT（部署別支給合計）  
	 */
	int insertDebitLinesForMonth(
		@Param("voucherId") Long voucherId,
		@Param("yyyymm") String yyyymm,
		@Param("payType") String payType,
		@Param("accountId") Long accountId
	);

	/** 
	 * 대변 라인 INSERT (부서별 공제 합계)  
	 * 貸方ラインINSERT（部署別控除合計）  
	 */
	int insertCreditLinesForMonth(
		@Param("voucherId") Long voucherId,
		@Param("yyyymm") String yyyymm,
		@Param("payType") String payType,
		@Param("accountId") Long accountId
	);

	/** 
	 * 발생 전표 라인 INSERT  
	 * 発生伝票ラインINSERT  
	 */
	int insertAccruedLinesForMonth(
		@Param("voucherId") Long voucherId,
		@Param("yyyymm") String yyyymm,
		@Param("payType") String payType,
		@Param("accountId") long accountId
	);

	/** 
	 * 지급 전표 차변 (예: 발생비용 대체)  
	 * 支給伝票の借方（例：発生費用振替）  
	 */
	int insertPaymentAccruedDebitForMonth(
		@Param("voucherId") Long voucherId,
		@Param("yyyymm") String yyyymm,
		@Param("payType") String payType,
		@Param("accountId") long accountId
	);

	/** 
	 * 지급 전표 대변 (은행계정 처리)  
	 * 支給伝票の貸方（銀行勘定処理）  
	 */
	int insertPaymentBankCreditForMonth(
		@Param("voucherId") Long voucherId,
		@Param("yyyymm") String yyyymm,
		@Param("payType") String payType,
		@Param("accountId") long accountId
	);

	/** 
	 * 토큰 기준 전표라인 삭제  
	 * トークン基準で伝票ライン削除  
	 */
	void deleteVoucherLinesByToken(@Param("token") String token);

	/** 
	 * 토큰 기준 전표헤더 삭제  
	 * トークン基準で伝票ヘッダー削除  
	 */
	int deleteVoucherHeaderByToken(@Param("token") String token);

	/** 
	 * 전표 미리보기(발생분) — 부서별 차/대변 구성  
	 * 伝票プレビュー（発生分）— 部署別の借方／貸方構成  
	 */
	List<VoucherLedgerRow> selectVoucherPreviewAccrual(
		@Param("yyyymm") String yyyymm,
		@Param("payType") String payType,
		@Param("wageAcct") long wageAcct,
		@Param("withholdAcct") long withholdAcct,
		@Param("accruedAcct") long accruedAcct,
		@Param("empNos") List<String> empNos
	);
}
