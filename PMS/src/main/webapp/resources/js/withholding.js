
(function () {
  /* -------------------------- 유틸 -------------------------- */
	//상단 조회버튼 
(function () {
  function onReady(fn) {
    if (document.readyState === 'complete' || document.readyState === 'interactive') setTimeout(fn, 0);
    else document.addEventListener('DOMContentLoaded', fn, false);
  }

  onReady(function () {
    var btnSearch = document.getElementById('btnSearch'); // 상단 "조회"
    if (!btnSearch) return;

    btnSearch.addEventListener('click', function (e) {
      if (e && e.preventDefault) e.preventDefault(); else e.returnValue = false;
      if (document.getElementById('panelSummary').style.display !== 'none') {
        loadSummary();
      } else {
        loadAnnex();
      }
    }, false);
  });
})();


  // 컨테이너에서 API URL 읽기
  var root = document.querySelector('.container');
  var SUMMARY_URL = root && root.getAttribute('data-summary-url');
  var ANNEX_URL   = root && root.getAttribute('data-annex-url');
  var GENERATE_URL = root && root.getAttribute('data-generate-url');

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
  /* ----------------------- 연말정산 반영 버튼 ----------------------- */
  // 연말정산반영 체크 ↔ 연말정산연도 입력 활성화
(function initYearEndToggle(){
  var chk  = document.getElementById('opt2');   // 연말정산반영
  var year = document.getElementById('annYear'); // 연말정산연도(YYYY)
  if (!chk || !year) return;

  function sync(){
    var on = !!chk.checked;
    year.readOnly = !on;                   // 체크되면 편집 가능, 아니면 읽기전용
    year.classList.toggle('is-readonly', !on);
  }

  // 숫자 4자리만 받도록(선택)
  year.addEventListener('input', function(){
    this.value = this.value.replace(/\D/g,'').slice(0,4);
  });

  chk.addEventListener('change', sync, false);
  sync(); // 초기 상태 반영
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
    var ym = val('ym') || '2025-09';
    fetchJson(SUMMARY_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb = document.getElementById('gridBody');
        if (!tb) return;
        var html = rows.map(function(r, i){
        	  return '<tr>'
        	    + '<td class="center">' + (i+1) + '</td>'
        	    + '<td>' + (r.incomeType || '') + '</td>'
        	    + '<td class="code">' + (r.code || '') + '</td>'
        	    + '<td class="num">' + (r.headCount || 0) + '</td>'          // 인원
        	    + '<td class="num">' + fmt(r.taxTotal) + '</td>'             // 총지급액
        	    + '<td class="num">' + fmt(r.ntWithheld) + '</td>'           // 징수농특세
        	    + '<td class="num">' + fmt(r.taxIncome) + '</td>'            // 납부소득세
        	    + '<td class="num">' + fmt(r.taxWithheld) + '</td>'          // 징수소득세 
        	    + '<td class="num">' + fmt(r.adjRefund) + '</td>'            // 조정환급세액
        	    + '<td class="num">' + fmt(r.taxNt) + '</td>'                // 납부농특세
        	    + '<td class="num">' + fmt(r.penaltyTax) + '</td>'           // 징수가산세 
        	  + '</tr>';
        	}).join('');
        	tb.innerHTML = html;
        	applyPerCellGrey(); // 색칠 적용
      })
      .catch(function(err){
        console.error('[SUMMARY]', err);
        alert('요약 조회 실패: ' + err.message);
      });
  }

  function loadAnnex(){
    var ym = val('ym') || '2025-09';
    fetchJson(ANNEX_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb = document.getElementById('annexBody');
        if (!tb) return;
        var html = rows.map(function(r, i){
        	  return '<tr>'
        	    + '<td class="center">' + (i+1) + '</td>'              // No
        	    + '<td>' + (r.incomeType || '') + '</td>'              // 소득 구분
        	    + '<td class="code">' + (r.code || '') + '</td>'       // 코드
        	    + '<td class="num">' + (r.headCount || 0) + '</td>'    // 인원
        	    + '<td class="num">' + fmt(r.taxTotal) + '</td>'       // 총지급액
        	    + '<td class="num">' + fmt(r.taxWithheld) + '</td>'    // 징수소득세
        	    + '<td class="num">' + fmt(r.ntWithheld) + '</td>'     // 징수농특세
        	    + '<td class="num">' + fmt(r.penaltyTax) + '</td>'     // 징수가산세
        	    + '<td class="num">' + fmt(r.adjRefund) + '</td>'      // 조정환급세액
        	    + '<td class="num">' + fmt(r.taxIncome) + '</td>'      // 납부소득세
        	    + '<td class="num">' + fmt(r.taxNt) + '</td>'          // 납부농특세
        	  + '</tr>';
        	}).join('');
        	tb.innerHTML = html;

        	applyPerCellGreyAnnex();
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
      var ym = document.getElementById('ym') ? document.getElementById('ym').value : '';
      if (!ym) { alert('귀속월(YYYY-MM)을 먼저 입력해 주세요.'); return; }

      if (!confirm("기존에 등록된 자료는 삭제됩니다. 삭제하시겠습니까?")) return;

      if (GENERATE_URL) {
    	  fetch(GENERATE_URL + "?applyYyyymm=" + encodeURIComponent(ym), {
    		  method: "POST",
    		  headers: { "Accept": "application/json" }   // ★ 추가
    		})
    		  .then(function(r){ return r.json(); })
    		  .then(function(j){
    		    alert(j && (j.message || "생성 완료"));
    		    if (document.getElementById('panelSummary').style.display !== 'none') {
    		      loadSummary();
    		    } else {
    		      loadAnnex();
    		    }
    		  })
    		  .catch(function(err){
    		    alert("생성 실패: " + err);
    		  });

      } else {
        // (혹시 URL 바인딩 안 됐으면) 그냥 현재 탭만 새로고침
        if (document.getElementById('panelSummary').style.display !== 'none') {
          loadSummary();
        } else {
          loadAnnex();
        }
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

	  /* -----------------------표 색칠부분 ----------------------- */
	
	// 헤더 텍스트 -> 컬럼 인덱스 매핑
	function buildHeaderIndexMap(tableSelector) {
	  var map = {};
	  var ths = document.querySelectorAll(tableSelector + " thead th");
	  ths.forEach(function(th, idx){
	    var key = (th.textContent || "").trim();
	    if (key) map[key] = idx + 1; // nth-child는 1부터
	  });
	  return map;
	}

	// ── 요약(Summary) 전용 매핑/규칙 ─────────────────────
	// ───────────────────────────────────────────────
	// 셀 단위 회색 매핑 

	var GREY_COLUMNS_BY_CODE = {
			  "A01": ["조정환급세액"],
			  "A02": ["조정환급세액"],
			  "A03": ["징수농특세","조정환급세액"],
			  "A04": ["징수농특세","징수소득세","조정환급세액","징수가산세"],
			  "A05": ["총지급액","조정환급세액"],
			  "A06": ["인원","총지급액","조정환급세액"],
			  "A10": ["인원","총지급액","징수농특세","징수소득세","징수가산세"],
			  "A21": ["징수농특세","조정환급세액"],
			  "A22": ["징수농특세","조정환급세액"],
			  "A20": ["인원","총지급액","징수농특세","징수소득세","징수가산세"],
			  "A25": ["징수농특세","조정환급세액"],
			  "A26": ["조정환급세액"],
			  "A30": ["인원","총지급액","징수농특세","징수소득세","징수가산세"],
			  "A41": ["징수농특세","조정환급세액"],
			  "A43": ["징수농특세","조정환급세액"],
			  "A44": ["징수농특세","조정환급세액"],
			  "A42": ["징수농특세","조정환급세액"],
			  "A40": ["인원","총지급액","징수농특세","징수소득세","징수가산세"],
			  "A48": ["징수농특세","조정환급세액"],
			  "A45": ["징수농특세","조정환급세액"],
			  "A46": ["징수농특세","조정환급세액"],
			  "A47": ["인원","총지급액","징수농특세","징수소득세","징수가산세"],
			  "A69": ["총지급액","징수농특세"],
			  "A70": ["징수농특세"],
			  "A80": ["징수농특세"],
			  "A90": ["인원","총지급액"],
			  "A99": ["인원","총지급액","징수농특세","징수소득세","징수가산세"]
			};

	// 2) 공통/가감계 규칙  
	// (1) 모든 행에서 무조건 회색으로 칠할 컬럼
	var ALWAYS_GREY_COLUMNS = ["소득 구분","코드","납부소득세","납부농특세"];

	// (2) '소득 구분/코드'만 연분홍으로 보일 코드들 (가감계/단독/총합)
	var SPECIAL_PINK_CODES = [
	  "A10","A20","A30","A40","A47", // 가감계
	  "A50","A60","A69","A70","A80","A90", // 단독 카테고리
	  "A99" // 총합계
	];


	// 3) 헤더 매핑 + 색칠 함수
	function applyPerCellGrey() {
		  var headerMap = buildHeaderIndexMap("#panelSummary table");
		  var rows = document.querySelectorAll("#gridBody tr");

		  // 공통 4컬럼 인덱스 미리 구해두기
		  var alwaysIdx = ALWAYS_GREY_COLUMNS
		    .map(function(h){ return headerMap[h]; })
		    .filter(Boolean);

		  var idxOf = function(h){
		    var nth = headerMap[h];
		    return nth ? nth : null;
		  };

		  rows.forEach(function(tr){
		    // 초기화
		    tr.querySelectorAll("td").forEach(function(td){
		      td.classList.remove("cell-grey","cell-pink");
		    });

		    // 코드(3번째 셀)
		    var codeCell = tr.querySelector("td:nth-child(3)");
		    if (!codeCell) return;
		    var code = (codeCell.textContent || "").trim();

		    // 1) 공통 4컬럼은 모든 행에서 우선 회색
		    alwaysIdx.forEach(function(nth){
		      var td = tr.querySelector("td:nth-child(" + nth + ")");
		      if (td) td.classList.add("cell-grey");
		    });

		    // 2) 특수코드면 '소득 구분/코드'를 회색 → 연분홍으로 덮어쓰기
		    if (SPECIAL_PINK_CODES.indexOf(code) >= 0) {
		      ["소득 구분","코드"].forEach(function(h){
		        var nth = idxOf(h);
		        if (!nth) return;
		        var td = tr.querySelector("td:nth-child(" + nth + ")");
		        if (!td) return;
		        td.classList.remove("cell-grey");
		        td.classList.add("cell-pink");
		      });
		      // 나머지 공통 회색(납부소득세/납부농특세)은 그대로 유지
		    }

		    // 3) 코드별 “추가 회색” 지정분(GREY_COLUMNS_BY_CODE)을 적용
		    //    (여기엔 '소득 구분/코드/납부소득세/납부농특세'를 넣지 않는 걸 권장)
		    (GREY_COLUMNS_BY_CODE[code] || []).forEach(function(h){
		      var nth = idxOf(h);
		      if (!nth) return;
		      var td = tr.querySelector("td:nth-child(" + nth + ")");
		      if (td) td.classList.add("cell-grey");
		    });
		  });
		}

	// ── 부표(Annex) 전용 매핑/규칙 ─────────────────────
	// ───────────────────────────────────────────────
	var GREY_COLUMNS_BY_CODE_ANNEX = {
	  "C01": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C02": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C03": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C05": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C06": ["징수소득세","조정환급세액"],
	  "C07": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C08": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C10": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C11": ["조정환급세액"],
	  "C12": ["조정환급세액"],
	  "C13": ["징수농특세","조정환급세액"],
	  "C14": ["징수농특세","조정환급세액"],
	  "C15": ["징수농특세","조정환급세액"],
	  "C16": ["징수농특세","조정환급세액"],
	  "C18": ["징수농특세","조정환급세액"],
	  "C19": ["징수소득세","조정환급세액"],
	  "C20": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C22": ["조정환급세액"],
	  "C23": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C24": ["징수농특세","조정환급세액"],
	  "C25": ["징수농특세","조정환급세액"],
	  "C26": ["징수농특세","조정환급세액"],
	  "C28": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C29": ["징수소득세","조정환급세액"],
	  "C30": ["징수농특세","징수소득세","총지급액","인원","징수가산세"],
	  "C31": ["조정환급세액"],
	  "C33": ["조정환급세액"],
	  "C34": ["조정환급세액"],
	  "C36": ["징수농특세","조정환급세액"],
	  "C37": ["징수농특세","조정환급세액"],
	  "C38": ["징수농특세","조정환급세액"],
	  "C39": ["징수농특세","조정환급세액"],
	  "C40": ["징수소득세","조정환급세액"],
	  "C41": ["조정환급세액","징수농특세","총지급액"],
	  "C42": ["조정환급세액","징수농특세","총지급액"],
	  "C43": ["조정환급세액","징수농특세","총지급액"],
	  "C44": ["조정환급세액","징수농특세","총지급액"],
	  "C45": ["조정환급세액","징수농특세","총지급액"],
	  "C46": ["조정환급세액","총지급액"],
	  "C50": ["인원","총지급액","징수소득세","징수농특세","징수가산세"],
	  "C52": ["징수농특세","조정환급세액"],
	  "C54": ["징수농특세","조정환급세액"],
	  "C55": ["징수농특세","조정환급세액"],
	  "C56": ["조정환급세액"],
	  "C57": ["조정환급세액"],
	  "C58": ["징수농특세","조정환급세액"],
	  "C70": ["인원","총지급액","징수소득세","징수농특세","징수가산세"],
	  "C71": ["징수농특세"],
	  "C72": ["징수농특세"],
	  "C73": ["징수농특세"],
	  "C74": ["징수농특세"],
	  "C75": ["징수농특세"],
	  "C76": ["징수소득세","징수농특세","징수가산세","조정환급세액"],
	  "C81": ["징수농특세"],
	  "C82": ["징수농특세"],
	  "C83": ["징수농특세"],
	  "C84": ["징수농특세"],
	  "C85": ["징수농특세"],
	  "C86": ["징수농특세"],
	  "C87": ["징수농특세"],
	  "C88": ["징수농특세"],
	  "C90": ["인원","총지급액","징수소득세","징수농특세","징수가산세"]
	};


	// 항상 회색: 소득 구분/코드/납부소득세/납부농특세
	var ALWAYS_GREY_COLUMNS_ANNEX = ["소득 구분","코드","납부소득세","납부농특세"];

	// ✅ 소득구분/코드만 연분홍으로 덮어쓸 코드들 (요청하신 C30, C50, C70, C90)
	var SPECIAL_PINK_CODES_ANNEX = ["C30","C50","C70","C90"];


	// (ES5 안전) header map 만들기
	function buildHeaderIndexMapAnnex(tableSelector) {
	  var map = {};
	  var ths = document.querySelectorAll(tableSelector + " thead th");
	  for (var i = 0; i < ths.length; i++) {
	    var key = (ths[i].textContent != null ? ths[i].textContent : ths[i].innerText);
	    key = (key || "").replace(/^\s+|\s+$/g, "");
	    if (key) map[key] = i + 1; // nth-child는 1부터
	  }
	  return map;
	}

	// 부표 색칠 적용
	function applyPerCellGreyAnnex() {
	  var headerMap = buildHeaderIndexMapAnnex("#panelAnnex table");
	  var rows = document.querySelectorAll("#annexBody tr");

	  // 공통 컬럼 인덱스 미리 계산
	  var alwaysIdx = [];
	  for (var i = 0; i < ALWAYS_GREY_COLUMNS_ANNEX.length; i++) {
	    var h = ALWAYS_GREY_COLUMNS_ANNEX[i];
	    if (headerMap[h]) alwaysIdx.push(headerMap[h]);
	  }

	  function idxOf(h){
	    return headerMap[h] ? headerMap[h] : null;
	  }

	  for (var r = 0; r < rows.length; r++) {
	    var tr = rows[r];

	    // 초기화
	    var tds = tr.querySelectorAll("td");
	    for (var k = 0; k < tds.length; k++) {
	      tds[k].classList.remove("cell-grey","cell-pink");
	    }

	    // 코드(3번째 셀)
	    var codeCell = tr.querySelector("td:nth-child(3)");
	    if (!codeCell) continue;
	    var code = (codeCell.textContent || codeCell.innerText || "").replace(/^\s+|\s+$/g, "");

	    // 1) 공통(소득 구분/코드/납부소득세/납부농특세) 회색
	    for (var a = 0; a < alwaysIdx.length; a++) {
	      var nth = alwaysIdx[a];
	      var td = tr.querySelector("td:nth-child(" + nth + ")");
	      if (td) td.classList.add("cell-grey");
	    }

	    // 2) 특수 코드는 '소득 구분/코드'를 분홍으로 덮어쓰기(필요시)
	    for (var sp = 0; sp < SPECIAL_PINK_CODES_ANNEX.length; sp++) {
	      if (SPECIAL_PINK_CODES_ANNEX[sp] === code) {
	        var heads = ["소득 구분","코드"];
	        for (var hidx = 0; hidx < heads.length; hidx++) {
	          var nth2 = idxOf(heads[hidx]);
	          if (!nth2) continue;
	          var td2 = tr.querySelector("td:nth-child(" + nth2 + ")");
	          if (!td2) continue;
	          td2.classList.remove("cell-grey");
	          td2.classList.add("cell-pink");
	        }
	        break;
	      }
	    }

	    // 3) 코드별 추가 회색 지정
	    var list = GREY_COLUMNS_BY_CODE_ANNEX[code] || [];
	    for (var c = 0; c < list.length; c++) {
	      var nth3 = idxOf(list[c]);
	      if (!nth3) continue;
	      var td3 = tr.querySelector("td:nth-child(" + nth3 + ")");
	      if (td3) td3.classList.add("cell-grey");
	    }
	  }
	}

//	/* =========================
//	   부표 페인트 모드(인터랙티브로 GREY_COLUMNS_BY_CODE_ANNEX 작성) 필요할 때 URL 끝에 ?dev=1 붙여서만 활성화!
//	   ========================= */
//	if (location.search.indexOf('dev=1') >= 0) {
//	// 전역(이미 있던 맵을 재사용/보강)
//	window.GREY_COLUMNS_BY_CODE_ANNEX = window.GREY_COLUMNS_BY_CODE_ANNEX || {};
//
//	// 상태
//	var _paintOn = false;
//	var _paintTableSel = "#panelAnnex table";
//	var _paintBodySel  = "#annexBody";
//	var _paintHeaderMap = null;
//
//	// 유틸: 배열 포함/제거 (ES5)
//	function _arrHas(arr, val){ for (var i=0;i<arr.length;i++){ if(arr[i]===val) return true; } return false; }
//	function _arrRemove(arr, val){ for (var i=0;i<arr.length;i++){ if(arr[i]===val){ arr.splice(i,1); return; }} }
//
//	// 헤더 맵 재활용
//	function _buildHeaderIndexMapES5(tableSelector){
//	  var map = {};
//	  var ths = document.querySelectorAll(tableSelector + " thead th");
//	  for (var i=0;i<ths.length;i++){
//	    var key = (ths[i].textContent != null ? ths[i].textContent : ths[i].innerText);
//	    key = (key || "").replace(/^\s+|\s+$/g, "");
//	    if (key) map[key] = i + 1;
//	  }
//	  return map;
//	}
//
//	// 클릭 핸들러
//	function _onPaintClick(e){
//	  var td = e.target || e.srcElement;
//	  if (!td || td.tagName !== 'TD') return;
//
//	  // 현재 행 코드는 3번째 셀(“코드”)
//	  var tr = td.parentNode;
//	  var codeCell = tr.querySelector("td:nth-child(3)");
//	  if (!codeCell) return;
//	  var code = (codeCell.textContent || codeCell.innerText || "").replace(/^\s+|\s+$/g, "");
//	  if (!code) return;
//
//	  // td의 컬럼 헤더명을 역추적
//	  var tds = tr.querySelectorAll("td");
//	  var idx = 0;
//	  for (var i=0;i<tds.length;i++){ if (tds[i]===td){ idx = i+1; break; } }
//
//	  var headerName = null;
//	  for (var k in _paintHeaderMap){ if (_paintHeaderMap[k] === idx){ headerName = k; break; } }
//	  if (!headerName) return;
//
//	  // ‘소득 구분/코드’는 항상 분홍/공통 규칙이라 수동 토글 대상에서 제외
//	  if (headerName === "소득 구분" || headerName === "코드") return;
//
//	  // 맵에서 토글
//	  var list = window.GREY_COLUMNS_BY_CODE_ANNEX[code] || [];
//	  if (_arrHas(list, headerName)) {
//	    _arrRemove(list, headerName);
//	    td.classList.remove("cell-grey");
//	  } else {
//	    list.push(headerName);
//	    td.classList.add("cell-grey");
//	  }
//	  window.GREY_COLUMNS_BY_CODE_ANNEX[code] = list;
//
//	  // 즉시 재도색(공통 규칙과 충돌 없이 정리)
//	  applyPerCellGreyAnnex();
//	}
//
//	// 페인트 모드 on/off
//	window.enableAnnexPaintMode = function(){
//	  if (_paintOn) return;
//	  _paintOn = true;
//	  _paintHeaderMap = _buildHeaderIndexMapES5(_paintTableSel);
//	  var body = document.querySelector(_paintBodySel);
//	  if (body) body.addEventListener('click', _onPaintClick, false);
//	  document.body.classList.add('annex-paint-on');
//	  alert("부표 페인트 모드 ON: 회색으로 만들 셀(0이든 값이 있든 상관없이)들을 클릭하세요.\n완료 후 exportAnnexGreyMap()을 콘솔에서 호출하면 JSON이 출력됩니다.");
//	};
//
//	window.disableAnnexPaintMode = function(){
//	  if (!_paintOn) return;
//	  _paintOn = false;
//	  var body = document.querySelector(_paintBodySel);
//	  if (body) body.removeEventListener('click', _onPaintClick, false);
//	  document.body.classList.remove('annex-paint-on');
//	  alert("부표 페인트 모드 OFF");
//	};
//
//	// 현재 맵을 JSON으로 출력(개발자도구 콘솔)
//	window.exportAnnexGreyMap = function(){
//	  // 키 순 정렬해서 깔끔하게
//	  var out = {};
//	  var keys = [];
//	  for (var k in window.GREY_COLUMNS_BY_CODE_ANNEX){ keys.push(k); }
//	  keys.sort();
//	  for (var i=0;i<keys.length;i++){
//	    var code = keys[i];
//	    var arr = window.GREY_COLUMNS_BY_CODE_ANNEX[code] || [];
//	    // 중복 제거
//	    var uniq = [];
//	    for (var j=0;j<arr.length;j++){ if (!_arrHas(uniq, arr[j])) uniq.push(arr[j]); }
//	    out[code] = uniq;
//	  }
//	  console.log(JSON.stringify(out, null, 2));
//	  alert("개발자도구 콘솔에 GREY_COLUMNS_BY_CODE_ANNEX JSON이 출력되었습니다.");
//	};
//	}


