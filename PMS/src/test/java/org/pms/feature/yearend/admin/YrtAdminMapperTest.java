package org.pms.feature.yearend.admin;

import static org.junit.Assert.*;

import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.pms.feature.yearend.admin.domain.AdminRowDTO;
import org.pms.feature.yearend.admin.domain.AdminSearchCond;
import org.pms.feature.yearend.admin.mapper.YrtAdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// root-context.xml, mybatis-config.xml, datasource-context.xml 등 설정파일 경로 맞춰주세요
@ContextConfiguration(locations = {
    "file:src/main/webapp/WEB-INF/spring/root-context.xml"
})
public class YrtAdminMapperTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private YrtAdminMapper mapper;

    @Test
    public void testDataSource() throws Exception {
        assertNotNull("DataSource가 null 입니다.", dataSource);
        System.out.println("DataSource = " + dataSource.getConnection());
    }

    @Test
    public void testSqlSessionFactory() throws Exception {
        assertNotNull("SqlSessionFactory가 null 입니다.", sqlSessionFactory);
        SqlSessionTemplate session = new SqlSessionTemplate(sqlSessionFactory);
        assertNotNull("SqlSessionTemplate 생성 실패", session);
    }

    @Test
    public void testFindRows() {
        AdminSearchCond cond = new AdminSearchCond();
        cond.setBaseYear("2018");   // 필요시 조건 입력
        cond.setDeptName(null);     // 조건 없으면 전체 조회
        cond.setEmpName(null);

        List<AdminRowDTO> list = mapper.findRows(cond);
        System.out.println("조회 결과 건수 = " + list.size());
        for (AdminRowDTO dto : list) {
            System.out.println(dto);
        }

        assertNotNull(list);
    }
}

