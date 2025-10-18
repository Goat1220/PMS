package org.pms.feature.voucher.service;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.pms.feature.runpayroll.domain.SimpleResult;
import org.pms.feature.voucher.domain.VoucherLedgerRow;
import org.pms.feature.voucher.domain.VoucherProcessRequest;
import org.pms.feature.voucher.mapper.PayrollVoucherMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * 전표 처리 서비스 (급여/상여)  
 * 伝票処理サービス（給与／賞与）
 *
 * - 전표 미리보기/실반영 로직을 제공  
 * - 伝票のプレビュー／本反映ロジックを提供
 */
@Service
@RequiredArgsConstructor
public class PayrollVoucherService {

	private final PayrollVoucherMapper mapper;

	/** yyyy-MM 형식 검증용 정규식 / yyyy-MM 形式検証用の正規表現 */
	private static final Pattern YYYYMM = Pattern.compile("^\\d{4}-\\d{2}$"); // e.g. 2025-08

	/**
	 * 전표 원장 조회 (읽기 전용)  
	 * 伝票元帳の取得（読取専用）
	 */
	@Transactional(readOnly = true)
	public List<VoucherLedgerRow> getVoucherLedger(String yyyymm, String payType) {
		// yyyymm 형식 확인 / yyyymm 形式チェック
		if (yyyymm == null || !YYYYMM.matcher(yyyymm).matches()) {
			throw new IllegalArgumentException("yyyymm 형식은 'YYYY-MM'이어야 합니다. (예: 2025-08)");
		}
		// payType은 선택값 / payType は任意
		return mapper.selectVoucherLedger(yyyymm, payType);
	}

	/**
	 * 전표 실반영: 헤더 없으면 생성 후 라인 재작성  
	 * 伝票本反映：ヘッダーが無ければ作成し、ラインを再作成
	 */
	@Transactional
	public SimpleResult processVoucher(VoucherProcessRequest req) {
		// 파라미터 정규화 / パラメータ正規化
		final String yyyymm = norm(req.getYyyymm());
		final String payType = norm(req.getPayType());
		if (yyyymm == null)
			return SimpleResult.fail("지급연월(yyyymm)이 없습니다.");
		if (payType == null)
			return SimpleResult.fail("급여유형(payType)이 없습니다.");

		// 계정 기본값 보정 / 勘定科目のデフォルト補正
		final long wageAcct = req.getWageAccountId() != null ? req.getWageAccountId().longValue() : 1001L;   // 급여비용(D) / 給与費用(D)
		final long withholdAcct = req.getWithholdAccountId() != null ? req.getWithholdAccountId().longValue() : 2101L; // 예수금(C) / 預り金(C)
		final long accruedAcct = req.getAccruedAccountId() != null ? req.getAccruedAccountId().longValue() : 221100L;  // 미지급급여(C) / 未払給与(C) ★
		final long bankAcct = req.getBankAccountId() != null ? req.getBankAccountId().longValue() : 111100L;           // 보통예금 / 普通預金 ★

		// 공통 토큰(요약) 구성: 전표 유형별 suffix 부여  
		// 共通トークン（サマリ）構成：伝票タイプ別にサフィックス付与
		String base = (req.getSummaryNote() != null && !req.getSummaryNote().trim().isEmpty())
				? req.getSummaryNote().trim()
				: ("PAYVCH-" + yyyymm + "-" + payType);

		int affected = 0;

		// ── ① 발생 전표(ACCRUAL) ─────────────────────────────────────────
		//    발생분을 헤더 보장 후, 차/대/미지급 라인을 재작성  
		//    発生分はヘッダーを保証し、借／貸／未払ラインを再作成
		String accrualToken = base.endsWith("#ACCRUAL") ? base : (base + "#ACCRUAL");
		mapper.insertVoucherIfAbsent(yyyymm, accrualToken);   // 헤더 없으면 INSERT / ヘッダーが無ければINSERT
		Long accrualId = mapper.findVoucherIdByToken(accrualToken);
		if (accrualId == null)
			return SimpleResult.fail("발생 전표 헤더 생성/조회 실패");

		mapper.deleteVoucherLines(accrualId);                                                     // 기존 라인 제거 / 既存ライン削除
		int dRows = mapper.insertDebitLinesForMonth(accrualId, yyyymm, payType, wageAcct);        // D: 급여비용(지급총액) / D: 給与費用（支給総額）
		int cRows = mapper.insertCreditLinesForMonth(accrualId, yyyymm, payType, withholdAcct);   // C: 예수금(공제총액) / C: 預り金（控除総額）
		int aRows = mapper.insertAccruedLinesForMonth(accrualId, yyyymm, payType, accruedAcct);   // C: 미지급급여(실지급) / C: 未払給与（実支給）
		affected += dRows + cRows + aRows;

		// ── ② 지급 전표(PAYMENT) ─────────────────────────────────────────
		//    필요 시 주석 해제하여 지급 분개 생성  
		//    必要に応じてコメント解除し、支給仕訳を生成
		/*
		 * String payToken = base.endsWith("#PAYMENT") ? base : (base + "#PAYMENT");
		 * mapper.insertVoucherIfAbsent(yyyymm, payToken);
		 * Long payId = mapper.findVoucherIdByToken(payToken);
		 * if (payId == null) return SimpleResult.fail("지급 전표 헤더 생성/조회 실패");
		 *
		 * mapper.deleteVoucherLines(payId);
		 * int ad = mapper.insertPaymentAccruedDebitForMonth(payId, yyyymm, payType, accruedAcct); // D: 미지급급여(=net)
		 * int bc = mapper.insertPaymentBankCreditForMonth(payId, yyyymm, payType, bankAcct);     // C: 보통예금(=net)
		 * affected += ad + bc;
		 */

		// 처리 건수 반환 / 処理件数を返却
		return SimpleResult.ok(affected);
	}

