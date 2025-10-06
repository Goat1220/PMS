<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급상여 전표처리</title>
<style>
:root { -
	-gap: 12px; -
	-radius: 10px; -
	-border: #e5e7eb; -
	-bg: #f8fafc; -
	-text: #111827; -
	-muted: #6b7280
}

* {
	box-sizing: border-box
}

body {
	margin: 0;
	font-family: system-ui, -apple-system, "Segoe UI", Roboto,
		"Noto Sans KR", "맑은 고딕", sans-serif;
	color: var(- -text);
	background: var(- -bg)
}

header {
	padding: 14px 18px;
	background: #fff;
	border-bottom: 1px solid var(- -border);
	position: sticky;
	top: 0;
	z-index: 5
}

.filters {
	display: flex;
	gap: 10px;
	align-items: center;
	flex-wrap: wrap
}

.filters label {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	font-size: 13px;
	color: var(- -muted)
}

.filters select, .filters input[type="month"] {
	padding: 6px 10px;
	border: 1px solid var(- -border);
	border-radius: 8px;
	background: #fff;
	color: var(- -text)
}

.filters button {
	padding: 7px 12px;
	border-radius: 8px;
	border: 1px solid #0ea5e9;
	background: #0ea5e9;
	color: #fff;
	cursor: pointer
}

.filters button.secondary {
	background: #fff;
	color: #0ea5e9
}

.totals {
	display: flex;
	gap: 10px;
	align-items: center;
	margin-left: auto
}

.totals input {
	width: 140px;
	text-align: right;
	border: 1px solid var(- -border);
	border-radius: 8px;
	padding: 6px 8px;
	background: #f1f5f9
}

.container {
	padding: 16px 18px
}

.card {
	background: #fff;
	border: 1px solid var(- -border);
	border-radius: 10px;
	overflow: hidden
}

.card+.card {
	margin-top: 14px
}

.card-header {
	padding: 10px 12px;
	border-bottom: 1px solid var(- -border);
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-weight: 600
}

.card-body {
	padding: 10px 12px
}

.table-wrap {
	overflow: auto;
	border: 1px solid var(- -border);
	border-radius: 8px
}

table {
	border-collapse: collapse;
	width: 100%;
	min-width: 1480px
}

th, td {
	border-bottom: 1px solid var(- -border);
	padding: 8px 10px;
	font-size: 13px;
	white-space: nowrap
}

thead th {
	position: sticky;
	top: 0;
	background: #f1f5f9
}

tbody tr:hover {
	background: #f8fafc
}

.right {
	text-align: right
}

.center {
	text-align: center
}
</style>
</head>
<body>

	<header>
		<div class="filters">
			<label>회계단위 <select id="orgUnit">
					<option value="본사" selected>본사</option>
			</select>
			</label> <label>급여작업군 <select id="jobGroup">
					<option value="정기급여" selected>정기급여</option>
			</select>
			</label> <label>전표처리대상자 <select id="targetType">
					<option value="재직" selected>재직</option>
					<option value="퇴직">퇴직</option>
					<option value="재직+퇴직">재직+퇴직</option>
			</select>
			</label> <label>적용연월 <input id="yyyymm" type="month"
				value="${defaultYyyymm != null ? defaultYyyymm : '2018-08'}">
			</label> <label>급상여종류 <select id="payType">
					<option value="SALARY" ${defaultPayType=='SALARY'?'selected':''}>급여</option>
					<option value="BONUS" ${defaultPayType=='BONUS'?'selected':''}>상여</option>
			</select>
			</label> <label>처리구분 <select id="procKind">
					<option value="분개">분개</option>
			</select>
			</label>

			<button id="btnBaseGenerate">기초자료생성</button>
			<button id="btnProcess" class="secondary">분개전표처리</button>

			<div class="totals">
				<span style="color: var(- -muted); font-size: 12px;">차변합계</span> <input
					id="sumDebit" type="text" readonly> <span
					style="color: var(- -muted); font-size: 12px;">대변합계</span> <input
					id="sumCredit" type="text" readonly>
			</div>
		</div>
	</header>

	<div class="container">
		<div class="card">
			<div class="card-header">전표 미리보기</div>
			<div class="card-body">
				<div class="table-wrap">
					<table id="tblVoucher">
						<thead>
							<tr>
								<th style="width: 40px;">#</th>
								<th>계정과목</th>
								<th>차대구분</th>
								<th class="right">차변금액</th>
								<th class="right">대변금액</th>
								<th>발생부서</th>
								<th>발생원천</th>
								<th>지급일</th>
								<th>적요</th>
								<th>전표형번호</th>
								<th>전표내부코드</th>
								<th>승인여부</th>
								<th>순번</th>
								<th>계정내부코드</th>
								<th>차대구분코드</th>
								<th>발생부서코드</th>
								<th>비용구분코드</th>
								<th>처리구분</th>
								<th>전표처리대상자코드</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
				<div style="margin-top: 8px; color: var(- -muted); font-size: 12px;">
					※ ‘분개전표처리’ 클릭 시 pay_voucher / pay_voucher_line / pay_month_summary에
					반영됩니다.</div>
			</div>
		</div>
	</div>

	<%-- API URL 바인딩 --%>
	<c:url var="baseGenerateUrl"
		value="/voucher/api//basegenerate" />
	<c:url var="viewUrl" value="/voucher/api/view" />
	<c:url var="processUrl" value="/voucher/api/process" />

	<script>
		window.VoucherConfig = {
			baseGenerate : '${baseGenerateUrl}',
			view : '${viewUrl}',
			process : '${processUrl}'
		};
	</script>
	<script
		src="${pageContext.request.contextPath}/resources/js/voucher.js" defer></script>
</body>
</html>
