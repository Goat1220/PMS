package org.pms.feature.yearend.result.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.yearend.result.domain.YrtDetailViewDTO;

public interface YrtDetailMapper {

	/* public List<YrtHeaderVO> getList(); */
	
    // 특정 YRT_ID에 해당하는 상세 내역 + 항목명 조회
    List<YrtDetailViewDTO> getDetailViewComputed(@Param("yrtId") int yrtId);
}