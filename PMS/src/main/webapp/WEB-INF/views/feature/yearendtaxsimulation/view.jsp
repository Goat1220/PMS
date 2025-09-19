<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>

<!-- ==================== 기본 스타일 ==================== -->
<style>
  html,body{
    margin:0; background:#ffffff; color:#222;
    font-family:system-ui, Segoe UI, Apple SD Gothic Neo, Malgun Gothic, sans-serif;
  }
  .container{ width:100%; max-width:1280px; margin:0 auto; padding:16px; }

  .page-title{ font-size:18px; font-weight:700; margin:6px 0 14px 0; }

  .searchbar{
    padding:10px 0; border-bottom:1px solid #e5e7eb; display:flex; flex-wrap:wrap; gap:10px; align-items:center;
  }
  .field{ display:flex; gap:6px; align-items:center; }
  .field input[type=text], .field select{
    height:30px; padding:0 8px; border:1px solid #d9dce3; border-radius:4px; background:#fff;
  }
  .w-yr{width:90px} .w-emp{width:120px} .w-mid{width:180px} .w-biz{width:140px}
  .spacer{ flex:1 1 auto; }
  .btn{
    height:32px; padding:0 12px; border:1px solid #d0d5dd; background:#fff; border-radius:4px; cursor:pointer;
  }
  .btn.primary{ background:#2f74ff; border-color:#2f74ff; color:#fff; font-weight:600; }
  .btn.alt{ background:#3d4f91; border-color:#3d4f91; color:#fff; }
  .btn.small{ height:30px; padding:0 10px; }

  .tabs{ display:flex; gap:6px; margin:12px 0 0 0; }
  .tab{ padding:6px 10px; border:1px solid #e5e7eb; background:#f6f7fb; border-bottom:none; border-radius:6px 6px 0 0; cursor:pointer; }
  .tab.active{ background:#2f74ff; border-color:#2f74ff; color:#fff; font-weight:600; }

  .panel{ border-top:1px solid #e5e7eb; padding:10px 0 18px 0; }
  .meta{ color:#777; font-size:12.5px; margin:6px 0 4px 0; }

  table.grid{ width:100%; border-collapse:collapse; margin-top:6px; }
  table.grid th, table.grid td{ border:1px solid #e5e7eb; padding:8px 10px; background:#fff; }
  table.grid th{ background:#f4f6fa; text-align:left; }
  td.right{text-align:right}

  /* ===== 요청 반영 커스터마이징 ===== */
  /* 정산연도 라벨만 빨강 */
  .field-year > span{ color:#e11d48; font-weight:700; }

  /* 읽기전용 회색 */
  .readonly-gray{
    background:#f3f4f6 !important;
    color:#777 !important;
    border-color:#e5e7eb !important;
  }

  /* 사원 입력 하늘색 래퍼 + 아이콘 버튼 */
  .input-wrap.sky{
    background:#e6f3ff; border:1px solid #b6d9ff; border-radius:4px;
    position:relative; display:inline-flex; align-items:center;
    height:30px; padding-right:28px;
  }
  .input-wrap.sky input{
    background:transparent !important; border:0 !important; outline:none;
    height:100%; padding:0 8px; width:180px;   /* 필요 시 너비 조정 */
  }
  .input-wrap.sky .icon-btn{
    position:absolute; right:6px; top:50%; transform:translateY(-50%);
    border:0; background:transparent; font-size:14px; opacity:.75;
    cursor:pointer; padding:0; width:22px; height:22px; line-height:22px;
  }
  .input-wrap.sky:focus-within{
    box-shadow:0 0 0 2px rgba(45,120,255,.15);
    border-color:#8fc0ff;
  }
</style>

<div class="container">

  <div class="page-title">연말정산시뮬레이션(개인원본)</div>

  <!-- 최신 실행 ID -->
  <input type="hidden" id="yrtId" value="${simHeader.yrtId}" />

  <!-- ==================== 조회 바 ==================== -->
  <div class="searchbar">
    <!-- 정산연도: 라벨 빨강, 입력 흰색 -->
    <div class="field field-year">
      <span>정산연도</span>
      <input id="baseYear" class="w-yr" type="text" value="${baseYear}" placeholder="YYYY">
    </div>

    <!-- 사원: 하늘색 입력(검색 버튼 클릭 훅만 제공) -->
    <div class="field">
      <span>사원</span>
      <span class="input-wrap sky">
        <input id="empName" type="text" value="${empName}" placeholder="사원 이름" aria-label="사원 이름">
        <button type="button" class="icon-btn" aria-label="사원 검색" onclick="onClickEmpSearch()">🔍</button>
      </span>
    </div>

    <!-- 사번: 자동표시(읽기전용 회색) -->
    <div class="field">
      <span>사번</span>
      <input id="empId" class="w-emp readonly-gray" type="text" value="${empId}" placeholder="사번" readonly>
    </div>

    <!-- 정산사업장: 회색 + 비활성 -->
    <div class="field">
      <span>정산사업장</span>
      <select id="bizPlace" class="w-biz readonly-gray" disabled>
        <option>본사</option>
      </select>
    </div>

    <!-- 체크박스: 비활성 -->
    <div class="field" style="gap:12px;color:#888;">
      <label><input type="checkbox" disabled> 개인마감</label>
      <label><input type="checkbox" disabled> 담당자마감</label>
      <label><input type="checkbox" checked disabled> 정산대상자</label>
    </div>

    <!-- 조회구분: 회색 + 비활성 -->
    <div class="field">
      <span>조회구분</span>
      <select id="searchType" class="w-emp readonly-gray" disabled>
        <option selected>정산</option>
      </select>
    </div>

    <!-- 세금적용결과: 읽기전용 회색 -->
    <div class="field">
      <span>세금적용결과</span>
      <input id="taxApplyResult" class="w-biz readonly-gray" type="text"
             value="${empty taxApplyResult ? '표준세액공제' : taxApplyResult}" readonly>
    </div>

    <div class="spacer"></div>

    <!-- 우측 버튼 -->
    <div class="field" style="gap:8px;">
      <button class="btn primary" onclick="onReason()">산출근거</button>
      <button class="btn alt" onclick="onSim()">정산시뮬레이션처리</button>
      <button class="btn alt" onclick="onDelete()">정산시뮬레이션결과삭제</button>
      <button class="btn alt" onclick="onInstallment()">납부특례세액시뮬레이션처리</button>
    </div>
  </div>

  <!-- ==================== 탭 ==================== -->
  <div class="tabs">
    <div id="tab-final" class="tab active" onclick="showTab('final')">최종</div>
    <div id="tab-sim"   class="tab" onclick="showTab('sim')">시뮬레이션</div>
  </div>

  <!-- ==================== 최종 탭 ==================== -->
  <div id="panel-final" class="panel">
    <div class="meta">
      실행라벨: <strong><c:out value="${simHeader != null ? simHeader.runLabel : '-'}"/></strong>
      · 기준연도: <strong><c:out value="${baseYear}"/></strong>
      · 생성/갱신: <c:out value="${simHeader != null ? simHeader.updatedAt : '-'}"/>
      · 확정여부: <c:out value="${simHeader != null ? simHeader.confirmYn : '-'}"/>
    </div>

    <table class="grid">
      <thead>
        <tr>
          <th style="width:22%">정산항목분류</th>
          <th style="width:38%">정산항목</th>
          <th style="width:20%">금액</th>
          <th style="width:20%">예상적용금액</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="row" items="${finalList}">
          <tr>
            <td>${row.itemClass}</td>
            <td>${row.itemName}</td>
            <td class="right"><fmt:formatNumber value="${row.amount}"/></td>
            <td class="right"><fmt:formatNumber value="${row.expectedAmount}"/></td>
          </tr>
        </c:forEach>
        <c:if test="${empty finalList}">
          <tr><td colspan="4" style="text-align:center;color:#777;">데이터가 없습니다</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>

  <!-- ==================== 시뮬레이션 탭 ==================== -->
  <div id="panel-sim" class="panel" style="display:none;">
    <table class="grid">
      <thead>
        <tr>
          <th>정산항목분류</th>
          <th>정산항목</th>
          <th>금액</th>
          <th>예상적용금액</th>
        </tr>
      </thead>
      <tbody id="simBody">
        <tr><td colspan="4" style="text-align:center;">버튼으로 조회하세요</td></tr>
      </tbody>
    </table>
    <div id="installmentBox" style="margin-top:10px;"></div>
  </div>

</div>

<!-- ==================== 스크립트 ==================== -->
<script>
  /* 컨텍스트/헬퍼 */
  var CTX = '<c:url value="/" />'.replace(/\/$/, '');
  function ctx(){ return CTX; }
  function emp(){ return document.getElementById('empId').value.trim(); }
  function yrt(){ return document.getElementById('yrtId').value; }
  function validYear(y){ return /^\d{4}$/.test(y) && (+y>=2000 && +y<=2100); }
  function fmt(n){ if(n==null) return ''; return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','); }

  /* 기본 연도: 올해-1 자동 */
  (function(){
    var v=document.getElementById('baseYear').value;
    if(!/^\d{4}$/.test(v)){ var d=new Date(); document.getElementById('baseYear').value=d.getFullYear()-1; }
  })();

  /* 탭 */
  function showTab(t){
    document.getElementById('tab-final').classList.remove('active');
    document.getElementById('tab-sim').classList.remove('active');
    document.getElementById('panel-final').style.display=(t==='final')?'block':'none';
    document.getElementById('panel-sim').style.display=(t==='sim')?'block':'none';
    document.getElementById('tab-'+t).classList.add('active');
  }

  /* 검색 아이콘 클릭 훅: 타팀 구현 함수(openEmpSearch)만 호출 */
  function onClickEmpSearch(){
    if (typeof window.openEmpSearch === 'function') {
      window.openEmpSearch(function(res){
        if(!res) return;
        setEmp(res.name, res.id);
      });
    } else {
      // 구현 전 임시 안내
      alert('사원 검색은 외부 기능입니다.');
    }
  }

  /* 이름/사번 동시 세팅 */
  function setEmp(name, id){
    var nameEl = document.getElementById('empName');
    var idEl   = document.getElementById('empId');
    if(nameEl) nameEl.value = name || '';
    if(idEl)   idEl.value   = id   || '';
    // 필요 시 후처리: refreshTaxApplyResult();
  }

  function needEmp(){ if(!emp()){ alert('선택된 사원이 없습니다.'); return true; } return false; }

  /* 산출근거 조회 */
  function onReason(){
    if(needEmp()) return;
    var y=document.getElementById('baseYear').value.trim();
    if(!validYear(y)){ alert('정산연도는 2000~2100의 4자리 숫자'); return; }
    var url=ctx()+'/feature/yearend-tax-simulation/api/sim?empId='+encodeURIComponent(emp())+'&baseYear='+encodeURIComponent(y);
    fetch(url)
      .then(function(r){return r.ok?r.json():Promise.reject(r);})
      .then(function(rows){renderSim(rows);showTab('sim');return refreshTaxApplyResult();})
      ["catch"](function(){alert('산출근거 조회 실패 또는 API 미구현');});
  }

  /* 시뮬레이션 처리 */
  function onSim(){
    if(needEmp()) return;
    var y=document.getElementById('baseYear').value.trim();
    if(!validYear(y)){alert('정산연도 형식 오류');return;}
    if(!confirm('기존 결과를 삭제하고 새로 생성하시겠습니까?'))return;
    var form=new URLSearchParams({empId:emp(),baseYear:y,overwrite:'true'});
    fetch(ctx()+'/feature/yearend-tax-simulation/api/simulate',{method:'POST',body:form})
      .then(function(r){return r.ok?r.text():Promise.reject(r);})
      .then(function(yrtId){document.getElementById('yrtId').value=yrtId;onReason();refreshTaxApplyResult(yrtId);})
      ["catch"](function(){alert('시뮬레이션 처리 실패 또는 API 미구현');});
  }

  /* 결과 삭제 */
  function onDelete(){
    var id=yrt();
    if(!id){alert('삭제할 실행이 없습니다.');return;}
    if(!confirm('기존 시뮬레이션 결과를 삭제하시겠습니까?'))return;
    fetch(ctx()+'/feature/yearend-tax-simulation/api/simulate?yrtId='+encodeURIComponent(id),{method:'DELETE'})
      .then(function(r){return r.ok?null:Promise.reject(r);})
      .then(function(){document.getElementById('simBody').innerHTML='<tr><td colspan="4" style="text-align:center;">삭제됨</td></tr>';document.getElementById('yrtId').value='';refreshTaxApplyResult();})
      ["catch"](function(){alert('삭제 실패 또는 API 미구현');});
  }

  /* 분납 시뮬레이션 */
  function onInstallment(){
    var id=yrt(); if(!id){alert('시뮬레이션 실행이 없습니다.');return;}
    var months=prompt('분납 개월수(2~3):','2');
    var start=prompt('시작월(YYYY-MM):',new Date().toISOString().slice(0,7));
    var form=new URLSearchParams({yrtId:id,months:months,startMonth:start});
    fetch(ctx()+'/feature/yearend-tax-simulation/api/installment-simulate',{method:'POST',body:form})
      .then(function(r){return r.ok?r.json():Promise.reject(r);})
      .then(renderInstallment)
      ["catch"](function(){alert('분납 시뮬레이션 실패 또는 API 미구현');});
    showTab('sim');
  }

  /* 그리드 렌더 */
  function renderSim(rows){
    var tb=document.getElementById('simBody');tb.innerHTML='';
    if(!rows||rows.length===0){tb.innerHTML='<tr><td colspan="4" style="text-align:center;color:#777;">데이터 없음</td></tr>';return;}
    rows.forEach(function(r){
      tb.insertAdjacentHTML('beforeend',
        '<tr>'
        +'<td>'+(r.itemClass||'')+'</td>'
        +'<td>'+(r.itemName||'')+'</td>'
        +'<td class="right">'+fmt(r.amount)+'</td>'
        +'<td class="right">'+fmt(r.expectedAmount)+'</td>'
        +'</tr>');
    });
  }

  /* 분납 표 렌더 */
  function renderInstallment(res){
    var box=document.getElementById('installmentBox');
    if(!res||!res.schedule){box.innerHTML='';return;}
    var html='<h4 style="margin:10px 0 6px 0;">분납 스케줄</h4><table class="grid"><tr><th>월</th><th>국세</th><th>지방세</th><th>합계</th></tr>';
    res.schedule.forEach(function(s){
      html+='<tr><td>'+s.yyyymm+'</td><td class="right">'+fmt(s.national)+'</td><td class="right">'+fmt(s.local)+'</td><td class="right">'+fmt(s.total)+'</td></tr>';
    });
    html+='</table>'; box.innerHTML=html;
  }

  /* 세금적용결과 갱신 */
  function refreshTaxApplyResult(yrtId){
    var y=document.getElementById('baseYear').value.trim();
    if(!validYear(y)||!emp()){document.getElementById('taxApplyResult').value='미판정';return;}
    var q=new URLSearchParams({empId:emp(),baseYear:y}); if(yrtId) q.append('yrtId',yrtId);
    return fetch(ctx()+'/feature/yearend-tax-simulation/api/tax-apply-result?'+q.toString())
      .then(function(r){return r.ok?r.text():Promise.reject(r);})
      .then(function(t){document.getElementById('taxApplyResult').value=t||'미판정';})
      ["catch"](function(){document.getElementById('taxApplyResult').value='미판정';});
  }
</script>
