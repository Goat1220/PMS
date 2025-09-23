package org.pms.feature.searchpopup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.pms.common.dto.PageResponse;
import org.pms.feature.searchpopup.domain.EmployeeRow;
import org.pms.feature.searchpopup.domain.DepartmentRow;
import org.pms.feature.searchpopup.mapper.SearchPopupMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchPopupService {

  private final SearchPopupMapper mapper;

  public PageResponse<EmployeeRow> employees(String by, String keyword, String status, int page, int size) {
    List<EmployeeRow> list = mapper.searchEmployees(by, keyword, status, page, size);
    long total = mapper.countEmployees(by, keyword, status);
    return new PageResponse<>(list, page, size, total);
  }

  public PageResponse<DepartmentRow> departments(String by, String keyword, int page, int size, String use) {
	  List<DepartmentRow> items = mapper.searchDepartments(by, keyword, page, size, use);
	  long total = mapper.countDepartments(by, keyword, use);
	  return new PageResponse<>(items, page, size, total);
  }
}
