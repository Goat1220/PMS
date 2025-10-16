package org.pms.feature.report.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.WithholdingSearch;

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
    // ★ 상단 합계용 쿼리
    // ===========================

    /**  월 기준 인원수(고유 지급대상자 수) 카운트  */
    Integer countHeadsByMonth(@Param("applyYyyymm") String applyYyyymm);
    
    /** 총 지급액 = 전표처리 차변  */
    BigDecimal sumVoucherDebitByMonth(@Param("applyYyyymm") String applyYyyymm);

    /** 징수 소득세 = 확정 급여의 공제(T% 코드) 합계 (payslip/payslip_deduction 기준) */
    BigDecimal sumWithheldTaxByMonthByPrefix(@Param("applyYyyymm") String applyYyyymm);

    /** (옵션) 코드 목록으로 세금 합계 */
    BigDecimal sumWithheldTaxByMonthByCodes(@Param("applyYyyymm") String applyYyyymm,
                                            @Param("taxCodes") List<String> taxCodes);
    // ===========================
    // 전월 미환급세액 조회 /저장 
    // ===========================
    BigDecimal selectPrevJ(@Param("yyyymm") String yyyymm);
    BigDecimal selectPrevK(@Param("yyyymm") String yyyymm);

    int upsertRefund(@Param("yyyymm") String yyyymm,
                     @Param("j") BigDecimal jValue,
                     @Param("k") BigDecimal kValue);

}
