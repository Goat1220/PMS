package org.pms.feature.runpayroll.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.runpayroll.domain.*;
import org.pms.feature.runpayroll.mapper.RunPayrollMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RunPayrollService {

    private final RunPayrollMapper mapper;

    // 회사 계정/원가 규칙에 맞게 교체하세요
    // (전표라인 생성시 사용되는 계정ID 예시)
    private static final long WAGE_ACCOUNT_ID = 1001L;     // 급여 비용 계정
    private static final long WITHHOLD_ACCOUNT_ID = 2101L; // 원천징수/예수금 계정

    // ===================== 조회 =====================

    public List<PayrollSummaryRow> getSummary(String yyyymm, String payType, String deptCode, String empNo){
        return mapper.selectPayrollSummary(
                blankToNull(yyyymm),
                blankToNull(payType),
                blankToNull(deptCode),
                blankToNull(empNo)
        );
    }

    public List<EarningItemRow> getItems(String empNo, String yyyymm, String payType){
        return mapper.selectEarningItems(
                blankToNull(empNo),
                blankToNull(yyyymm),
                blankToNull(payType)
        );
    }

    public List<DeductionRow> getDeductions(String empNo, String yyyymm, String payType){
        return mapper.selectDeductions(
                blankToNull(empNo),
                blankToNull(yyyymm),
                blankToNull(payType)
        );
    }

    // ===================== 배치 처리 =====================

    /**
     * 급상여 처리
     * - (선행) 요약행 플래그 저장
     * - 지급합계 - 공제합계 = 실지급액(payslip.net_pay_amt) 반영
     * - 전표 생성(요약토큰 중복 시 경고)
     * - 처리 후 월집계(pay_month_summary) 재산출/업서트
     */
    @Transactional
    public SimpleResult processPayroll(BatchEmpRequest req){
        final String yyyymm = blankToNull(req.getYyyymm());
        final String payType = blankToNull(req.getPayType());
        final List<String> empNos = req.getEmpNos() != null ? req.getEmpNos() : Collections.<String>emptyList();

        if(yyyymm == null) return SimpleResult.fail("연월(yyyymm)이 없습니다.");
        if(payType == null) return SimpleResult.fail("급여유형이 없습니다.");
        if(empNos.isEmpty()) return SimpleResult.fail("처리할 사원이 없습니다.");

        // 확정 잠금 체크
        int locked = mapper.countConfirmedPayslips(yyyymm, payType, empNos);
        if(locked > 0){
            return SimpleResult.fail("확정된 전표가 있어 처리할 수 없습니다.");
        }

        // 플래그 저장 (emp_tax_profile에 upsert)
        if(req.getFlags()!=null && !req.getFlags().isEmpty()){
            for(EmpFlag f : req.getFlags()){
                if(f == null || blankToNull(f.getEmpNo()) == null) continue;
                Long empId = mapper.findEmpIdByEmpNo(f.getEmpNo());
                if(empId == null) continue;
                mapper.upsertPayslipFlags(empId, yyyymm, payType, f);
            }
        }

        List<String> warnings = new ArrayList<String>();
        int affected = 0;

        for(String empNo : empNos){
            if(blankToNull(empNo) == null) continue;

            Long empId = mapper.findEmpIdByEmpNo(empNo);
            if(empId==null) {
                warnings.add("사번 " + empNo + " : 대상 사원을 찾을 수 없습니다.");
                continue;
            }

            // payslip 확보/ID 조회
            mapper.ensurePayslipExists(empId, yyyymm, payType); // 없으면 생성
            Long payslipId = mapper.ensurePayslip(empId, yyyymm, payType);

            // 지급/공제 합계
            Long payTot = mapper.sumEarningItems(payslipId);
            Long dedTot = mapper.sumDeductionItems(payslipId);
            long net = (payTot==null?0L:payTot) - (dedTot==null?0L:dedTot);

            // 실지급액 반영
            mapper.updatePayslipNet(payslipId, net);

            // 전표 처리: 요약 토큰으로 중복 방지
            String token = voucherToken(empId, yyyymm, payType);
            Integer has = mapper.hasVoucherByToken(token);
            if(has != null && has > 0){
                warnings.add("사번 " + empNo + " : 이미 전표처리 데이터가 있습니다.");
            }else{
                mapper.insertVoucherHeaderByToken(yyyymm, token);
                Long voucherId = mapper.findVoucherIdByToken(token);
                // 회사 규칙에 맞는 계정/귀속부서 등 매핑
                mapper.upsertVoucherLinesForPayslip(payslipId, voucherId, WAGE_ACCOUNT_ID, WITHHOLD_ACCOUNT_ID);
            }

            affected++;
        }

        // 월집계 재산출 (월/유형 기준 전체 합계)
        rollupMonthlySummary(yyyymm, payType);

        String msg = "급상여 처리가 완료되었습니다.";
        if(!warnings.isEmpty()){
            msg += " (경고: " + joinWarnings(warnings) + ")";
        }
        return new SimpleResult(true, affected, msg);
    }

    /**
     * 세금 재처리
     * - 지급총액 기준으로 소득세/지방세 예시 재계산 후 공제항목 갱신
     * - 처리 후 월집계 재산출
     */
    @Transactional
    public SimpleResult recalcTaxes(BatchEmpRequest req){
        final String yyyymm = blankToNull(req.getYyyymm());
        final String payType = blankToNull(req.getPayType());
        final List<String> empNos = req.getEmpNos() != null ? req.getEmpNos() : Collections.<String>emptyList();

        if(yyyymm == null) return SimpleResult.fail("연월(yyyymm)이 없습니다.");
        if(payType == null) return SimpleResult.fail("급여유형이 없습니다.");
        if(empNos.isEmpty()) return SimpleResult.fail("처리할 사원이 없습니다.");

        int affected = 0;

        for(String empNo : empNos){
            if(blankToNull(empNo) == null) continue;

            Long empId = mapper.findEmpIdByEmpNo(empNo);
            if(empId==null) continue;

            mapper.ensurePayslipExists(empId, yyyymm, payType);
            Long payslipId = mapper.ensurePayslip(empId, yyyymm, payType);

            long earningTot = 0L;
            Long tmp = mapper.sumEarningItems(payslipId);
            if(tmp != null) earningTot = tmp;

            // TODO: 실제 세법 로직으로 교체 (근로/퇴직/사업 분기, 4대보험 등)
            long incomeTax = Math.round(earningTot * 0.033);  // 예: 3.3%
            long localTax  = Math.round(incomeTax * 0.1);     // 예: 지방세 10%

            mapper.deleteTaxDeductions(payslipId);
            mapper.insertTaxDeduction(payslipId, "TAX_INCOME", "소득세", incomeTax);
            mapper.insertTaxDeduction(payslipId, "TAX_LOCAL",  "지방소득세", localTax);

            affected++;
        }

        // 월집계 재산출
        rollupMonthlySummary(yyyymm, payType);

        return new SimpleResult(true, affected, "세금 재처리가 완료되었습니다.");
    }

    /**
     * 세금 반영(YRT)
     * - 최신 연말정산 결과(환급/추징) 반영
     * - 처리 후 월집계 재산출
     */
    @Transactional
    public SimpleResult applyYrt(BatchEmpRequest req){
        final String yyyymm = blankToNull(req.getYyyymm());
        final String payType = blankToNull(req.getPayType());
        final List<String> empNos = req.getEmpNos() != null ? req.getEmpNos() : Collections.<String>emptyList();

        if(yyyymm == null) return SimpleResult.fail("연월(yyyymm)이 없습니다.");
        if(payType == null) return SimpleResult.fail("급여유형이 없습니다.");
        if(empNos.isEmpty()) return SimpleResult.fail("처리할 사원이 없습니다.");

        final int baseYear = Integer.parseInt(yyyymm.substring(0,4));
        int affected = 0;

        for(String empNo : empNos){
            if(blankToNull(empNo) == null) continue;

            Long empId = mapper.findEmpIdByEmpNo(empNo);
            if(empId==null) continue;

            mapper.ensurePayslipExists(empId, yyyymm, payType);
            Long payslipId = mapper.ensurePayslip(empId, yyyymm, payType);

            YrtAdjustment adj = mapper.findLatestYrtAdjustment(empId, baseYear);
            if(adj == null) continue;

            // TODO: 실제 환급/추징 금액 산정은 yrt_result_total 등으로 확장 권장
            if(adj.getRefundAmt()!=null && adj.getRefundAmt() > 0){
                mapper.insertEarningItem(payslipId, "연말정산 환급", adj.getRefundAmt());
            }
            if(adj.getAdditionalTaxAmt()!=null && adj.getAdditionalTaxAmt() > 0){
                mapper.insertDeductionItem(payslipId, "YRT_ADD_TAX", "연말정산 추징", adj.getAdditionalTaxAmt());
            }

            affected++;
        }

        // 월집계 재산출
        rollupMonthlySummary(yyyymm, payType);

        return new SimpleResult(true, affected, "세금 반영(YRT)이 완료되었습니다.");
    }

    /**
     * 확정
     * - 확정 상태로 전환 → 이후 급상여 처리 차단
     */
    @Transactional
    public SimpleResult confirm(BatchEmpRequest req){
        final String yyyymm = blankToNull(req.getYyyymm());
        final String payType = blankToNull(req.getPayType());
        final List<String> empNos = req.getEmpNos() != null ? req.getEmpNos() : Collections.<String>emptyList();

        if(yyyymm == null) return SimpleResult.fail("연월(yyyymm)이 없습니다.");
        if(payType == null) return SimpleResult.fail("급여유형이 없습니다.");
        if(empNos.isEmpty()) return SimpleResult.fail("처리할 사원이 없습니다.");

        int n = mapper.confirmPayslips(yyyymm, payType, empNos);
        return new SimpleResult(true, n, "확정 완료 (" + n + "건)");
    }

    // ===================== 내부 유틸 =====================

    private void rollupMonthlySummary(String yyyymm, String payType){
        // 월/유형 전체 합계 계산
        Map<String,Object> roll = mapper.calcMonthRollup(yyyymm, payType);
        long totalPayAmt = num(roll.get("TOTAL_PAY_AMT"));
        long prevPaidAmt = num(roll.get("PREV_PAID_AMT"));
        long totalDedAmt = num(roll.get("TOTAL_DED_AMT"));
        long netPayAmt   = num(roll.get("NET_PAY_AMT"));

        mapper.upsertMonthlySummaryMonth(
                payType, yyyymm,
                totalPayAmt, prevPaidAmt, totalDedAmt, netPayAmt
        );
    }

    private long num(Object o){
        if(o == null) return 0L;
        if(o instanceof Number) return ((Number)o).longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception e){ return 0L; }
    }

    private String voucherToken(Long empId, String yyyymm, String payType){
        return "PSLIP-EMP-" + empId + "-" + yyyymm + "-" + payType;
    }

    private String blankToNull(String s){
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private String joinWarnings(List<String> warnings){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<warnings.size();i++){
            if(i>0) sb.append(" / ");
            sb.append(warnings.get(i));
        }
        return sb.toString();
    }
}
