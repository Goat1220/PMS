<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<ul class="menu">

<style>
/* 리스트 점 제거 */
.menu, .submenu {
  list-style: none;
  margin: 0;
  padding: 0;
}

.menu-section { 
  margin-bottom: 16px; 
}

.section-title {
  font-weight: 600;
  font-size: 13px;
  padding: 8px 14px;
  color: rgba(255,255,255,0.6);
}

.submenu {
  padding-left: 12px;
}

.submenu a {
  display: block;
  padding: 8px 10px;
  border-radius: 6px;
  color: rgba(255,255,255,0.9);
  text-decoration: none;
  font-size: 13px;
  transition: 0.2s;
}

.submenu a:hover {
  background: rgba(255,255,255,0.1);
}

.submenu a.active {
  background: #2563eb;
  color: white;
  font-weight: 600;
  box-shadow: 0 0 6px rgba(37,99,235,0.4);
}
</style>


<li class="menu-section">
  <div class="section-title">급여담당자의 급상여처리</div>
  <ul class="submenu">
    <li><a href="<c:url value='/runpayroll'/>">급상여처리하기</a></li>
    <li><a href="<c:url value='/voucher'/>">급상여전표처리</a></li>
    <li><a href="<c:url value='/report/withholding'/>">신고서확인하기</a></li>
  </ul>
</li>

<li class="menu-section">
  <div class="section-title">개인 급여처리내역 확인</div>
  <ul class="submenu">
    <li><a href="<c:url value='/feature/payslip/view'/>">급여명세서 확인하기</a></li>
  </ul>
</li>

<li class="menu-section">
  <div class="section-title">연말정산 프로세스</div>
  <ul class="submenu">
    <li><a href="<c:url value='/feature/yearend-tax-simulation/view'/>">개인별 연말정산 시뮬레이션</a></li>
    <li><a href="<c:url value='/yearend/result/list'/>">개인별 연말정산 결과조회</a></li>
    <li><a href="<c:url value='/yearend/admin/list'/>">급여담당자의 연말정산 처리/신고</a></li>
  </ul>
</li>
</ul>
