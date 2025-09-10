<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <title>Employee Table</title>
  <!-- AG Grid CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-grid.css"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-theme-alpine.css"/>
  <!-- AG Grid JS (UMD 빌드) -->
  <script src="https://cdn.jsdelivr.net/npm/ag-grid-community/dist/ag-grid-community.min.js"></script>
</head>
<body>
  <h1>직원 목록 (테스트)</h1>

  <!-- AG Grid 컨테이너 -->
  <div id="myGrid" class="ag-theme-alpine" style="height: 400px; width: 600px;"></div>

  <script>
    document.addEventListener("DOMContentLoaded", function () {
      console.log("agGrid 객체:", agGrid);

      // 컬럼 정의
      const columnDefs = [
        { headerName: "사번", field: "empNo" },
        { headerName: "이름", field: "empName" }
      ];

      // 테스트 데이터
      const rowData = [
        { empNo: "1001", empName: "홍길동" },
        { empNo: "1002", empName: "김철수" }
      ];

      // Grid 옵션
      const gridOptions = { columnDefs, rowData };

      // ✅ 최신 AG Grid는 createGrid() 사용
      agGrid.createGrid(document.querySelector("#myGrid"), gridOptions);
    });
  </script>
</body>
</html>