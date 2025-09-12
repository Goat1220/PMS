<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<style>
  /* 레이아웃 */
  .qbar { margin:10px 0 12px; display:flex; gap:10px; align-items:center; }
  /* 좌측 1.2, 우측 1 비율 + 우측을 상/하로 나눔 */
  .grid {
    display:grid;
    grid-template-columns: 1.2fr 1fr;
    grid-template-rows: auto auto;
    grid-template-areas:
      "left rightTop"
      "left rightBottom";
    gap:16px;
  }
  .left       { grid-area:left; }
  .right-top  { grid-area:rightTop; }
  .right-btm  { grid-area:rightBottom; }

  /* 카드 & 표 */
  .card { border:1px solid #ddd; border-radius:6px; background:#fff; overflow:hidden; }
  .card .title { padding:10px 12px; font-weight:700; background:#f5f7fa; border-bottom:1px solid #eee; }
  table { width:100%; border-collapse:collapse; }
  th, td { border-bottom:1px solid #eee; padding:8px 10px; text-align:left; }
  th { background:#fafbfc; font-weight:600; }
  .right { text-align:right; white-space:nowrap; }
  .center { text-align:center; }

  /* 목록 행 클릭 */
  .row-select { cursor:pointer; }
  .row-select.active > td { background:#fffbe6; }

  /* 컨트롤 */
  .btn { padding:6px 10px; border:1px solid #ccc; background:#fff; border-radius:4px; cursor:pointer; }
  .input { height:28px; padding:0 8px; border:1px solid #ccc; border-radius:4px; }
  .sel   { height:30px; padding:0 6px; border:1px solid #ccc; border-radius:4px; }
  .muted { color:#888; }

  /* 읽기전용 인풋 */
  .readonly { background:#f3f4f6; color:#6b7280; cursor:not-allowed; }
  .readonly:focus { outline:none; box-shadow:none; }

  .emp-block { display:flex; align-items:center; gap:6px; }
  .emp-name  { width:110px; }
  .emp-no    { width:110px; }

  /* 상단 제목줄(+엑셀버튼 같은 영역 넣을 때 사용 가능) */
  .page-head { display:flex; align-items:center; justify-content:space-between; margin:6px 0 10px; }
  .page-head h2 { margin:0; font-size:18px; }
</style>

<!-- 상단 제목줄 (원하면 오른쪽에 버튼 추가 가능) -->
<div class="page-head">
  <h2>급여명세서(조회/출력)(개인)</h2>
  <!-- <button class="btn">엑셀 추출</button> -->
</div>

<!-- ===================== 조회 조건 바 ===================== -->
<form id="searchForm" method="get" action="">
  <div class="qbar">
    <div>적용연월:
      <input class="input" type="text" name="fromYm" value="${fromYm}" placeholder="YYYY-MM / yyyymm" style="width:110px;">
      ~
      <input class="input" type="text" name="toYm"   value="${toYm}"   placeholder="YYYY-MM / yyyymm" style="width:110px;">
    </div>

    <div>급상여종류:
      <select class="sel" name="payType">
        <option value="" <c:if test="${empty payType}">selected</c:if>>전체</option>
        <c:forEach var="c" items="${payTypeCodes}">
          <option value="${c.code}" <c:if test="${payType == c.code}">selected</c:if>>
            ${c.name} (${c.code})
          </option>
        </c:forEach>
      </select>
    </div>

    <div class="emp-block">
      <span>사원:</span>
      <input class="input readonly emp-name" type="text" value="${empName}" readonly aria-readonly="true">
      <input class="input readonly emp-no"   type="text" name="empNo" value="${empNo}" readonly aria-readonly="true">
    </div>

    <label style="user-select:none;">
      <input type="checkbox" name="excludeZero" value="Y" <c:if test="${excludeZero == 'Y'}">checked</c:if> />
      금액 0 미출력
    </label>

    <button type="submit" class="btn">조회</button>

    <!-- 선택된 payslip id 보관 -->
    <input type="hidden" name="selectedId" id="selectedId"
           value="<c:out value='${selected != null ? selected.payslipId : param.selectedId}'/>">
  </div>
</form>

<!-- ===================== 본문 ===================== -->
<div class="grid">

  <!-- 좌측: 월별급상여내역 목록 -->
  <div class="card left">
    <div class="title">월별급상여내역</div>
    <table id="listTbl">
      <thead>
        <tr>
          <th>급상여종류</th>
          <th>적용연월</th>
          <th class="right">지급총액</th>
          <th class="right">기지급액</th>
          <th class="right">공제총액</th>
          <th class="right">실지급액</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="row" items="${rows}">
          <tr class="row-select" data-id="${row.payslipId}">
            <td><c:out value="${empty row.payTypeName ? row.payType : row.payTypeName}"/></td>
            <td><c:out value="${row.periodYm}"/></td>
            <td class="right"><fmt:formatNumber value="${row.grossAmount}" pattern="#,###"/></td>
            <td class="right"><fmt:formatNumber value="${row.prevPaidAmount}" pattern="#,###"/></td>
            <td class="right"><fmt:formatNumber value="${row.deductionSum}" pattern="#,###"/></td>
            <td class="right"><fmt:formatNumber value="${row.netAmount}" pattern="#,###"/></td>
          </tr>
        </c:forEach>
        <c:if test="${empty rows}">
          <tr><td colspan="6" class="muted">데이터가 없습니다. (사번/연월을 확인하세요)</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>

  <!-- 우측 상단: 지급항목내역 (체크박스 제거, 텍스트 Y/N) -->
  <div class="card right-top">
    <div class="title">지급항목내역</div>
    <table>
      <thead>
        <tr>
          <th style="width:40px;">#</th>
          <th class="center" style="width:90px;">기지급여부</th>
          <th class="center" style="width:100px;">인정성여부</th>
          <th>항목명</th>
          <th class="right" style="width:120px;">금액</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${not empty selected and not empty selected.payItems}">
            <c:forEach var="it" items="${selected.payItems}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td class="center"><c:out value="${it.chkPaid}"/></td>
                <td class="center"><c:out value="${it.chkValid}"/></td>
                <td><c:out value="${it.itemName}"/></td>
                <td class="right"><fmt:formatNumber value="${it.amount}" pattern="#,###"/></td>
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr><td colspan="5" class="muted">선택된 내역이 없습니다.</td></tr>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>

<div class="card right-btm">
  <div class="title">공제항목내역</div>
  <table>
    <thead>
      <tr>
        <th>공제항목</th>
        <th style="width:140px;">공제항목코드</th>  <!-- 추가 -->
        <th class="right" style="width:140px;">금액</th>
      </tr>
    </thead>
    <tbody>
      <c:set var="dedTotal" value="0"/>
      <c:choose>
        <c:when test="${not empty selected and not empty selected.deductionItems}">
          <c:forEach var="d" items="${selected.deductionItems}">
            <tr>
              <td><c:out value="${d.itemName}"/></td>
              <td>
                <c:choose>
                  <c:when test="${empty d.itemCode}">-</c:when>
                  <c:otherwise><c:out value="${d.itemCode}"/></c:otherwise>
                </c:choose>
              </td>
              <td class="right">
                <fmt:formatNumber value="${d.amount}" pattern="#,###"/>
                <c:set var="dedTotal" value="${dedTotal + d.amount}"/>
              </td>
            </tr>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <!-- 컬럼 3개로 변경했으니 colspan=3 -->
          <tr><td colspan="3" class="muted">공제 내역이 없습니다.</td></tr>
        </c:otherwise>
      </c:choose>
      <tr>
        <!-- 합계 라벨은 두 칸 병합 -->
        <th colspan="2">TOTAL</th>
        <th class="right"><fmt:formatNumber value="${dedTotal}" pattern="#,###"/></th>
      </tr>
    </tbody>
  </table>
</div>


</div>

<script>
  (function () {
    var form = document.getElementById('searchForm');
    var hid  = document.getElementById('selectedId');

    // 좌측 목록 클릭 → 상세 갱신
    Array.prototype.forEach.call(
      document.querySelectorAll('#listTbl tbody tr.row-select'),
      function (tr) {
        tr.addEventListener('click', function () {
          hid.value = this.dataset.id;
          form.submit();
        });
      }
    );

    // 현재 선택 강조
    var cur = hid.value;
    if (cur) {
      var sel = document.querySelector('#listTbl tbody tr.row-select[data-id="' + cur + '"]');
      if (sel) sel.classList.add('active');
    }

    // 연월 입력 보정(6자리 → YYYY-MM)
    function normalizeYmInput(inp){
      inp.addEventListener('blur', function(){
        var v = (this.value || '').trim();
        if (/^\d{6}$/.test(v)) this.value = v.substring(0,4) + '-' + v.substring(4,6);
      });
    }
    normalizeYmInput(document.querySelector('input[name="fromYm"]'));
    normalizeYmInput(document.querySelector('input[name="toYm"]'));
  })();
</script>

