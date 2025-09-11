package org.pms.feature.yearend.mapper;

import java.util.List;
import java.util.Map;

import org.pms.feature.yearend.domain.YrtHeaderVO;

public interface YrtHeaderMapper {

	public List<YrtHeaderVO> getList();
	
    // yrt_header 건수 확인
    int countHeader();
}
