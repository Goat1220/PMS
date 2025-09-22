<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%-- JSP 페이지 설정 / JSP ページ設定 --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>       <%-- JSTL Core 태그 / JSTL Core タグ --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>     <%-- 숫자/날짜 포맷 / 数値・日付フォーマット --%>

<!-- ==========================================
     [공통1] 공용 버튼 폼 적용: 스타일 + 팩토리
     共通ボタン：スタイル＋ファクトリ
========================================== -->
<style>
  .common-btn {
    padding: 8px 16px;
    margin: 4px;
    border: none;
    border-radius: 4px;
    background-color: #4CAF50; /* 서비스 공통 버튼 색 */
    color: white;
    cursor: pointer;
  }
  .common-btn:hover { background-color: #45a049; }
</style>
<script>
  /**
   * 공용 버튼 생성 함수
   * 共通ボタン生成
   * @param {string} label - 버튼 텍스트
   * @param {string} onClickFn - 클릭 시 실행할 전역 함수명
   * @returns {HTMLButtonElement} 버튼 DOM
   */
  function createCommonButton(label, onClickFn) {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "common-btn";
    btn.textContent = label;
    btn.setAttribute("onclick", onClickFn + "()");
    return btn;
  }
</script>

<!-- ==========================================
     [공통2] AG Grid 초기화 적용: CSS/JS + initCommonGrid
     AG Grid 初期化：リソース＋initCommonGrid
     ※ 현재 화면은 커스텀 테이블이므로 리소스만 로드(필요 시 즉시 전환 가능)
========================================== -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-grid.css"/>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-theme-alpine.css"/>
<script src="https://cdn.jsdelivr.net/npm/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
<script>
  /**
   * 공용 그리드 초기화(기본)
   * 共通グリッド初期化（基本）
   * @param {Array} columnDefs
   * @param {Array} rowData
   */
  function initCommonGrid(columnDefs, rowData) {
    const gridOptions = {
      columnDefs: columnDefs,
      rowData: rowData || [],
      defaultColDef: { sortable: true, filter: true, resizable: true }
    };
    new agGrid.Grid(document.querySelector("#commonGrid"), gridOptions);
    return gridOptions;
  }
</script>
<!-- (숨김) AG Grid 컨테이너: 레이아웃 영향 제거 / 非表示コンテナ -->
<div id="commonGrid" class="ag-theme-alpine" style="height:0;width:100%;overflow:hidden;"></div>

<style>
  /* ===== 기본 레이아웃 / 基本レイアウト ===== */
  html,body{
    margin:0; background:#ffffff; color:#222;
    font-family:system-ui, Segoe UI, Apple SD Gothic Neo, Malgun Gothic, sans-serif;
  }
  .container{ width:100%; max-width:1280px; margin:0 auto; padding:16px; } /* 중앙 고정 폭 / 中央固定幅 */

  .page-title{ font-size:18px; font-weight:700; margin:6px 0 14px 0; } /* 페이지 제목 / ページタイトル */

  /* ===== 검색바 / 検索バー ===== */
  .searchbar{
    padding:10px 0; border-bottom:1px solid #e5e7eb; display:flex; flex-wrap:wrap; gap:10px; align-items:center;
  }
  .field{ display:flex; gap:6px; align-items:center; } /* 라벨+입력 묶음 / ラベル+入力の塊 */
  .field input[type=text], .field select{
    height:30px; padding:0 8px; border:1px solid #d9dce3; border-radius:4px; background:#fff;
  }
  .w-yr{width:90px} .w-emp{width:120px} .w-mid{width:180px} .w-biz{width:140px} /* 폭 프리셋 / 幅プリセット */
  .spacer{ flex:1 1 auto; } /* 오른쪽 버튼 밀어내기 / 右側ボタンを押し出す */
  .btn{
    height:32px; padding:0 12px; border:1px solid #d0d5dd; background:#fff; border-radius:4px; cursor:pointer;
  }
  .btn.primary{ background:#2f74ff; border-color:#2f74ff; color:#fff; font-weight:600; }
  .btn.alt{ background:#3d4f91; border-color:#3d4f91; color:#fff; }
  .btn.small{ height:30px; padding:0 10px; }

  /* ===== 탭 / タブ ===== */
  .tabs{ display:flex; gap:6px; margin:12px 0 0 0; }
  .tab{ padding:6px 10px; border:1px solid #e5e7eb; background:#f6f7fb; border-bottom:none; border-radius:6px 6px 0 0; cursor:pointer; }
  /* 활성 탭 컬러 = 공통 버튼 컬러(#4CAF50) */
  .tab.active{ background:#4CAF50; border-color:#4CAF50; color:#fff; font-weight:600; }

  .panel{ border-top:1px solid #e5e7eb; padding:10px 0 18px 0; } /* 탭 내용 영역 / タブ内容領域 */
  .meta{ color:#777; font-size:12.5px; margin:6px 0 4px 0; }    /* 메타 정보 / メタ情報 */

  /* ===== 표 / テーブル ===== */
  table.grid{ width:100%; border-collapse:collapse; margin-top:6px; }
  table.grid th, table.grid td{ border:1px solid #e5e7eb; padding:8px 10px; background:#fff; }
  table.grid th{ background:#f4f6fa; text-align:left; }
  td.right{text-align:right}

  .field-year > span{ color:#e11d48; font-weight:700; } /* 연도 라벨 강조 / 年度ラベル強調 */

  .readonly-gray{
    background:#f3f4f6 !important;
    color:#777 !important;
    border-color:#e5e7eb !important;
  } /* 읽기전용 스타일 / 読み取り専用スタイル */

  /* ===== 사원 입력(하늘색) / 社員入力（空色） ===== */
  .input-wrap.sky{
    background:#e6f3ff; border:1px solid #b6d9ff; border-radius:4px;
    position:relative; display:inline-flex; align-items:center;
    height:30px; padding-right:28px;
  }
  .input-wrap.sky input{
    background:transparent !important; border:0 !important; outline:none;
    height:100%; padding:0 8px; width:180px;
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

  <input type="hidden" id="yrtId" value="${simHeader.yrtId}" />

  <!-- ===== 검색 영역 ===== -->
  <div class="searchbar">
    <div class="field field-year">
      <span>정산연도</span>
      <input id="baseYear" class="w-yr" type="text" value="${baseYear}" placeholder="YYYY">
    </div>

    <div class="field">
      <span>사원</span>
      <span class="input-wrap sky">
        <input id="empName" type="text" value="${empName}" placeholder="사원 이름" aria-label="사원 이름">
        <button type="button" class="icon-btn" aria-label="사원 검색" onclick="onClickEmpSearch()">🔍</button>
      </span>
    </div>

    <div class="field">
      <span>사번</span>
      <input id="empId" class="w-emp readonly-gray" type="text" value="${empId}" placeholder="사번" readonly>
    </div>

    <div class="field">
      <span>정산사업장</span>
      <select id="bizPlace" class="w-biz readonly-gray" disabled>
        <option>본사</option>
      </select>
    </div>

    <div class="field" style="gap:12px;color:#888;">
      <label><input type="checkbox" disabled> 개인마감</label>
      <label><input type="checkbox" disabled> 담당자마감</label>
      <label><input type="checkbox" checked disabled> 정산대상자</label>
    </div>

    <div class="field">
      <span>조회구분</span>
      <select id="searchType" class="w-emp readonly-gray" disabled>
        <option selected>정산</option>
      </select>
    </div>

    <div class="field">
      <span>세금적용결과</span>
      <input id="taxApplyResult" class="w-biz readonly-gray" type="text"
             value="${empty taxApplyResult ? '표준세액공제' : taxApplyResult}" readonly>
    </div>

    <div class="spacer"></div>

    <!-- 공용 버튼 주입 -->
    <div class="field" id="opsBtnArea" style="gap:8px;"></div>
    <script>
      (function mountOps(){
        var area = document.getElementById('opsBtnArea');
        area.appendChild(createCommonButton('산출근거', 'onReason'));
        area.appendChild(createCommonButton('정산시뮬레이션처리', 'onSim'));
        area.appendChild(createCommonButton('정산시뮬레이션결과삭제', 'onDelete'));
        area.appendChild(createCommonButton('납부특례세액시뮬레이션처리', 'onInstallment'));
      })();
    </script>
  </div>

  <!-- ===== 탭 ===== -->
  <div class="tabs">
    <div id="tab-final" class="tab active" onclick="showTab('final')">최종</div>
    <div id="tab-sim"   class="tab" onclick="showTab('sim')">시뮬레이션</div>
  </div>

  <!-- 최종 탭 -->
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

  <!-- 시뮬레이션 탭 -->
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

</div> <!-- /.container -->

<script>
  /* ===== JS 유틸 ===== */
  var CTX = '<c:url value="/" />'.replace(/\/$/, '');
  function ctx(){ return CTX; }
  function emp(){ return document.getElementById('empId').value.trim(); }
  function yrt(){ return document.getElementById('yrtId').value; }
  function validYear(y){ return /^\d{4}$/.test(y) && (+y>=2000 && +y<=2100); }
  function fmt(n){ if(n==null) return ''; return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','); }

  /* 초기 연도 보정 */
  (function(){
    var v=document.getElementById('baseYear').value;
    if(!/^\d{4}$/.test(v)){ var d=new Date(); document.getElementById('baseYear').value=d.getFullYear()-1; }
  })();

  /* 탭 전환 */
  function showTab(t){
    document.getElementById('tab-final').classList.remove('active');
    document.getElementById('tab-sim').classList.remove('active');
    document.getElementById('panel-final').style.display=(t==='final')?'block':'none';
    document.getElementById('panel-sim').style.display=(t==='sim')?'block':'none';
    document.getElementById('tab-'+t).classList.add('active');
  }

  /* 사원 검색(외부 기능 연동) */
  function onClickEmpSearch(){
    if (typeof window.openEmpSearch === 'function') {
      window.openEmpSearch(function(res){
        if(!res) return;
        setEmp(res.name, res.id);
      });
    } else {
      alert('사원 검색은 외부 기능입니다.');
    }
  }

  /* 사원 세팅 */
  function setEmp(name, id){
    var nameEl = document.getElementById('empName');
    var idEl   = document.getElementById('empId');
    if(nameEl) nameEl.value = name || '';
    if(idEl)   idEl.value   = id   || '';
  }

  /* 사원 선택 여부 확인 */
  function needEmp(){ if(!emp()){ alert('선택된 사원이 없습니다.'); return true; } return false; }

  /* 산출근거 조회 → 시뮬 탭 표시 */
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

  /* 시뮬레이션 실행(덮어쓰기) */
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

  /* 시뮬레이션 결과 삭제 */
  function onDelete(){
    var id=yrt();
    if(!id){alert('삭제할 실행이 없습니다.');return;}
    if(!confirm('기존 시뮬레이션 결과를 삭제하시겠습니까?'))return;
    fetch(ctx()+'/feature/yearend-tax-simulation/api/simulate?yrtId='+encodeURIComponent(id),{method:'DELETE'})
      .then(function(r){return r.ok?null:Promise.reject(r);})
      .then(function(){
        document.getElementById('simBody').innerHTML='<tr><td colspan="4" style="text-align:center;">삭제됨</td></tr>';
        document.getElementById('yrtId').value='';
        refreshTaxApplyResult();
      })
      ["catch"](function(){alert('삭제 실패 또는 API 미구현');});
  }

  /* 분납 시뮬레이션 실행 */
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

  /* 시뮬 표 렌더링 */
  function renderSim(rows){
    var tb=document.getElementById('simBody');tb.innerHTML='';
    if(!rows||rows.length===0){
      tb.innerHTML='<tr><td colspan="4" style="text-align:center;color:#777;">데이터 없음</td></tr>';return;
    }
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

  /* 분납 결과 렌더링 */
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

