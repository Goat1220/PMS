package org.pms.feature.yearend.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.pms.feature.yearend.admin.domain.AdminRowDTO;
import org.pms.feature.yearend.admin.domain.AdminSearchCond;

@Mapper
public interface YrtAdminMapper {

List<AdminRowDTO> findRows(AdminSearchCond cond);
}
