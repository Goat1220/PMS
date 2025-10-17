<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급여 처리 시스템</title>
<base target="contentFrame">

<style>
:root {
  --radius: 10px;
  --border: #e5e7eb;
  --bg: #f9fafb;
  --text: #1e293b;
  --muted: #64748b;
}

body {
  margin: 0;
  font-family: "Noto Sans KR", "Segoe UI", system-ui, sans-serif;
  color: var(--text);
  background: var(--bg);
}

.app {
  display: grid;
  grid-template-columns: 240px 1fr;
  height: 100vh;
}

.aside {
  background: #0f172a; /* 어두운 남색 */
  color: white;
}

.main {
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.logo {
  font-weight: 700;
  font-size: 18px;
  height: 60px;               /* 높이를 명확히 지정 */
  display: flex;
  align-items: center;
  padding-left: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  box-sizing: border-box;
}

.header {
  background: #fff;
  height: 60px;               /* logo와 동일한 높이 */
  padding: 0 24px;            /* 상하 padding 제거 */
  border-bottom: 1px solid var(--border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

#pageTitle {
  font-weight: 600;
  font-size: 16px;
}

.version {
  color: var(--muted);
  font-size: 13px;
}

.content {
  flex: 1;
  padding: 24px;
  overflow: hidden;
}

iframe[name="contentFrame"] {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: var(--radius);
  background: #fff;
  box-shadow: 0 1px 6px rgba(0,0,0,0.08);
  zoom: 0.73;
  transform-origin: top left;
}
</style>

<script>
document.addEventListener('DOMContentLoaded', function(){
  const menu = document.querySelector('.menu');
  const pageTitle = document.getElementById('pageTitle');
  const frame = document.querySelector('iframe[name="contentFrame"]');

  const setActive = (href)=>{
    [...menu.querySelectorAll('a')].forEach(a=>{
      a.classList.toggle('active', a.getAttribute('href')===href);
    });
  };

  menu.addEventListener('click', (e)=>{
    const a = e.target.closest('a');
    if(!a) return;
    setActive(a.getAttribute('href'));
    pageTitle.textContent = a.textContent.trim();
  });

  if(frame && frame.getAttribute('src')){
    const current = [...menu.querySelectorAll('a')]
      .find(a => a.getAttribute('href') === frame.getAttribute('src'));
    if(current){
      setActive(current.getAttribute('href'));
      pageTitle.textContent = current.textContent.trim();
    }
  }
});
</script>
</head>

<body>
<div class="app">
  <aside class="aside">
    <div class="logo" style="font-weight:700;font-size:18px;padding:20px;border-bottom:1px solid rgba(255,255,255,0.1)">급여 처리 시스템</div>
    <%@ include file="/WEB-INF/views/includes/leftMenu.jsp"%>
  </aside>

  <section class="main">
    <div class="header">
      <div id="pageTitle">급여 관리</div>
      <div class="version">v1.0</div>
    </div>
    <div class="content">
      <iframe name="contentFrame" src="<c:url value='/mainbanner'/>"></iframe>
    </div>
  </section>
</div>
</body>
</html>
