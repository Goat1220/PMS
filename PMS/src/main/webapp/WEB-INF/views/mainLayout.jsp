<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>공용 레이아웃</title>
  <base target="contentFrame">

  <style>
    :root {
      --gap:12px;--radius:10px;--border:#e5e7eb;
      --bg:#f8fafc;--text:#111827;--muted:#6b7280;
      --total:#eaf7ea;--primary:#0ea5e9;
    }
    *{box-sizing:border-box}
    body{margin:0;font-family:system-ui,-apple-system,"Segoe UI",Roboto,"Noto Sans KR","Apple SD Gothic Neo",Arial,"맑은 고딕",sans-serif;
      color:var(--text);background:var(--bg)}
    .app{display:flex;min-height:100vh}

    /* 왼쪽 메뉴 */
    .aside{width:240px;border-right:1px solid var(--border);
      background:#fff;position:sticky;top:0;height:100vh;overflow:auto}
    .logo{padding:16px 20px;font-weight:700;border-bottom:1px solid var(--border)}
    nav{padding:12px}
    .menu{list-style:none;padding:0;margin:0}
    .menu li{margin:4px 0}
    .menu a{display:block;padding:10px 14px;border-radius:var(--radius);
      color:var(--text);text-decoration:none;font-size:14px}
    .menu a:hover{background:#f1f5f9}
    .menu a.active{background:#e0f2fe;font-weight:600}

    /* 오른쪽 영역 */
    .main{flex:1;min-width:0;display:flex;flex-direction:column}
    .header{padding:16px 20px;background:#fff;border-bottom:1px solid var(--border);
      position:sticky;top:0;z-index:5;display:flex;justify-content:space-between;align-items:center}
    .content{flex:1;min-height:0}
    iframe[name="contentFrame"]{width:100%;height:calc(100vh - 60px);border:0}
  </style>

  <script>
    document.addEventListener('DOMContentLoaded', function(){
      const menu = document.querySelector('.menu');
      const setActive = (href)=>{
        [...menu.querySelectorAll('a')].forEach(a=>{
          a.classList.toggle('active', a.getAttribute('href')===href);
        });
      };
      const frame = document.querySelector('iframe[name="contentFrame"]');
      if(frame && frame.getAttribute('src')) setActive(frame.getAttribute('src'));
      menu.addEventListener('click', (e)=>{
        const a = e.target.closest('a'); if(!a) return;
        setActive(a.getAttribute('href'));
      });
    });
  </script>
</head>
<body>
<div class="app">
  <aside class="aside">
    <div class="logo">급여 처리 시스템</div>
    <%@ include file="/WEB-INF/views/includes/leftMenu.jsp" %>
  </aside>

  <section class="main">
    <div class="header">
      <div id="pageTitle">급여 관리</div>
      <div style="color:var(--muted);font-size:13px;">v1.0</div>
    </div>
    <div class="content">
      <iframe name="contentFrame" src="<c:url value='/mainbanner'/>"></iframe>
    </div>
  </section>
</div>

<script>
document.addEventListener('DOMContentLoaded', function(){
    const menu = document.querySelector('.menu');
    const pageTitle = document.getElementById('pageTitle');
    const frame = document.querySelector('iframe[name="contentFrame"]');

    // active 상태 업데이트
    const setActive = (href)=>{
      [...menu.querySelectorAll('a')].forEach(a=>{
        a.classList.toggle('active', a.getAttribute('href')===href);
      });
    };

    // 메뉴 클릭 시
    menu.addEventListener('click', (e)=>{
      const a = e.target.closest('a'); 
      if(!a) return;
      setActive(a.getAttribute('href'));
      pageTitle.textContent = a.textContent.trim();  // ← 제목 갱신
    });

    // 첫 진입 시 iframe의 src에 맞춰 active & 제목 설정
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


</body>
</html>

