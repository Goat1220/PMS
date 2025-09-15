package org.pms.feature.yearend.domain;

import lombok.Data;

@Data
public class YrtHeaderViewDTO {

	private int yrtId;
	private String empName; //사원 이름
	private String empNo; //사번
	private String deptName; //부서
	private String bizPlace; //사업장
	private String taxApplyType; //세금 적용 구분
	private String taxApplyResult; //세금 적용 결과
	private String confirmYn; //확정 여부(Y/N)
}
