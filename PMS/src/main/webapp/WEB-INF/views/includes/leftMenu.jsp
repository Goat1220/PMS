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
  <div class="section-title">給与担当者の給与処理</div>
  <ul class="submenu">
    <li><a href="<c:url value='/runpayroll'/>">昇給処理</a></li>
    <li><a href="<c:url value='/voucher'/>">給与伝票処理</a></li>
    <li><a href="<c:url value='/report/withholding'/>">申告書確認</a></li>
  </ul>
</li>

<li class="menu-section">
  <div class="section-title">個人給与処理内訳確認</div>
  <ul class="submenu">
    <li><a href="<c:url value='/feature/payslip/view'/>">給与明細書確認</a></li>
  </ul>
</li>

<li class="menu-section">
  <div class="section-title">年末調整プロセス</div>
  <ul class="submenu">
    <li><a href="<c:url value='/feature/yearend-tax-simulation/view'/>">個人別年末調整シミュレーション</a></li>
    <li><a href="<c:url value='/yearend/result/list'/>">個人別年末調整結果照会</a></li>
    <li><a href="<c:url value='/yearend/admin/list'/>">年末調整処理/申告</a></li>
  </ul>
</li>
</ul>
