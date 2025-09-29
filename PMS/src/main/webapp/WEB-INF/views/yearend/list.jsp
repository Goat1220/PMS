<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../includes/commonform.jsp"%>
<%@ include file="../includes/table.jsp"%>

<html>
<head>
<title>정산관리결과조회</title>
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
	height: 100%
}

.card-body {
	flex: 1;
	overflow: hidden;
	display: flex;
	flex-direction: column
}

.table-wrap {
	flex: 1;
	overflow-y: auto
}

.right-toolbar {
	display: flex;
	gap: 8px;
	align-items: center
}

.right-toolbar input {
	padding: 6px 8px;
	border: 1px solid var(--border);
	border-radius: 8px
}

table {
  border-collapse: collapse;
  font-size: 13px;
}

th, td {
  padding: 3px 4px;
  white-space: nowrap;
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
	<header>
		<h2>정산관리결과조회</h2>
	</header>

	<div class="container">
		<!-- 검색바(샘플) -->
		<div><span style="font-weight: bold;">조회조건</span></div>
		<div class="filters">
			<label>정산연도</label><input type="text" id="searchYear" value="2025" />
			<label>정산사업장</label> <select id="searchBizPlace">
				<option value=""></option>
				<option value="본사">본사</option>
			</select> <label>부서</label><input type="text" id="searchDept" /> <label>사원</label><input
				type="text" id="searchEmp" />
			<button onclick="searchHeader()">조회</button>
		</div>

		<div class="row">
			<!-- LEFT : HEADER -->
			<div class="card">
				<div class="card-body">
					<div class="table-wrap">
						<table id="headerTable">
							<thead>
								<tr>
									<th>번호</th>
									<th>정산사업장</th>
									<th>사원</th>
									<th>사번</th>
									<th>부서</th>
									<th>세금 적용 구분</th>
									<th>세금 적용 결과</th>
									<th>확정 여부</th>
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

			<!-- RIGHT : DETAIL + 산출근거 -->
			<div class="card">
<div class="card-header">
  <div class="right-toolbar">
    <span class="muted">사원</span> 
    <input id="basisEmpName" type="text" placeholder="사원명" readonly style="width:100px;">
    <input id="basisEmpNo" type="text" placeholder="사번" readonly style="width:100px;">
    <button id="btnBasis">산출근거</button>
  </div>
</div>

				<div class="card-body">
					<div class="table-wrap">
						<table id="detailTable">
							<thead>
								<tr>
									<th>정산항목분류</th>
									<th>정산항목</th>
									<th>금액</th>
									<th>예상금액</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td colspan="4" style="text-align: center; color: #666;">대상
										행을 더블클릭하세요</td>
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

  // 헤더 더블클릭 → detail 조회 + 산출근거 입력 채움
document.querySelector('#headerTable tbody').addEventListener('dblclick', function(e){
  const tr = e.target.closest('tr'); 
  if(!tr) return;

  const yrtId = tr.dataset.yrtId;
  if(!yrtId) return;

  const empName = tr.children[2].innerText; // 사원명
  const empNo   = tr.children[3].innerText; // 사번

  document.querySelectorAll('#headerTable tbody tr').forEach(r=>r.classList.remove('active'));
  tr.classList.add('active');

  // 입력창 채우기
  document.getElementById('basisEmpName').value = empName;
  document.getElementById('basisEmpNo').value   = empNo;

  loadDetail(yrtId);
});


  // 산출근거 버튼
  document.getElementById('btnBasis').addEventListener('click', function(){
    if(!selected.yrtId){
      alert('왼쪽 헤더에서 행을 먼저 더블클릭해 주세요.');
      return;
    }
    // 실제 산출근거 URL은 프로젝트에 맞게 수정
    const url = '/yearend/basis?yrtId=' + encodeURIComponent(selected.yrtId);
    window.open(url, '_blank'); // 새 탭/창으로 열기
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
	
  //detail 가져오기
  function renderDetail(rows){
    const tb = document.querySelector('#detailTable tbody');
    tb.innerHTML = '';
    if(!rows || rows.length === 0){
      tb.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#666;">데이터 없음</td></tr>';
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
  
  // 공통 유틸(정렬 등) 활성화
  enableSort('#headerTable');
  enableSort('#detailTable');
})();

//검색 기능
function searchHeader(){
	  const dept = document.getElementById('searchDept').value;
	  const emp  = document.getElementById('searchEmp').value;

	  const params = new URLSearchParams();
	  if (dept) params.append('deptName', dept);
	  if (emp)  params.append('empName', emp);

	  window.location.href = '/yearend/list?' + params.toString();
	}
</script>
</body>
</html>
