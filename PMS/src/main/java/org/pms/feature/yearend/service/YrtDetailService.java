package org.pms.feature.yearend.service;

import java.util.List;
import org.pms.feature.yearend.domain.YrtDetailViewDTO;

public interface YrtDetailService {
    List<YrtDetailViewDTO> getDetailView(int yrtId);
}
