package org.pms.feature.payslip.application;

import lombok.RequiredArgsConstructor;
import org.pms.feature.payslip.domain.PayslipListRow;
import org.pms.feature.payslip.domain.PayTypeCode;
import org.pms.feature.payslip.infra.PayslipMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayslipInquiryService {
    private final PayslipMapper mapper;

    public List<PayslipListRow> search(String empNo, String fromYm, String toYm,
                                       String payType, String excludeZero) {
        return mapper.selectPayslipList(empNo, fromYm, toYm, payType, excludeZero);
    }

    public List<PayTypeCode> getPayTypeCodes() {
        return mapper.selectPayTypeCodes();
    }
}
