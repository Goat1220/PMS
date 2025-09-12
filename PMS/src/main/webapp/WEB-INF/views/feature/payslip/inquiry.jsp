<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<style>
  /* ===== 레이아웃 ===== */
  .qbar { margin:10px 0 12px; display:flex; gap:10px; align-items:center; }
  .grid {
    display:grid; gap:16px;
    grid-template-columns: 1.2fr 1fr;
    grid-template-rows: auto auto;
    grid-template-areas: "left rightTop" "left rightBottom";
  }
  .left{grid-area:left;} .right-top{grid-area:rightTop;} .right-btm{grid-area:rightBottom;}

  /* ===== 카드/표 기본 ===== */
  .card{ border:1px solid #ddd; border-radius:6px; background:#fff; overflow:hidden; }
  .card .title{ padding:10px 12px; font-weight:700; background:#f5f7fa; border-bottom:1px solid #eee; }

  table{ width:100%; border-collapse:collapse; table-layout:fixed; }
  th,td{ border:1px solid #cfd8dc; padding:6px 8px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
  thead th{ background:#fafbfc; text-align:center; }  /* 헤더는 회색 고정 */
  .right{ text-align:right; } .center{ text-align:center; }

  /* 좌측 목록(월별급상여내역) 전용 */
  #listTbl { table-layout: fixed; }
  #listTbl thead th:first-child,
  #listTbl tbody td:first-child { width:36px; text-align:center; background:#f5f5f5; }
  #listTbl tbody tr.active td { background:#fff799; }            /* 선택행 노란 강조 */
  #listTbl tbody tr.active td:first-child { background:#f5f5f5; } /* 1열은 계속 회색 */

  /* 입력 컨트롤 */
  .btn{ padding:6px 10px; border:1px solid #ccc; background:#fff; border-radius:4px; cursor:pointer; }
  .input{ height:28px; padding:0 8px; border:1px solid #ccc; border-radius:4px; }
  .sel{ height:30px; padding:0 6px; border:1px solid #ccc; border-radius:4px; }
  .muted{ color:#888; }

  /* 읽기전용 인풋 */
  .readonly{ background:#f3f4f6; color:#6b7280; cursor:not-allowed; }
  .readonly:focus{ outline:none; box-shadow:none; }
  .emp-block{ display:flex; align-items:center; gap:6px; }
  .emp-name{ width:110px; } .emp-no{ width:110px; }

  /* 합계/0번 라인만 연두 */
  .total-row th, .total-row td{ background:#e8f5e9; font-weight:700; }
</style>

<div class="page-head" style="display:flex;align-items:center;justify-content:space-between;margin:6px 0 10px;">
  <h2 style="margin:0;font-size:18px;">급여명세서(조회/출력)(개인)</h2>
  <!-- <button class="btn" onclick="window.print()">인쇄</button> -->
</div>

<!-- ============ 조회 조건 바 ============ -->
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

    <input type="hidden" name="selectedId" id="selectedId"
           value="<c:out value='${selected != null ? selected.payslipId : param.selectedId}'/>">
  </div>
</form>

<!-- ============ 본문 ============ -->
<div class="grid">

  <!-- 좌측: 월별급상여내역 (요청 양식 반영) -->
  <div class="card left">
    <div class="title">월별급상여내역</div>
    <table id="listTbl">
      <thead>
        <tr>
          <th>⚙</th>  <!-- 번호/기어 -->
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
          <tr class="row-select" data-id="${row.payslipId}">
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
          <tr><td colspan="7" class="muted">데이터가 없습니다. (사번/연월을 확인하세요)</td></tr>
        </c:if>
      </tbody>
    </table>
  </div>

  <!-- 우측 상단: 지급항목내역 (0번 라인만 연두, 체크박스 제거) -->
<div class="card right-top">
  <div class="title">지급항목내역</div>

  <!-- 합계 계산 (0행에 표시) -->
  <c:set var="payTotal" value="0"/>
  <c:if test="${not empty selected and not empty selected.payItems}">
    <c:forEach var="pi" items="${selected.payItems}">
      <c:set var="payTotal" value="${payTotal + pi.amount}"/>
    </c:forEach>
  </c:if>

  <table>
    <thead>
      <tr>
        <th>⚙</th>
        <th>기지급여부</th>
        <th>인정상여여부</th>
        <th class="right">금액</th>
      </tr>
    </thead>
    <tbody>
      <!-- 0번 라인: 체크박스 대신 대시(-) -->
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
          <tr><td colspan="4" class="muted">지급 내역이 없습니다.</td></tr>
        </c:otherwise>
      </c:choose>
    </tbody>
  </table>
</div>


  <!-- 우측 하단: 공제항목내역 (TOTAL 행만 연두) -->
  <div class="card right-btm">
    <div class="title">공제항목내역</div>

    <!-- 합계 계산 (TOTAL에 표시) -->
    <c:set var="dedTotal" value="0"/>
    <c:if test="${not empty selected and not empty selected.deductionItems}">
      <c:forEach var="dx" items="${selected.deductionItems}">
        <c:set var="dedTotal" value="${dedTotal + dx.amount}"/>
      </c:forEach>
    </c:if>

    <table>
      <thead>
        <tr>
          <th>⚙</th>
          <th>공제항목</th>
          <th>공제항목코드</th>
          <th class="right">금액</th>
        </tr>
      </thead>
      <tbody>
        <!-- TOTAL -->
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
            <tr><td colspan="4" class="muted">공제 내역이 없습니다.</td></tr>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>

</div>

<script>
  (function(){
    var form = document.getElementById('searchForm');
    var hid  = document.getElementById('selectedId');

    // 좌측 목록 클릭 → 상세 갱신
    document.querySelectorAll('#listTbl tbody tr.row-select').forEach(function(tr){
      tr.addEventListener('click', function(){
        hid.value = this.dataset.id;
        form.submit();
      });
    });

    // 현재 선택 강조(노란색)
    var cur = hid.value;
    if (cur){
      var sel = document.querySelector('#listTbl tbody tr.row-select[data-id="'+cur+'"]');
      if (sel) sel.classList.add('active');
    }

    // 연월 입력 보정(6자리 → YYYY-MM)
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

