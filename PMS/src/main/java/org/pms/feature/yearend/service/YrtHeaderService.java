package org.pms.feature.yearend.service;

import java.util.List;

import org.pms.feature.yearend.domain.YrtHeaderViewDTO;

public interface YrtHeaderService {

	public List<YrtHeaderViewDTO> getList(String deptName, String empName);
}