	/**
	 * 발생 전표 미리보기 (ACCRUAL)  
	 * 発生伝票プレビュー（ACCRUAL）
	 *
	 * - cleanupExisting=true 시 기존 발생 전표를 삭제(멱등성 확보)  
	 * - cleanupExisting=true の場合、既存の発生伝票を削除（冪等性確保）
	 * - empNos 지정 시 해당 사번만 대상  
	 * - empNos 指定時は当該社員のみ対象
	 */
	@Transactional 
	public List<VoucherLedgerRow> previewVoucherAccrual(VoucherProcessRequest req) {
		// 파라미터 정규화 / パラメータ正規化
		final String yyyymm = norm(req.getYyyymm());
		final String payType = norm(req.getPayType());
		if (yyyymm == null)
			return Collections.emptyList();
		if (payType == null)
			return Collections.emptyList();

		// 계정 기본값 / 勘定科目デフォルト
		final long wageAcct = req.getWageAccountId() != null ? req.getWageAccountId() : 1001L;     // 급여비용(D) / 給与費用(D)
		final long withholdAcct = req.getWithholdAccountId() != null ? req.getWithholdAccountId() : 2101L; // 예수금(C) / 預り金(C)
		final long accruedAcct = req.getAccruedAccountId() != null ? req.getAccruedAccountId() : 221100L;  // 미지급급여(C) / 未払給与(C)

		// 발생 전표 토큰(멱등 키) / 発生伝票トークン（冪等キー）
		final String token = "PAYVCH-" + yyyymm + "-" + payType + "#ACCRUAL";

		// 1) 요청 시 기존 발생 전표 삭제(헤더/라인)  
		// 1) リクエスト指定時に既存の発生伝票を削除（ヘッダー／ライン）
        if (Boolean.TRUE.equals(req.getCleanupExisting())) {
            mapper.deleteVoucherLinesByToken(token);   // 라인 삭제 / ライン削除
            mapper.deleteVoucherHeaderByToken(token);  // 헤더 삭제 / ヘッダー削除
        }

		// 2) 미리보기 라인 조회 (차변→대변 기본 정렬)  
		// 2) プレビューライン取得（借方→貸方の基本ソート）
		return mapper.selectVoucherPreviewAccrual(
			yyyymm, payType, wageAcct, withholdAcct, accruedAcct, req.getEmpNos()
		);
	}

	/** 문자열 정규화: null/빈문자 처리 / 文字列正規化：null/空文字の処理 */
	private String norm(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
}
