package org.pms.feature.report.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.pms.feature.report.domain.*;

@Mapper
public interface WithholdingReportMapper {
    List<WithholdingRow> selectSummary(WithholdingSearch s);
    List<AnnexRow>       selectAnnex(WithholdingSearch s);
}
