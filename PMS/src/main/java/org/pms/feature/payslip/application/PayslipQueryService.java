package org.pms.feature.payslip.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.payslip.domain.*;
import org.pms.feature.payslip.infra.PayslipMapper;

@Service
@RequiredArgsConstructor
public class PayslipQueryService {

    private final PayslipMapper mapper;

    /** 사번+연월로 단건 상세 */
    @Transactional(readOnly = true)
    public PayslipSummary getPayslip(String empNo, String periodYm) {
        PayslipSummary summary = mapper.selectPayslipSummary(empNo, periodYm);
        if (summary == null) return null;

        List<PayItem> payItems = mapper.selectPayItems(summary.getPayslipId());
        List<DeductionItem> dedItems = mapper.selectDeductionItems(summary.getPayslipId());
        summary.setPayItems(payItems);
        summary.setDeductionItems(dedItems);
        return summary;
    }

    /** payslipId로 단건 상세 (좌측 목록에서 행 선택 시 사용) */
    @Transactional(readOnly = true)
    public PayslipSummary getPayslipById(Long payslipId) {
        if (payslipId == null) return null;

        PayslipSummary summary = mapper.selectPayslipSummaryById(payslipId);
        if (summary == null) return null;

        List<PayItem> payItems = mapper.selectPayItems(payslipId);
        List<DeductionItem> dedItems = mapper.selectDeductionItems(payslipId);
        summary.setPayItems(payItems);
        summary.setDeductionItems(dedItems);
        return summary;
    }
}

