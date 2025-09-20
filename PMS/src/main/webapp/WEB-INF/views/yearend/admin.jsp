<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../includes/commonform.jsp"%>
<%@ include file="../includes/table.jsp"%>

<html>
<head>
<title>연말정산 처리/신고</title>
<style>
  :root { --gap: 12px; --border: #e5e7eb; --header:#f8fafc; }
  .toolbar { display:flex; gap:8px; align-items:center; margin: 8px 0 12px; }
  .filters  { display:flex; gap:10px; align-items:center; margin: 12px 0; }
  .filters input, .filters select { padding:6px 8px; border:1px solid var(--border); border-radius:8px; }
  button     { padding:8px 12px; border:1px solid #cbd5e1; background:#f1f5f9; border-radius:8px; cursor:pointer; }
  button.primary { background:#2563eb; color:#fff; border-color:#2563eb; }

  /* 테이블을 엑셀 표처럼 컴팩트하게 */
  table { border-collapse: collapse; width: 100%; font-size: 13px; table-layout: fixed; }
  thead th { position: sticky; top: 0; background: var(--header); z-index: 1; }
  th, td { border: 1px solid #e5e7eb; padding: 4px 6px; line-height: 1.2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
  /* 열 폭 – 너무 넓지 않게 기본값 지정(필요시 조정) */
  .col-select{ width: 48px;  text-align:center; }
  .col-file  { width: 64px;  text-align:center; }
  .col-name  { width: 90px; }
  .col-no    { width: 90px; }
  .col-dept  { width: 110px; }
  .col-biz   { width: 110px; }
  .col-drop  { width: 120px; }
  .col-chk   { width: 64px;  text-align:center; }
  .col-result{ width: 120px; text-align:center; }
  .sticky-wrap { height: calc(100vh - 220px); overflow: auto; border:1px solid var(--border); border-radius: 8px; }

  /* 행 하이라이트 */
  #adminTable tbody tr.active { background:#eef6ff; }

  /* 읽기전용처럼 보이도록 */
  input[disabled] { filter: grayscale(0.2); cursor: not-allowed; }
</style>
</head>
<body>

<header><h2>연말정산 처리/신고</h2></header>

<div class="filters">
  <label><b>정산연도</b></label>
  <input id="searchYear" type="text" value="${empty cond.baseYear ? '2025' : cond.baseYear}">
  <label><b>정산사업장</b></label>
  <select id="searchBizPlace">
    <option value="">전체</option>
    <option value="본사" ${cond.bizPlace=='본사' ? 'selected' : ''}>본사</option>
  </select>
  <label>부서</label><input id="searchDept" type="text" value="${cond.deptName}">
  <label>사원</label><input id="searchEmp" type="text" value="${cond.empName}">
  <button class="primary" id="btnQuery">조회</button>
</div>

<div class="toolbar">
  <button id="btnSettle"       class="primary">정산처리</button>
  <button id="btnUnsettle">정산결과삭제</button>
  <button id="btnPenalty">납부특례세액반영</button>
</div>

<div class="sticky-wrap">
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
        <th class="col-chk">납부특례<br>세액반영</th>
        <th class="col-chk">거주자<br>여부</th>
        <th class="col-chk">담당자<br>마감</th>
        <th class="col-result">세금 적용<br>구분결과</th>
        <th class="col-chk">개인<br>마감</th>
        <th class="col-chk">확정</th>
        <th class="col-chk">인정공제<br>반영</th>
        <th class="col-chk">정산신고<br>제외</th>
        <th class="col-chk">건강보험<br>PDF반영</th>
        <th class="col-chk">국민연금<br>PDF반영</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="h" items="${list}">
        <tr data-yrt-id="${h.yrtId}">
          <!-- 선택/파일생성 -->
          <td class="col-select"><input type="checkbox" data-col="select"></td>
          <td class="col-file"><input type="checkbox" data-col="file"></td>

          <!-- 인적정보 -->
          <td class="col-name">${h.empName}</td>
          <td class="col-no">${h.empNo}</td>
          <td class="col-dept">${h.deptName}</td>
          <td class="col-biz">${h.bizPlace}</td>

          <!-- 세금 적용 구분: 기본(비교) 디폴트 -->
          <td class="col-drop">
            <select data-col="applyType">
              <option value="기본(비교)" selected>기본(비교)</option>
              <option value="표준세액공제">표준세액공제</option>
              <option value="특례">특례</option>
            </select>
          </td>

          <!-- 버튼으로만 토글되는 체크들 -->
          <td class="col-chk"><input type="checkbox" data-col="settle"></td>
          <td class="col-chk"><input type="checkbox" data-col="penalty" disabled></td>

          <!-- 디폴트 고정값(수정불가) -->
          <td class="col-chk"><input type="checkbox" data-col="resident" checked disabled></td>

          <!-- 직접 수정 가능 -->
          <td class="col-chk"><input type="checkbox" data-col="staffClosed"></td>

          <!-- 결과 고정 표기 -->
          <td class="col-result"><input type="text" value="표준세액공제" readonly style="width:100%;border:none;background:transparent;text-align:center;"></td>

          <!-- 디폴트 체크(수정불가) -->
          <td class="col-chk"><input type="checkbox" data-col="personalClosed" checked disabled></td>

          <!-- 직접 수정 가능 -->
          <td class="col-chk"><input type="checkbox" data-col="confirm"></td>

          <!-- 디폴트 해제(수정불가) -->
          <td class="col-chk"><input type="checkbox" data-col="allowanceApplied" disabled></td>

          <!-- 직접 수정 가능 -->
          <td class="col-chk"><input type="checkbox" data-col="excludeSubmit"></td>

          <!-- 디폴트 해제(수정불가) -->
          <td class="col-chk"><input type="checkbox" data-col="hiPdf" disabled></td>
          <td class="col-chk"><input type="checkbox" data-col="npPdf" disabled></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</div>

<script>
/* ===================== 공통 유틸 ===================== */
function getSelectedRows(){
  const rows=[];
  document.querySelectorAll('#adminTable tbody tr').forEach(tr=>{
    const cb=tr.querySelector('input[type=checkbox][data-col="select"]');
    if(cb && cb.checked) rows.push(tr);
  });
  return rows;
}

/* ===================== 버튼 액션 ===================== */
document.getElementById('btnSettle').addEventListener('click', ()=>{
  const rows = getSelectedRows();
  if(rows.length===0){ alert('선택된 사원이 없습니다.'); return; }
  rows.forEach(tr=>{
    tr.querySelector('input[data-col="settle"]').checked = true;    // 정산처리 체크
    // 필요하면 행 하이라이트
    tr.classList.add('active');
    setTimeout(()=>tr.classList.remove('active'), 300);
  });
});

document.getElementById('btnUnsettle').addEventListener('click', ()=>{
  const rows = getSelectedRows();
  if(rows.length===0){ alert('선택된 사원이 없습니다.'); return; }
  rows.forEach(tr=>{
    tr.querySelector('input[data-col="settle"]').checked = false;   // 정산처리 해제
    tr.classList.add('active');
    setTimeout(()=>tr.classList.remove('active'), 300);
  });
});

document.getElementById('btnPenalty').addEventListener('click', ()=>{
  const rows = getSelectedRows();
  if(rows.length===0){ alert('선택된 사원이 없습니다.'); return; }
  rows.forEach(tr=>{
    const cb = tr.querySelector('input[data-col="penalty"]');
    cb.checked = true;   // 수정불가지만 프로그램으로는 체크 가능
    tr.classList.add('active');
    setTimeout(()=>tr.classList.remove('active'), 300);
  });
});

/* 조회 버튼 (지금은 단순 페이지 이동 – 필요 시 실제 파라미터 연결) */
document.getElementById('btnQuery').addEventListener('click', ()=>{
  const year = document.getElementById('searchYear').value;
  const biz  = document.getElementById('searchBizPlace').value;
  const dept = document.getElementById('searchDept').value;
  const emp  = document.getElementById('searchEmp').value;

  const q = new URLSearchParams();
  if(year) q.append('baseYear', year);
  if(biz)  q.append('bizPlace', biz);
  if(dept) q.append('deptName', dept);
  if(emp)  q.append('empName', emp);

  location.href = '/yearend/admin/list?' + q.toString();
});
</script>
</body>
</html>
