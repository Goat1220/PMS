package org.pms.feature.yearend.admin.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import org.pms.feature.yearend.admin.mapper.YrtAdminMapper;
import org.pms.feature.yearend.admin.domain.*;

@Service
@RequiredArgsConstructor
public class YrtAdminServiceImpl implements YrtAdminService {

    private final YrtAdminMapper mapper;

    @Override
    public List<AdminRowDTO> findRows(AdminSearchCond cond) {
        // 기본값: 작년
        if (!StringUtils.hasText(cond.getBaseYear())) {
            cond.setBaseYear(String.valueOf(LocalDate.now().minusYears(1).getYear()));
        }
        return mapper.findRows(cond);
    }
}
