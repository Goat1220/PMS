package org.pms.feature.yearend.domain;

import java.util.Date;

import lombok.Data;

@Data
public class YrtHeaderVO {
	private int yrtId;
	private int empId;
	private String bizPlace; //사업장
	private int baseYear; //기준년도 (4자리 사용. 예:2018)
	private String taxApplyType; //세금 적용 구분
	private String taxApplyResult; //세금 적용 결과
	private String confirmYn; //확정 여부(Y/N)
	private Date createAt; //생성 일시
}
