package org.pms.feature.runpayroll.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pms.feature.runpayroll.domain.BatchEmpRequest;
import org.pms.feature.runpayroll.domain.DeductionRow;
import org.pms.feature.runpayroll.domain.EarningItemRow;
import org.pms.feature.runpayroll.domain.EmpFlag;
import org.pms.feature.runpayroll.domain.PayrollSummaryRow;
import org.pms.feature.runpayroll.domain.SimpleResult;
import org.pms.feature.runpayroll.domain.YrtAdjustment;
import org.pms.feature.runpayroll.mapper.RunPayrollMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunPayrollService {

	private final RunPayrollMapper mapper;

	private static final long WAGE_ACCOUNT_ID = 1001L; // 급여 비용 계정 / 給与費用勘定
	private static final long WITHHOLD_ACCOUNT_ID = 2101L; // 원천징수/예수금 계정 / 源泉徴収/預り金勘定

	// ===================== 조회 / 照会 =====================

	public List<PayrollSummaryRow> getSummary(String yyyymm, String payType, String deptCode, String empNo) {
		return mapper.selectPayrollSummary(blankToNull(yyyymm), blankToNull(payType), blankToNull(deptCode),
				blankToNull(empNo));
	}

	public List<EarningItemRow> getItems(String empNo, String yyyymm, String payType) {
		return mapper.selectEarningItems(blankToNull(empNo), blankToNull(yyyymm), blankToNull(payType));
	}

	public List<DeductionRow> getDeductions(String empNo, String yyyymm, String payType) {
		return mapper.selectDeductions(blankToNull(empNo), blankToNull(yyyymm), blankToNull(payType));
	}

	// ===================== 배치 처리 / バッチ処理 =====================
	/**
	 * 급상여 처리
	 *  - (선행) 요약행 플래그 저장(emp_tax_profile upsert, 퇴직여부 반영)
	 *  - 지급합계 - 공제합계 = 실지급액(payslip.net_pay_amt) 반영
	 *  - 전표 생성(요약토큰 중복 시 경고)
	 *  - 루프 종료 후 월집계(pay_month_summary) 재산출/업서트
	 *
	 * 給与処理
	 *  - （先行）サマリー行のフラグ保存（emp_tax_profileのUPSERT、退職有無の反映）
	 *  - 支給合計－控除合計＝実支給額（payslip.net_pay_amt）を反映
	 *  - 伝票作成（サマリートークン重複時は警告）
	 *  - ループ終了後、月次集計（pay_month_summary）を再計算/UPSERT
	 */
	@Transactional
	public SimpleResult processPayroll(BatchEmpRequest req) {
		final String yyyymm = blankToNull(req.getYyyymm());
		final String payType = blankToNull(req.getPayType());
		final List<String> empNos = (req.getEmpNos() != null) ? req.getEmpNos() : Collections.<String>emptyList();

		if (yyyymm == null)
			return SimpleResult.fail("年月(yyyymm)が入力されていません。");
		if (payType == null)
			return SimpleResult.fail("給与区分が指定されていません。");
		if (empNos.isEmpty())
			return SimpleResult.fail("対象社員が存在しません。");

		// 1) 확정 잠금 체크 / 確定ロックの確認
		int locked = mapper.countConfirmedPayslips(yyyymm, payType, empNos);
		if (locked > 0) {
			return SimpleResult.fail("確定済みの伝票があるため処理できません。");
		}

		// 2) flags를 empNo로 매핑 (선택 사번에만 정확 적용)
		//    flagsをempNoにマッピング（選択された社員番号にのみ適用）
		final Map<String, EmpFlag> flagMap = new HashMap<>();
		if (req.getFlags() != null) {
			for (EmpFlag f : req.getFlags()) {
				if (f == null)
					continue;
				String no = blankToNull(f.getEmpNo());
				if (no != null)
					flagMap.put(no, f);
			}
		}

		// 3) 사번별 처리 / 社員番号ごとの処理
		final List<String> warnings = new ArrayList<>();
		int affected = 0;

		for (String empNo : empNos) {
			if (blankToNull(empNo) == null)
				continue;

			Long empId = mapper.findEmpIdByEmpNo(empNo);
			if (empId == null) {
				// 경고 목록에만 추가되는 사용자 메시지 / ユーザ向け警告メッセージ（リストに追加）
				warnings.add("社員番号 " + empNo + "：対象社員が見つかりません。");
				continue;
			}

			// payslip 확보(없으면 생성) 및 id 조회
			// payslipを確保（なければ作成）しID取得
			mapper.ensurePayslipExists(empId, yyyymm, payType);
			Long payslipId = mapper.ensurePayslip(empId, yyyymm, payType);

			// (선행) 플래그 저장 + 퇴직여부 반영
			// （先行）フラグ保存＋退職有無の反映
			EmpFlag f = flagMap.get(empNo);

			if (f != null) {
				// emp_tax_profile 업서트 (XML은 NVL 패턴 권장)
				// emp_tax_profileのUPSERT（XMLはNVLパターン推奨）
				mapper.upsertPayslipFlags(empId, yyyymm, payType, f);

				// 퇴직여부가 전달된 경우, 해당 월말/NULL로 갱신
				// 退職有無が渡された場合、当月末/NULLで更新
				if (f.getRetiredYn() != null && !f.getRetiredYn().isEmpty()) {
					mapper.updateEmpRetiredYn(empId, yyyymm, f.getRetiredYn()); // 'Y'|'N'
				}
			}

			// 지급/공제 합계 → 실지급액 반영
			// 支給/控除合計 → 実支給額を反映
			Long payTot = mapper.sumEarningItems(payslipId);
			Long dedTot = mapper.sumDeductionItems(payslipId);
			long net = nvl(payTot) - nvl(dedTot);
			mapper.updatePayslipNet(payslipId, net);

			/*
			 * // 전표 생성: 토큰 중복 방지 (idempotent)
			 * // 伝票作成：トークン重複の防止（冪等性）
			 * String token = voucherToken(empId, yyyymm, payType);
			 * Integer has = mapper.hasVoucherByToken(token);
			 * if (has != null && has > 0) {
			 *   warnings.add("사번 " + empNo + " : 이미 전표처리 데이터가 있습니다.");
			 *   // 社員番号 " + empNo + " ：既に伝票処理データがあります。
			 * } else {
			 *   mapper.insertVoucherHeaderByToken(yyyymm, token);
			 *   Long voucherId = mapper.findVoucherIdByToken(token);
			 *   mapper.upsertVoucherLinesForPayslip(
			 *     payslipId, voucherId, WAGE_ACCOUNT_ID, WITHHOLD_ACCOUNT_ID
			 *   );
			 * }
			 */

			affected++;
		}

		// 4) 루프 이후 월 집계 재산출/업서트 (해당 yyyymm, payType 전체)
		// 4) ループ後、月次集計を再計算/アップサート（該当yyyymm, payType全体）
		mapper.upsertMonthSummary(yyyymm, payType);

		// 5) 결과 메시지
		// 5) 結果メッセージ
		String msg = "給与処理が完了しました。";
		if (!warnings.isEmpty()) {
			msg += " 注意: " + String.join(" / ", warnings);
		}
		return new SimpleResult(true, affected, msg);
	}

	/**
	 * 세금 재처리
	 *  - 지급총액 기준으로 소득세/지방세 예시 재계산 후 공제항목 갱신
	 *  - 처리 후 월집계 재산출
	 *
	 * 税金の再処理
	 *  - 支給総額に基づく所得税/地方税の試算→控除項目更新
	 *  - 処理後に月次集計を再計算
	 */
	@Transactional
	public SimpleResult recalcTaxes(BatchEmpRequest req) {
		final String yyyymm = blankToNull(req.getYyyymm());
		final String payType = blankToNull(req.getPayType());
		final List<String> empNos = req.getEmpNos() != null ? req.getEmpNos() : Collections.<String>emptyList();

		if (yyyymm == null)
			return SimpleResult.fail("年月(yyyymm)が入力されていません。");
		if (payType == null)
			return SimpleResult.fail("給与区分が指定されていません。");
		if (empNos.isEmpty())
			return SimpleResult.fail("対象社員が存在しません。");

		int affected = 0;

		for (String empNo : empNos) {
			if (blankToNull(empNo) == null)
				continue;

			Long empId = mapper.findEmpIdByEmpNo(empNo);
			if (empId == null)
				continue;

			mapper.ensurePayslipExists(empId, yyyymm, payType);
			Long payslipId = mapper.ensurePayslip(empId, yyyymm, payType);

			long earningTot = 0L;
			Long tmp = mapper.sumEarningItems(payslipId);
			if (tmp != null)
				earningTot = tmp;

			// TODO: 실제 세법 로직으로 교체 (근로/퇴직/사업 분기, 4대보험 등)
			// TODO: 実際の税法ロジックへ置換（給与/退職/事業の分岐、社会保険等）
			long incomeTax = Math.round(earningTot * 0.033); // 예: 3.3% / 例: 3.3%
			long localTax = Math.round(incomeTax * 0.1); // 예: 지방세 10% / 例: 地方税 10%

			mapper.deleteTaxDeductions(payslipId);
			mapper.insertTaxDeduction(payslipId, "TAX_INCOME", "所得税", incomeTax);
			mapper.insertTaxDeduction(payslipId, "TAX_LOCAL", "地方所得税", localTax);

			affected++;
		}

		// 월집계 재산출 / 月次集計の再計算
		rollupMonthlySummary(yyyymm, payType);

		return new SimpleResult(true, affected, "税金の再処理が完了しました。");
	}

	/**
	 * 세금 반영(YRT)
	 *  - 최신 연말정산 결과(환급/추징) 반영
	 *  - 처리 후 월집계 재산출
	 *
	 * 税金反映（YRT）
	 *  - 最新の年末調整結果（還付/追徴）を反映
	 *  - 処理後に月次集計を再計算
	 */
	@Transactional
	public SimpleResult applyYrt(BatchEmpRequest req) {
		final String yyyymm = blankToNull(req.getYyyymm());
		final String payType = blankToNull(req.getPayType());
		final List<String> empNos = req.getEmpNos() != null ? req.getEmpNos() : Collections.<String>emptyList();

		if (yyyymm == null)
			return SimpleResult.fail("年月(yyyymm)が入力されていません。");
		if (payType == null)
			return SimpleResult.fail("給与区分が指定されていません。");
		if (empNos.isEmpty())
			return SimpleResult.fail("対象社員が存在しません。");

		final int baseYear = Integer.parseInt(yyyymm.substring(0, 4));
		int affected = 0;

		for (String empNo : empNos) {
			if (blankToNull(empNo) == null)
				continue;

			Long empId = mapper.findEmpIdByEmpNo(empNo);
			if (empId == null)
				continue;

			mapper.ensurePayslipExists(empId, yyyymm, payType);
			Long payslipId = mapper.ensurePayslip(empId, yyyymm, payType);

			YrtAdjustment adj = mapper.findLatestYrtAdjustment(empId, baseYear);
			if (adj == null)
				continue;

			// TODO: 실제 환급/추징 금액 산정은 yrt_result_total 등으로 확장 권장
			// TODO: 実額算定は yrt_result_total 等への拡張を推奨
			if (adj.getRefundAmt() != null && adj.getRefundAmt() > 0) {
				mapper.insertEarningItem(payslipId, "年末調整還付", adj.getRefundAmt());
			}
			if (adj.getAdditionalTaxAmt() != null && adj.getAdditionalTaxAmt() > 0) {
				mapper.insertDeductionItem(payslipId, "YRT_ADD_TAX", "年末調整追徴", adj.getAdditionalTaxAmt());
			}

			affected++;
		}

		// 월집계 재산출 / 月次集計の再計算
		rollupMonthlySummary(yyyymm, payType);

		return new SimpleResult(true, affected, "年末調整(YRT)の反映が完了しました。");
	}

	/**
	 * 확정 - 확정 상태로 전환 → 이후 급상여 처리 차단
	 * 確定 - 確定状態へ変更 → 以後の給与処理をブロック
	 */
	@Transactional
	public SimpleResult confirm(BatchEmpRequest req) {
		final String yyyymm = blankToNull(req.getYyyymm());
		final String payType = blankToNull(req.getPayType());
		final List<String> empNos = req.getEmpNos() != null ? req.getEmpNos() : Collections.<String>emptyList();

		if (yyyymm == null)
			return SimpleResult.fail("年月(yyyymm)が入力されていません。");
		if (payType == null)
			return SimpleResult.fail("給与区分が指定されていません。");
		if (empNos.isEmpty())
			return SimpleResult.fail("対象社員が存在しません。");

		int n = mapper.confirmPayslips(yyyymm, payType, empNos);
		return new SimpleResult(true, n, "確定が完了しました（" + n + "件）。");
	}

	// ===================== 내부 유틸 / 内部ユーティリティ =====================

	private void rollupMonthlySummary(String yyyymm, String payType) {
		// 월/유형 전체 합계 계산 / 月・区分の全体合計を計算
		Map<String, Object> roll = mapper.calcMonthRollup(yyyymm, payType);
		long totalPayAmt = num(roll.get("TOTAL_PAY_AMT"));
		long prevPaidAmt = num(roll.get("PREV_PAID_AMT"));
		long totalDedAmt = num(roll.get("TOTAL_DED_AMT"));
		long netPayAmt = num(roll.get("NET_PAY_AMT"));

		mapper.upsertMonthlySummaryMonth(payType, yyyymm, totalPayAmt, prevPaidAmt, totalDedAmt, netPayAmt);
	}

	private long num(Object o) {
		if (o == null)
			return 0L;
		if (o instanceof Number)
			return ((Number) o).longValue();
		try {
			return Long.parseLong(String.valueOf(o));
		} catch (Exception e) {
			return 0L;
		}
	}

	private String voucherToken(Long empId, String yyyymm, String payType) {
		return "PSLIP-EMP-" + empId + "-" + yyyymm + "-" + payType;
	}

	private static long nvl(Long v) {
		return (v == null) ? 0L : v;
	}

	private String blankToNull(String s) {
		return (s == null || s.trim().isEmpty()) ? null : s.trim();
	}

	private String joinWarnings(List<String> warnings) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < warnings.size(); i++) {
			if (i > 0)
				sb.append(" / ");
			sb.append(warnings.get(i));
		}
		return sb.toString();
	}

	@Transactional
	public SimpleResult unconfirmPayslips(String yyyymm, String payType, List<String> empNos) {
		// 대상 사원이 없습니다. / 対象社員が存在しません。
		if (empNos == null || empNos.isEmpty()) {
			return SimpleResult.fail("対象社員が存在しません。");
		}
		int cnt = mapper.unconfirmPayslips(yyyymm, payType, empNos);
		// 원본 유지: 메시지 없이 카운트만 반환 / 原型を維持：メッセージなしで件数のみ返却
		return SimpleResult.ok(cnt);
	}
}
