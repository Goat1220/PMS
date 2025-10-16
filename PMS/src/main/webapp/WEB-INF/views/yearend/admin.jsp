<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../includes/commonform.jsp"%>
<%@ include file="../includes/table.jsp"%>

<html>
<head>
<title>연말정산 처리/신고</title>
<style>
body {
	font-size: 12px;
}

button, select, input {
	font-size: 12px;
	min-width: 60px;
}

button {
	white-space: nowrap;
}

select {
	
}

/* ====== layout ====== */
.layout {
	display: grid;
	grid-template-columns: 1fr;
	gap: var(--gap);
	height: calc(100vh - 180px)
}

.layout .card {
	display: flex;
	flex-direction: column;
	min-width: 0
}

.left-body {
	flex: 1;
	min-height: 0;
	display: flex;
	flex-direction: column;
	gap: 4px
}

.left-wrap {
	height: 100%;
	overflow: auto;
	border: 1px solid var(--border);
	border-radius: 8px;
	background: #fff
}

/* 표 */
#adminTable {
width: 100%;
	min-width: 1200px;
	table-layout: fixed
}

#adminTable thead th {
	position: sticky;
	top: 0;
	background: #f1f5f9;
	z-index: 1
}

#adminTable th, #adminTable td {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	padding: 4px 6px
}

.col-select {
	width: 48px;
	text-align: center
}

.col-file {
	width: 64px;
	text-align: center
}

.col-name {
	width: 90px
}

.col-no {
	width: 95px
}

.col-dept {
	width: 110px
}

.col-biz {
	width: 100px
}

.col-drop {
	width: 160px
}

.col-chk {
	width: 64px;
	text-align: center
}

.col-result {
	width: 130px;
	text-align: center
}

#adminTable tbody tr.active {
	background: #eef6ff
}

/* 오른쪽 패널 */
.panel {
	border: 1px solid var(- -border);
	border-radius: var(- -radius);
	padding: 4px;
	background: #fff
}

.panel .title {
	font-weight: 700;
	margin-bottom: 8px;
	display: flex;
	gap: 4px;
	align-items: center
}

.form-grid {
	display: grid;
	grid-template-columns: auto;
	column-gap: 6px; /* 좌우 간격 최소 */
	row-gap: 4px; /* 위아래 간격 최소 */
	align-items: center;
}

.form-grid.report {
	display: grid;
	grid-template-columns: 60px 170px 70px 160px 40px 60px;
	column-gap: 6px; /* 좌우 간격 최소 */
	row-gap: 4px; /* 위아래 간격 최소 */
	align-items: center;
	justify-items: start;
}

.form-grid>* {
	min-width: 0
}

/* 라벨 왼쪽 정렬 */
.label {
	text-align: left;
	white-space: nowrap;
}

/* 출력구분+버튼 한 줄 전체 차지 */
.form-grid .span-all {
	grid-column: 2/7;
}

/* 버튼 줄바꿈 방지 */
.inline-join {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	white-space: nowrap;
	flex-wrap: nowrap;
}

.inline-join>* {
	flex: 0 0 auto; /* shrink 금지 */
}

/* 컨트롤 높이 통일 */
.panel input[type="text"], .panel select, .panel button {
	height: 26px;
	line-height: 24px;
	padding: 2px 6px;
}

.muted {
	color: #94a3b8
}
</style>
</head>
<body>

	<header>
		<h2>연말정산 처리/신고</h2>
	</header>

	<div class="container">

		<!-- 검색바 -->
		<div class="filters" style="margin: 10px 0 12px">
  <!-- 1행 -->
  <div style="display:flex;gap:6px;align-items:center;flex-wrap:wrap;width:100%">
    <label><b>정산연도</b></label>
    <input id="searchYear" type="text" value="${empty cond.baseYear ? '2018' : cond.baseYear}">

    <label><b>정산사업장</b></label>
    <select id="searchBizPlace">
      <option value="">전체</option>
      <option value="본사" ${cond.bizPlace=='본사' ? 'selected' : ''}>본사</option>
    </select>

    <button id="btnAllSettle">대상자전체정산처리</button>
    <button id="btnQuery">조회</button>
  </div>

  <!-- 2행 -->
  <div style="display:flex;gap:6px;align-items:center;flex-wrap:wrap;width:100%;margin-top:6px">
    <label>부서</label>
    <input id="searchDept" type="text" value="${cond.deptName}">

    <label>사원</label>
    <input id="searchEmp" type="text" value="${cond.empName}">

    <button id="btnSettle">정산처리</button>
    <button class="secondary" id="btnUnsettle">정산결과삭제</button>
    <button class="secondary" id="btnPenalty">납부특례세액반영</button>
    <button class="secondary" onclick="exportTableToExcel('#adminTable','연말정산_처리목록.csv')">엑셀</button>
  </div>
