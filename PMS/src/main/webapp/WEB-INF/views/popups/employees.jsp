<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>사원</title>
  <style>
    :root{ --line:#dcdfe6; --bg:#f6f7fb; --text:#303133; --muted:#909399; --blue:#409eff; }
    *{box-sizing:border-box} body{font-family:Segoe UI, Malgun Gothic, Apple SD Gothic Neo, sans-serif; color:var(--text); margin:0}
    .wrap{padding:14px}
    .titlebar{display:flex; justify-content:center; border-bottom:1px solid var(--line); padding:10px 12px; font-weight:700}
    .searchbar{display:flex; gap:8px; align-items:center; padding:10px 12px; border-bottom:1px solid var(--line); flex-wrap:wrap}
    .searchbar select, .searchbar input{height:30px; border:1px solid var(--line); padding:0 8px; border-radius:4px}
    .searchbar input{width:320px}
    .btn{height:30px; padding:0 12px; border:1px solid var(--line); background:#fff; border-radius:4px; cursor:pointer}
    .btn.primary{background:var(--blue); border-color:var(--blue); color:#fff}
    .grid{padding:10px 12px}
    table{width:100%; border-collapse:collapse}
    th,td{border:1px solid var(--line); font-size:12.5px; padding:6px 8px; white-space:nowrap}
    th{background:var(--bg); text-align:left}
    tr:hover td{background:#fafafa}
    .footer{display:flex; align-items:center; gap:12px; padding:8px 12px; border-top:1px solid var(--line)}
    .footer .left{margin-left:auto; display:flex; gap:6px; align-items:center}
    .pager a{display:inline-block; min-width:24px; text-align:center; padding:2px 6px; border:1px solid var(--line); border-radius:4px; color:var(--text); text-decoration:none}
    .pager a.on{background:var(--blue); color:#fff; border-color:var(--blue)}
    .muted{color:var(--muted)}
  </style>
</head>
<body>
<div class="wrap">

  <!-- 타이틀 -->
  <div class="titlebar"><div id="popupTitle">사원</div></div>

  <!-- 검색바 -->
  <div class="searchbar">
    <select id="by">
      <option value="empName">사원명</option>
      <option value="empNo">사번</option>
    </select>
    <input id="keyword" type="text" placeholder="%" />
    <button id="btnSearch" class="btn primary">검색</button>

</div >
<div class="searchbar">
    <!-- 재직/퇴직 드롭다운 -->
    <span class="muted" style="margin-left:12px;">재직구분</span>
    <select id="selStatus" title="재직구분">
      <option value="ALL">전체</option>
      <option value="재직">재직</option>
      <option value="퇴직">퇴직</option>
    </select>
  </div>

  <!-- 표 -->
  <div class="grid">
    <table id="grid">
      <thead><tr id="gridHead"></tr></thead>
      <tbody id="gridBody"></tbody>
    </table>
  </div>

  <!-- 하단: 조회갯수/페이지 -->
  <div class="footer">
    <div class="left">
      <span>조회갯수설정</span>
      <select id="pageSize">
        <option>10</option><option>20</option><option selected>50</option><option>100</option>
      </select>
      <div class="pager" id="pagerNumbers"></div>
    </div>
  </div>
</div>

<script src="<c:url value='/resources/js/popup-common.js'/>"></script>
<script>
  // 팝업 설정
  window.POPUP_CONFIG = {
    title: '사원',
    api: '<c:url value="/api/popups/employees"/>',
    pageSize: 50,
    showPick: false,   // 선택버튼/체크박스 없음(더블클릭으로 선택)

    // ★ 첫 컬럼에 순번 표시(__seq는 프론트에서 계산)
    columns: [
      { key:'__seq',          name:'No',           width:56 },
      { key:'empName',        name:'사원명',       width:150 },
      { key:'empNo',          name:'사번',         width:90  },
      { key:'deptName',       name:'부서'                    },
      { key:'deptCode',       name:'부서코드',     width:50  },
      { key:'workDeptName',   name:'근무부서',     width:20 },
      { key:'positionName',   name:'직위',         width:60  },
      { key:'titleName',      name:'직급',         width:60  },
      { key:'dutyName',       name:'직책',         width:60  },
      { key:'appointDate',    name:'발령일',       width:100 },   // 밀리초 → YYYY-MM-DD
      { key:'workStatusName', name:'재직/퇴직구분', width:110 },
      { key:'workStatusDetail', name:'근무상태', width:80 },   // 데이터 없으면 재직/퇴직으로 표기
      { key:'payTypeName',    name:'급여형태',     width:80  },
      { key:'payApplyCode',   name:'급여적용군',   width:90  },
      { key:'birthDate', name:'생년월일', width:100, format:'date' }

    ],

    // 행 더블클릭 시 부모창 콜백 호출
    onRowClick: function(row){
      if (window.opener && typeof window.opener.onEmployeePicked === 'function') {
        window.opener.onEmployeePicked(row);
      }
      window.close();
    }
  };

  // 엔터로 검색
  document.getElementById('keyword')
    .addEventListener('keydown', function(e){
      if (e.key === 'Enter') document.getElementById('btnSearch').click();
    });

  POPUP.init();
</script>
</body>
</html>
