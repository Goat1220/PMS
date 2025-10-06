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

@Service
@RequiredArgsConstructor
public class PayrollVoucherService {

	private final PayrollVoucherMapper mapper;

	private static final Pattern YYYYMM = Pattern.compile("^\\d{4}-\\d{2}$"); // e.g. 2025-08

	@Transactional(readOnly = true)
	public List<VoucherLedgerRow> getVoucherLedger(String yyyymm, String payType) {
		if (yyyymm == null || !YYYYMM.matcher(yyyymm).matches()) {
			throw new IllegalArgumentException("yyyymm 형식은 'YYYY-MM'이어야 합니다. (예: 2025-08)");
		}
		// payType은 선택값이므로 별도 검증 없이 전달
		return mapper.selectVoucherLedger(yyyymm, payType);
	}

	/** 실반영: 헤더 없으면 생성, 라인 재작성 */
	@Transactional
	public SimpleResult processVoucher(VoucherProcessRequest req) {
		final String yyyymm = norm(req.getYyyymm());
		final String payType = norm(req.getPayType());
		if (yyyymm == null)
			return SimpleResult.fail("지급연월(yyyymm)이 없습니다.");
		if (payType == null)
			return SimpleResult.fail("급여유형(payType)이 없습니다.");

		final long wageAcct = req.getWageAccountId() != null ? req.getWageAccountId().longValue() : 1001L; // 급여비용
		final long withholdAcct = req.getWithholdAccountId() != null ? req.getWithholdAccountId().longValue() : 2101L; // 예수금
		final long accruedAcct = req.getAccruedAccountId() != null ? req.getAccruedAccountId().longValue() : 221100L; // 미지급급여
																														// ★
		final long bankAcct = req.getBankAccountId() != null ? req.getBankAccountId().longValue() : 111100L; // 보통예금 ★
																												// // ★

		// 기본 토큰(요약)은 재사용하되, 전표 유형별 suffix 부여
		String base = (req.getSummaryNote() != null && !req.getSummaryNote().trim().isEmpty())
				? req.getSummaryNote().trim()
				: ("PAYVCH-" + yyyymm + "-" + payType);

		int affected = 0;

		// ── ① 발생 전표(ACCRUAL) ─────────────────────────────────────────
		String accrualToken = base.endsWith("#ACCRUAL") ? base : (base + "#ACCRUAL");
		mapper.insertVoucherIfAbsent(yyyymm, accrualToken);
		Long accrualId = mapper.findVoucherIdByToken(accrualToken);
		if (accrualId == null)
			return SimpleResult.fail("발생 전표 헤더 생성/조회 실패");

		mapper.deleteVoucherLines(accrualId);
		int dRows = mapper.insertDebitLinesForMonth(accrualId, yyyymm, payType, wageAcct); // D: 급여비용(지급총액)
		int cRows = mapper.insertCreditLinesForMonth(accrualId, yyyymm, payType, withholdAcct); // C: 예수금(공제총액)
		int aRows = mapper.insertAccruedLinesForMonth(accrualId, yyyymm, payType, accruedAcct); // C: 미지급급여(실지급) ★
		affected += dRows + cRows + aRows;

		// ── ② 지급 전표(PAYMENT) ─────────────────────────────────────────
		/*
		 * String payToken = base.endsWith("#PAYMENT") ? base : (base + "#PAYMENT");
		 * mapper.insertVoucherIfAbsent(yyyymm, payToken); Long payId =
		 * mapper.findVoucherIdByToken(payToken); if (payId == null) return
		 * SimpleResult.fail("지급 전표 헤더 생성/조회 실패");
		 * 
		 * mapper.deleteVoucherLines(payId); int ad =
		 * mapper.insertPaymentAccruedDebitForMonth(payId, yyyymm, payType,
		 * accruedAcct); // D: 미지급급여(=net) int bc =
		 * mapper.insertPaymentBankCreditForMonth(payId, yyyymm, payType, bankAcct); //
		 * C: 보통예금(=net) affected += ad + bc;
		 */

		return SimpleResult.ok(affected);
	}

	@Transactional 
	public List<VoucherLedgerRow> previewVoucherAccrual(VoucherProcessRequest req) {
		final String yyyymm = norm(req.getYyyymm());
		final String payType = norm(req.getPayType());
		if (yyyymm == null)
			return Collections.emptyList();
		if (payType == null)
			return Collections.emptyList();

		final long wageAcct = req.getWageAccountId() != null ? req.getWageAccountId() : 1001L; // 급여비용(D)
		final long withholdAcct = req.getWithholdAccountId() != null ? req.getWithholdAccountId() : 2101L; // 예수금(C)
		final long accruedAcct = req.getAccruedAccountId() != null ? req.getAccruedAccountId() : 221100L; // 미지급급여(C)

		// 발생 전표 토큰(멱등 키)
		final String token = "PAYVCH-" + yyyymm + "-" + payType + "#ACCRUAL";

		// 1) 발생 전표 삭제
		 // 1) 요청 시 기존 발생 전표 삭제
        if (Boolean.TRUE.equals(req.getCleanupExisting())) {
            mapper.deleteVoucherLinesByToken(token);   // 라인 → 
            mapper.deleteVoucherHeaderByToken(token);  // 헤더 순으로 삭제
        }

		// 2) 미리보기 라인 SELECT (차변→대변 최상위 정렬, 대변 내부: 예수금 → 미지급)
		return mapper.selectVoucherPreviewAccrual(yyyymm, payType, wageAcct, withholdAcct, accruedAcct,
				req.getEmpNos());
	}

	private String norm(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}
}
