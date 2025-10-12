// 공통 팝업 엔진 (ES5)
var POPUP = (function () {
  // ---- 기본 설정(페이지에서 window.POPUP_CONFIG로 덮어씀) ----
  var cfg = {
    title: '',
    api: '',
    pageSize: 50,
    columns: [],             // [{ key, name, width, format?('date'), render?(row,i,seq) }]
    extraParams: null,       // {k:v} 또는 () => ({k:v}) 로 추가 쿼리 파라미터
    onRowClick: function () {} // 행 더블클릭 콜백
  };

  var page = 1;
  var total = 0;

  // ---- 유틸 ----
  function qs(sel)  { return document.querySelector(sel); }
  function qsa(sel) { return Array.prototype.slice.call(document.querySelectorAll(sel)); }

  // 날짜 포맷터: 초/밀리초/문자열 모두 안전 처리 → 'YYYY-MM-DD'
  function fmtDate(v) {
    if (v == null || v === '') return '';
    var n = Number(v);
    if (!isNaN(n)) {
      // 10자리면 초(second)로 간주 → ms 변환
      if (String(n).length === 10) n = n * 1000;
      var d = new Date(n);
      if (isNaN(d.getTime())) return '';
      var y = d.getFullYear();
      var m = ('0' + (d.getMonth() + 1)).slice(-2);
      var day = ('0' + d.getDate()).slice(-2);
      return y + '-' + m + '-' + day;
    }
    // 문자열이면 앞 10자리 사용(YYYY-MM-DD...)
    return String(v).substring(0, 10);
  }

  // ---- 헤더 구성 ----
  function buildHead() {
    var head = qs('#gridHead');
    if (!head) return;
    head.innerHTML = '';
    cfg.columns.forEach(function (c) {
      var th = document.createElement('th');
      th.textContent = c.name || c.key;
      if (c.width) th.style.width = (c.width | 0) + 'px';
      head.appendChild(th);
    });
  }

  // ---- 본문 렌더링 ----
  function drawRows(items) {
    var body = qs('#gridBody');
    if (!body) return;

    body.innerHTML = '';
    var startIdx = (page - 1) * cfg.pageSize;

    for (var i = 0; i < items.length; i++) {
      var row = items[i];
      var tr = document.createElement('tr');

      for (var j = 0; j < cfg.columns.length; j++) {
        var col = cfg.columns[j];
        var td = document.createElement('td');

        if (col.render && typeof col.render === 'function') {
          td.innerHTML = col.render(row, i, startIdx + i + 1);
        } else if (col.key === '__seq') {
          td.textContent = startIdx + i + 1; // 순번
        } else if (col.format === 'date' || /date$/i.test(col.key)) {
          td.textContent = fmtDate(row[col.key]); // 날짜
        } else {
          td.textContent = (row[col.key] != null ? row[col.key] : '');
        }

        tr.appendChild(td);
      }
      
      
      // 행 더블클릭 → 선택 콜백
      (function (r) { tr.ondblclick = function () {  console.log(r); cfg.onRowClick(r); }; })(row);

      body.appendChild(tr);
    }
  }

  // ---- 페이지 네비게이션 ----
  function renderPager() {
    var last = Math.max(1, Math.ceil(total / cfg.pageSize));

    // 조회갯수 select
    var sel = qs('#pageSize');
    if (sel && !sel._inited) {
      sel.addEventListener('change', function () {
        cfg.pageSize = parseInt(sel.value, 10) || 50;
        page = 1;
        fetchList();
      });
      sel._inited = true;
      sel.value = String(cfg.pageSize);
    }

    var cont = qs('#pagerNumbers');
    if (!cont) return;
    cont.innerHTML = '';

    function add(num, text, on) {
      var a = document.createElement('a');
      a.href = 'javascript:void(0)';
      a.textContent = text || num;
      a.dataset.page = num;
      if (on) a.className = 'on';
      cont.appendChild(a);
    }

    var start = Math.max(1, page - 2);
    var end   = Math.min(last, start + 4);
    start     = Math.max(1, end - 4);

    if (page > 1) add(page - 1, '‹');
    for (var i = start; i <= end; i++) add(i, null, i === page);
    if (page < last) add(page + 1, '›');

    if (!cont._inited) {
      cont.addEventListener('click', function (e) {
        var a = e.target.closest('a');
        if (!a) return;
        var to = parseInt(a.dataset.page, 10);
        if (!isNaN(to) && to !== page) { page = to; fetchList(); }
      });
      cont._inited = true;
    }
  }

  // ---- 조회 ----
  function fetchList() {
    var bySel   = qs('#by');
    var kwInput = qs('#keyword');
    var by      = bySel ? bySel.value : '';
    var keyword = kwInput ? kwInput.value.trim() : '';

    // 사원 화면: 재직/퇴직 드롭다운이 있으면 읽어서 status 로 보냄 (전체/재직/퇴직)
    var st = qs('#selStatus');
    var status = st ? (st.value === 'ALL' ? '전체' : st.value) : null;

    // 부서 화면: 사용/미사용 드롭다운이 있으면 읽어서 use 로 보냄 (ALL/Y/N) — 그대로 보냄!
    var us = qs('#selUse');
    var use = us ? us.value : null;

    // 기본 파라미터
    var params = [
      'by='      + encodeURIComponent(by),
      'keyword=' + encodeURIComponent(keyword),
      'page='    + page,
      'size='    + cfg.pageSize
    ];
    if (status != null) params.push('status=' + encodeURIComponent(status));
    if (use    != null) params.push('use='    + encodeURIComponent(use));

    // ★ extraParams: function() | object 모두 지원
    var extra = (typeof cfg.extraParams === 'function') ? (cfg.extraParams() || {}) : (cfg.extraParams || {});
    for (var k in extra) {
      if (Object.prototype.hasOwnProperty.call(extra, k)) {
        params.push(encodeURIComponent(k) + '=' + encodeURIComponent(extra[k]));
      }
    }

    var url = cfg.api + (cfg.api.indexOf('?') >= 0 ? '&' : '?') + params.join('&');

    return fetch(url)
      .then(function (r) { return r.json(); })
      .then(function (d) {
        total = d.total || 0;
        drawRows(d.items || []);
        renderPager();
      })
      .catch(function (err) { alert('조회 실패: ' + err); });
  }

  // ---- 이벤트 바인딩 ----
  function bindEvents() {
    var s = qs('#btnSearch'); if (s) s.onclick = function(){ page = 1; fetchList(); };

    var kw = qs('#keyword');
    if (kw && !kw._inited) {
      kw.addEventListener('keydown', function(e){ if (e.key === 'Enter'){ page = 1; fetchList(); }});
      kw._inited = true;
    }

    var st = qs('#selStatus');
    if (st && !st._inited) {
      st.addEventListener('change', function(){ page = 1; fetchList(); });
      st._inited = true;
    }

    var us = qs('#selUse');
    if (us && !us._inited) {
      us.addEventListener('change', function(){ page = 1; fetchList(); });
      us._inited = true;
    }
  }

  // ---- 시작 ----
  function init() {
    if (window.POPUP_CONFIG) {
      for (var k in window.POPUP_CONFIG) {
        if (Object.prototype.hasOwnProperty.call(window.POPUP_CONFIG, k)) {
          cfg[k] = window.POPUP_CONFIG[k];
        }
      }
    }
    buildHead();
    bindEvents();
    fetchList();
  }

  return { init: init, reload: fetchList };
})();
