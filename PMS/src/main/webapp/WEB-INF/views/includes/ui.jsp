<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- ui.jsp -->

<div class="toolbar">
  <!-- 검색 영역 -->
  <input type="text" id="searchInput" placeholder="이름으로 검색"/>
  <button onclick="onSearch()">검색</button>

  <!-- 테이블 조작 버튼 -->
  <button onclick="onAddRow()">행 추가</button>
  <button onclick="onDeleteRow()">행 삭제</button>
  <button onclick="onExportExcel()">엑셀 다운로드</button>
</div>

<script>
  function onSearch() {
    const keyword = document.getElementById("searchInput").value;
    if (window.commonGridOptions) {
      window.commonGridOptions.api.setQuickFilter(keyword); // AG Grid 내장 검색
    }
  }

  function onAddRow() {
    if (window.commonGridOptions) {
      const newItem = { empNo: "", empName: "", deptName: "" };
      window.commonGridOptions.api.applyTransaction({ add: [newItem] });
    }
  }

  function onDeleteRow() {
    if (window.commonGridOptions) {
      const selected = window.commonGridOptions.api.getSelectedRows();
      window.commonGridOptions.api.applyTransaction({ remove: selected });
    }
  }

  function onExportExcel() {
    if (window.commonGridOptions) {
      window.commonGridOptions.api.exportDataAsExcel();
    }
  }
</script>