package org.pms.feature.yearend.result.service;

import java.util.List;

import org.pms.feature.yearend.result.domain.YrtDetailViewDTO;

public interface YrtDetailService {
    List<YrtDetailViewDTO> getDetailView(int yrtId);
}
