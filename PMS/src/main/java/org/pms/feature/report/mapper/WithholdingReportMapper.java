package org.pms.feature.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingSearch;

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
}
