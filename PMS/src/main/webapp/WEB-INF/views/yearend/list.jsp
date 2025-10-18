<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../includes/commonform.jsp"%>
<%@ include file="../includes/table.jsp"%>

<html>
<head>
<title>調整管理結果照会</title>
<style>
.row {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: var(--gap);
	height: calc(100vh - 250px);
}

.card {
	display: flex;
	flex-direction: column;
	height: 100%;
}

.card-body {
	flex: 1;
	overflow: hidden;
	display: flex;
	flex-direction: column;
}

.right-toolbar {
	display: flex;
	gap: 8px;
	align-items: center;
}

.right-toolbar input {
	padding: 6px 8px;
	border: 1px solid var(--border);
	border-radius: 8px;
}

#headerTable{
with:800px !important;
min-width:300 !important;
display: table;
}
#detailTable{
with:500px !important;
min-width:300 !important;
display: table;
}
</style>
</head>
<body>

	<!-- レイアウト上でタイトルが別表示されるためコメントアウト -->
	<!-- <header>
		<h2>調整管理結果照会</h2>
	</header> -->

	<div class="container">
		<!-- 検索バー -->
		<div><span style="font-weight: bold;">照会条件</span></div>
		<div class="filters">
			<label>調整年度</label><input type="text" id="searchYear" value="2025" />
			<label>調整事業所</label> 
			<select id="searchBizPlace">
				<option value=""></option>
				<option value="本社">本社</option>
			</select> 
			<label>部署</label><input type="text" id="searchDept" /> 
			<label>社員</label><input type="text" id="searchEmp" />
			<button onclick="searchHeader()">照会</button>
		</div>

		<div class="row">
			<!-- 左側：ヘッダー一覧 -->
			<div class="card">
				<div class="card-body">
					<div class="table-wrap">
						<table id="headerTable">
							<thead>
								<tr>
									<th>番号</th>
									<th>調整事業所</th>
									<th>氏名</th>
									<th>社員番号</th>
									<th>部署</th>
									<th>税額区分</th>
									<th>税額結果</th>
									<th>確定</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="h" items="${list}">
									<tr data-yrt-id="${h.yrtId}">
										<td>${h.yrtId}</td>
										<td>${h.bizPlace}</td>
										<td>${h.empName}</td>
										<td>${h.empNo}</td>
										<td>${h.deptName}</td>
										<td>${h.taxApplyType}</td>
										<td>${h.taxApplyResult}</td>
										<td><input type="checkbox" disabled="disabled"
											<c:if test="${h.confirmYn eq 'Y'}">checked</c:if> /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>

			<!-- 右側：詳細 + 算出根拠 -->
			<div class="card">
				<div class="card-header">
				  <div class="right-toolbar">
				    <span class="muted">社員</span> 
				    <input id="basisEmpName" type="text" placeholder="氏名" readonly style="width:100px;">
				    <input id="basisEmpNo" type="text" placeholder="社員番号" readonly style="width:100px;">
				    <button id="btnBasis">算出根拠</button>
				  </div>
				</div>

				<div class="card-body">
					<div class="table-wrap">
						<table id="detailTable">
							<thead>
								<tr>
									<th>調整項目分類</th>
									<th>調整項目</th>
									<th>金額</th>
									<th>予想金額</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td colspan="4" style="text-align: center; color: #666;">対象行をダブルクリックしてください。</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script>
(function(){
  let selected = { yrtId:null, empId:null };

  // ヘッダー行をダブルクリック → 詳細照会 + 算出根拠入力を反映
  document.querySelector('#headerTable tbody').addEventListener('dblclick', function(e){
    const tr = e.target.closest('tr'); 
    if(!tr) return;

    const yrtId = tr.dataset.yrtId;
    if(!yrtId) return;

    const empName = tr.children[2].innerText; // 氏名
    const empNo   = tr.children[3].innerText; // 社員番号

    document.querySelectorAll('#headerTable tbody tr').forEach(r=>r.classList.remove('active'));
    tr.classList.add('active');

    // 入力欄に反映
    document.getElementById('basisEmpName').value = empName;
    document.getElementById('basisEmpNo').value   = empNo;

    selected.yrtId = yrtId;
    loadDetail(yrtId);
  });

  // 算出根拠ボタン
  document.getElementById('btnBasis').addEventListener('click', function(){
    if(!selected.yrtId){
      alert('左側のヘッダー行をダブルクリックしてください。');
      return;
    }
    const url = '/yearend/basis?yrtId=' + encodeURIComponent(selected.yrtId);
    window.open(url, '_blank'); // 新しいタブで開く
  });

  async function loadDetail(yrtId){
    try{
      const res = await fetch('/yearend/result/detail?yrtId=' + encodeURIComponent(yrtId), {
        headers:{'Accept':'application/json'}
      });
      if(!res.ok) throw new Error(res.status);
      const data = await res.json();
      renderDetail(data);
    }catch(e){
      console.error('[detail] error:', e);
      renderDetail([]);
    }
  }
	
  // 詳細データ表示
  function renderDetail(rows){
    const tb = document.querySelector('#detailTable tbody');
    tb.innerHTML = '';
    if(!rows || rows.length === 0){
      tb.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#666;">データがありません。</td></tr>';
      return;
    }
    const frag = document.createDocumentFragment();
    rows.forEach(r=>{
      const tr = document.createElement('tr');
      tr.innerHTML =
        '<td>'+(r.category||'')+'</td>'+
        '<td>'+(r.itemName||'')+'</td>'+
        '<td style="text-align:right">'+((r.amount==null?0:r.amount).toLocaleString())+'</td>'+
        '<td style="text-align:right">'+((r.expectedAmount==null?0:r.expectedAmount).toLocaleString())+'</td>';
      frag.appendChild(tr);
    });
    tb.appendChild(frag);
  }
  
  // 共通ユーティリティ（ソートなど）有効化
  enableSort('#headerTable');
  enableSort('#detailTable');
})();

// 検索機能
function searchHeader(){
  const dept = document.getElementById('searchDept').value;
  const emp  = document.getElementById('searchEmp').value;

  const params = new URLSearchParams();
  if (dept) params.append('deptName', dept);
  if (emp)  params.append('empName', emp);

  window.location.href = '/yearend/result/list?' + params.toString();
}
</script>
</body>
</html>
