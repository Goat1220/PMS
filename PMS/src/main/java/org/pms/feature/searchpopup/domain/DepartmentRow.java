package org.pms.feature.searchpopup.domain;

import lombok.Data;

/**
 * [부서 정보 행 / 部署情報DTO]  
 * - 부서 검색 팝업이나 목록 화면에 표시되는 한 행의 데이터  
 * - 部署検索ポップアップや一覧画面で使用される行データ
 */
@Data // getter/setter 자동 생성 / getter・setter自動生成
public class DepartmentRow {

  /** 부서 ID (PK) / 部署ID（主キー） */
  private Long deptId;

  /** 부서 코드 / 部署コード */
  private String deptCode;

  /** 부서 이름 / 部署名 */
  private String deptName;

  /** 시작일 (부서 생성일 등) / 開始日（部署作成日など） */
  private java.util.Date startDate;

  /** 종료일 (폐지일 등) / 終了日（廃止日など） */
  private java.util.Date endDate;

  /** 메모 / 備考・メモ */
  private String memo;

  /** 사용 여부(Y/N) / 使用可否（Y/N） */
  private String useYn;

  /** 사용 여부 이름(예: 사용/미사용) / 使用区分名（例：使用中・未使用） */
  private String useName;
}
