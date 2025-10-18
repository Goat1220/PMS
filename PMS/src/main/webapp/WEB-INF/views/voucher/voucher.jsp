<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../includes/commonform.jsp" %>
<%@ include file="../includes/table.jsp" %>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>給与・賞与 伝票処理</title>
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
	border-radius: 8px;
	max-height: calc(100vh - 220px);
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
			<label>会計単位
				<select id="orgUnit">
					<option value="본사" selected>本社</option>
				</select>
			</label>
			<label>給与作業グループ
				<select id="jobGroup">
					<option value="정기급여" selected>定期給与</option>
				</select>
			</label>
			<label>伝票処理対象
				<select id="targetType">
					<option value="재직" selected>在職</option>
					<option value="퇴직">退職</option>
					<option value="재직+퇴직">在職+退職</option>
				</select>
			</label>
			<label>適用年月
				<input id="yyyymm" type="month" value="${defaultYyyymm != null ? defaultYyyymm : '2018-08'}">
			</label>
			<label>給与種別
				<select id="payType">
					<option value="SALARY" ${defaultPayType=='SALARY'?'selected':''}>給与</option>
					<option value="BONUS" ${defaultPayType=='BONUS'?'selected':''}>賞与</option>
				</select>
			</label>
			<label>処理区分
				<select id="procKind">
					<option value="분개">仕訳</option>
				</select>
			</label>

			<button id="btnBaseGenerate">基礎データ生成</button>
			<button id="btnProcess" class="secondary">仕訳伝票処理</button>

			<div class="totals">
				<span style="color: var(- -muted); font-size: 12px;">借方合計</span>
				<input id="sumDebit" type="text" readonly>
				<span style="color: var(- -muted); font-size: 12px;">貸方合計</span>
				<input id="sumCredit" type="text" readonly>
			</div>
		</div>
	</header>

	<div class="container">
		<div class="card">
			<div class="card-header">伝票プレビュー</div>
			<div class="card-body">
				<div class="table-wrap">
					<table id="tblVoucher">
						<thead>
							<tr>
								<th style="width: 40px;">#</th>
								<th>勘定科目</th>
								<th>借貸区分</th>
								<th class="right">借方金額</th>
								<th class="right">貸方金額</th>
								<th>発生部門</th>
								<th>発生元</th>
								<th>支給日</th>
								<th>摘要</th>
								<th>伝票型番号</th>
								<th>伝票内部コード</th>
								<th>承認有無</th>
								<th>連番</th>
								<th>勘定内部コード</th>
								<th>借貸区分コード</th>
								<th>発生部門コード</th>
								<th>費用区分コード</th>
								<th>処理区分</th>
								<th>伝票処理対象者コード</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
				<div style="margin-top: 8px; color: var(- -muted); font-size: 12px;">
					※ 「仕訳伝票処理」をクリックすると、伝票および伝票行に反映されます。
				</div>
			</div>
		</div>
	</div>

	<%-- API URL バインド --%>
	<c:url var="baseGenerateUrl" value="/voucher/api//basegenerate" />
	<c:url var="viewUrl" value="/voucher/api/view" />
	<c:url var="processUrl" value="/voucher/api/process" />

	<script>
		window.VoucherConfig = {
			baseGenerate : '${baseGenerateUrl}',
			view : '${viewUrl}',
			process : '${processUrl}'
		};
	</script>
	<script src="${pageContext.request.contextPath}/resources/js/voucher.js" defer></script>
	<script>
		document.addEventListener('DOMContentLoaded', function() {
			enableSort('#tblVoucher');
		});
	</script>
</body>
</html>
