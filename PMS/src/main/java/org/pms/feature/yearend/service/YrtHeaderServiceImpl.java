package org.pms.feature.yearend.service;

import java.util.List;

import org.pms.feature.yearend.domain.YrtHeaderVO;
import org.pms.feature.yearend.mapper.YrtHeaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@AllArgsConstructor
public class YrtHeaderServiceImpl implements YrtHeaderService {
	
	@Setter(onMethod_=@Autowired)
	private YrtHeaderMapper mapper;
	
	@Override
		public List<YrtHeaderVO> getList() {
		log.info("getList..........");
			return mapper.getList();
		}
}
