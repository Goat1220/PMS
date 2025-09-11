package org.pms.feature.yearend.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pms.feature.yearend.domain.YrtHeaderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
    "file:src/main/webapp/WEB-INF/spring/root-context.xml"
})
@Log4j
public class YrtHeaderMapperTest {

    @Autowired
    private YrtHeaderMapper mapper;

    @Test
    public void testGetList() {
        List<YrtHeaderVO> list = mapper.getList();
        for (YrtHeaderVO vo : list) {
            log.info(vo);   // VO에 @ToString 붙어 있으면 필드값 출력됨
        }
    }
    
    @Test
    public void testCheckSchema() {
        System.out.println("헤더 카운트 = " + mapper.countHeader());
    }

}
