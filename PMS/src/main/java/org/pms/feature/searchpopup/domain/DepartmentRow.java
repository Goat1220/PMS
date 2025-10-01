// DepartmentRow.java
package org.pms.feature.searchpopup.domain;

import lombok.Data;

@Data
public class DepartmentRow {
  private Long   deptId;
  private String deptCode;
  private String deptName;
  
  private java.util.Date startDate;
  private java.util.Date endDate;
  private String  memo;
  private String useYn; 
  private String useName;
}
