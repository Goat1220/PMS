package org.pms.feature.yearend.result.service;

import java.util.List;

import org.pms.feature.yearend.result.domain.YrtHeaderViewDTO;
import org.pms.feature.yearend.result.mapper.YrtHeaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Setter;

@Service
@AllArgsConstructor
public class YrtHeaderServiceImpl implements YrtHeaderService {
	
	@Setter(onMethod_=@Autowired)
	private YrtHeaderMapper mapper;
	
	@Override
	public List<YrtHeaderViewDTO> getList(String deptName, String empName){
		return mapper.getList(deptName, empName);
	}
}
