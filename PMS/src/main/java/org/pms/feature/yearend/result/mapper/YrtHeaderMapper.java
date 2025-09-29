package org.pms.feature.yearend.result.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.pms.feature.yearend.result.domain.YrtHeaderViewDTO;

public interface YrtHeaderMapper {

	public List<YrtHeaderViewDTO> getList(
			@Param("deptName")String deptName,
			@Param("empName") String empName);
}
