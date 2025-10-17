<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../includes/commonform.jsp" %>
<%@ include file="../includes/table.jsp" %>

<!-- API URL 정의 / API URL定義 -->
<c:url var="apiSummary" value="/api/report/withholding"/>
<c:url var="apiAnnex"   value="/api/report/withholding/annex"/>
<c:url var="apiGenerate"    value="/api/report/withholding/generate"/>
<c:url var="apiRefundPrev"  value="/api/report/withholding/refund-prev"/>
<c:url var="apiRefundSave"  value="/api/report/withholding/refund-save"/> 

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8"/>
  <title>源泉徴収状況報告書</title>

  <style>
	/* =========================
	   상단 제목 + 조회 / 上部タイトル + 検索
	   ========================= */
	.wh .page-header{
	  display:flex; align-items:center;
	  padding:6px var(--gutter);
	  margin-bottom:8px;
	  border-bottom:1px solid #eee;
	  background:#fff; border-radius:8px;
	}
	.wh .page-title{
	  font-size:12px; font-weight:700; color:#111;
	  margin-right:auto;
	}
	.wh #btnSearch{
	  height:28px; padding:0 12px;
	}
	
	/* =========================
	   페이지 공통 / ページ共通
	   ========================= */
	html { overflow-y: scroll; }
	body { font-family: Arial, Helvetica, 'Malgun Gothic', sans-serif; font-size:12px; color:#333; }
	
	/* 네임스페이스 컨테이너 / ネームスペースコンテナ */
	.wh .container { --gutter:16px; padding-inline: var(--gutter); box-sizing: border-box; }
	
	/* 카드 & 패널 / カード＆パネル */
	.wh .card, .wh .panel{
	  box-sizing:border-box; width:100%; background:#fff;
	  border:1px solid #e5e7eb; border-radius:8px;
	}
	.wh .card{ padding:8px 10px; margin-bottom:8px; }
	.wh .panel{ padding:10px; border-color:#bbb; border-radius:6px; margin-bottom:22px; }
	.wh .card-title{ font-weight:700; margin-bottom:6px; font-size:12px; }
	
	/* 2컬럼 레이아웃 / 2列レイアウト */
	.wh .two-cols{
	  display:grid; grid-template-columns: minmax(0, 2fr) minmax(0, 1fr);
	  gap:9px; align-items:stretch;
	}
	.wh .two-cols > .card{ min-width:0; }
	
	/* 폼 공통 / フォーム共通 */
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
	
	/* 유틸 너비 / ユーティリティ幅 */
	.wh .w-80{width:80px} .wh .w-100{width:100%} .wh .w-110{width:110px}
	.wh .w-120{width:120px} .wh .w-140{width:140px} .wh .w-160{width:160px}
	.wh .w-180{width:180px}
	
	.wh .min-160 { min-width: 160px; }

	/* 체크박스 라벨 / チェックボックスラベル */
	.wh .chk{ display:flex; align-items:center; gap:6px; color:#374151; font-size:12px; white-space:nowrap; }
	.wh .checks{ display:flex; align-items:center; gap:40px; }
	
	/* 2번째 줄(체크/연말정산연도) 정렬 / 2行目(チェック/年末調整年度)の整列 */
	.wh .row-split{
	  display:grid;
	  grid-template-columns: 1fr auto;
	  align-items:center;
	  column-gap:12px;
	}
	
	.wh .row-split .checks{display:flex; width:100%; gap:12px;}
	.wh .row-split .checks .mid{margin-left: auto; margin-right:250px;}
	
	.wh .row-split .right{justify-self:end; margin-right:210px;}

	/* 버튼 / ボタン */
	.wh .btn, .wh .btn-primary{
	  height:32px; padding:0 12px; border:1px solid #d1d5db; background:#fff; border-radius:6px; cursor:pointer;
	  font-size:12px;
	}
	.wh .btn.primary, .wh .btn-primary{ background:#2563eb; color:#fff; border-color:#2563eb; }
	.wh .btn:hover, .wh .btn-primary:hover{ filter:brightness(0.97); }
	.wh .btn:disabled, .wh .btn-primary:disabled{ opacity:.6; cursor:not-allowed; }
	
	.wh #btnLoad{ margin-right:20px; }
	
	.wh input#annYear.is-readonly{
	  background:#f3f4f6 !important;
	  color:#6b7280 !important;
	  cursor:not-allowed;
	}
	
	/* 탭 / タブ */
	.wh .tabs{ display:flex; gap:8px; margin:16px 0; }
	.wh .tab{ padding:8px 12px; border:1px solid #bbb; border-bottom:none; background:#f7f7f7; cursor:pointer; border-radius:6px 6px 0 0; font-size:12px; }
	.wh .tab.active{ background:#fff; font-weight:bold; }
	
	/* =========================
		신고파일생성 / 報告ファイル生成
	   ========================= */
	.wh .file-card{ position:relative; margin-bottom:4px; }
	.wh .file-card__header{ display:flex; align-items:center; justify-content:space-between; cursor:pointer; user-select:none; }
	.wh .file-card .card-title{ margin-bottom:4px; }
	.wh .file-card__body{ display:block; }
	.wh .file-card.is-collapsed .file-card__body{ display:none; }
	
	.wh .file-grid{
	  --file-name-w: 480px;
	  display:grid;
	  grid-template-columns: max-content var(--file-name-w) 180px;
	  grid-template-areas:
	    "lblName name btns";
	  gap:10px 12px; align-items:center;
	}
	.wh .file-grid{ row-gap:6px; }
	.wh .btns-col{ grid-area: btns; display:flex; flex-direction:column; gap:6px; align-items:stretch; width:100%; }
	.wh .lbl-name{ grid-area: lblName; }
	.wh .in-name{ grid-area: name; }
	
	.wh input#fileName[readonly]{
	  background:#f3f4f6;
	  color:#6b7280;
	  cursor:not-allowed;
	  border-color:#e5e7eb;
	}
	
	/* =========================
		요약/부표 / サマリー/別紙
	   ========================= */
	.wh .data-area { margin-top:4px; }
	.wh .data-area .tabs{
	  position:static; top:0; z-index:2; background:#fff;
	  padding-top:6px; margin-bottom:6px;
	}
	.wh .data-scroll{
	  overflow:auto; border:1px solid #e5e7eb; border-radius:8px; background:#fff; padding:0px;
	}
	.wh .data-scroll .scroll-inner{ padding:8px; }
	
	/* 테이블 스타일 / テーブルスタイル */
	.wh table{ width:100%; border-collapse: separate; border-spacing: 0; font-size:12px; }
	.wh th, .wh td{ border:1px solid #ddd; padding:4px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
	.wh th{ background:#f0f3f7; font-weight: bold; }
	.wh td.num{text-align:right}
	.wh td.center{text-align:center}
	.wh td.code{font-family:Consolas,monospace}
	.wh td.cell-grey { background:#f3f4f6 !important; }
	.wh td.cell-pink { background:#ffe2e2 !important; }

	/* 요약: 2번째 열(소득 구분) 폭 / サマリー: 2番目の列(所得区分)の幅 */
	#panelSummary table th:nth-child(2),
	#panelSummary table td:nth-child(2) { width: 450px; }
	
	/* 부표: 2번째 열(소득 구분) 폭 / 別紙: 2番目の列(所得区分)の幅 */
	#panelAnnex table th:nth-child(2),
	#panelAnnex table td:nth-child(2) { width: 450px; }
	
	#panelSummary table thead th,
	#panelAnnex table thead th {
	  position: sticky;
	  top: 0;
	  z-index: 3;
	}
	
	/* =========================
		전월미환급세액 / 前月未還付税額
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

	#refundBlock input.input-grey {
	  background: #f3f4f6 !important;
	  cursor: default;
	}

	/* =========================
		반응형 / レスポンシブ
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
	      "btns";
	  }
	  .wh .btns-col{ grid-column:1; grid-row:auto; flex-direction:row; }
	  .wh .btns-col .btn-primary{ width:auto; }
	}

  </style>
</head>

<body>
	<!-- 네임스페이스 시작 / ネームスペース開始 -->
	<div class="wh">
	<!-- 데이터 속성으로 API URL 저장 / データ属性でAPI URLを保存 -->
	<div class="container"
     data-summary-url="${apiSummary}"
     data-annex-url="${apiAnnex}"
	 data-generate-url="${apiGenerate}"
	 data-prev-refund-url="${apiRefundPrev}"
	 data-save-refund-url="${apiRefundSave}">
	</div>

	<!-- 페이지 헤더 / ページヘッダー -->
	<div class="page-header">
  <div class="page-title">源泉徴収状況報告書</div>
  <!-- 조회 버튼 / 検索ボタン -->
  <button id="btnSearch" class="btn primary">🔍検索</button>
</div>
	
  <!-- 상단 2컬럼 / 上部2列 -->
  <div class="two-cols">

    <!-- ▣ 신고집계기준 / ▣ 報告集計基準 -->
    <div class="card">
      <div class="card-title">報告集計基準(源泉徴収明細及び別紙)</div>

      <!-- 1줄 / 1行 -->
      <div class="form-row" style="gap: 16px;">
      	<div>
        <span class="label-80 req">源泉税事業場</span>
        <select class="w-110 min-160"><option>源泉税事業場</option></select>
		</div>
		<div>
        <span class="label-80 req">帰属月</span>
        <input id="ym" type="text" class="w-110" value="2025-09"/>
		</div>
		<div>
        <span class="label-80 req">支給月</span>
        <input id="payYm" type="text" class="w-110" placeholder="yyyy-MM"/>
		</div>
		<div>
        <span class="label-80 req">報告年月</span>
        <input id="reportYm" type="text" class="w-110" placeholder="yyyy-MM"/>
		</div>
		<div>
        <span class="label-80">報告区分</span>
        <select class="w-110">
          <option>定期報告</option>
          <option>期限後報告</option>
        </select>
        </div>

        <button id="btnLoad" class="btn primary ml-auto no-shrink">データ生成</button>
      </div>

      <!-- 2줄 / 2行 -->
      <div class="row-split mt-8">
        <div class="checks">
          <label class="chk"><input type="checkbox" id="opt1"> 退職/中途精算支給月基準で集計</label>
          <label class="chk mid"><input type="checkbox" id="opt2"> 年末調整反映</label>
        </div>

        <div class="right pair" style="margin-right:204px">
          <span class="label-80 req">年末調整年度</span>
          <input id="annYear" style="width:84px" type="text" class="w-80" placeholder="YYYY"/>
        </div>
      </div>
    </div>

    <!-- ▣ 출력용 / ▣ 印刷用 -->
    <div class="card">
      <div class="card-title">印刷用</div>

      <!-- 1줄 / 1行 -->
      <div class="form-row" style="gap:20px;">
        <span class="pair">
          <span class="label-80">報告日</span>
          <input id="reportDate" type="text" class="w-80" placeholder="YYYY-MM"/>
        </span>

        <label class="chk"><input type="checkbox" id="optMonthPay" checked> 一括納付</label>
        <label class="chk"><input type="checkbox" id="optBizUnit"> 事業者単位課税</label>
        <label class="chk"><input type="checkbox" id="optConfirm"> 確定</label>
        <label class="chk"><input type="checkbox" id="optAgent"> 税務代理人</label>
      </div>

      <!-- 2줄 / 2行 -->
      <div class="form-row mt-12" style="gap:12px;">
        <span class="label-80" style="margin-right:10px;">報告区分</span>
        <div class="checks">
          <label class="chk"><input type="checkbox" name="repType" checked> 毎月</label>
          <label class="chk"><input type="checkbox" name="repType"> 半期</label>
          <label class="chk"><input type="checkbox" name="repType"> 年末</label>
          <label class="chk"><input type="checkbox" name="repType"> 所得処分</label>
          <label class="chk"><input type="checkbox" name="repType"> 還付税額</label>
        </div>
      </div>
    </div>
  </div><!-- /.two-cols -->

 <!-- ▣ 신고파일생성 / ▣ 報告ファイル生成 -->
<div class="card file-card" id="fileCard">
  <div class="card-title file-card__header" id="fileCardHeader">
    <span>▼ 報告ファイル生成</span>
  </div>

  <div class="file-card__body">
    <div class="file-grid">
      <span class="label-80 lbl-name" style="margin-right:10px;">ファイル名</span>
      <input id="fileName"
             class="in-name"
             type="text"
             readonly
             style="width:448px;"
             placeholder="帰属月で自動生成されます。" />

      <div class="btns-col">
        <button id="btnMake" class="btn-primary">報告ファイル生成</button>
      </div>
    </div>
  </div>
</div>

  <!-- 탭 / タブ -->
  <div class="data-area">
  <div class="tabs">
    <button id="tabSummary" class="tab active">源泉徴収明細及び納付税額(要約)</button>
    <button id="tabAnnex" class="tab">源泉徴収状況報告書(別紙)</button>
  </div>

  <!-- 요약 패널 / サマリーパネル -->
  <div class="data-scroll" id="dataScroll">
   <div class="scroll-inner">
  <div id="panelSummary" class="panel">
    <table id="tblSummary">
      <thead>
        <tr>
          <th style="width:50px;">No</th>
          <th style="width:450px;">所得区分</th>
          <th style="width:60px;">コード</th>
          <th style="width:70px;">人員</th>
          <th style="width:110px;">総支給額</th>
          <th style="width:110px;">徴収農特税</th>
          <th style="width:110px;">納付所得税</th>
          <th style="width:110px;">徴収所得税</th>
          <th style="width:110px;">調整還付税額</th>
          <th style="width:110px;">納付農特税</th>
          <th style="width:110px;">徴収加算税</th>
        </tr>
      </thead>
      <tbody id="gridBody"></tbody>
    </table>
  </div>

  <!-- 부표 패널 / 別紙パネル -->
  <div id="panelAnnex" class="panel" style="display:none;">
    <table id="tblAnnex">
      <thead>
        <tr>
          <th style="width:50px;">No</th>
          <th style="width:450px;">所得区分</th>
          <th style="width:60px;">コード</th>
          <th style="width:70px;">人員</th>
          <th style="width:110px;">総支給額</th>
          <th style="width:110px;">徴収所得税</th>
          <th style="width:110px;">徴収農特税</th>
          <th style="width:110px;">徴収加算税</th>
          <th style="width:110px;">調整還付税額</th>
          <th style="width:110px;">納付所得税</th>
          <th style="width:110px;">納付農特税</th>
        </tr>
      </thead>
      <tbody id="annexBody"></tbody>
    </table>
  </div>
</div>
</div>

  <!-- 전월 미환급세액 / 前月未還付税額 -->
<div class="card" id="refundBlock">
  <div class="card-title">前月未還付税額</div>

  <div class="refund-compact">
    <!-- ① 前月未還付税額 -->
    <div class="row-title">前月未還付税額 :</div><div></div>
<label>(A) 前月未還付税額</label><input id="A" type="text" value="0">
<label>(B) 既還付申請税額</label><input id="B" type="text" value="0">
<label>(C) 差減残額  (A - B)</label><input id="C" type="text" value="0" readonly class="input-grey">
    <span class="placeholder"></span><span></span>

    <!-- ② 当月発生還付税額 -->
    <div class="row-title">当月発生 還付税額 :</div><div></div>
<label>(D) 一般還付</label><input id="D" type="text" value="0" readonly class="input-grey">
<label>(E) 信託資産(金融会社等)</label><input id="E" type="text" value="0">
<label>(F) 金融会社等還付残額</label><input id="F" type="text" value="0">
<label>(G) 合併等還付税額</label><input id="G" type="text" value="0">

    <!-- ③ 還付税額 -->
    <div class="row-title">還付税額 :</div><div></div>
<label>(H) 調整対象還付税額 <br><span style="font-size:12px;color:#666">( C + D + E + F + G )</span></label><input id="H" type="text" value="0" readonly class="input-grey">
<label>(I) 当月調整還付税額</label><input id="I" type="text" value="0" readonly class="input-grey">
<label>(J) 翌月繰越還付税額 (H - I)</label><input id="J" type="text" value="0" readonly class="input-grey">
<label>還付申請金額</label><input id="K" type="text" value="0"> 
  </div>
</div>

</div><!-- /.container -->
</div><!-- /.wh -->

<!-- PDF 저장 / PDF保存 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/jspdf@2.5.1/dist/jspdf.umd.min.js"></script>

<script>
  if (window.jspdf && window.jspdf.jsPDF && !window.jsPDF) {
    window.jsPDF = window.jspdf.jsPDF;
  }
</script>

<script src="https://unpkg.com/jspdf-encrypt/dist/jspdf.plugin.encrypt.min.js"></script>

<script src="<c:url value='/resources/js/withholding.js'/>"></script>
	<script>
		// DOM 로드 완료 후 테이블 정렬 기능 활성화 / DOMロード完了後テーブルソート機能を有効化
		document.addEventListener('DOMContentLoaded', function() {
			enableSort('#tblSummary');
			enableSort('#tblAnnex');
		});
	</script>
</body>
</html>