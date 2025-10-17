package org.pms.feature.searchpopup.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.pms.common.dto.PageResponse;
import org.pms.feature.searchpopup.domain.DepartmentRow;
import org.pms.feature.searchpopup.domain.EmployeeRow;
import org.pms.feature.searchpopup.service.SearchPopupService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * [검색 팝업 컨트롤러]  
 * - 사원 / 부서 검색 팝업에서 사용하는 API  
 * - フロントの検索ポップアップ用API（社員・部署）
 */
@Log4j
@RestController              // JSON 응답 컨트롤러 / JSON応答コントローラ
@RequiredArgsConstructor     // final 필드 자동 주입 / finalフィールド自動注入
@RequestMapping("/api/popups") // 기본 경로 / ベースパス
public class SearchPopupController {

  /** 서비스 주입 / サービス注入 */
  private final SearchPopupService service;

  /**
   * 사원 검색 API / 社員検索API  
   * - 이름, 코드, 상태(전체/재직/퇴직)로 검색  
   * - 페이지네이션 포함 / ページング対応
   */
  @GetMapping(value = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PageResponse<EmployeeRow>> employees(
      @RequestParam(defaultValue = "empName") String by,     // 검색 기준 (empName 등) / 検索基準
      @RequestParam(defaultValue = "") String keyword,       // 검색어 / キーワード
      @RequestParam(defaultValue = "전체") String status,     // 상태 (전체/재직/퇴직) / 状態
      @RequestParam(defaultValue = "1") int page,            // 페이지 번호 / ページ番号
      @RequestParam(defaultValue = "50") int size) {         // 페이지 크기 / ページサイズ

    log.info("employees by=" + by + " keyword=" + keyword + " status=" + status + " page=" + page);
    // 서비스에서 검색 실행 / サービスで検索実行
    return ResponseEntity.ok(service.employees(by, keyword, status, page, size));
  }

  /**
   * 부서 검색 API / 部署検索API  
   * - 부서명으로 검색, 페이지네이션 지원  
   * - 사용여부(ALL/Y/N) 필터 가능
   */
  @GetMapping(value = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PageResponse<DepartmentRow>> departments(
      @RequestParam(defaultValue = "deptName") String by,     // 검색 기준 / 検索基準
      @RequestParam(defaultValue = "") String keyword,        // 검색어 / キーワード
      @RequestParam(defaultValue = "1") int page,             // 페이지 번호 / ページ番号
      @RequestParam(defaultValue = "50") int size,            // 페이지 크기 / ページサイズ
      @RequestParam(defaultValue = "ALL") String use) {       // 사용여부 / 使用区分

    log.info("departments by=" + by + " keyword=" + keyword + " page=" + page);
    // 서비스 호출 후 결과 반환 / サービス呼出し→結果返却
    return ResponseEntity.ok(service.departments(by, keyword, page, size, use));
  }
}
