<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<style>
  /* ============================
     레이아웃/테이블 기본 스타일
     レイアウト／テーブル基本スタイル
  ============================ */
  .qbar { margin:10px 0 12px; display:flex; gap:10px; align-items:center; } /* 조회 조건 영역 / 検索条件バー */
  .grid {
    display:grid; gap:16px;
    grid-template-columns: 1.2fr 1fr;
    grid-template-rows: auto auto;
    grid-template-areas: "left rightTop" "left rightBottom";
  }
  .left{grid-area:left;} .right-top{grid-area:rightTop;} .right-btm{grid-area:rightBottom;}

  .card{ border:1px solid #ddd; border-radius:6px; background:#fff; overflow:hidden; } /* 카드 컨테이너 / カードコンテナ */
  .card .title{ padding:10px 12px; font-weight:700; background:#f5f7fa; border-bottom:1px solid #eee; } /* 카드 제목 / カード見出し */

  table{ width:100%; border-collapse:collapse; table-layout:fixed; } /* 고정 레이아웃 / 固定レイアウト */
  th,td{ border:1px solid #cfd8dc; padding:6px 8px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
  thead th{ background:#fafbfc; text-align:center; } /* 헤더 배경 / ヘッダー背景 */
  .right{ text-align:right; } .center{ text-align:center; }

  /* 좌측 월별급상여내역 테이블
     左側 月別支給・賞与一覧テーブル */
  #listTbl { table-layout: fixed; }
  #listTbl thead th:first-child,
  #listTbl tbody td:first-child { width:36px; text-align:center; background:#f5f5f5; } /* 번호 열 / 番号列 */
  #listTbl tbody tr.active td { background:#fff799; } /* 선택 행 강조 / 選択行の強調 */
  #listTbl tbody tr.active td:first-child { background:#f5f5f5; }

  /* 입력 컨트롤 공통
     入力コントロール共通 */
  .btn{ padding:6px 10px; border:1px solid #ccc; background:#fff; border-radius:4px; cursor:pointer; }
  .input{ height:28px; padding:0 8px; border:1px solid #ccc; border-radius:4px; }
  .sel{ height:30px; padding:0 6px; border:1px solid #ccc; border-radius:4px; }
  .muted{ color:#888; }

  /* 읽기전용 입력
     読み取り専用入力 */
  .readonly{ background:#f3f4f6; color:#6b7280; cursor:not-allowed; }
  .readonly:focus{ outline:none; box-shadow:none; }
  .emp-block{ display:flex; align-items:center; gap:6px; }
  .emp-name{ width:110px; } .emp-no{ width:110px; }

  /* 합계(0번행) 강조 스타일
     合計(0行目)強調スタイル */
  .total-row th, .total-row td{ background:#e8f5e9; font-weight:700; }

  /* 페이지 첫 요소 상단여백 차단(안전)
     ページ先頭マージン対策(安全策) */
  html, body { margin:0; padding:0; }
  body > *:first-child { margin-top:0; padding-top:0; }
</style>

<!-- ==========================================
     페이지 헤더: 제목 + 엑셀 추출 버튼
     ページヘッダー：タイトル＋Excel出力ボタン
========================================== -->
<div class="page-head" style="display:flex;align-items:center;justify-content:space-between;margin:6px 0 10px;">
  <h2 style="margin:0;font-size:18px;">급여명세서(조회/출력)(개인)</h2> <!-- 제목 / タイトル -->
  <button class="btn" onclick="downloadPayslipExcel()">엑셀 추출</button> <!-- 엑셀(CSV) 다운로드 / Excel(CSV) ダウンロード -->
</div>

<!-- ==========================================
     조회 폼 영역
     検索フォーム領域
========================================== -->
<form id="searchForm" method="get" action="">
  <div class="qbar">
    <div>적용연월: <!-- 適用年月 -->
      <input class="input" type="text" name="fromYm" value="${fromYm}" placeholder="YYYY-MM / yyyymm" style="width:110px;">
      ~
      <input class="input" type="text" name="toYm"   value="${toYm}"   placeholder="YYYY-MM / yyyymm" style="width:110px;">
    </div>

    <div>급상여종류: <!-- 支給／賞与区分 -->
      <select class="sel" name="payType">
        <option value="" <c:if test="${empty payType}">selected</c:if>>전체</option> <!-- 全体 -->
        <c:forEach var="c" items="${payTypeCodes}">
          <option value="${c.code}" <c:if test="${payType == c.code}">selected</c:if>>
            ${c.name} (${c.code})
          </option>
        </c:forEach>
      </select>
    </div>

    <div class="emp-block">
      <span>사원:</span> <!-- 社員 -->
      <input class="input readonly emp-name" type="text" value="${empName}" readonly aria-readonly="true">
      <input class="input readonly emp-no"   type="text" name="empNo" value="${empNo}" readonly aria-readonly="true">
    </div>

    <label style="user-select:none;">
      <input type="checkbox" name="excludeZero" value="Y" <c:if test="${excludeZero == 'Y'}">checked</c:if> />
      금액 0 미출력 <!-- 金額0を非表示 -->
    </label>

    <button type="submit" class="btn">조회</button> <!-- 検索 -->
    <input type="hidden" name="selectedId" id="selectedId"
           value="<c:out value='${selected != null ? selected.payslipId : param.selectedId}'/>"><!-- 현재 선택 payslipId / 現在選択のpayslipId -->
  </div>
</form>

<!-- ==========================================
     본문: 좌/우 그리드
     本文：左／右グリッド
========================================== -->
<div class="grid">

  <!-- 좌측: 월별급상여내역 / 左：月別支給・賞与一覧 -->
  <div class="card left">
    <div class="title">월별급상여내역</div>
    <table id="listTbl">
      <thead>
        <tr>
          <th>⚙</th>
          <th>급상여종류</th>
          <th>적용연월</th>
          <th class="right">지급총액</th>
          <th class="right">기지급액</th>
          <th class="right">공제총액</th>
          <th class="right">실지급액</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="row" items="${rows}" varStatus="st">
          <tr class="row-select" data-id="${row.payslipId}"> <!-- 클릭 시 상세 갱신 / クリックで詳細更新 -->
            <td class="center">${st.index + 1}</td>
            <td><c:out value="${empty row.payTypeName ? row.payType : row.payTypeName}"/></td>
            <td><c:out value="${row.periodYm}"/></td>
            <td class="right"><fmt:formatNumber value="${row.grossAmount}"   pattern="#,###"/></td>
            <td class="right"><fmt:formatNumber value="${row.prevPaidAmount}" pattern="#,###"/></td>
            <td class="right"><fmt:formatNumber value="${row.deductionSum}"   pattern="#,###"/></td>
            <td class="right"><fmt:formatNumber value="${row.netAmount}"      pattern="#,###"/></td>
          </tr>
        </c:forEach>
        <c:if test="${empty rows}">
          <tr><td colspan="7" class="muted">데이터가 없습니다. (사번/연월을 확인하세요)</td></tr> <!-- データなし -->
        </c:if>
      </tbody>
    </table>
  </div>

  <!-- 우측 상단: 지급항목내역(0번행=합계) / 右上：支給項目(0行目=合計) -->
  <div class="card right-top">
    <div class="title">지급항목내역</div>

    <!-- 합계 계산 / 合計計算 -->
    <c:set var="payTotal" value="0"/>
    <c:if test="${not empty selected and not empty selected.payItems}">
      <c:forEach var="pi" items="${selected.payItems}">
        <c:set var="payTotal" value="${payTotal + pi.amount}"/>
      </c:forEach>
    </c:if>

    <table id="payTbl">
      <thead>
        <tr>
          <th>⚙</th>
          <th>기지급여부</th>
          <th>인정상여여부</th>
          <th class="right">금액</th>
        </tr>
      </thead>
      <tbody>
        <!-- 0번 라인: 합계 표시 / 0行目：合計表示 -->
        <tr class="total-row">
          <td>0</td>
          <td class="center">-</td>
          <td class="center">-</td>
          <td class="right"><fmt:formatNumber value="${payTotal}" pattern="#,###"/></td>
        </tr>

        <c:choose>
          <c:when test="${not empty selected and not empty selected.payItems}">
            <c:forEach var="it" items="${selected.payItems}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td class="center">
                  <input type="checkbox" disabled <c:if test="${it.chkPaid=='Y'}">checked</c:if> />
                </td>
                <td class="center">
                  <input type="checkbox" disabled <c:if test="${it.chkValid=='Y'}">checked</c:if> />
                </td>
                <td class="right"><fmt:formatNumber value="${it.amount}" pattern="#,###"/></td>
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr><td colspan="4" class="muted">지급 내역이 없습니다.</td></tr> <!-- 支給内訳なし -->
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>

  <!-- 우측 하단: 공제항목내역(0번행=TOTAL) / 右下：控除項目(0行目=TOTAL) -->
  <div class="card right-btm">
    <div class="title">공제항목내역</div>

    <!-- 합계 계산 / 合計計算 -->
    <c:set var="dedTotal" value="0"/>
    <c:if test="${not empty selected and not empty selected.deductionItems}">
      <c:forEach var="dx" items="${selected.deductionItems}">
        <c:set var="dedTotal" value="${dedTotal + dx.amount}"/>
      </c:forEach>
    </c:if>

    <table id="dedTbl">
      <thead>
        <tr>
          <th>⚙</th>
          <th>공제항목</th>
          <th>공제항목코드</th>
          <th class="right">금액</th>
        </tr>
      </thead>
      <tbody>
        <!-- 0번 라인: TOTAL / 0行目：TOTAL -->
        <tr class="total-row">
          <td>0</td>
          <td>TOTAL</td>
          <td></td>
          <td class="right"><fmt:formatNumber value="${dedTotal}" pattern="#,###"/></td>
        </tr>

        <c:choose>
          <c:when test="${not empty selected and not empty selected.deductionItems}">
            <c:forEach var="d" items="${selected.deductionItems}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td><c:out value="${d.itemName}"/></td>
                <td><c:out value="${empty d.itemCode ? '-' : d.itemCode}"/></td>
                <td class="right"><fmt:formatNumber value="${d.amount}" pattern="#,###"/></td>
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr><td colspan="4" class="muted">공제 내역이 없습니다.</td></tr> <!-- 控除内訳なし -->
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>

</div>

<script>
  (function(){
    // 좌측 목록 클릭 시 상세 갱신 / 左テーブル行クリックで詳細更新
    var form = document.getElementById('searchForm');
    var hid  = document.getElementById('selectedId');

    document.querySelectorAll('#listTbl tbody tr.row-select').forEach(function(tr){
      tr.addEventListener('click', function(){
        hid.value = this.dataset.id; // 선택된 payslipId / 選択されたpayslipId
        form.submit();
      });
    });

    // 현재 선택 강조 / 現在選択行の強調表示
    var cur = hid.value;
    if (cur){
      var sel = document.querySelector('#listTbl tbody tr.row-select[data-id="'+cur+'"]');
      if (sel) sel.classList.add('active');
    }

    // yyyymm → yyyy-mm 자동 변환 / 自動整形
    function normalizeYmInput(inp){
      inp.addEventListener('blur', function(){
        var v = (this.value || '').trim();
        if (/^\d{6}$/.test(v)) this.value = v.substring(0,4)+'-'+v.substring(4,6);
      });
    }
    normalizeYmInput(document.querySelector('input[name="fromYm"]'));
    normalizeYmInput(document.querySelector('input[name="toYm"]'));
  })();
</script>

<!-- ==========================================
     정렬/합계 상단 고정/엑셀 추출 (폴백 포함)
     並べ替え／合計先頭固定／Excel出力（フォールバック付き）
========================================== -->
<script>
(function(){
  "use strict";

  /* 합계행을 tbody 최상단으로 고정
     合計行をtbodyの先頭に固定 */
  function pinTotalRowsToTop(tableSelector) {
    var table = document.querySelector(tableSelector);
    if (!table) { console.warn("[pinTotalRowsToTop] 테이블 못 찾음:", tableSelector); return; }
    var tbody = table.tBodies && table.tBodies[0];
    if (!tbody) { console.warn("[pinTotalRowsToTop] tbody 없음:", tableSelector); return; }

    var rows = Array.prototype.slice.call(tbody.rows);
    function isTotal(tr){
      var first = (tr.cells[0] && tr.cells[0].innerText) || "";
      var last  = (tr.cells[tr.cells.length-1] && tr.cells[tr.cells.length-1].innerText) || "";
      return tr.classList.contains("total-row") ||
             tr.getAttribute("data-role") === "total" ||
             /합계|총계|total/i.test(first + last);
    }

    var totals  = rows.filter(isTotal);
    var normals = rows.filter(function(r){ return !isTotal(r); });

    tbody.innerHTML = "";
    totals.forEach(function(tr){ tr.setAttribute("data-pinned","true"); tbody.appendChild(tr); });
    normals.forEach(function(tr){ tbody.appendChild(tr); });
  }

  /* 헤더 클릭 정렬(숫자/문자 자동 판별)
     ヘッダクリックで並べ替え（数値／文字自動判定）
     ※ 공통 enableSort가 없을 때만 주입 / 共通が無い場合のみ注入 */
  if (typeof window.enableSort !== "function") {
    window.enableSort = function(selector) {
      var table = document.querySelector(selector);
      if (!table) { console.warn("[enableSort] 테이블 못 찾음:", selector); return; }
      var tbody = table.tBodies && table.tBodies[0];
      if (!tbody) { console.warn("[enableSort] tbody 없음:", selector); return; }

      var ths = table.tHead ? Array.prototype.slice.call(table.tHead.rows[0].cells) : [];
      ths.forEach(function(th, colIdx){
        th.style.cursor = "pointer";
        th.addEventListener("click", function(){
          var pinned = Array.prototype.slice.call(tbody.querySelectorAll('tr[data-pinned="true"]'));
          var rows = Array.prototype.slice.call(tbody.rows).filter(function(tr){
            return tr.getAttribute("data-pinned") !== "true";
          });

          var dir = th.getAttribute("data-sortdir") === "asc" ? "desc" : "asc";
          ths.forEach(function(h){ h.removeAttribute("data-sortdir"); });
          th.setAttribute("data-sortdir", dir);

          rows.sort(function(a,b){
            var av = (a.cells[colIdx] && a.cells[colIdx].innerText || "").trim();
            var bv = (b.cells[colIdx] && b.cells[colIdx].innerText || "").trim();
            var an = av.replace(/,/g,"");
            var bn = bv.replace(/,/g,"");
            var aNum = an !== "" && !isNaN(an);
            var bNum = bn !== "" && !isNaN(bn);
            var cmp  = aNum && bNum ? (Number(an) - Number(bn)) : av.localeCompare(bv, "ko");
            return dir === "asc" ? cmp : -cmp;
          });

          tbody.innerHTML = "";
          pinned.forEach(function(tr){ tbody.appendChild(tr); });
          rows.forEach(function(tr){ tbody.appendChild(tr); });
        });
      });
    };
  }

  /* 테이블을 CSV로 다운로드(엑셀 호환)
     テーブルをCSVでダウンロード（Excel互換）
     ※ 공통 exportTableToExcel이 없을 때만 주입 / 共通が無い場合のみ注入 */
  if (typeof window.exportTableToExcel !== "function") {
    window.exportTableToExcel = function(selector, filename) {
      var table = document.querySelector(selector);
      if (!table) { alert("내보낼 테이블을 찾지 못했습니다: " + selector); return; }
      var lines = Array.prototype.slice.call(table.rows).map(function(tr){
        return Array.prototype.slice.call(tr.cells).map(function(td){
          var t = (td.innerText || "").replace(/\r?\n|\r/g, " ").trim(); // 줄바꿈 제거 / 改行除去
          t = t.replace(/"/g, '""'); // " 이스케이프 / ダブルクォートエスケープ
          return '"' + t + '"';
        }).join(",");
      }).join("\r\n");

      var blob = new Blob(["\uFEFF" + lines], { type: "text/csv;charset=utf-8;" }); // BOM으로 한글 깨짐 방지 / BOMで日本語化け防止
      var url  = URL.createObjectURL(blob);
      var a    = document.createElement("a");
      a.href = url;
      a.download = filename || "table.csv";
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    };
  }

  /* 페이지 로드시 기능 연결
     ページ読み込み時に機能を適用 */
  window.addEventListener("DOMContentLoaded", function () {
    try { enableSort("#listTbl"); } catch(e){ console.error(e); }
    try { enableSort("#payTbl");  } catch(e){ console.error(e); }
    try { enableSort("#dedTbl");  } catch(e){ console.error(e); }
    try { pinTotalRowsToTop("#payTbl"); } catch(e){ console.error(e); }
    try { pinTotalRowsToTop("#dedTbl"); } catch(e){ console.error(e); }
  });

  /* 엑셀 버튼 핸들러(전역 노출)
     Excelボタン用ハンドラ（グローバル公開） */
  window.downloadPayslipExcel = function(){
    try { exportTableToExcel("#listTbl","급여명세_요약.csv"); }
    catch(e) { console.error(e); alert("CSV 내보내기 중 오류가 발생했습니다."); }
  };

})();  // IIFE 종료 / IIFE 終了
</script>

<!-- ==========================================
    include를 '보이지 않게' 하단 로드
     余白原因対策：includeを「非表示」でページ下部に読み込み
     ※ 스크립트/스타일은 정상 로드됨 / script・styleは正常に読み込まれます
========================================== -->
<div style="display:none" aria-hidden="true">
  <%@ include file="/WEB-INF/views/includes/form.jsp" %>
  <%@ include file="/WEB-INF/views/includes/table.jsp" %>
</div>
