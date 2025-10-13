<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url var="apiSummary" value="/api/report/withholding"/>
<c:url var="apiAnnex"   value="/api/report/withholding/annex"/>
<c:url var="apiGenerate"    value="/api/report/withholding/generate"/>
<c:url var="apiRefundPrev"  value="/api/report/withholding/refund-prev"/>
<c:url var="apiRefundSave"  value="/api/report/withholding/refund-save"/> 

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <title>원천징수이행상황신고서</title>

  <style>
	/* =========================
	   상단 제목 + 조회
	   ========================= */
	.wh .page-header{
	  display:flex; align-items:center;
	  padding:6px var(--gutter);     /* 컨테이너와 좌우 여백 통일 */
	  margin-bottom:8px;
	  border-bottom:1px solid #eee;
	  background:#fff; border-radius:8px;
	}
	.wh .page-title{
	  font-size:12px; font-weight:700; color:#111;
	  margin-right:auto;              /* 버튼을 오른쪽으로 밀기 */
	}
	.wh #btnSearch{
	  height:28px; padding:0 12px;
	}
	
	/* =========================
	   페이지 공통 (컴팩트 기본)
	   ========================= */
	html { overflow-y: scroll; }
	body { font-family: Arial, Helvetica, 'Malgun Gothic', sans-serif; font-size:12px; color:#333; }
	
	/* 네임스페이스 컨테이너 */
	.wh .container { --gutter:16px; padding-inline: var(--gutter); box-sizing: border-box; }
	
	/* 카드 & 패널 */
	.wh .card, .wh .panel{
	  box-sizing:border-box; width:100%; background:#fff;
	  border:1px solid #e5e7eb; border-radius:8px;
	}
	.wh .card{ padding:8px 10px; margin-bottom:8px; }
	.wh .panel{ padding:10px; border-color:#bbb; border-radius:6px; margin-bottom:22px; }
	.wh .card-title{ font-weight:700; margin-bottom:6px; font-size:12px; }
	
	/* 2컬럼 레이아웃 */
	.wh .two-cols{
	  display:grid; grid-template-columns: minmax(0, 2fr) minmax(0, 1fr);
	  gap:9px; align-items:stretch;
	}
	.wh .two-cols > .card{ min-width:0; }
	
	/* 폼 공통 */
	.wh .form-row{ display:flex; align-items:center; gap:9px; flex-wrap:wrap; }
	.wh .mt-8{ margin-top:8px; }
	.wh .mt-12{ margin-top:8px; }
	.wh .ml-auto{ margin-left:auto; }
	.wh .no-shrink{ flex:0 0 auto; }
	
	.wh .label-80{ width:auto; margin-right:6px; color:#555; display:inline-block; white-space:nowrap; }
	.wh .req{ color:#e11d48; }
	
	.wh input[type="text"],
	.wh input[type="password"],
	.wh select{
	  height:28px; padding:0 6px; border:1px solid #dcdfe6; border-radius:4px; background:#fff;
	}
	
	/* 유틸 너비 (오타세이프 포함) */
	.wh .w-80{width:80px} .wh .w-100{width:100%} .wh .w-110{width:110px}
	.wh .w-120{width:120px} .wh .w-140{width:140px} .wh .w-160{width:160px}
	.wh .w-180{width:180px}
	
	.wh .min-160 { min-width: 160px; }
	
	/* ── 첫 번째 카드의 '첫 번째 줄'만 간격 넓게 */
	.wh .two-cols > .card:first-child .card-title + .form-row{
	  gap: 18px;           /* 필요값으로 조절 */
	  column-gap: 18px;    /* 호환용 */
	}

	/* 체크박스 라벨 */
	.wh .chk{ display:flex; align-items:center; gap:6px; color:#374151; font-size:12px; white-space:nowrap; }
	.wh .checks{ display:flex; align-items:center; gap:40px; }
	
	/* 2번째 줄(체크/연말정산연도) 정렬 */
	.wh .row-split{
	  display:grid;
	  grid-template-columns: 1fr auto;  /* 좌: 체크들 / 우: 연도 */
	  align-items:center;
	  column-gap:12px;
	}
	
	/* 체크 영역을 가로 배치하고, 두 번째(.mid)만 오른쪽 끝으로 밀기 */
	.wh .row-split .checks{display:flex; width:100%; gap:12px;}
	.wh .row-split .checks .mid{margin-left: auto; margin-right:300px;}  /* ← 이게 포인트 */
	
	/* 우측 블록은 너무 벌어지지 않게 */
	.wh .row-split .right{justify-self:end; margin-right:135px;}

	/* 버튼 */
	.wh .btn, .wh .btn-primary{
	  height:32px; padding:0 12px; border:1px solid #d1d5db; background:#fff; border-radius:6px; cursor:pointer;
	  font-size:12px;
	}
	/* 파란 버튼(.btn primary, .btn-primary 둘 다 수용) */
	.wh .btn.primary, .wh .btn-primary{ background:#2563eb; color:#fff; border-color:#2563eb; }
	.wh .btn:hover, .wh .btn-primary:hover{ filter:brightness(0.97); }
	.wh .btn:disabled, .wh .btn-primary:disabled{ opacity:.6; cursor:not-allowed; }
	
	/* 데이터생성 버튼만 카드 오른쪽에서 살짝 안쪽으로 */
	.wh #btnLoad{ margin-right:20px; }
	
	/* 	연말정산 readonly */
	.wh input#annYear.is-readonly{
	  background:#f3f4f6 !important;
	  color:#6b7280 !important;
	  cursor:not-allowed;
	}
	.wh input#annYear.is-readonly::placeholder{
	  color:#9ca3af;
	}
	
	/* 	확정버튼 체크박스 readonly */
	.wh label.chk.is-readonly{
	  color:#111316;       
	}
	.wh label.chk.is-readonly input[type="checkbox"]{
	  accent-color:#111316;
	  opacity:1;            
	  pointer-events:none;
	}
	
	
	/* 표/탭 */
	.wh table{ width:100%; border-collapse:collapse; table-layout:fixed; font-size:12px; }
	.wh th, .wh td{ border:1px solid #ddd; padding:4px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
	.wh th{ background:#f0f3f7; }
	.wh td.num{text-align:right} .wh td.center{text-align:center} .wh td.code{font-family:Consolas,monospace}
	
	.wh .tabs{ display:flex; gap:8px; margin:16px 0; }
	.wh .tab{ padding:8px 12px; border:1px solid #bbb; border-bottom:none; background:#f7f7f7; cursor:pointer; border-radius:6px 6px 0 0; font-size:12px; }
	.wh .tab.active{ background:#fff; font-weight:bold; }
	/* =========================
		신고파일생성
	   ========================= */
	/* 신고파일생성 (접기/펼치기) */
	.wh .file-card{ position:relative; margin-bottom:4px; }
	.wh .file-card__header{ display:flex; align-items:center; justify-content:space-between; cursor:pointer; user-select:none; }
	.wh .file-card .card-title{ margin-bottom:4px; }
	.wh .file-card__body{ display:block; }
	.wh .file-card.is-collapsed .file-card__body{ display:none; }
	
	/* 신고파일생성 내부 그리드 */
	.wh .file-grid{
	  --file-name-w: 480px;
	  display:grid;
	  grid-template-columns: max-content var(--file-name-w) 180px;
	  grid-template-areas:
	    "lblName name btns"
	    "lblPwd  pwd  btns";
	  gap:10px 12px; align-items:center;
	}
	.wh .file-grid{ row-gap:6px; }
	.wh .btns-col{ grid-area: btns; display:flex; flex-direction:column; gap:6px; align-items:stretch; width:100%; }
	.wh .lbl-name{ grid-area: lblName; }
	.wh .in-name{ grid-area: name; }
	.wh .lbl-pwd{ grid-area: lblPwd; }
	.wh .in-pwd{ grid-area: pwd; }
	
	/* 파일명 입력: readonly일 때 회색 배경/텍스트 */
	.wh input#fileName[readonly]{
	  background:#f3f4f6;   /* 연한 회색 */
	  color:#6b7280;        /* 회색 글자 */
	  cursor:not-allowed;   /* 손모양 금지 */
	  border-color:#e5e7eb; /* (선택) 테두리도 연하게 */
	}
	/* 플레이스홀더도 연하게 */
	.wh input#fileName[readonly]::placeholder{ color:#9ca3af; }
	
	/* =========================
		요약/부표
	   ========================= */
		/* 요약/부표 스크롤 영역 */
	.wh .data-area { margin-top:4px; }
	.wh .data-area .tabs{
	  position:static; top:0; z-index:2; background:#fff;
	  padding-top:6px; margin-bottom:6px;
	}
	.wh .data-scroll{
	  overflow:auto; border:1px solid #e5e7eb; border-radius:8px; background:#fff; padding:0px;
	}
	.wh .data-scroll .scroll-inner{ padding:8px; }
	
	/* 셀 단위 배경색 */
	.wh td.cell-grey { background:#f3f4f6 !important; }   /* 회색 */
	.wh td.cell-pink { background:#ffe2e2 !important; }   /* 연분홍(가감계에서 '소득 구분/코드'만) */

	/* 요약: 2번째 열(소득 구분) 폭 */
	#panelSummary table th:nth-child(2),
	#panelSummary table td:nth-child(2) { width: 450px; }
	
	/* 부표: 2번째 열(소득 구분) 폭 */
	#panelAnnex table th:nth-child(2),
	#panelAnnex table td:nth-child(2) { width: 450px; }
	
	/* 테이블은 기본값 유지 (보더가 끼어 들뜸 방지) */
	#panelSummary table, #panelAnnex table{
	  border-collapse: separate;
	  border-spacing: 0;
	}
	   /* 스크롤 영역 안에서 헤더 고정 */
	#panelSummary table thead th,
	#panelAnnex   table thead th {
	  position: sticky;
	  top: 0;
	  z-index: 3;                 /* 본문 셀 위로 */

	}
	
	/* =========================
		전월미환급세액
	   ========================= */
	.wh #refundBlock{ padding:6px 8px; }
	.wh #refundBlock .card-title{ margin-bottom:6px; font-size:12px; }
	.wh .refund-compact{
	  --label-w:150px; --input-w:120px;
	  display:grid;
	  grid-template-columns: max-content 16px repeat(4, var(--label-w) var(--input-w));
	  column-gap:18px; row-gap:6px; align-items:center;
	}
	.wh .refund-compact .row-title{ font-weight:400; font-size:12px; color:#374151; white-space:nowrap; }
	.wh .refund-compact label:not(.row-title){
	  display:inline-block; width:var(--label-w); line-height:1.15; white-space:normal;
	  font-weight:400; font-size:12px; color:#444;
	}
	.wh .refund-compact small{ font-size:10px; line-height:1; position:relative; top:-1px; }
	.wh .refund-compact input[type="text"]{ width:var(--input-w); height:26px; padding:0 6px; box-sizing:border-box; }
	.wh .refund-compact .placeholder{ width:var(--label-w); height:1px; display:block; }

	/* 전월 미환급세액 박스 회색 */
	#refundBlock input.input-grey {
	  background: #f3f4f6 !important; /* 연회색 */
	  cursor: default;
	}

	
	/* =========================
		반응형
	   ========================= */
	@media (max-width:1200px){
	  .wh .two-cols{ grid-template-columns:1fr; }
	}
	@media (max-width:960px){
	  .wh .file-grid{
	    grid-template-columns:1fr;
	    grid-template-areas:
	      "lblName"
	      "name"
	      "lblPwd"
	      "pwd"
	      "btns";
	  }
	  .wh .btns-col{ grid-column:1; grid-row:auto; flex-direction:row; }
	  .wh .btns-col .btn-primary{ width:auto; }
	}

</style>
</head>

<body>
	<div class="wh"><!-- 네임스페이스 시작 -->
	<div class="container"
     data-summary-url="${apiSummary}"
     data-annex-url="${apiAnnex}"
	 data-generate-url="${apiGenerate}"
	 data-prev-refund-url="${apiRefundPrev}"
	 data-save-refund-url="${apiRefundSave}">

	<!-- 페이지 헤더 -->
	<div class="page-header">
  <div class="page-title">원천징수이행상황신고서</div>
  <button id="btnSearch" class="btn primary">🔍조회</button>
</div>
	

  <!-- 상단 2컬럼 -->
  <div class="two-cols">

    <!-- ▣ 신고집계기준 -->
    <div class="card">
      <div class="card-title">신고집계기준(원천징수명세서 및 부표)</div>

      <!-- 1줄: 라벨+입력 촘촘, 오른쪽에 '데이터생성' -->
      <div class="form-row">
        <span class="label-80 req">원천세사업장</span>
        <select class="w-110 min-160"><option>원천세사업장</option></select>

        <span class="label-80 req">귀속월</span>
        <input id="ym" type="text" class="w-110" value="2025-09"/>

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
	    <span class="label-80 lbl-pwd" style="margin-right:15px;">비밀번호</span>
	    <div class="pwd-row in-pwd">
	      <input id="pwd1" type="password" class="w-160" placeholder="*****"/>
	      <span class="label-80" style="margin-left:40px;">비밀번호 확인</span>
	      <input id="pwd2" type="password" class="w-160" placeholder="*****"/>
	    </div>
	
	  </div>
	</div>
  </div>

  <!-- 탭 -->
  <div class="data-area">
  <div class="tabs">
    <button id="tabSummary" class="tab active">원천징수명세및납부세액(요약)</button>
    <button id="tabAnnex"   class="tab">원천징수이행상황신고서(부표)</button>
  </div>

  <!-- 요약 -->
   <div class="data-scroll"  id="dataScroll">
   <div class="scroll-inner">
  <div id="panelSummary" class="panel">
    <table>
      <thead>
        <tr>
          <th style="width:50px;">No</th>
          <th style="width:450px;">소득 구분</th>
          <th style="width:60px;">코드</th>
          <th style="width:70px;">인원</th>
          <th style="width:110px;">총지급액</th>
          <th style="width:110px;">징수농특세</th>
          <th style="width:110px;">납부소득세</th>
          <th style="width:110px;">징수소득세</th>
          <th style="width:110px;">조정환급세액</th>
          <th style="width:110px;">납부농특세</th>
          <th style="width:110px;">징수가산세</th>
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
    <th style="width:450px;">소득 구분</th>
    <th style="width:60px;">코드</th>
    <th style="width:70px;">인원</th>
    <th style="width:110px;">총지급액</th>
    <th style="width:110px;">징수소득세</th>
    <th style="width:110px;">징수농특세</th>
    <th style="width:110px;">징수가산세</th>
    <th style="width:110px;">조정환급세액</th>
    <th style="width:110px;">납부소득세</th>
    <th style="width:110px;">납부농특세</th>

        </tr>
      </thead>
      <tbody id="annexBody"></tbody>
    </table>
  </div>
</div>
</div>

  <!-- 전월 미환급세액 -->
<div class="card"  id="refundBlock">
  <div class="card-title">전월 미환급세액</div>

  <div class="refund-compact">
    <!-- ① 전월미환급세액: A B C + (빈 1쌍) -->
    <div class="row-title">전월미환급세액 :</div><div></div>
<label>(A) 전월미환급세액</label><input id="A" type="text" value="0">
<label>(B) 기환급신청한세액</label><input id="B" type="text" value="0">
<label>(C) 차감잔액  (A - B)</label><input id="C" type="text" value="0" readonly class="input-grey">
    <!-- 남는 1쌍을 빈 칸으로 채워 정렬 유지 -->
    <span class="placeholder"></span><span></span>

    <!-- ② 당월발생 환급세액: D E F G -->
    <div class="row-title">당월발생 환급세액 :</div><div></div>
<label>(D) 일반환급</label><input id="D" type="text" value="0" readonly class="input-grey">
<label>(E) 신탁재산(금융회사 등)</label><input id="E" type="text" value="0">
<label>(F) 금융회사 등 환급잔액</label><input id="F" type="text" value="0">
<label>(G) 합병 등 환급세액</label><input id="G" type="text" value="0">

    <!-- ③ 환급세액: H I J K -->
    <div class="row-title">환급세액 :</div><div></div>
<label>(H) 조정대상환급세액 <br><span style="font-size:12px;color:#666">( C + D + E + F + G )</span></label><input id="H" type="text" value="0" readonly class="input-grey">
<label>(I) 당월조정환급세액</label><input id="I" type="text" value="0" readonly class="input-grey">
<label>(J) 차월이월환급세액 (H - I)</label><input id="J" type="text" value="0" readonly class="input-grey">
<label>환급신청금액</label><input id="K" type="text" value="0"> 
  </div>
</div>


</div><!-- /.container -->
</div><!-- /.wh -->

<!-- PDF 저장 -->
<!-- 1) html2canvas -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>

<!-- 2) jsPDF (UMD) -->
<script src="https://cdn.jsdelivr.net/npm/jspdf@2.5.1/dist/jspdf.umd.min.js"></script>

<!-- ⭐ UMD→전역 shim (반드시 플러그인보다 먼저) -->
<script>
  if (window.jspdf && window.jspdf.jsPDF && !window.jsPDF) {
    window.jsPDF = window.jspdf.jsPDF;
  }
</script>

<!-- 3) 암호 플러그인 -->
<script src="https://unpkg.com/jspdf-encrypt/dist/jspdf.plugin.encrypt.min.js"></script>

<!-- 4) 너의 페이지 스크립트 -->
<script src="<c:url value='/resources/js/withholding.js'/>"></script>

</body>
</html>
