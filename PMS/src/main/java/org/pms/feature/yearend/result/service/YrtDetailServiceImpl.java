package org.pms.feature.yearend.result.service;

import java.util.List;

import org.pms.feature.yearend.result.domain.YrtDetailViewDTO;
import org.pms.feature.yearend.result.mapper.YrtDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Setter;

@Primary
@Service
@AllArgsConstructor
public class YrtDetailServiceImpl implements YrtDetailService {

    @Setter (onMethod_=@Autowired)
    private YrtDetailMapper mapper;

    @Override
    public List<YrtDetailViewDTO> getDetailView(int yrtId) {
        return mapper.getDetailViewComputed(yrtId);
    }
}
