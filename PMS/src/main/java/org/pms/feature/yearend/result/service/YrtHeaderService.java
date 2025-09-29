package org.pms.feature.yearend.result.service;

import java.util.List;

import org.pms.feature.yearend.result.domain.YrtHeaderViewDTO;

public interface YrtHeaderService {

	public List<YrtHeaderViewDTO> getList(String deptName, String empName);
}
