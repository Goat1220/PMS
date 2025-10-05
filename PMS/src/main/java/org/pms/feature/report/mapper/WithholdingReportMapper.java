package org.pms.feature.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingSearch;

import java.math.BigDecimal;
import java.util.List;

/**
 * [MyBatis Mapper 인터페이스]
 * - XML의 namespace와 이 인터페이스의 FQCN이 일치해야 합니다.
 */
@Mapper
public interface WithholdingReportMapper {

    /** 요약 데이터 조회 */
    List<WithholdingRow> selectSummary(WithholdingSearch cond);

    /** 부표 데이터 조회 */
    List<AnnexRow> selectAnnex(WithholdingSearch cond);

    // ===========================
    // ★ 추가: 상단 합계용 쿼리 2개
    // ===========================

    /** 총 지급액 = 전표라인(D) 차변 합계 (pay_voucher/pay_voucher_line 기준) */
    BigDecimal sumTotalPaymentByMonth(@Param("applyYyyymm") String applyYyyymm);

    /** 징수 소득세 = 확정 급여의 공제(T% 코드) 합계 (payslip/payslip_deduction 기준) */
    BigDecimal sumWithheldTaxByMonthByPrefix(@Param("applyYyyymm") String applyYyyymm);

    /** (옵션) 코드 목록으로 세금 합계 */
    BigDecimal sumWithheldTaxByMonthByCodes(@Param("applyYyyymm") String applyYyyymm,
                                            @Param("taxCodes") List<String> taxCodes);
}
