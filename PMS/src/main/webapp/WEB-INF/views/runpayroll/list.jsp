<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급상여 처리</title>
<style>
:root { -
	-gap: 12px; -
	-radius: 10px; -
	-border: #e5e7eb; -
	-bg: #f8fafc; -
	-text: #111827; -
	-muted: #6b7280; -
	-total: #eaf7ea
}

* {
	box-sizing: border-box
}

body {
	margin: 0;
	font-family: system-ui, -apple-system, "Segoe UI", Roboto,
		"Noto Sans KR", "Apple SD Gothic Neo", Arial, "맑은 고딕", sans-serif;
	color: var(- -text);
	background: #f8fafc
}

header {
	padding: 16px 20px;
	background: #fff;
	border-bottom: 1px solid var(- -border);
	position: sticky;
	top: 0;
	z-index: 5
}

.container {
	padding: 16px 20px
}

.row {
	display: grid;
	grid-template-columns: 2fr 1fr;
	gap: 12px;
	align-items: start
}

.card {
	background: #fff;
	border: 1px solid var(- -border);
	border-radius: 10px;
	overflow: hidden
}

.card>.card-header {
	padding: 12px 14px;
	border-bottom: 1px solid var(- -border);
	font-weight: 600;
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 8px
}

.card>.card-body {
	padding: 12px 14px
}

.filters {
	display: flex;
	gap: 8px;
	align-items: center;
	flex-wrap: wrap
}

.filters input[type="month"], .filters select, .filters input[type="text"]
	{
	padding: 8px 10px;
	border: 1px solid var(- -border);
	border-radius: 8px
}

.filters button {
	padding: 8px 12px;
	border: 1px solid #0ea5e9;
	background: #0ea5e9;
	color: #fff;
	border-radius: 8px;
	cursor: pointer
}

.filters button.secondary {
	background: #fff;
	color: #0ea5e9
}

.actions {
	display: flex;
	gap: 8px;
	align-items: center;
	flex-wrap: wrap
}

.actions .btn {
	padding: 6px 10px;
	border: 1px solid #334155;
	background: #334155;
	color: #fff;
	border-radius: 8px;
	cursor: pointer;
	font-size: 12px
}

.actions .btn.alt {
	background: #fff;
	color: #334155
}

.actions .sep {
	width: 1px;
	height: 24px;
	background: #e5e7eb;
	margin: 0 2px
}

.chk {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	font-size: 12px
}

.table-wrap {
	overflow: auto;
	border: 1px solid var(- -border);
	border-radius: 8px
}

table {
	border-collapse: collapse;
	width: 100%;
	min-width: 1760px
}

thead th {
	position: sticky;
	top: 0;
	background: #f1f5f9;
	border-bottom: 1px solid var(- -border);
	font-weight: 600;
	white-space: nowrap
}

th, td {
	border-bottom: 1px solid var(- -border);
	padding: 8px 10px;
	text-align: left;
	font-size: 13px
}

tbody tr:hover {
	background: #f8fafc
}

tbody tr.active {
	background: #e0f2fe
}

.muted {
	color: #6b7280
}

.grid-2 {
	display: grid;
	grid-template-columns: 1fr;
	gap: 12px
}

.list {
	border: 1px solid var(- -border);
	border-radius: 8px;
	overflow: hidden
}

.pill {
	display: inline-block;
	padding: 2px 8px;
	font-size: 12px;
	border-radius: 999px;
	border: 1px solid var(- -border);
	background: #f8fafc
}

.right-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 6px
}

.ghost {
	opacity: .5
}

.row-total {
	background: #eaf7ea !important;
	font-weight: 600
}

.right-tables table {
	min-width: 100%
}

