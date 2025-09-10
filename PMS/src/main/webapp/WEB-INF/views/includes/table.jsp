<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- AG Grid CSS & JS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-grid.css"/>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-theme-alpine.css"/>
<script src="https://cdn.jsdelivr.net/npm/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>

<!-- 그리드 컨테이너 -->
<div id="commonGrid" class="ag-theme-alpine" style="height: 500px; width: 100%;"></div>

<script>
  function initCommonGrid(columnDefs, rowData) {
    const gridOptions = {
      columnDefs: columnDefs,
      rowData: rowData || [],
      defaultColDef: { sortable: true, filter: true, resizable: true }
    };
    new agGrid.Grid(document.querySelector("#commonGrid"), gridOptions);
    return gridOptions;
  }
</script>