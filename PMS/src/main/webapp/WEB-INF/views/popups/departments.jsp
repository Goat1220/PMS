<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../includes/commonform.jsp"%>
<%@ include file="../includes/table.jsp"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<title>부서</title>
<style>
:root { -
	-line: #dcdfe6; -
	-bg: #f6f7fb; -
	-text: #303133; -
	-muted: #909399; -
	-blue: #409eff;
}

* {
	box-sizing: border-box
}

body {
	font-family: Segoe UI, Malgun Gothic, Apple SD Gothic Neo, sans-serif;
	color: var(- -text);
	margin: 0
}

.wrap {
	padding: 14px
}

.titlebar {
	display: flex;
	justify-content: center;
	border-bottom: 1px solid var(- -line);
	padding: 10px 12px;
	font-weight: 700
}

.searchbar {
	display: flex;
	gap: 8px;
	align-items: center;
	padding: 10px 12px;
	border-bottom: 1px solid var(- -line);
	flex-wrap: wrap
}

.searchbar select, .searchbar input {
	height: 30px;
	border: 1px solid var(- -line);
	padding: 0 8px;
	border-radius: 4px
}

.searchbar input {
	width: 320px
}

.btn {
	height: 30px;
	padding: 0 12px;
	border: 1px solid var(- -line);
	background: #fff;
	border-radius: 4px;
	cursor: pointer
}

.btn.primary {
	background: var(- -blue);
	border-color: var(- -blue);
	color: #fff
}

.grid {
	padding: 10px 12px
}

/* table {
	width: 100%;
	border-collapse: collapse
}

th, td {
	border: 1px solid var(- -line);
	font-size: 12.5px;
	padding: 6px 8px;
	white-space: nowrap
}

th {
	background: var(- -bg);
	text-align: left
} */

tr:hover td {
	background: #fafafa
}

.footer {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 8px 12px;
	border-top: 1px solid var(- -line)
}

.footer .left {
	margin-left: auto;
	display: flex;
	gap: 6px;
	align-items: center
}

.pager a {
	display: inline-block;
	min-width: 24px;
	text-align: center;
	padding: 2px 6px;
	border: 1px solid var(- -line);
	border-radius: 4px;
	color: var(- -text);
	text-decoration: none
}

.pager a.on {
	background: var(- -blue);
	color: #fff;
	border-color: var(- -blue)
}
</style>
</head>
<body>
	<div class="wrap">

		<!-- 타이틀 -->
		<div class="titlebar">
			<div id="popupTitle">부서</div>
		</div>

		<!-- 검색바: 부서명 / 부서코드 -->
		<div class="searchbar">
			<select id="by">
				<option value="deptName">부서명</option>
				<option value="deptCode">부서코드</option>
			</select> <input id="keyword" type="text" placeholder="%" />
			<button id="btnSearch" class="btn primary">검색</button>
		</div>
		<div class="searchbar">
			<!-- 사용/미사용 드롭다운 -->
			<span class="muted" style="margin-left: 12px;">사용여부</span> <select
				id="selUse" title="사용여부">
				<option value="ALL">전체</option>
				<option value="Y">사용</option>
				<option value="N">미사용</option>
			</select>
		</div>




		<!-- 표 -->
		<div class="grid">
			<table id="grid">
				<thead>
					<tr id="gridHead"></tr>
				</thead>
				<tbody id="gridBody"></tbody>
			</table>
		</div>

		<!-- 하단: 조회갯수/페이지 -->
		<div class="footer">
			<div class="left">
				<span>조회갯수설정</span> <select id="pageSize">
					<option>10</option>
					<option>20</option>
					<option selected>50</option>
					<option>100</option>
				</select>
				<div class="pager" id="pagerNumbers"></div>
			</div>
		</div>
	</div>

	<script src="<c:url value='/resources/js/popup-common.js'/>"></script>
	<script>
		window.POPUP_CONFIG = {
			title : '부서',
			api : '<c:url value="/api/popups/departments"/>',
			pageSize : 50,
			byOptions : [ {
				value : 'deptName',
				label : '부서명'
			}, {
				value : 'deptCode',
				label : '부서코드'
			} ],
			columns : [ {
				key : '__seq',
				name : 'No',
				width : 56
			}, {
				key : 'deptCode',
				name : '부서코드',
				width : 80
			}, {
				key : 'deptName',
				name : '부서명'
			}, {
				key : 'startDate',
				name : '시작일',
				width : 100,
				format : 'date'
			}, {
				key : 'endDate',
				name : '종료일',
				width : 100,
				format : 'date'
			}, {
				key : 'memo',
				name : '비고'
			} ],
			// 행 더블클릭 시 부모창 콜백 호출
			onRowClick : function(row) {
				if (window.opener
						&& typeof window.opener.onDepartmentPicked === 'function') {
					window.opener.onDepartmentPicked(row);
				}
				window.close();
			}

		};

		// 드롭다운 변경 시 즉시 재조회
		document.getElementById('selUse').addEventListener('change',
				function() {
					if (window.POPUP && POPUP.search)
						POPUP.search(); // 없으면 아래 한 줄로
					else
						document.getElementById('btnSearch').click();
				});

		// 엔터로 검색
		document.getElementById('keyword').addEventListener('keydown',
				function(e) {
					if (e.key === 'Enter')
						document.getElementById('btnSearch').click();
				});

		POPUP.init();
	</script>
	<script>
		document.addEventListener('DOMContentLoaded', function() {
			enableSort('#grid');
			enablePaging('#grid', 20);
		});
	</script>
</body>
</html>
