<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../includes/commonform.jsp" %>
<%@ include file="../includes/table.jsp" %>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>給与処理</title>
<style>
</style>
</head>
<body>
	<header>
		<div class="filters">
			<div class="muted">給与照会</div>
			<div class="filters">
				<label>支給年月 <input id="yyyymm" type="month" value="2025-09"></label>
				<label>給与区分 <select id="payType">
						<option value="">全体</option>
						<option value="SALARY" selected>給与</option>
						<option value="BONUS">賞与</option>
				</select>
			</div>
			<div class="filters">
				</label> <label>部署コード <input id="deptCode" type="text" readonly
					style="width: 110px"> <label>部署名 <input
						id="deptName" type="text" readonly style="width: 130px">
				</label>
					<button id="btnSearchDep">部署検索</button>
				</label> <label>社員番号 <input id="empNo" type="text" readonly
					style="width: 130px">
				</label> <label>氏名 <input id="empName" type="text" readonly
					style="width: 130px"></label>
				<button id="btnSearchEmp">社員検索</button>

				<button id="btnSearch">照会</button>
				<button id="btnReset" class="secondary">リセット</button>
				<span id="summaryCount" class="muted" style="margin-left: 8px;"></span>
			</div>
		</div>
	</header>

	<div class="container">
		<div class="row">
			<!-- 左側 サマリー -->
			<div class="card">
				<div class="card-header">
					<span>社員別給与サマリー</span>
					<div class="actions">
						<label class="chk"><input type="checkbox" id="chkAll">
							全選択</label> <span class="sep"></span>
						<button class="btn" id="btnProcess">給与処理</button>
						<button class="btn" id="btnReTax">税金再処理</button>
						<button class="btn" id="btnApplyYrt">年末調整反映</button>
						<span class="sep"></span>
						<button class="btn alt" id="btnConfirm">確定</button>
						<button class="btn alt" id="btnUnconfirm">確定解除</button>
					</div>
				</div>
				<div class="card-body">
					<div class="table-wrap">
						<table id="tblSummary">
							<thead>
								<tr>
									<th style="width: 36px;"><input type="checkbox"
										id="chkAllHeader" aria-label="全行選択"></th>
									<th>社員番号</th>
									<th>氏名</th>
									<th>部署</th>
									<th>税金適用</th>
									<th>税率調整率</th>
									<th>プロジェクト</th>
									<th>税計算なし</th>
									<th>日割計算</th>
									<th>調整反映</th>
									<th>生産職非課税</th>
									<th>海外勤務非課税</th>
									<th>研究員非課税</th>
									<th>所得税減免率</th>
									<th>個人税適用</th>
									<th>賞与率</th>
									<th>支給総額</th>
									<th>既支給額</th>
									<th>控除総額</th>
									<th>実支給額</th>
									<th>退職有無</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
					<div class="muted" style="margin-top: 6px;">行をクリックすると右側に支給/控除項目が表示されます。（チェックボックスで複数選択可能）</div>
				</div>
			</div>

			<!-- 右側 詳細 -->
			<div class="card">
				<div class="card-header">支給 / 控除 詳細</div>
				<div class="card-body right-tables">
					<div class="right-top">
						<div class="title">
							選択社員番号: <span id="selEmpNo" class="pill ghost">-</span> <span
								id="selEmpName" class="pill ghost">-</span>
						</div>
						<div class="muted">
							照会年月: <span id="selYyyymm">-</span> / 区分: <span id="selPayType">-</span>
						</div>
					</div>

					<div class="grid-2" style="margin-top: 10px;">
						<div class="list">
							<div class="card-header">支給項目</div>
							<div class="card-body" style="padding: 0;">
								<table id="tblItems">
									<thead>
										<tr>
											<th>支給項目</th>
											<th>非課税区分</th>
											<th>既支給</th>
											<th style="text-align: right;">金額</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>
						</div>

						<div class="list">
							<div class="card-header">控除項目</div>
							<div class="card-body" style="padding: 0;">
								<table id="tblDeds">
									<thead>
										<tr>
											<th>控除項目</th>
											<th style="text-align: right;">金額</th>
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

	<%-- API URL バインディング --%>
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
	<script>
		document.addEventListener('DOMContentLoaded', function() {
			enableSort('#tblSummary');
			enableSort('#tblItems');
			enableSort('#tblDeds');
		});
	</script>
</body>
</html>
