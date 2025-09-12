package org.pms.feature.payslip.infra;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.payslip.domain.*;

// XML namespace: org.pms.feature.payslip.infra.PayslipMapper
public interface PayslipMapper {

    // === 개인 상세 조회용 ===
    // XML: selectPayslipSummary에서 #{empNo}, #{ym}를 사용하므로 @Param 이름을 empNo, ym으로 맞춰야 함
    PayslipSummary selectPayslipSummary(@Param("empNo") String empNo,
                                        @Param("ym")    String periodYm);

    // XML: selectPayItems에서 #{payslipId}
    List<PayItem> selectPayItems(@Param("payslipId") Long payslipId);

    // XML: selectDeductionItems에서 #{payslipId}
    List<DeductionItem> selectDeductionItems(@Param("payslipId") Long payslipId);

    // === 목록/조회 조건 화면(inquiry)에서 쓰는 것들 (이미 사용 중이면 유지) ===
    List<PayslipListRow> selectPayslipList(@Param("empNo")       String empNo,
                                           @Param("fromYm")      String fromYm,
                                           @Param("toYm")        String toYm,
                                           @Param("payType")     String payType,
                                           @Param("excludeZero") String excludeZero);

    PayslipSummary selectPayslipSummaryById(@Param("payslipId") Long payslipId);

    List<PayTypeCode> selectPayTypeCodes();
}
