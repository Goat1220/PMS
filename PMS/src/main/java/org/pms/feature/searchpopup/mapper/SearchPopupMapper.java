package org.pms.feature.searchpopup.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.searchpopup.domain.DepartmentRow;
import org.pms.feature.searchpopup.domain.EmployeeRow;

/**
 * [검색 팝업 Mapper / 検索ポップアップ用Mapper]  
 * - MyBatis XML과 연결되어 실제 DB 검색 쿼리를 수행  
 * - 社員・部署検索用のSQLを定義するMapperインターフェース
 */
public interface SearchPopupMapper {

  // ===============================
  // 직원 검색 / 社員検索
  // ===============================

  /**
   * 직원 목록 검색 / 社員一覧検索  
   * - 이름, 코드 등(by)에 따라 검색어(keyword)로 필터링  
   * - 상태(전체/재직/퇴직) 조건 포함  
   * - 페이지/사이즈 기준으로 부분 조회
   */
  List<EmployeeRow> searchEmployees(
      @Param("by") String by,           // 검색 기준 (empName 등) / 検索基準
      @Param("keyword") String keyword, // 검색어 / キーワード
      @Param("status") String status,   // 전체/재직/퇴직 / 全体・在職・退職
      @Param("page") int page,          // 페이지 번호 / ページ番号
      @Param("size") int size           // 페이지 크기 / ページサイズ
  );

  /**
   * 직원 전체 건수 조회 / 社員総件数取得  
   * - 페이징 처리를 위한 총 레코드 수 반환  
   */
  long countEmployees(
      @Param("by") String by,
      @Param("keyword") String keyword,
      @Param("status") String status
  );

  // ===============================
  // 부서 검색 / 部署検索
  // ===============================

  /**
   * 부서 목록 검색 / 部署一覧検索  
   * - 부서명(keyword)과 사용여부(use)로 검색  
   * - 페이지/사이즈 기준으로 조회
   */
  List<DepartmentRow> searchDepartments(
      @Param("by") String by,           // 검색 기준 (deptName 등) / 検索基準
      @Param("keyword") String keyword, // 검색어 / キーワード
      @Param("page") int page,          // 페이지 번호 / ページ番号
      @Param("size") int size,          // 페이지 크기 / ページサイズ
      @Param("use") String use          // 사용 여부 (Y/N/ALL) / 使用可否
  );

  /**
   * 부서 전체 건수 조회 / 部署総件数取得  
   * - 페이징용 총 건수 반환  
   */
  long countDepartments(
      @Param("by") String by,
      @Param("keyword") String keyword,
      @Param("use") String use
  );
}
