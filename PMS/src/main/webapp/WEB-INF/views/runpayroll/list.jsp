<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급상여 처리</title>
<style>
  :root{--gap:12px;--radius:10px;--border:#e5e7eb;--bg:#f8fafc;--text:#111827;--muted:#6b7280;--total:#eaf7ea}
  *{box-sizing:border-box}
  body{margin:0;font-family:system-ui,-apple-system,"Segoe UI",Roboto,"Noto Sans KR","Apple SD Gothic Neo",Arial,"맑은 고딕",sans-serif;color:var(--text);background:#f8fafc}
  header{padding:16px 20px;background:#fff;border-bottom:1px solid var(--border);position:sticky;top:0;z-index:5}
  .container{padding:16px 20px}
  .row{display:grid;grid-template-columns:2fr 1fr;gap:12px;align-items:start}
  .card{background:#fff;border:1px solid var(--border);border-radius:10px;overflow:hidden}
  .card>.card-header{padding:12px 14px;border-bottom:1px solid var(--border);font-weight:600;display:flex;justify-content:space-between;align-items:center;gap:8px}
  .card>.card-body{padding:12px 14px}
  .filters{display:flex;gap:8px;align-items:center;flex-wrap:wrap}
  .filters input[type="month"],.filters select,.filters input[type="text"]{padding:8px 10px;border:1px solid var(--border);border-radius:8px}
  .filters button{padding:8px 12px;border:1px solid #0ea5e9;background:#0ea5e9;color:#fff;border-radius:8px;cursor:pointer}
  .filters button.secondary{background:#fff;color:#0ea5e9}
  .actions{display:flex;gap:8px;align-items:center;flex-wrap:wrap}
  .actions .btn{padding:6px 10px;border:1px solid #334155;background:#334155;color:#fff;border-radius:8px;cursor:pointer;font-size:12px}
  .actions .btn.alt{background:#fff;color:#334155}
  .actions .sep{width:1px;height:24px;background:#e5e7eb;margin:0 2px}
  .chk{display:inline-flex;align-items:center;gap:6px;font-size:12px}
  .table-wrap{overflow:auto;border:1px solid var(--border);border-radius:8px}
  table{border-collapse:collapse;width:100%;min-width:1760px}
  thead th{position:sticky;top:0;background:#f1f5f9;border-bottom:1px solid var(--border);font-weight:600;white-space:nowrap}
  th,td{border-bottom:1px solid var(--border);padding:8px 10px;text-align:left;font-size:13px}
  tbody tr:hover{background:#f8fafc}
  tbody tr.active{background:#e0f2fe}
  .muted{color:#6b7280}
  .grid-2{display:grid;grid-template-columns:1fr;gap:12px}
  .list{border:1px solid var(--border);border-radius:8px;overflow:hidden}
  .pill{display:inline-block;padding:2px 8px;font-size:12px;border-radius:999px;border:1px solid var(--border);background:#f8fafc}
  .right-top{display:flex;justify-content:space-between;align-items:center;gap:6px}
  .ghost{opacity:.5}
  .row-total{background:#eaf7ea!important;font-weight:600}
  .right-tables table{min-width:100%}
</style>
</head>
<body>
<header>
  <div class="filters">
    <div class="muted">급여조회</div>
    <label>지급연월 <input id="yyyymm" type="month" value="2018-08"></label>
    <label>급여유형
      <select id="payType">
        <option value="">전체</option>
        <option value="SALARY" selected>SALARY</option>
        <option value="BONUS">BONUS</option>
      </select>
    </label>
    <label>부서코드 <input id="deptCode" type="text" placeholder="예: D004" style="width:110px"></label>
    <label>사번 <input id="empNo" type="text" placeholder="예: 20131201" style="width:130px"></label>
    <button id="btnSearch">조회</button>
    <button id="btnReset" class="secondary">초기화</button>
    <span id="summaryCount" class="muted" style="margin-left:8px;"></span>
  </div>
</header>

<div class="container">
  <div class="row">
    <!-- 좌측 요약 -->
    <div class="card">
      <div class="card-header">
        <span>사원별 급여 요약</span>
        <div class="actions">
          <label class="chk"><input type="checkbox" id="chkAll"> 전체선택</label>
          <span class="sep"></span>
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
                <th style="width:36px;"><!-- row checkbox -->
                  <input type="checkbox" id="chkAllHeader">
                </th>
                <th>사번</th><th>사원</th><th>부서</th><th>세금적용</th>
                <th>세액조정율</th><th>프로젝트</th>
                <th>세금계산안함</th><th>일할계산</th><th>정산반영</th>
                <th>생산직비과세</th><th>국외근로비과세</th><th>연구원비과세</th>
                <th>소득세감면율</th><th>세금적용(개인)</th><th>상여율</th>
                <th>지급총액</th><th>기지급총액</th><th>공제총액</th><th>실지급액</th><th>퇴직여부</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
        <div class="muted" style="margin-top:6px;">행을 클릭하면 우측에 지급/공제 항목이 표시됩니다. (체크박스로 여러 명 선택 가능)</div>
      </div>
    </div>

    <!-- 우측 상세 -->
    <div class="card">
      <div class="card-header">지급/공제 상세</div>
      <div class="card-body right-tables">
        <div class="right-top">
          <div class="title">
            선택사번: <span id="selEmpNo" class="pill ghost">-</span>
            <span id="selEmpName" class="pill ghost">-</span>
          </div>
          <div class="muted">조회연월: <span id="selYyyymm">-</span> / 유형: <span id="selPayType">-</span></div>
        </div>

        <div class="grid-2" style="margin-top:10px;">
          <div class="list">
            <div class="card-header">지급항목</div>
            <div class="card-body" style="padding:0;">
              <table id="tblItems">
                <thead>
                  <tr><th>지급항목</th><th>비과세유형</th><th>기지급</th><th style="text-align:right;">금액</th></tr>
                </thead>
                <tbody></tbody>
              </table>
            </div>
          </div>

          <div class="list">
            <div class="card-header">공제항목</div>
            <div class="card-body" style="padding:0;">
              <table id="tblDeds">
                <thead>
                  <tr><th>공제항목</th><th style="text-align:right;">금액</th></tr>
                </thead>
                <tbody></tbody>
              </table>
            </div>
          </div>
        </div>

      </div>
    </div><!-- /right -->
  </div>
</div>

<%-- API URL --%>
<c:url var="summaryUrl"    value="/runpayroll/api/summary"/>
<c:url var="itemsUrl"      value="/runpayroll/api/items"/>
<c:url var="deductionsUrl" value="/runpayroll/api/deductions"/>
<c:url var="processUrl"    value="/runpayroll/api/process"/>
<c:url var="retaxUrl"      value="/runpayroll/api/recalc-taxes"/>
<c:url var="confirmUrl"    value="/runpayroll/api/confirm"/>
<c:url var="applyYrtUrl"   value="/runpayroll/api/apply-yrt"/>

<script>
  // helpers
  var $  = function(s, root){ return (root||document).querySelector(s); };
  var $$ = function(s, root){ return Array.prototype.slice.call((root||document).querySelectorAll(s)); };
  var fmt= function(n){ return (n==null||n==='') ? '' : Number(n).toLocaleString('ko-KR'); };
  var yn = function(v){ return (v==='Y'||v==='N') ? v : (v ? 'Y' : 'N'); };
  function post(url, body){
    return fetch(url,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)}).then(function(r){return r.json()});
  }

  var urls = {
    summary:'${summaryUrl}', items:'${itemsUrl}', deductions:'${deductionsUrl}',
    process:'${processUrl}', retax:'${retaxUrl}', confirm:'${confirmUrl}', applyYrt:'${applyYrtUrl}'
  };

  var state = { yyyymm:$('#yyyymm').value||'2018-08', payType:$('#payType').value||'SALARY', deptCode:'', empNo:'', rows:[], selected:null };

  $('#btnSearch').addEventListener('click', function(){
    state.yyyymm  = $('#yyyymm').value || '2018-08';
    state.payType = $('#payType').value || '';
    state.deptCode= $('#deptCode').value.trim();
    state.empNo   = $('#empNo').value.trim();
    loadSummary();
  });

  $('#btnReset').addEventListener('click', function(){
    $('#yyyymm').value='2018-08'; $('#payType').value='SALARY'; $('#deptCode').value=''; $('#empNo').value='';
    state = { yyyymm:'2018-08', payType:'SALARY', deptCode:'', empNo:'', rows:[], selected:null };
    loadSummary();
  });

  // 전체선택 동기화
  $('#chkAll').addEventListener('change', function(e){
    var checked = e.target.checked;
    $$('#tblSummary tbody input[type="checkbox"]').forEach(function(cb){ cb.checked = checked; });
    $('#chkAllHeader').checked = checked;
  });
  $('#chkAllHeader').addEventListener('change', function(e){
    var checked = e.target.checked;
    $$('#tblSummary tbody input[type="checkbox"]').forEach(function(cb){ cb.checked = checked; });
    $('#chkAll').checked = checked;
  });

  function loadSummary(){
    var p = new URLSearchParams();
    p.set('yyyymm', state.yyyymm);
    if (state.payType) p.set('payType', state.payType);
    if (state.deptCode) p.set('deptCode', state.deptCode);
    if (state.empNo) p.set('empNo', state.empNo);

    fetch(urls.summary + '?' + p.toString(), { headers:{'Accept':'application/json'} })
      .then(function(res){ if(!res.ok) throw new Error('요약 조회 실패'); return res.json(); })
      .then(function(data){
        state.rows = Array.isArray(data)? data : [];
        $('#summaryCount').textContent = '총 ' + state.rows.length + ' 건';
        renderSummary();
        $('#selYyyymm').textContent = state.yyyymm;
        $('#selPayType').textContent = state.payType || 'ALL';
      })
      .catch(function(e){ alert(e.message); });
  }

  function renderSummary(){
    var tbody = $('#tblSummary tbody');
    tbody.innerHTML = '';
    state.rows.forEach(function(r, idx){
      var tr = document.createElement('tr');
      tr.dataset.empNo = r.empNo;

      // 체크박스 셀 + 나머지
      var html = '';
      html += '<td><input type="checkbox" class="rowchk" data-empno="' + (r.empNo||'') + '"></td>';
      html += '<td>' + (r.empNo||'') + '</td>';
      html += '<td>' + (r.empName||'') + '</td>';
      html += '<td>' + (r.deptName||'') + '</td>';
      html += '<td>' + (r.taxApplyType||'') + '</td>';
      html += '<td style="text-align:right;">' + (r.taxAdjustRate!=null?r.taxAdjustRate:'') + '</td>';
      html += '<td>' + (r.projectName||'') + '</td>';
      html += '<td>' + yn(r.taxCalcExemptYn) + '</td>';
      html += '<td>' + yn(r.prorateYn) + '</td>';
      html += '<td>' + yn(r.settlementReflectYn) + '</td>';
      html += '<td>' + yn(r.manufTaxExemptYn) + '</td>';
      html += '<td>' + yn(r.overseasTaxExemptYn) + '</td>';
      html += '<td>' + yn(r.researcherTaxExemptYn) + '</td>';
      html += '<td style="text-align:right;">' + (r.incomeTaxReductionRate!=null?r.incomeTaxReductionRate:'') + '</td>';
      html += '<td>' + (r.personalTaxApplyType||'') + '</td>';
      html += '<td style="text-align:right;">' + (r.bonusRate!=null?r.bonusRate:'') + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.payTotAmt) + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.prevPayTotAmt) + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.dedTotAmt) + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.netPayAmt) + '</td>';
      html += '<td>' + (r.retireYn||'') + '</td>';

      tr.innerHTML = html;

      // 행 클릭 시 상세 로딩
      tr.addEventListener('click', function(e){
        // 체크박스 클릭은 행 선택 이벤트와 분리
        if (e.target && e.target.classList && e.target.classList.contains('rowchk')) return;
        $$('#tblSummary tbody tr').forEach(function(x){ x.classList.remove('active'); });
        tr.classList.add('active');
        onSelectRow(r);
      });

      // 체크박스 개별 변경 시 전체선택 상태 갱신
      tr.addEventListener('change', function(e){
        if (e.target && e.target.classList && e.target.classList.contains('rowchk')) {
          var all = $$('#tblSummary tbody .rowchk');
          var checked = all.filter(function(c){return c.checked}).length;
          var allChecked = checked === all.length && all.length>0;
          $('#chkAll').checked = allChecked;
          $('#chkAllHeader').checked = allChecked;
        }
      });

      tbody.appendChild(tr);
      if (idx===0) { tr.classList.add('active'); onSelectRow(r); }
    });
  }

  function onSelectRow(row){
    state.selected = row;
    $('#selEmpNo').textContent = row.empNo || '-';
    $('#selEmpNo').classList.remove('ghost');
    $('#selEmpName').textContent = row.empName || '-';
    $('#selEmpName').classList.remove('ghost');
    Promise.all([loadItems(row.empNo), loadDeds(row.empNo)]).catch(function(){});
  }

  function loadItems(empNo){
    var p = new URLSearchParams();
    p.set('empNo', empNo);
    p.set('yyyymm', state.yyyymm);
    if (state.payType) p.set('payType', state.payType);

    return fetch(urls.items + '?' + p.toString(), { headers:{'Accept':'application/json'} })
      .then(function(res){ return res.ok ? res.json() : []; })
      .then(function(list){
        var tbody = $('#tblItems tbody'); tbody.innerHTML = '';
        (Array.isArray(list)?list:[]).forEach(function(r){
          var tr = document.createElement('tr');
          if ((r.itemName||'') === 'TOTAL') { tr.className='row-total'; }
          var html = '';
          html += '<td>' + (r.itemName||'') + '</td>';
          html += '<td>' + (r.nonTaxType||'') + '</td>';
          html += '<td>' + (r.previousYn||'') + '</td>';
          html += '<td style="text-align:right;">' + fmt(r.amount) + '</td>';
          tr.innerHTML = html;
          tbody.appendChild(tr);
        });
      });
  }

  function loadDeds(empNo){
    var p = new URLSearchParams();
    p.set('empNo', empNo);
    p.set('yyyymm', state.yyyymm);
    if (state.payType) p.set('payType', state.payType);

    return fetch(urls.deductions + '?' + p.toString(), { headers:{'Accept':'application/json'} })
      .then(function(res){ return res.ok ? res.json() : []; })
      .then(function(list){
        var tbody = $('#tblDeds tbody'); tbody.innerHTML = '';
        (Array.isArray(list)?list:[]).forEach(function(r){
          var tr = document.createElement('tr');
          if ((r.deductionName||'') === 'TOTAL') { tr.className='row-total'; }
          var html = '';
          html += '<td>' + (r.deductionName||'') + '</td>';
          html += '<td style="text-align:right;">' + fmt(r.amount) + '</td>';
          tr.innerHTML = html;
          tbody.appendChild(tr);
        });
      });
  }

  function selectedEmpNos(){
    return $$('#tblSummary tbody .rowchk')
            .filter(function(cb){ return cb.checked; })
            .map(function(cb){ return cb.getAttribute('data-empno'); });
  }

  // 액션 버튼들 (전표처리 제거됨)
  $('#btnProcess').addEventListener('click', function(){
    var empNos = selectedEmpNos();
    if (empNos.length===0){ alert('대상 사원을 선택하세요.'); return; }
    post('${processUrl}', { yyyymm: state.yyyymm, payType: state.payType, empNos: empNos })
      .then(function(){ loadSummary(); });
  });

  $('#btnReTax').addEventListener('click', function(){
    var empNos = selectedEmpNos();
    if (empNos.length===0){ alert('대상 사원을 선택하세요.'); return; }
    post('${retaxUrl}', { yyyymm: state.yyyymm, payType: state.payType, empNos: empNos })
      .then(function(){
        // 활성 행 기준 우측 패널 갱신
        if (state.selected && state.selected.empNo) {
          loadItems(state.selected.empNo); loadDeds(state.selected.empNo);
        }
        loadSummary();
      });
  });

  $('#btnApplyYrt').addEventListener('click', function(){
    var empNos = selectedEmpNos();
    if (empNos.length===0){ alert('대상 사원을 선택하세요.'); return; }
    post('${applyYrtUrl}', { yyyymm: state.yyyymm, empNos: empNos, splitMonths: 1 })
      .then(function(){
        if (state.selected && state.selected.empNo) {
          loadItems(state.selected.empNo); loadDeds(state.selected.empNo);
        }
        loadSummary();
      });
  });

  $('#btnConfirm').addEventListener('click', function(){
    var empNos = selectedEmpNos();
    if (empNos.length===0){ alert('대상 사원을 선택하세요.'); return; }
    post('${confirmUrl}', { yyyymm: state.yyyymm, payType: state.payType, empNos: empNos, confirm: true })
      .then(function(){ loadSummary(); });
  });

  $('#btnUnconfirm').addEventListener('click', function(){
    var empNos = selectedEmpNos();
    if (empNos.length===0){ alert('대상 사원을 선택하세요.'); return; }
    post('${confirmUrl}', { yyyymm: state.yyyymm, payType: state.payType, empNos: empNos, confirm: false })
      .then(function(){ loadSummary(); });
  });

  // 초기 로드
  window.addEventListener('DOMContentLoaded', loadSummary);
</script>
</body>
</html>
