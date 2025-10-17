package org.pms.feature.report.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.WithholdingSearch;

/**
 * MyBatis Mapper  
 * SQL 실행용 인터페이스 / SQLを実行するためのインターフェース
 */
@Mapper
public interface WithholdingReportMapper {

    /** 요약 조회 / 概要データ取得 */
    List<WithholdingRow> selectSummary(WithholdingSearch cond);

    /** 부표 조회 / 付表データ取得 */
    List<AnnexRow> selectAnnex(WithholdingSearch cond);

    // ===========================
    // 상단 합계용 / 合計関連
    // ===========================

    /** 인원수 / 人数 */
    Integer countHeadsByMonth(@Param("applyYyyymm") String applyYyyymm);
    
    /** 총지급액 / 総支給額 */
    BigDecimal sumVoucherDebitByMonth(@Param("applyYyyymm") String applyYyyymm);

    /** 징수소득세 / 源泉所得税 */
    BigDecimal sumWithheldTaxByMonthByPrefix(@Param("applyYyyymm") String applyYyyymm);

    /** 세금코드별 합계 / 税コード別合計 */
    BigDecimal sumWithheldTaxByMonthByCodes(@Param("applyYyyymm") String applyYyyymm,
                                            @Param("taxCodes") List<String> taxCodes);

    // ===========================
    // 전월 미환급 / 前月未還付
    // ===========================

    /** 전월 J값 / 前月J値 */
    BigDecimal selectPrevJ(@Param("yyyymm") String yyyymm);

    /** 전월 K값 / 前月K値 */
    BigDecimal selectPrevK(@Param("yyyymm") String yyyymm);

    /** 미환급세액 저장/갱신 / 未還付税額の保存・更新 */
    int upsertRefund(@Param("yyyymm") String yyyymm,
                     @Param("j") BigDecimal jValue,
                     @Param("k") BigDecimal kValue);
}
