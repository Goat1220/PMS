// EmployeeRow.java
package org.pms.feature.searchpopup.domain;

import lombok.Data;
import java.util.Date;

/**
 * [사원 정보 행 / 社員情報DTO]  
 * - 사원 검색 팝업이나 리스트 화면에서 한 사람의 정보를 표현  
 * - 社員検索ポップアップ・一覧画面の1行データ
 */
@Data // getter/setter 자동 생성 / getter・setter自動生成
public class EmployeeRow {

  /** 사원 ID (PK) / 社員ID（主キー） */
  private Long empId;

  /** 사번 / 社員番号 */
  private String empNo;

  /** 사원명 / 社員名 */
  private String empName;

  /** 소속 부서명 / 所属部署名 */
  private String deptName;

  /** 소속 부서코드 / 所属部署コード */
  private String deptCode;

  /** 근무 부서명 (겸직 등) / 勤務部署名（兼務など） */
  private String workDeptName;

  /** 직위명 (예: 사원, 대리, 과장…) / 職位名（例：社員・代理・課長など） */
  private String positionName;

  /** 직급명 (예: 1급, 2급 등) / 職級名（例：1級・2級など） */
  private String titleName;

  /** 직책명 (예: 팀장 등) / 職責名（例：チーム長など） */
  private String dutyName;

  /** 발령일 (입사/전배 등) / 発令日（入社・異動など） */
  private Date appointDate;

  /** 재직/퇴직 상태명 / 在職・退職区分名 */
  private String workStatusName;

  /** 세부 근무상태 (예: 일반, 휴직 등) / 勤務状態詳細（例：通常・休職など） */
  private String workStatusDetail;

  /** 급여 형태 (월급직/시급직 등) / 給与形態（月給・時給など） */
  private String payTypeName;

  /** 급여 적용군 (정기급여 등) / 給与適用区分（定期給与など） */
  private String payApplyCode;

  /** 생년월일 / 生年月日 */
  private java.util.Date birthDate;
}
