
(function () {
  /* -------------------------- 유틸 -------------------------- */
	//상단 조회버튼 
(function () {
  function onReady(fn) {
    if (document.readyState === 'complete' || document.readyState === 'interactive') {
      setTimeout(fn, 0);
    } else {
      document.addEventListener('DOMContentLoaded', fn, false);
    }
  }

  onReady(function () {
    var btnSearch = document.getElementById('btnSearch'); // 상단 "조회"
    var btnLoad   = document.getElementById('btnLoad');   // "데이터생성"
    if (!btnSearch || !btnLoad) return;

    btnSearch.addEventListener('click', function (e) {
      if (e && e.preventDefault) e.preventDefault(); else e.returnValue = false;
      btnLoad.click(); // 같은 동작으로 연결
    }, false);
  });
})();


  // 컨테이너에서 API URL 읽기
  var root = document.querySelector('.container');
  var SUMMARY_URL = root && root.getAttribute('data-summary-url');
  var ANNEX_URL   = root && root.getAttribute('data-annex-url');

  // 숫자 3자리 콤마
  function fmt(n){
    if (n == null) return '0';
    return String(n).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  // fetch + JSON (간단 래퍼)
  function fetchJson(url){
    return fetch(url, { headers: { 'Accept': 'application/json' } })
      .then(function(r){
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      });
  }

  // 엘리먼트 value 안전 추출 (없으면 빈문자열, trim)
  function val(id) {
    var el = document.getElementById(id);
    return (el && typeof el.value === 'string') ? el.value.trim() : '';
  }

  // YYYY-MM → 다음달(YYYY-MM)
  function addMonth(ym) {
    ym = (ym || '').trim();
    var m = /^(\d{4})-(\d{2})$/.exec(ym);
    if (!m) return '';
    var y  = parseInt(m[1], 10);
    var mo = parseInt(m[2], 10) + 1;
    if (mo === 13) { y += 1; mo = 1; }
    return y + '-' + ('0' + mo).slice(-2);
  }

  // "YYYY-MM" -> "WH_YYYYMM.txt"
  function makeFileName(ym) {
    ym = (ym || '').trim();
    var m = /^(\d{4})-(\d{2})$/.exec(ym);
    return m ? 'WH_' + m[1] + m[2] + '.txt' : '';
  }

  /* --------------------- 월 동기화/신고일 --------------------- */
  (function autoSyncMonths(){
    var $ym   = document.getElementById('ym');         // 귀속월 (YYYY-MM)
    var $pay  = document.getElementById('payYm');      // 지급월 (YYYY-MM)
    var $rep  = document.getElementById('reportYm');   // 신고연월 (YYYY-MM)
    var $repD = document.getElementById('reportDate'); // 신고일 (YYYY-MM-DD)

    if (!$ym || !$pay || !$rep) return;

    // 신고연월 기준으로 신고일(매월 10일) 세팅
    function setReportDate(){
      if ($rep && $rep.value && $repD) {
        $repD.value = $rep.value + '-10';
      }
    }

    // 귀속월 변경: 지급월 = 귀속월, 신고연월 = 지급월의 다음달
    function syncFromAccrual(){
      var ym = val('ym');
      if (ym && $pay) $pay.value = ym;
      var pay = val('payYm');
      if (pay && $rep) $rep.value = addMonth(pay);
      setReportDate();
    }

    // 지급월 변경: 신고연월 = 지급월의 다음달
    function syncFromPay(){
      var pay = val('payYm');
      if (pay && $rep) $rep.value = addMonth(pay);
      setReportDate();
    }

    // 신고연월 직접 수정 시, 신고일만 10일로 동기화
    function syncFromReportYm(){
      setReportDate();
    }

    // 초기 1회 동기화
    syncFromAccrual();

    // 변경 이벤트 바인딩
    ['change','blur','keyup'].forEach(function(ev){
      $ym.addEventListener(ev,  syncFromAccrual);
      $pay.addEventListener(ev, syncFromPay);
      $rep.addEventListener(ev, syncFromReportYm);
    });
  })();

  /* ----------------------- 신고파일생성 카드 ----------------------- */
  (function toggleFileCard(){
    var card   = document.getElementById('fileCard');
    var header = document.getElementById('fileCardHeader');
    if(!card || !header) return;

    header.addEventListener('click', function(e){
      // 헤더 내부의 버튼 클릭은 토글 제외
      var t = e.target;
      while (t && t !== header) {
        if (t.tagName === 'BUTTON') return;
        t = t.parentNode;
      }
      card.classList.toggle('is-collapsed');
    });
  })();

  /* ------------------- 버튼: 신고/암호화 파일 생성 ------------------- */

  // 신고파일생성: 비번 검증 + 파일명 계산(placeholder는 그대로 두고 내부 변수로만 사용)
  (function bindMakeButtons(){
    var btnMake    = document.getElementById('btnMake');
    var btnEncrypt = document.getElementById('btnEncrypt');

    if (btnMake) {
      btnMake.addEventListener('click', function () {
        // 1) 비밀번호 일치 검사
        var p1 = val('pwd1'), p2 = val('pwd2');
        if (p1 !== p2) {
          alert('비밀번호가 일치하지 않습니다.');
          return;
        }

        // 2) 파일명 계산: 귀속월 → 없으면 신고연월
        var ym = val('ym') || val('reportYm');
        var fileName = makeFileName(ym);
        if (!fileName) {
          alert('귀속월(YYYY-MM)을 먼저 입력해 주세요.');
          return;
        }

        // 3) TODO: 실제 신고 파일 생성 로직 호출 (예: generateReport)
        // generateReport({ fileName: fileName, password: p1 });
        alert('생성 준비 완료. 파일명: ' + fileName);
      });
    }

    if (btnEncrypt) {
      btnEncrypt.addEventListener('click', function () {
        // 암호화 파일 생성은 비번 일치 검사 없이 파일명만 계산(필요하면 위와 동일하게 검증 추가)
        var ym = val('ym') || val('reportYm');
        var fileName = makeFileName(ym);
        if (!fileName) {
          alert('귀속월(YYYY-MM)을 먼저 입력해 주세요.');
          return;
        }

        // TODO: 암호화 파일 생성 로직 호출 (예: encryptReport)
        alert('암호화 준비 완료. 파일명: ' + fileName);
      });
    }
  })();

  /* ----------------------- 요약/부표 데이터 로드 ----------------------- */

  function loadSummary(){
    var ym = val('ym') || '2025-05';
    fetchJson(SUMMARY_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb = document.getElementById('gridBody');
        if (!tb) return;
        var html = rows.map(function(r, i){
          return '<tr>'
            + '<td class="center">' + (i+1) + '</td>'
            + '<td>' + (r.incomeType || '') + '</td>'
            + '<td class="code">' + (r.code || '') + '</td>'
            + '<td class="num">' + (r.headCount || 0) + '</td>'
            + '<td class="num">' + fmt(r.taxTotal) + '</td>'
            + '<td class="num">' + fmt(r.ntWithheld) + '</td>'
            + '<td class="num">' + fmt(r.taxIncome) + '</td>'
            + '<td class="num">' + fmt(r.penaltyTax) + '</td>'
            + '<td class="num">' + fmt(r.taxWithheld) + '</td>'
            + '<td class="num">' + fmt(r.adjRefund) + '</td>'
            + '<td class="num">' + fmt(r.taxNt) + '</td>'
          + '</tr>';
        }).join('');
        tb.innerHTML = html;
      })
      .catch(function(err){
        console.error('[SUMMARY]', err);
        alert('요약 조회 실패: ' + err.message);
      });
  }

  function loadAnnex(){
    var ym = val('ym') || '2025-05';
    fetchJson(ANNEX_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb = document.getElementById('annexBody');
        if (!tb) return;
        var html = rows.map(function(r, i){
          return '<tr>'
            + '<td class="center">' + (i+1) + '</td>'
            + '<td>' + (r.incomeType || '') + '</td>'
            + '<td class="code">' + (r.code || '') + '</td>'
            + '<td class="num">' + (r.headCount || 0) + '</td>'
            + '<td class="num">' + fmt(r.taxTotal) + '</td>'
            + '<td class="num">' + fmt(r.ntWithheld) + '</td>'
            + '<td class="num">' + fmt(r.taxIncome) + '</td>'
            + '<td class="num">' + fmt(r.penaltyTax) + '</td>'
            + '<td class="num">' + fmt(r.taxWithheld) + '</td>'
            + '<td class="num">' + fmt(r.adjRefund) + '</td>'
            + '<td class="num">' + fmt(r.taxNt) + '</td>'
          + '</tr>';
        }).join('');
        tb.innerHTML = html;
      })
      .catch(function(err){
        console.error('[ANNEX]', err);
        alert('부표 조회 실패: ' + err.message);
      });
  }

  // 탭 전환
  function activate(which){
    var sTab = document.getElementById('tabSummary');
    var aTab = document.getElementById('tabAnnex');
    var sPan = document.getElementById('panelSummary');
    var aPan = document.getElementById('panelAnnex');
    if (!sTab || !aTab || !sPan || !aPan) return;

    if (which === 'summary'){
      sTab.classList.add('active'); aTab.classList.remove('active');
      sPan.style.display = 'block'; aPan.style.display = 'none';
    } else {
      aTab.classList.add('active'); sTab.classList.remove('active');
      aPan.style.display = 'block'; sPan.style.display = 'none';
    }
  }

  // 버튼/탭 이벤트
  (function bindDataButtons(){
    var btnLoad = document.getElementById('btnLoad');
    if (btnLoad) {
      btnLoad.onclick = function(){
        if (document.getElementById('panelSummary').style.display !== 'none') {
          loadSummary();
        } else {
          loadAnnex();
        }
      };
    }

    var tabSummary = document.getElementById('tabSummary');
    var tabAnnex   = document.getElementById('tabAnnex');
    if (tabSummary) tabSummary.onclick = function(){ activate('summary'); loadSummary(); };
    if (tabAnnex)   tabAnnex.onclick   = function(){ activate('annex');   loadAnnex();   };
  })();

  // 초기 로딩(요약 탭)
  document.addEventListener('DOMContentLoaded', loadSummary);
})();

	
	//── 요약/부표 스크롤 박스를 화면에 딱 맞추기(아래 '전월 미환급세액' 카드까지 고려)
	(function(){
	function fit(){
	 var sc = document.getElementById('dataScroll');
	 if(!sc) return;
	
	 var refund = document.getElementById('refundBlock');
	 var rect   = sc.getBoundingClientRect();   // 스크롤 영역의 화면상 top
	 var gap    = 12;                           // 화면 하단 여백
	 var underH = 0;
	
	 // 아래 전월 미환급세액 카드가 있으면 그 높이(+ 간격)만큼 제외
	 if (refund) {
	   underH = refund.offsetHeight + 12;       // 필요시 12 조절
	 }
	
	 // 사용 가능한 높이 = 창높이 - 스크롤영역 top - 아래카드 높이 - 여백
	 var h = window.innerHeight - rect.top - underH - gap;
	 if (h < 220) h = 220;                      // 너무 작지 않게 최소 보장
	
	 // 핵심: height 직접 지정 (max-height X)
	 sc.style.height = h + 'px';
	}
	
	function on(el, type, fn){ if(el && el.addEventListener){ el.addEventListener(type, fn, false); } }
	
	on(window, 'load',   fit);
	on(window, 'resize', fit);
	
	// 탭 전환 시에도 다시 맞춤 (요약/부표)
	on(document.getElementById('tabSummary'), 'click', fit);
	on(document.getElementById('tabAnnex'),   'click', fit);
	
	// 혹시 표 렌더링 후 높이가 변하는 경우를 대비해서 약간 늦게 한 번 더
	setTimeout(fit, 80);
	})();




