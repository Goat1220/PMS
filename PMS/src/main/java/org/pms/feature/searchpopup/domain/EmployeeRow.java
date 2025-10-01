// EmployeeRow.java
package org.pms.feature.searchpopup.domain;

import lombok.Data;
import java.util.Date;

@Data
public class EmployeeRow {
  private Long   empId;
  private String empNo;
  private String empName;

  private String deptName;
  private String deptCode;
  private String workDeptName;

  private String positionName;     // 직위(대리/사원/상무…)
  private String titleName;        // 직급(1급/2급…)
  private String dutyName;         // 직책(팀장…)

  private Date   appointDate;      // 발령일(Date)

  private String workStatusName;   // 재직/퇴직
  private String workStatusDetail; // 근무상태(일반 등)

  private String payTypeName;      // 급여형태(월급직/시급직)
  private String payApplyCode;     // 급여적용군(정기급여)
  private java.util.Date birthDate;
}
