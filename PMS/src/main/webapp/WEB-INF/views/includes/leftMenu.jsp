<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<ul class="menu">

<style>
.menu-section { margin-bottom: 16px; }
.section-title {
  font-weight: 600;
  font-size: 14px;
  padding: 10px 12px;
  color: var(--muted);
  border-left: 3px solid transparent;
}
.submenu { list-style: none; margin: 0; padding-left: 20px; }
.submenu li { margin: 4px 0; }
.submenu a {
  display: block;
  padding: 8px 10px;
  border-radius: var(--radius);
  text-decoration: none;
  font-size: 13px;
  color: var(--text);
}
.submenu a:hover { background: #f1f5f9; }
.submenu a.active { background: #e0f2fe; font-weight: 600; }

</style>



  <!-- 1. 급상여 처리 -->
  <li class="menu-section">
    <div class="section-title">급여담당자의 급상여처리</div>
    <ul class="submenu">
      <li><a href="<c:url value='/'/>">급상여처리하기</a></li>
      <li><a href="<c:url value='/pages/bonusVoucher.do'/>">급상여전표처리</a></li>
      <li><a href="<c:url value='/pages/reportCheck.do'/>">신고서확인하기</a></li>
    </ul>
  </li>

  <!-- 2. 개인 급여처리내역 -->
  <li class="menu-section">
    <div class="section-title">개인 급여처리내역 확인</div>
    <ul class="submenu">
      <li><a href="<c:url value='/pages/payslip.do'/>">급여명세서 확인하기</a></li>
    </ul>
  </li>

  <!-- 3. 연말정산 -->
  <li class="menu-section">
    <div class="section-title">연말정산 프로세스</div>
    <ul class="submenu">
      <li><a href="<c:url value='/pages/taxSim.do'/>">개인별 연말정산 시뮬레이션</a></li>
      <li><a href="<c:url value='/pages/taxResult.do'/>">개인별 연말정산 결과조회</a></li>
      <li><a href="<c:url value='/pages/taxProcess.do'/>">급여담당자의 연말정산 처리/신고</a></li>
    </ul>
  </li>
</ul>
