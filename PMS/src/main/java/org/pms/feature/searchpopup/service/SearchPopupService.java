package org.pms.feature.searchpopup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.pms.common.dto.PageResponse;
import org.pms.feature.searchpopup.domain.EmployeeRow;
import org.pms.feature.searchpopup.domain.DepartmentRow;
import org.pms.feature.searchpopup.mapper.SearchPopupMapper;

import java.util.List;

/**
 * [검색 팝업 서비스 / 検索ポップアップサービス]  
 * - 사원 및 부서 검색 기능을 제공  
 * - Controller와 Mapper 사이에서 데이터 흐름을 담당
 */
@Service
@RequiredArgsConstructor  // final 필드 자동 주입 / finalフィールド自動注入
public class SearchPopupService {

  /** Mapper 주입 / Mapper注入 */
  private final SearchPopupMapper mapper;

  /**
   * 사원 검색 / 社員検索  
   * - 검색 조건(by, keyword, status)으로 사원 리스트 조회  
   * - 총 레코드 수를 구해 PageResponse로 반환
   */
  public PageResponse<EmployeeRow> employees(String by, String keyword, String status, int page, int size) {
    List<EmployeeRow> list = mapper.searchEmployees(by, keyword, status, page, size); // 목록 조회 / 一覧取得
    long total = mapper.countEmployees(by, keyword, status);                         // 전체 개수 / 総件数取得
    return new PageResponse<>(list, page, size, total);                              // 페이지 응답 구성 / ページ応答を作成
  }

  /**
   * 부서 검색 / 部署検索  
   * - 검색 조건(by, keyword, use)으로 부서 리스트 조회  
   * - 전체 개수 포함하여 PageResponse로 반환
   */
  public PageResponse<DepartmentRow> departments(String by, String keyword, int page, int size, String use) {
    List<DepartmentRow> items = mapper.searchDepartments(by, keyword, page, size, use); // 부서 목록 / 部署一覧
    long total = mapper.countDepartments(by, keyword, use);                            // 전체 건수 / 総件数
    return new PageResponse<>(items, page, size, total);                               // 결과 반환 / 結果を返す
  }
}
