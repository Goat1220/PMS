package org.pms.feature.yearend.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Setter;

import org.pms.feature.yearend.domain.YrtDetailViewDTO;
import org.pms.feature.yearend.mapper.YrtDetailMapper;

@Service
@AllArgsConstructor
public class YrtDetailServiceImpl implements YrtDetailService {

    @Setter (onMethod_=@Autowired)
    private YrtDetailMapper mapper;

    @Override
    public List<YrtDetailViewDTO> getDetailView(int yrtId) {
        return mapper.getDetailViewByHeader(yrtId);
    }
}
