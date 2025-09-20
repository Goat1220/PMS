package org.pms.feature.yearend.admin.service;

import java.util.List;

import org.pms.feature.yearend.admin.domain.*;

public interface YrtAdminService {
    List<AdminRowDTO> findRows(AdminSearchCond cond);
}