</div>

		<div class="layout">
			<!-- ============ LEFT: 목록 테이블 ============ -->
			<div class="card">
				<div class="card-body left-body">
					<div class="left-wrap">
						<table id="adminTable">
							<thead>
								<tr>
									<th class="col-select">선택</th>
									<th class="col-file">파일생성</th>
									<th class="col-name">사원</th>
									<th class="col-no">사번</th>
									<th class="col-dept">부서</th>
									<th class="col-biz">정산사업장</th>
									<th class="col-drop">세금 적용 구분</th>
									<th class="col-chk">정산처리</th>
									<th class="col-chk">납부특례<br>세액반영
									</th>
									<th class="col-chk">거주자<br>여부
									</th>
									<th class="col-chk">담당자<br>마감
									</th>
									<th class="col-result">세금 적용<br>구분결과
									</th>
									<th class="col-chk">개인<br>마감
									</th>
									<th class="col-chk">확정</th>
									<th class="col-chk">인정공제<br>반영
									</th>
									<th class="col-chk">정산신고<br>제외
									</th>
									<th class="col-chk">건강보험<br>PDF반영
									</th>
									<th class="col-chk">국민연금<br>PDF반영
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="h" items="${list}">
									<tr data-yrt-id="${h.yrtId}">
										<td class="col-select"><input type="checkbox"
											data-col="select"></td>
										<td class="col-file"><input type="checkbox"
											data-col="file"></td>

										<td class="col-name">${h.empName}</td>
										<td class="col-no">${h.empNo}</td>
										<td class="col-dept">${h.deptName}</td>
										<td class="col-biz">${h.bizPlace}</td>

										<td class="col-drop"><select data-col="applyType">
												<option value="기본(비교)" selected>기본(비교)</option>
												<option value="일반(간이세액)">일반(간이세액)</option>
												<option value="표준세액공제">표준세액공제</option>
												<option value="단일세율">단일세율</option>
												<option value="단일세율-분리과세">단일세율-분리과세</option>
												<option value="간이세액-기본공제없음">간이세액-기본공제없음</option>
												<option value="누진공제없음">누진공제없음</option>
												<option value="임의세율">임의세율</option>
												<option value="강제정산">강제정산</option>
										</select></td>

										<!-- 버튼으로 토글되는 항목 -->
										<td class="col-chk"><input type="checkbox"
											data-col="settle" disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="penalty" disabled></td>

										<!-- 디폴트 고정/편집 가능 -->
										<td class="col-chk"><input type="checkbox"
											data-col="resident" checked disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="staffClosed"></td>

										<td class="col-result"><input type="text" value="표준세액공제"
											readonly
											style="width: 100%; border: none; background: transparent; text-align: center;">
										</td>

										<td class="col-chk"><input type="checkbox"
											data-col="personalClosed" checked disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="confirm"></td>
										<td class="col-chk"><input type="checkbox"
											data-col="allowanceApplied" disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="excludeSubmit"></td>
										<td class="col-chk"><input type="checkbox"
											data-col="hiPdf" disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="npPdf" disabled></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

					<!-- include(table.jsp)의 기능 실제 호출 -->
					<script>
          enableSort('#adminTable');       // 헤더 클릭 정렬
          enablePaging('#adminTable', 20); // 20행씩 페이징
        </script>
				</div>
			</div>

		</div>
	</div>

	<script>

  /* 선택된 행 수집 */
   function getSelectedRows(){
    const rows=[];
    document.querySelectorAll('#adminTable tbody tr').forEach(tr=>{
      const cb=tr.querySelector('input[type=checkbox][data-col="select"]');
      if(cb && cb.checked) rows.push(tr);
    });
    return rows;
  } 

  /* 상단 버튼 동작 */
document.getElementById('btnAllSettle').addEventListener('click', ()=>{
  const rows = document.querySelectorAll('#adminTable tbody tr');
  if(!rows.length){ alert('처리할 데이터가 없습니다.'); return; }

  rows.forEach(tr=>{
    const cb = tr.querySelector('input[type=checkbox][data-col="settle"]');
    if(cb) cb.checked = true;
    tr.classList.add('active');
    setTimeout(()=>tr.classList.remove('active'),300);
  });
});
  document.getElementById('btnSettle').addEventListener('click', ()=>{
    const rows=getSelectedRows(); if(!rows.length){alert('선택된 사원이 없습니다.'); return;}
    rows.forEach(tr=>{
      tr.querySelector('input[data-col="settle"]').checked=true;
      tr.classList.add('active'); setTimeout(()=>tr.classList.remove('active'),300);
    });
  });
  document.getElementById('btnUnsettle').addEventListener('click', ()=>{
    const rows=getSelectedRows(); if(!rows.length){alert('선택된 사원이 없습니다.'); return;}
    rows.forEach(tr=>{
      tr.querySelector('input[data-col="settle"]').checked=false;
      tr.classList.add('active'); setTimeout(()=>tr.classList.remove('active'),300);
    });
  }); 
  document.getElementById('btnPenalty').addEventListener('click', ()=>{
    const rows=getSelectedRows(); if(!rows.length){alert('선택된 사원이 없습니다.'); return;}
    rows.forEach(tr=>{
      tr.querySelector('input[data-col="penalty"]').checked=true;
      tr.classList.add('active'); setTimeout(()=>tr.classList.remove('active'),300);
    });
  });

  /* 조회 버튼 → 파라미터 구성해서 이동 */
  document.getElementById('btnQuery').addEventListener('click', ()=>{
    const year=document.getElementById('searchYear').value;
    const biz =document.getElementById('searchBizPlace').value;
    const dept=document.getElementById('searchDept').value;
    const emp =document.getElementById('searchEmp').value;

    const q=new URLSearchParams();
    if(year) q.append('baseYear',year);
    if(biz)  q.append('bizPlace',biz);
    if(dept) q.append('deptName',dept);
    if(emp)  q.append('empName',emp);

    location.href='/yearend/admin/list?'+q.toString();
  });
  
  // 1~12월 옵션 자동 생성
/*   function fillMonths(selectId) {
    const sel = document.getElementById(selectId);
    for (let m = 1; m <= 12; m++) {
      const opt = document.createElement("option");
      opt.value = m;
      opt.text = m + "월";
      sel.appendChild(opt);
    }
  }

</script>

</body>
</html>

