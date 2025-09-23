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

@Log4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/popups")
public class SearchPopupController {

  private final SearchPopupService service;

  @GetMapping(value = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PageResponse<EmployeeRow>> employees(
      @RequestParam(defaultValue = "empName") String by,
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "전체") String status,   // ★ 추가: 전체/재직/퇴직
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "50") int size) {

    log.info("employees by=" + by + " keyword=" + keyword + " status=" + status + " page=" + page);
    return ResponseEntity.ok(service.employees(by, keyword, status, page, size));
  }

  @GetMapping(value = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PageResponse<DepartmentRow>> departments(
      @RequestParam(defaultValue = "deptName") String by,
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "50") int size,
	  @RequestParam(defaultValue = "ALL") String use) {

    log.info("departments by=" + by + " keyword=" + keyword + " page=" + page);
    return ResponseEntity.ok(service.departments(by, keyword,page, size, use));
  }
}
