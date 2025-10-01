package org.pms.feature.searchpopup.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.pms.feature.searchpopup.domain.DepartmentRow;
import org.pms.feature.searchpopup.domain.EmployeeRow;

public interface SearchPopupMapper {

  // 직원
  List<EmployeeRow> searchEmployees(
      @Param("by") String by,
      @Param("keyword") String keyword,
      @Param("status") String status,     // 전체/재직/퇴직
      @Param("page") int page,
      @Param("size") int size
  );

  long countEmployees(
      @Param("by") String by,
      @Param("keyword") String keyword,
      @Param("status") String status
  );

  // 부서
  List<DepartmentRow> searchDepartments(
		  @Param("by") String by,
		  @Param("keyword") String keyword,
		  @Param("page") int page,
		  @Param("size") int size,
		  @Param("use") String use // Y/N/ALL
		);

  long countDepartments(
      @Param("by") String by,
      @Param("keyword") String keyword,
      @Param("use") String use);
  
}
