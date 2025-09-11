<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<title>정산관리결과조회</title>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community@26.2.1/dist/styles/ag-grid.css"/>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community@26.2.1/dist/styles/ag-theme-alpine.css"/>
<script src="https://cdn.jsdelivr.net/npm/ag-grid-community@26.2.1/dist/ag-grid-community.min.noStyle.js"></script>



<style>
.container {
	display: flex;
	gap: 20px;
}

.grid-box {
	flex: 1;
}

.toolbar {
	margin-bottom: 10px;
}
</style>
</head>
<body>
	<h2>정산관리결과조회</h2>

	<!-- 검색 영역 -->
	<div class="toolbar">
		<label>정산년도:</label> <input type="text" id="searchYear" value="2018" />
		<label>사번:</label> <input type="text" id="searchEmpId" />
		<button onclick="searchHeader()">검색</button>
	</div>

	<!-- 두 개의 grid를 나란히 배치 -->
	<div class="container">
		<!-- header grid -->
		<div id="headerGrid" class="ag-theme-alpine grid-box"
			style="height: 500px; width: 50%;"></div>
		<!-- detail grid -->
		<div id="detailGrid" class="ag-theme-alpine grid-box"
			style="height: 500px; width: 50%;"></div>
	</div>

	<script>
  document.addEventListener("DOMContentLoaded", function() {
	 
    // Header grid 컬럼 정의
const headerColumnDefs = [
  { headerName: "번호", field: "yrtId", width: 80 },
  { headerName: "사원ID", field: "empId", width: 100 },
  { headerName: "사업장", field: "bizPlace", width: 120 },
  { headerName: "기준년도", field: "baseYear", width: 100 },
  { headerName: "세금 적용 구분", field: "taxApplyType", width: 180 },
  { headerName: "세금 적용 결과", field: "taxApplyResult", width: 180 },
  { headerName: "확정 여부", field: "confirmYn", width: 100 },
  { headerName: "생성 일시", field: "createAt", width: 150 }
];

    // 서버에서 받은 header 데이터
    const headerRowData = [
      <c:forEach var="item" items="${list}" varStatus="s">
        {
          yrtId: "<c:out value='${item.yrtId}'/>",
          empId: "<c:out value='${item.empId}'/>",
          bizPlace: "<c:out value='${item.bizPlace}'/>",
          baseYear: "<c:out value='${item.baseYear}'/>",
          taxApplyType: "<c:out value='${item.taxApplyType}'/>",
          taxApplyResult: "<c:out value='${item.taxApplyResult}'/>",
          confirmYn: "<c:out value='${item.confirmYn}'/>",
          createAt: "<c:out value='${item.createAt}'/>"
        }<c:if test="${!s.last}">,</c:if>
      </c:forEach>
    ];

    // Header Grid 생성
    const headerGridOptions = {
      columnDefs: headerColumnDefs,
      rowData: headerRowData,
      defaultColDef: { sortable: true, filter: true, resizable: true },
      onRowDoubleClicked: params => {
        // 더블클릭 시 detailGrid 데이터 조회
        loadDetail(params.data.empId);
      }
    };
    new agGrid.Grid(document.querySelector("#headerGrid"), headerGridOptions);

    // Detail grid 컬럼 정의
   const detailColumnDefs = [
  { headerName: "정산항목분류", field: "category", width: 120 },
  { headerName: "정산항목", field: "itemName", width: 200 },
  { headerName: "금액", field: "amount", width: 120 },
  { headerName: "예상금액", field: "expectedAmount", width: 120 }
];
    const detailGridOptions = {
      columnDefs: detailColumnDefs,
      rowData: [],
      defaultColDef: { sortable: true, filter: true, resizable: true }
    };
    new agGrid.Grid(document.querySelector("#detailGrid"), detailGridOptions);

    // Detail 데이터 로드 (Ajax 호출 자리)
    window.loadDetail = function(empId) {
    	  fetch('/yearend/detail?empId=${empId}')
    	    .then(response => response.json())
    	    .then(data => {
    	      detailGridOptions.api.setRowData(data);
    	    })
    	    .catch(error => {
    	      console.error("디테일 데이터 로드 실패:", error);
    	    });
    	};

  });
</script>
</body>
</html>
