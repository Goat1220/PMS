<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url var="apiSummary" value="/api/report/withholding"/>
<c:url var="apiAnnex"   value="/api/report/withholding/annex"/>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <title>원천징수이행상황신고서</title>

  <style>
    /* =========================
       페이지 공통
       ========================= */
    html { overflow-y: scroll; }
    body { font-family: Arial, Helvetica, 'Malgun Gothic', sans-serif; font-size:14px; color:#333; }

    /* 네임스페이스: .wh 내부에만 스타일 적용 */
    .wh .container { --gutter: 16px; padding-inline: var(--gutter); box-sizing: border-box; }

    /* 카드 & 패널 */
    .wh .card, .wh .panel{
      box-sizing:border-box; width:100%; background:#fff;
      border:1px solid #e5e7eb; border-radius:8px;
    }
    .wh .card{ padding:12px 14px; margin-bottom:12px; }
    .wh .panel{ padding:10px; border-color:#bbb; border-radius:6px; }
    .wh .card-title{ font-weight:700; margin-bottom:10px; }

    /* 상단 2컬럼 레이아웃 */
    .wh .two-cols{
      display:grid; grid-template-columns: minmax(0, 2fr) minmax(0, 1fr);
      gap:9px; align-items:stretch;
    }
    .wh .two-cols > .card{ min-width:0; }

    /* 폼 공통 */
    .wh .form-row{ display:flex; align-items:center; gap:6px; flex-wrap:wrap; }
    .wh .mt-8{ margin-top:8px; }
    .wh .mt-12{ margin-top:12px; }
    .wh .ml-auto{ margin-left:auto; }
    .wh .no-shrink{ flex:0 0 auto; }

    /* 라벨+입력 한 쌍(간격 촘촘히) */
    .wh .pair{
      display:inline-flex;
      align-items:center;
      gap:6px;              /* 라벨↔입력 간격 */
      white-space:nowrap;   /* 줄바꿈 방지 */
    }

    /* 2번째 줄: 좌(체크들) / 우(연말정산연도) 양끝 정렬 */
    .wh .row-split{
      position: relative;
      display:grid;
      grid-template-columns: 1fr auto;
      align-items:center;
      column-gap:16px;
    }
    /* 가운데 보내고 싶을 때 쓰는 미세 조정용(현재 '연말정산반영'에 사용) */
    .wh .row-split .mid{
      position:absolute; left:50%; transform:translateX(-50%);
    }
    .wh .row-split .right{ justify-self:end; margin-right:140px; }

    /* 라벨 형태(가변폭, 우측 여백만) */
    .wh .label-80{
      width:auto; margin-right:6px; color:#555;
      display:inline-block; white-space:nowrap;
    }
    /* 필수/중요 표시: 요구사항(4) 반영 */
    .wh .req{ color:#e11d48; } /* 빨간색 */

    /* 입력/셀렉트 */
    .wh input[type="text"],
    .wh input[type="password"],
    .wh select{
      height:32px; padding:0 8px; border:1px solid #dcdfe6; border-radius:4px; background:#fff;
    }

    /* 유틸 폭 */
    .wh .w-80{width:80px} .wh .w-100{width:100%} .wh .w-110{width:110px}
    .wh .w-120{width:120px} .wh .w-140{width:140px} .wh .w-160{width:160px}
    .wh .min-200{ min-width:200px; }
    .wh .flex-1{flex:1 1 auto}

    /* 체크박스 라벨 */
    .wh .chk{ display:flex; align-items:center; gap:6px; color:#374151; font-size:13px; white-space:nowrap; }
    /* 요구사항(3): 체크박스 간격 45px로 통일 */
    .wh .checks{ display:flex; align-items:center; gap:45px; }

    /* 버튼 */
    .wh .btn, .wh .btn-primary{
      height:32px; padding:0 12px; border:1px solid #d1d5db; background:#fff; border-radius:6px; cursor:pointer;
    }
    .wh .btn.primary, .wh .btn-primary{ background:#2563eb; color:#fff; border-color:#2563eb;}
    .wh .btn:hover, .wh .btn-primary:hover{ filter:brightness(0.97); }
    .wh #btnLoad{ margin-right:20px; }
    .wh .btn:disabled, .wh .btn-primary:disabled{ opacity:.6; cursor:not-allowed; }

    /* 포커스 */
    .wh input:focus, .wh select:focus, .wh .btn:focus, .wh .btn-primary:focus{
      outline:none; box-shadow:0 0 0 2px rgba(37,99,235,.15); border-color:#2563eb;
    }

    /* 표/탭 */
    .wh table{ width:100%; border-collapse:collapse; table-layout:fixed; }
    .wh th, .wh td{ border:1px solid #ddd; padding:6px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
    .wh th{ background:#f0f3f7; }
    .wh td.num{text-align:right} .wh td.center{text-align:center} .wh td.code{font-family:Consolas,monospace}
    .wh .tabs{ display:flex; gap:8px; margin:16px 0; }
    .wh .tab{ padding:8px 12px; border:1px solid #bbb; border-bottom:none; background:#f7f7f7; cursor:pointer; border-radius:6px 6px 0 0; }
    .wh .tab.active{ background:#fff; font-weight:bold; }

    /* 전월 미환급세액 */
    .wh .refund-grid{ display:grid; grid-template-columns: repeat(14, max-content 160px); gap:8px 12px; }
    .wh .refund-grid input{ width:160px; }

    /* ==============
       신고파일생성 카드 (접기/펼치기 + 레이아웃)
       ============== */
    .wh .file-card { position:relative; }
    .wh .file-card__header{
      display:flex; align-items:center; justify-content:space-between;
      cursor:pointer; user-select:none;
    }
    .wh .file-card__body{ display:block; }
    .wh .file-card.is-collapsed .file-card__body{ display:none; }

	/* === 신고파일생성(한 줄: [라벨 | 파일명(가변) | 버튼묶음]) === */
	/* 파일명 입력폭(원하면 숫자만 바꾸면 됨) */
	:root{ --file-name-w: 480px; } /* 대략 '비밀번호 확인' 박스 영역까지 오는 폭 */
	
	.wh .file-grid{
	  display:grid;
	  grid-template-columns: max-content var(--file-name-w) 180px; /* 라벨 | 파일명(고정) | 버튼묶음 */
	  grid-template-areas:
	    "lblName name btns"
	    "lblPwd  pwd  btns";
	  gap:10px 12px;
	  align-items:center;
	}
	
	.wh .lbl-name { grid-area: lblName; }
	.wh .in-name  { grid-area: name; }
	.wh .lbl-pwd  { grid-area: lblPwd; }
	.wh .in-pwd   { grid-area: pwd; }
	
	.wh .btns-col{
	  grid-area: btns;
	  display:flex; flex-direction:column; gap:8px;
	  align-items:stretch;
	  width:100%;
	}
	


    /* =========================
       반응형
       - 요구사항(2): 960px 이하 미디어쿼리 단일 블록으로 통합
       ========================= */
    @media (max-width:1200px){
      .wh .two-cols{ grid-template-columns:1fr; }
      .wh .refund-grid{ grid-template-columns: repeat(2, max-content 160px); }
    }
    @media (max-width:700px){
      .wh .refund-grid{ grid-template-columns: max-content 160px; }
    }
    @media (max-width:960px){
      /* 신고파일생성 그리드 한 칸씩 세로 배치 */
      .wh .file-grid{ grid-template-columns: 1fr; }
      .wh .btns-col{ grid-column:1; grid-row:auto; flex-direction:row; }
      .wh .btns-col .btn-primary{ width:auto; }
    }
    
    	@media (max-width:960px){
	  .wh .file-grid{
	    grid-template-columns: 1fr;
	    grid-template-areas:
	      "lblName"
	      "name"
	      "lblPwd"
	      "pwd"
	      "btns";
	  }
	  .wh .btns-col{ flex-direction:row; }
	}
  </style>
</head>

<body>
<div class="wh"><!-- 네임스페이스 시작 -->
<div class="container"
     data-summary-url="${apiSummary}"
     data-annex-url="${apiAnnex}">

  <!-- 상단 2컬럼 -->
  <div class="two-cols">

    <!-- ▣ 신고집계기준 -->
    <div class="card">
      <div class="card-title">신고집계기준(원천징수명세서 및 부표)</div>

      <!-- 1줄: 라벨+입력 촘촘, 오른쪽에 '데이터생성' -->
      <div class="form-row">
        <span class="label-80 req">원천세사업장</span>
        <select class="w-140 min-200"><option>원천세사업장</option></select>

        <span class="label-80 req">귀속월</span>
        <input id="ym" type="text" class="w-110" value="2025-05"/>

        <span class="label-80 req">지급월</span>
        <input id="payYm" type="text" class="w-110" placeholder="yyyy-MM"/>

        <span class="label-80 req">신고연월</span>
        <input id="reportYm" type="text" class="w-110" placeholder="yyyy-MM"/>

        <span class="label-80">신고구분</span>
        <select class="w-110">
          <option>정기신고</option>
          <option>기한후신고</option>
        </select>

        <button id="btnLoad" class="btn primary ml-auto no-shrink">데이터생성</button>
      </div>

      <!-- 2줄: 좌측(체크 2개) / 우측(연말정산연도) -->
      <div class="row-split mt-8">
        <div class="checks">
          <label class="chk"><input type="checkbox" id="opt1"> 퇴직/중도정산 지급월 기준으로 집계</label>
          <label class="chk mid"><input type="checkbox" id="opt2"> 연말정산반영</label>
        </div>

        <div class="right pair">
          <span class="label-80 req">연말정산연도</span>
          <input id="annYear" type="text" class="w-80" placeholder="YYYY"/>
        </div>
      </div>
    </div>

    <!-- ▣ 출력용 -->
    <div class="card">
      <div class="card-title">출력용</div>

      <!-- 신고일 + 체크박스들 -->
      <div class="form-row" style="gap:20px;">
        <span class="pair">
          <span class="label-80">신고일</span>
          <input id="reportDate" type="text" class="w-80" placeholder="YYYY-MM"/>
        </span>

        <label class="chk"><input type="checkbox" id="optMonthPay" checked> 일괄납부</label>
        <label class="chk"><input type="checkbox" id="optBizUnit"> 사업자단위과세</label>
        <label class="chk"><input type="checkbox" id="optConfirm"> 확정</label>
        <label class="chk"><input type="checkbox" id="optAgent"> 세무대리인</label>
      </div>

      <!-- 신고구분(체크박스) : 간격 45px 통일 -->
      <div class="form-row mt-12" style="gap:12px;">
        <span class="label-80" style="margin-right:10px;">신고구분</span>
        <div class="checks">
          <label class="chk"><input type="checkbox" name="repType" checked> 매월</label>
          <label class="chk"><input type="checkbox" name="repType"> 반기</label>
          <label class="chk"><input type="checkbox" name="repType"> 연말</label>
          <label class="chk"><input type="checkbox" name="repType"> 소득처분</label>
          <label class="chk"><input type="checkbox" name="repType"> 환급세액</label>
        </div>
      </div>
    </div>
  </div><!-- /.two-cols -->

  <!-- ▣ 신고파일생성 (접기/펼치기 지원) -->
  <div class="card file-card" id="fileCard">
  <div class="card-title file-card__header" id="fileCardHeader">
    <span>▼ 신고파일생성</span>
  </div>

  <div class="file-card__body">
    <div class="file-grid">
      <!-- 1행 -->
      <span class="label-80 lbl-name" style="margin-right:10px;">파일명</span>
      <input id="fileName"
             class="in-name"
             type="text"
             readonly
             placeholder="귀속월로 자동 생성됩니다."
             />
	
	    <div class="btns-col">
	      <button id="btnMake"    class="btn-primary">신고파일생성</button>
	      <button id="btnEncrypt" class="btn-primary">암호화파일생성</button>
	    </div>
	
	    <!-- 2행: 라벨 | 비번 2개 | (버튼묶음은 위에서 아래까지 고정) -->
	    <span class="label-80 lbl-pwd" style="margin-right:10px;">비밀번호</span>
	    <div class="pwd-row in-pwd">
	      <input id="pwd1" type="password" class="w-160" placeholder="*****"/>
	      <span class="label-80" style="margin-left:16px;">비밀번호 확인</span>
	      <input id="pwd2" type="password" class="w-160" placeholder="*****"/>
	    </div>
	
	  </div>
	</div>
  </div>

  <!-- 탭 -->
  <div class="tabs">
    <button id="tabSummary" class="tab active">원천징수명세및납부세액(요약)</button>
    <button id="tabAnnex"   class="tab">원천징수이행상황신고서(부표)</button>
  </div>

  <!-- 요약 -->
  <div id="panelSummary" class="panel">
    <table>
      <thead>
        <tr>
          <th style="width:50px;">No</th>
          <th>소득 구분</th>
          <th style="width:60px;">코드</th>
          <th style="width:70px;">인원</th>
          <th style="width:110px;">총지급액</th>
          <th style="width:110px;">징수농특세</th>
          <th style="width:110px;">납부소득세</th>
          <th style="width:110px;">징수가산세</th>
          <th style="width:110px;">징수소득세</th>
          <th style="width:110px;">조정환급세액</th>
          <th style="width:110px;">납부농특세</th>
        </tr>
      </thead>
      <tbody id="gridBody"></tbody>
    </table>
  </div>

  <!-- 부표 -->
  <div id="panelAnnex" class="panel" style="display:none;">
    <table>
      <thead>
        <tr>
          <th style="width:50px;">No</th>
          <th>소득 구분</th>
          <th style="width:60px;">코드</th>
          <th style="width:70px;">인원</th>
          <th style="width:110px;">총지급액</th>
          <th style="width:110px;">징수농특세</th>
          <th style="width:110px;">납부소득세</th>
          <th style="width:110px;">징수가산세</th>
          <th style="width:110px;">징수소득세</th>
          <th style="width:110px;">조정환급세액</th>
          <th style="width:110px;">납부농특세</th>
        </tr>
      </thead>
      <tbody id="annexBody"></tbody>
    </table>
  </div>

  <!-- 전월 미환급세액 -->
  <div class="card">
    <div class="card-title">전월 미환급세액</div>
    <div class="refund-grid">
      <label> 전월미환급세액: </label>
      <label>(A) 전월미환급세액</label><input type="text"/>
      <label>(B) 기환급신청한세액</label><input type="text"/>
      <label>(C) 차감잔액(A-B)</label><input type="text"/>
    </div>
    <div class="refund-grid">
      <label> 당월발생 환급세액: </label>
      <label>(D) 일반환급</label><input type="text"/>
      <label>(E) 신탁재산(금융회사등)</label><input type="text"/>
      <label>(F) 금융회사등환급세액</label><input type="text"/>
      <label>(G) 합병등환급세액</label><input type="text"/>
    </div>
    <div class="refund-grid">
      <label> 환급세액: </label>
      <label>(H) 조정대상환급세액(C+D+E+F+G)</label><input type="text"/>
      <label>(I) 당월조정환급세액</label><input type="text"/>
      <label>(J) 차월이월환급세액(H-I)</label><input type="text"/>
      <label>환급신청금액</label><input type="text"/>
    </div>
  </div>

</div><!-- /.container -->
</div><!-- /.wh -->

<!-- 페이지 스크립트 -->
<script src="<c:url value='/resources/js/withholding.js'/>"></script>
<script>
  /* 신고파일 생성 시 비밀번호 일치 체크 */
  (function () {
    var btn = document.getElementById('btnMake');
    if (btn) {
      btn.addEventListener('click', function () {
        var p1 = document.getElementById('pwd1').value;
        var p2 = document.getElementById('pwd2').value;
        if (p1 !== p2) {
          alert('비밀번호가 일치하지 않습니다.');
          return;
        }
        // TODO: 생성 로직
      });
    }
  })();

  /* YYYY-MM → 다음달(YYYY-MM) */
  function addMonth(ym) {
    ym = (ym || '').trim();
    var m = /^(\d{4})-(\d{2})$/.exec(ym);
    if (!m) return '';
    var y  = parseInt(m[1], 10);
    var mo = parseInt(m[2], 10) + 1;
    if (mo === 13) { y++; mo = 1; }
    return y + '-' + String(mo).padStart(2, '0');
  }

  /* 자동 동기화 + 신고파일생성 접기/펼치기 */
  (function autoSyncMonths(){
    var $ym   = document.getElementById('ym');         // 귀속월 (YYYY-MM)
    var $pay  = document.getElementById('payYm');      // 지급월 (YYYY-MM)
    var $rep  = document.getElementById('reportYm');   // 신고연월 (YYYY-MM)
    var $repD = document.getElementById('reportDate'); // 신고일 (YYYY-MM-DD)

    if (!$ym || !$pay || !$rep) return;

    // 신고연월 기준으로 신고일(10일) 세팅
    function setReportDate(){
      if ($rep && $rep.value && $repD) {
        $repD.value = $rep.value + '-10';
      }
    }

    function syncFromAccrual(){
      if ($ym.value) $pay.value = $ym.value;          // 지급월 = 귀속월
      if ($pay.value) $rep.value = addMonth($pay.value); // 신고연월 = 지급월 다음달
      setReportDate();
    }
    function syncFromPay(){
      if ($pay.value) $rep.value = addMonth($pay.value);
      setReportDate();
    }
    function syncFromReportYm(){ setReportDate(); }

    // 초기 1회 동기화
    syncFromAccrual();

    // 변경 시 업데이트
    ['change','blur','keyup'].forEach(function(ev){
      $ym.addEventListener(ev,  syncFromAccrual);
      $pay.addEventListener(ev, syncFromPay);
      $rep.addEventListener(ev, syncFromReportYm);
    });

    // --- 신고파일생성: 접기/펼치기 ---
    (function(){
      var card   = document.getElementById('fileCard');
      var header = document.getElementById('fileCardHeader');
      if(!card || !header) return;

      header.addEventListener('click', function(e){
        // 헤더 내부 버튼 클릭이면 토글 방지
        if (e.target.closest('button')) return;
        card.classList.toggle('is-collapsed');
      });
    })();

    // --- "YYYY-MM" → "WH_YYYYMM.txt" 유틸 ---
    function makeFileName(ym) {
      const m = /^(\d{4})-(\d{2})$/.exec((ym || '').trim());
      return m ? `WH_${m[1]}${m[2]}.txt` : '';
    }

    // --- 생성 버튼: 파일명 변수로 계산해서 사용 ---
    document.getElementById('btnMake')?.addEventListener('click', function () {
      const ym =
        document.getElementById('ym')?.value?.trim() ||
        document.getElementById('reportYm')?.value?.trim() ||
        '';
      const fileName = makeFileName(ym);
      console.log("fileName" + fileName);
        return;
      }

    });

    // --- 암호화 버튼: 동일하게 파일명 계산해서 사용 ---
    document.getElementById('btnEncrypt')?.addEventListener('click', function () {
      const ym =
        document.getElementById('ym')?.value?.trim() ||
        document.getElementById('reportYm')?.value?.trim() ||
        '';
      const fileName = makeFileName(ym);
      if (!fileName) {
        alert('귀속월(YYYY-MM)을 먼저 입력해 주세요.');
        return;
      }

    });

</script>

</body>
</html>