.sr-only {
	position: absolute;
	width: 1px;
	height: 1px;
	padding: 0;
	margin: -1px;
	overflow: hidden;
	clip: rect(0, 0, 0, 0);
	white-space: nowrap;
	border: 0
}
</style>
</head>
<body>
	<header>
		<div class="filters">
			<div class="muted">급여조회</div>
			<div class="filters">
				<label>지급연월 <input id="yyyymm" type="month" value="2018-08"></label>
				<label>급여유형 <select id="payType">
						<option value="">전체</option>
						<option value="SALARY" selected>SALARY</option>
						<option value="BONUS">BONUS</option>
				</select>
			</div>
			<div class="filters">
				</label> <label>부서코드 <input id="deptCode" type="text" readonly
					style="width: 110px"> <label>부서이름 <input
						id="deptName" type="text" readonly style="width: 130px">
				</label>
					<button id="btnSearchDep">부서검색</button>
				</label> <label>사번 <input id="empNo" type="text" readonly
					style="width: 130px">
				</label> <label>이름 <input id="empName" type="text" readonly
					style="width: 130px"></label>
				<button id="btnSearchEmp">사원검색</button>

				<button id="btnSearch">조회</button>
				<button id="btnReset" class="secondary">초기화</button>
				<span id="summaryCount" class="muted" style="margin-left: 8px;"></span>
			</div>
		</div>
	</header>

	<div class="container">
		<div class="row">
			<!-- 좌측 요약 -->
			<div class="card">
				<div class="card-header">
					<span>사원별 급여 요약</span>
					<div class="actions">
						<label class="chk"><input type="checkbox" id="chkAll">
							전체선택</label> <span class="sep"></span>
						<button class="btn" id="btnProcess">급상여처리</button>
						<button class="btn" id="btnReTax">세금재처리</button>
						<button class="btn" id="btnApplyYrt">정산세금반영</button>
						<span class="sep"></span>
						<button class="btn alt" id="btnConfirm">확정</button>
						<button class="btn alt" id="btnUnconfirm">확정해제</button>
					</div>
				</div>
				<div class="card-body">
					<div class="table-wrap">
						<table id="tblSummary">
							<thead>
								<tr>
									<th style="width: 36px;"><input type="checkbox"
										id="chkAllHeader" aria-label="전체행 선택"></th>
									<th>사번</th>
									<th>사원</th>
									<th>부서</th>
									<th>세금적용</th>
									<th>세액조정율</th>
									<th>프로젝트</th>
									<th>세금계산안함</th>
									<th>일할계산</th>
									<th>정산반영</th>
									<th>생산직비과세</th>
									<th>국외근로비과세</th>
									<th>연구원비과세</th>
									<th>소득세감면율</th>
									<th>세금적용(개인)</th>
									<th>상여율</th>
									<th>지급총액</th>
									<th>기지급총액</th>
									<th>공제총액</th>
									<th>실지급액</th>
									<th>퇴직여부</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
					<div class="muted" style="margin-top: 6px;">행을 클릭하면 우측에 지급/공제
						항목이 표시됩니다. (체크박스로 여러 명 선택 가능)</div>
				</div>
			</div>

			<!-- 우측 상세 -->
			<div class="card">
				<div class="card-header">지급/공제 상세</div>
				<div class="card-body right-tables">
					<div class="right-top">
						<div class="title">
							선택사번: <span id="selEmpNo" class="pill ghost">-</span> <span
								id="selEmpName" class="pill ghost">-</span>
						</div>
						<div class="muted">
							조회연월: <span id="selYyyymm">-</span> / 유형: <span id="selPayType">-</span>
						</div>
					</div>

					<div class="grid-2" style="margin-top: 10px;">
						<div class="list">
							<div class="card-header">지급항목</div>
							<div class="card-body" style="padding: 0;">
								<table id="tblItems">
									<thead>
										<tr>
											<th>지급항목</th>
											<th>비과세유형</th>
											<th>기지급</th>
											<th style="text-align: right;">금액</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>
						</div>

						<div class="list">
							<div class="card-header">공제항목</div>
							<div class="card-body" style="padding: 0;">
								<table id="tblDeds">
									<thead>
										<tr>
											<th>공제항목</th>
											<th style="text-align: right;">금액</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>
						</div>
					</div>

				</div>
			</div>
			<!-- /right -->
		</div>
	</div>

	<%-- API URL 바인딩 --%>
	<c:url var="summaryUrl" value="/runpayroll/api/summary" />
	<c:url var="itemsUrl" value="/runpayroll/api/items" />
	<c:url var="deductionsUrl" value="/runpayroll/api/deductions" />
	<c:url var="processUrl" value="/runpayroll/api/process" />
	<c:url var="retaxUrl" value="/runpayroll/api/recalc-taxes" />
	<c:url var="confirmUrl" value="/runpayroll/api/confirm" />
	<c:url var="applyYrtUrl" value="/runpayroll/api/apply-yrt" />
	<c:url var="unconfirmUrl" value="/runpayroll/api/unconfirm" />

	<script>
		window.RunPayrollConfig = {
			summary : '${summaryUrl}',
			items : '${itemsUrl}',
			deductions : '${deductionsUrl}',
			process : '${processUrl}',
			retax : '${retaxUrl}',
			confirm : '${confirmUrl}',
			applyYrt : '${applyYrtUrl}',
			unconfirm : '${unconfirmUrl}'
		};
	</script>

	<script
		src="${pageContext.request.contextPath}/resources/js/runpayroll.js"
		defer></script>

</body>
</html>
