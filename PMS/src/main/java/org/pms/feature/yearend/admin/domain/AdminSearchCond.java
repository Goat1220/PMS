package org.pms.feature.yearend.admin.domain;

import lombok.Data;

@Data
public class AdminSearchCond {
	private String baseYear;   // 정산년도 (텍스트)
    private String bizPlace;   // 정산사업장
    private String deptName;   // 부서
    private String empName;    // 사원
}
