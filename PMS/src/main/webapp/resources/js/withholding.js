(function () {
  /* ========================= 공용 유틸/전역 ========================= */
  function onReady(fn){
    if (document.readyState === 'complete' || document.readyState === 'interactive') setTimeout(fn,0);
    else document.addEventListener('DOMContentLoaded', fn, false);
  }

  // jsPDF 생성자 감지(전역형/UMD 둘 다 지원)
  window.__getJsPDFCtor = function(){
    return window.jsPDF || (window.jspdf && window.jspdf.jsPDF) || null;
  };
  // 라이브러리 로드 확인
  window.__ensurePdfLibs = function(){
    var ctor = window.__getJsPDFCtor();
    var ok = !!ctor && (typeof window.html2canvas === 'function');
    if (!ok) alert('PDF 라이브러리가 로드되지 않았습니다.\nhtml2canvas/jsPDF 스크립트와 로딩 순서를 확인하세요.');
    return ok;
  };

  // DOM/데이터 공통
  var root = document.querySelector('.container') || document.body;
  var SUMMARY_URL     = root && root.getAttribute('data-summary-url');
  var ANNEX_URL       = root && root.getAttribute('data-annex-url');
  var GENERATE_URL    = root && root.getAttribute('data-generate-url');
  var PREV_REFUND_URL = root && root.getAttribute('data-prev-refund-url');
  var SAVE_REFUND_URL = root && root.getAttribute('data-save-refund-url');

  function fmt3(n){ if(n==null) return '0'; return String(n).replace(/\B(?=(\d{3})+(?!\d))/g, ','); }
  function fetchJson(url){
    return fetch(url, {headers:{'Accept':'application/json'}}).then(function(r){
      if(!r.ok) throw new Error('HTTP '+r.status); return r.json();
    });
  }
  function val(id){ var el=document.getElementById(id); return (el && typeof el.value==='string')? el.value.trim():''; }
  function addMonth(ym){
    var m=/^(\d{4})-(\d{2})$/.exec((ym||'').trim()); if(!m) return '';
    var y=+m[1], mo=+m[2]+1; if(mo===13){y+=1; mo=1;} return y+'-'+('0'+mo).slice(-2);
  }
  function makeFileName(ym){
    var m=/^(\d{4})-(\d{2})$/.exec((ym||'').trim()); return m? ('WH_'+m[1]+m[2]+'.txt'):'';
  }

  /* ========================= 상단 조회 버튼 ========================= */
  onReady(function(){
    var btnSearch=document.getElementById('btnSearch');
    if(!btnSearch) return;
    btnSearch.addEventListener('click', function(e){
      if(e&&e.preventDefault) e.preventDefault(); else e.returnValue=false;
      if(document.getElementById('panelSummary').style.display!=='none'){ loadSummary(); } else { loadAnnex(); }
    }, false);
  });

  /* ========================= 월 동기화/신고일 ========================= */
  (function autoSyncMonths(){
    var $ym=document.getElementById('ym'), $pay=document.getElementById('payYm'),
        $rep=document.getElementById('reportYm'), $repD=document.getElementById('reportDate');
    if(!$ym||!$pay||!$rep) return;

    function setReportDate(){ if($rep && $rep.value && $repD) $repD.value = $rep.value + '-10'; }
    function syncFromAccrual(){ var ym=val('ym'); if(ym&&$pay) $pay.value=ym; var pay=val('payYm'); if(pay&&$rep) $rep.value=addMonth(pay); setReportDate(); }
    function syncFromPay(){ var pay=val('payYm'); if(pay&&$rep) $rep.value=addMonth(pay); setReportDate(); }
    function syncFromReportYm(){ setReportDate(); }

    syncFromAccrual();
    ['change','blur','keyup'].forEach(function(ev){
      $ym.addEventListener(ev,  syncFromAccrual);
      $pay.addEventListener(ev, syncFromPay);
      $rep.addEventListener(ev, syncFromReportYm);
    });
  })();

  /* ========================= 연말정산 토글 ========================= */
  (function initYearEndToggle(){
    var chk=document.getElementById('opt2'), year=document.getElementById('annYear'); if(!chk||!year) return;
    function sync(){ var on=!!chk.checked; year.readOnly=!on; year.classList.toggle('is-readonly', !on); }
    year.addEventListener('input', function(){ this.value=this.value.replace(/\D/g,'').slice(0,4); });
    chk.addEventListener('change', sync, false); sync();
  })();

  /* ========================= 확정 체크박스 비활성 ========================= */
  (function(){
    var cb=document.getElementById('optConfirm'); if(!cb) return;
    cb.disabled=true; cb.tabIndex=-1; if(cb.parentNode&&cb.parentNode.classList){ cb.parentNode.classList.add('is-readonly'); }
  })();

  /* ========================= 신고파일생성 카드 접기 ========================= */
  (function(){
    var card=document.getElementById('fileCard'), header=document.getElementById('fileCardHeader');
    if(!card||!header) return;
    header.addEventListener('click', function(e){
      var t=e.target; while(t && t!==header){ if(t.tagName==='BUTTON') return; t=t.parentNode; }
      card.classList.toggle('is-collapsed');
    });
  })();

  /* ========================= 요약/부표 데이터 로드 ========================= */
  window.loadSummary = function(){
    var ym = val('ym') || '2025-09';
    fetchJson(SUMMARY_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb=document.getElementById('gridBody'); if(!tb) return;
        var html = rows.map(function(r,i){
          return '<tr>'
            + '<td class="center">'+(i+1)+'</td>'
            + '<td>'+(r.incomeType||'')+'</td>'
            + '<td class="code">'+(r.code||'')+'</td>'
            + '<td class="num">'+(r.headCount||0)+'</td>'
            + '<td class="num">'+fmt3(r.taxTotal)+'</td>'
            + '<td class="num">'+fmt3(r.ntWithheld)+'</td>'
            + '<td class="num">'+fmt3(r.taxIncome)+'</td>'
            + '<td class="num">'+fmt3(r.taxWithheld)+'</td>'
            + '<td class="num">'+fmt3(r.adjRefund)+'</td>'
            + '<td class="num">'+fmt3(r.taxNt)+'</td>'
            + '<td class="num">'+fmt3(r.penaltyTax)+'</td>'
          + '</tr>';
        }).join('');
        tb.innerHTML = html;
        if (typeof window.applyPerCellGrey === 'function') window.applyPerCellGrey();
        if (window.recalcRefundByTables) window.recalcRefundByTables();
      })
      .catch(function(err){ console.error('[SUMMARY]', err); alert('요약 조회 실패: '+err.message); });
  };

  window.loadAnnex = function(){
    var ym = val('ym') || '2025-09';
    fetchJson(ANNEX_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb=document.getElementById('annexBody'); if(!tb) return;
        var html = rows.map(function(r,i){
          return '<tr>'
            + '<td class="center">'+(i+1)+'</td>'
            + '<td>'+(r.incomeType||'')+'</td>'
            + '<td class="code">'+(r.code||'')+'</td>'
            + '<td class="num">'+(r.headCount||0)+'</td>'
            + '<td class="num">'+fmt3(r.taxTotal)+'</td>'
            + '<td class="num">'+fmt3(r.taxWithheld)+'</td>'
            + '<td class="num">'+fmt3(r.ntWithheld)+'</td>'
            + '<td class="num">'+fmt3(r.penaltyTax)+'</td>'
            + '<td class="num">'+fmt3(r.adjRefund)+'</td>'
            + '<td class="num">'+fmt3(r.taxIncome)+'</td>'
            + '<td class="num">'+fmt3(r.taxNt)+'</td>'
          + '</tr>';
        }).join('');
        tb.innerHTML = html;
        if (typeof window.applyPerCellGreyAnnex === 'function') window.applyPerCellGreyAnnex();
        if (window.recalcRefundByTables) window.recalcRefundByTables();
      })
      .catch(function(err){ console.error('[ANNEX]', err); alert('부표 조회 실패: '+err.message); });
  };

  /* ========================= 탭 전환 ========================= */
  function activate(which){
    var sTab=document.getElementById('tabSummary'), aTab=document.getElementById('tabAnnex'),
        sPan=document.getElementById('panelSummary'), aPan=document.getElementById('panelAnnex');
    if(!sTab||!aTab||!sPan||!aPan) return;
    if(which==='summary'){ sTab.classList.add('active'); aTab.classList.remove('active'); sPan.style.display='block'; aPan.style.display='none'; }
    else{ aTab.classList.add('active'); sTab.classList.remove('active'); aPan.style.display='block'; sPan.style.display='none'; }
  }

  (function bindDataButtons(){
    var btnLoad=document.getElementById('btnLoad');
    if(btnLoad){
      btnLoad.onclick=function(){
        var ym=val('ym'); if(!ym){ alert('귀속월(YYYY-MM)을 먼저 입력해 주세요.'); return; }
        if(!confirm('기존에 등록된 자료는 삭제됩니다. 삭제하시겠습니까?')) return;

        if(GENERATE_URL){
          fetch(GENERATE_URL + '?applyYyyymm=' + encodeURIComponent(ym), {
            method:'POST', headers:{'Accept':'application/json'}
          })
          .then(function(r){ return r.json(); })
          .then(function(j){
            alert((j && (j.message||'')) || '생성 완료');
            if(document.getElementById('panelSummary').style.display!=='none') loadSummary(); else loadAnnex();
          })
          .catch(function(err){ alert('생성 실패: '+err); });
        }else{
          if(document.getElementById('panelSummary').style.display!=='none') loadSummary(); else loadAnnex();
        }
      };
    }
    var tabSummary=document.getElementById('tabSummary');
    var tabAnnex=document.getElementById('tabAnnex');
    if(tabSummary) tabSummary.onclick=function(){ activate('summary'); loadSummary(); };
    if(tabAnnex)   tabAnnex.onclick=function(){ activate('annex');   loadAnnex();   };
  })();

  /* ========================= 스크롤 영역 자동 높이 ========================= */
  (function(){
    function fit(){
      var sc=document.getElementById('dataScroll'); if(!sc) return;
      var refund=document.getElementById('refundBlock');
      var rect=sc.getBoundingClientRect(); var gap=12; var underH= refund? (refund.offsetHeight+12):0;
      var h=window.innerHeight - rect.top - underH - gap; if(h<220) h=220; sc.style.height=h+'px';
    }
    function on(el,t,fn){ if(el&&el.addEventListener){ el.addEventListener(t,fn,false);} }
    on(window,'load',fit); on(window,'resize',fit);
    on(document.getElementById('tabSummary'),'click',fit);
    on(document.getElementById('tabAnnex'),'click',fit);
    setTimeout(fit,80);
  })();

  /* ========================= 표 색칠(요약/부표) ========================= */
  var GREY_COLUMNS_BY_CODE = {
    "A01":["조정환급세액"], "A02":["조정환급세액"], "A03":["징수농특세","조정환급세액"],
    "A04":["징수농특세","징수소득세","조정환급세액","징수가산세"], "A05":["총지급액","조정환급세액"],
    "A06":["인원","총지급액","조정환급세액"], "A10":["인원","총지급액","징수농특세","징수소득세","징수가산세"],
    "A21":["징수농특세","조정환급세액"], "A22":["징수농특세","조정환급세액"], "A20":["인원","총지급액","징수농특세","징수소득세","징수가산세"],
    "A25":["징수농특세","조정환급세액"], "A26":["조정환급세액"], "A30":["인원","총지급액","징수농특세","징수소득세","징수가산세"],
    "A41":["징수농특세","조정환급세액"], "A43":["징수농특세","조정환급세액"], "A44":["징수농특세","조정환급세액"],
    "A42":["징수농특세","조정환급세액"], "A40":["인원","총지급액","징수농특세","징수소득세","징수가산세"],
    "A48":["징수농특세","조정환급세액"], "A45":["징수농특세","조정환급세액"], "A46":["징수농특세","조정환급세액"],
    "A47":["인원","총지급액","징수농특세","징수소득세","징수가산세"], "A69":["총지급액","징수농특세"],
    "A70":["징수농특세"], "A80":["징수농특세"], "A90":["인원","총지급액"],
    "A99":["인원","총지급액","징수농특세","징수소득세","징수가산세"]
  };
  var ALWAYS_GREY_COLUMNS = ["소득 구분","코드","납부소득세","납부농특세"];
  var SPECIAL_PINK_CODES = ["A10","A20","A30","A40","A47","A50","A60","A69","A70","A80","A90","A99"];

  function buildHeaderIndexMap(tableSelector){
    var map={}; var ths=document.querySelectorAll(tableSelector+' thead th');
    ths.forEach(function(th,idx){ var key=(th.textContent||'').trim(); if(key) map[key]=idx+1; });
    return map;
  }
  window.applyPerCellGrey = function(){
    var headerMap=buildHeaderIndexMap('#panelSummary table');
    var rows=document.querySelectorAll('#gridBody tr');
    var alwaysIdx = ALWAYS_GREY_COLUMNS.map(function(h){return headerMap[h];}).filter(Boolean);
    function idxOf(h){ return headerMap[h] || null; }

    rows.forEach(function(tr){
      tr.querySelectorAll('td').forEach(function(td){ td.classList.remove('cell-grey','cell-pink'); });
      var codeCell=tr.querySelector('td:nth-child(3)'); if(!codeCell) return;
      var code=(codeCell.textContent||'').trim();

      alwaysIdx.forEach(function(nth){ var td=tr.querySelector('td:nth-child('+nth+')'); if(td) td.classList.add('cell-grey'); });

      if(SPECIAL_PINK_CODES.indexOf(code)>=0){
        ["소득 구분","코드"].forEach(function(h){
          var nth=idxOf(h); if(!nth) return; var td=tr.querySelector('td:nth-child('+nth+')');
          if(td){ td.classList.remove('cell-grey'); td.classList.add('cell-pink'); }
        });
      }
      (GREY_COLUMNS_BY_CODE[code]||[]).forEach(function(h){
        var nth=idxOf(h); if(!nth) return; var td=tr.querySelector('td:nth-child('+nth+')'); if(td) td.classList.add('cell-grey');
      });
    });
  };

  // 부표
  var GREY_COLUMNS_BY_CODE_ANNEX = {
    "C01":["징수소득세","징수농특세","징수가산세","조정환급세액"], "C02":["징수소득세","징수농특세","징수가산세","조정환급세액"],
    "C03":["징수소득세","징수농특세","징수가산세","조정환급세액"], "C05":["징수소득세","징수농특세","징수가산세","조정환급세액"],
    "C06":["징수소득세","조정환급세액"], "C07":["징수소득세","징수농특세","징수가산세","조정환급세액"],
    "C08":["징수소득세","징수농특세","징수가산세","조정환급세액"], "C10":["징수소득세","징수농특세","징수가산세","조정환급세액"],
    "C11":["조정환급세액"], "C12":["조정환급세액"], "C13":["징수농특세","조정환급세액"], "C14":["징수농특세","조정환급세액"],
    "C15":["징수농특세","조정환급세액"], "C16":["징수농특세","조정환급세액"], "C18":["징수농특세","조정환급세액"],
    "C19":["징수소득세","조정환급세액"], "C20":["징수소득세","징수농특세","징수가산세","조정환급세액"], "C22":["조정환급세액"],
    "C23":["징수소득세","징수농특세","징수가산세","조정환급세액"], "C24":["징수농특세","조정환급세액"], "C25":["징수농특세","조정환급세액"],
    "C26":["징수농특세","조정환급세액"], "C28":["징수소득세","징수농특세","징수가산세","조정환급세액"], "C29":["징수소득세","조정환급세액"],
    "C30":["징수농특세","징수소득세","총지급액","인원","징수가산세"], "C31":["조정환급세액"], "C33":["조정환급세액"],
    "C34":["조정환급세액"], "C36":["징수농특세","조정환급세액"], "C37":["징수농특세","조정환급세액"], "C38":["징수농특세","조정환급세액"],
    "C39":["징수농특세","조정환급세액"], "C40":["징수소득세","조정환급세액"], "C41":["조정환급세액","징수농특세","총지급액"],
    "C42":["조정환급세액","징수농특세","총지급액"], "C43":["조정환급세액","징수농특세","총지급액"], "C44":["조정환급세액","징수농특세","총지급액"],
    "C45":["조정환급세액","징수농특세","총지급액"], "C46":["조정환급세액","총지급액"], "C50":["인원","총지급액","징수소득세","징수농특세","징수가산세"],
    "C52":["징수농특세","조정환급세액"], "C54":["징수농특세","조정환급세액"], "C55":["징수농특세","조정환급세액"],
    "C56":["조정환급세액"], "C57":["조정환급세액"], "C58":["징수농특세","조정환급세액"],
    "C70":["인원","총지급액","징수소득세","징수농특세","징수가산세"], "C71":["징수농특세"], "C72":["징수농특세"],
    "C73":["징수농특세"], "C74":["징수농특세"], "C75":["징수농특세"],
    "C76":["징수소득세","징수농특세","징수가산세","조정환급세액"],
    "C81":["징수농특세"], "C82":["징수농특세"], "C83":["징수농특세"], "C84":["징수농특세"], "C85":["징수농특세"],
    "C86":["징수농특세"], "C87":["징수농특세"], "C88":["징수농특세"],
    "C90":["인원","총지급액","징수소득세","징수농특세","징수가산세"]
  };
  var ALWAYS_GREY_COLUMNS_ANNEX = ["소득 구분","코드","납부소득세","납부농특세"];
  var SPECIAL_PINK_CODES_ANNEX = ["C30","C50","C70","C90"];

  function buildHeaderIndexMapAnnex(tableSelector){
    var map={}; var ths=document.querySelectorAll(tableSelector+' thead th');
    for(var i=0;i<ths.length;i++){
      var key=(ths[i].textContent!=null?ths[i].textContent:ths[i].innerText); key=(key||'').replace(/^\s+|\s+$/g,'');
      if(key) map[key]=i+1;
    } return map;
  }
  window.applyPerCellGreyAnnex = function(){
    var headerMap=buildHeaderIndexMapAnnex('#panelAnnex table');
    var rows=document.querySelectorAll('#annexBody tr');

    var alwaysIdx=[]; for(var i=0;i<ALWAYS_GREY_COLUMNS_ANNEX.length;i++){ var h=ALWAYS_GREY_COLUMNS_ANNEX[i]; if(headerMap[h]) alwaysIdx.push(headerMap[h]); }
    function idxOf(h){ return headerMap[h] ? headerMap[h] : null; }

    for(var r=0;r<rows.length;r++){
      var tr=rows[r]; var tds=tr.querySelectorAll('td'); for(var k=0;k<tds.length;k++){ tds[k].classList.remove('cell-grey','cell-pink'); }
      var codeCell=tr.querySelector('td:nth-child(3)'); if(!codeCell) continue;
      var code=(codeCell.textContent || codeCell.innerText || '').replace(/^\s+|\s+$/g,'');

      for(var a=0;a<alwaysIdx.length;a++){ var nth=alwaysIdx[a]; var td=tr.querySelector('td:nth-child('+nth+')'); if(td) td.classList.add('cell-grey'); }

      for(var sp=0; sp<SPECIAL_PINK_CODES_ANNEX.length; sp++){
        if(SPECIAL_PINK_CODES_ANNEX[sp]===code){
          ["소득 구분","코드"].forEach(function(hh){
            var nth2=idxOf(hh); if(!nth2) return; var td2=tr.querySelector('td:nth-child('+nth2+')'); if(td2){ td2.classList.remove('cell-grey'); td2.classList.add('cell-pink'); }
          });
          break;
        }
      }

      var list=GREY_COLUMNS_BY_CODE_ANNEX[code]||[];
      for(var c=0;c<list.length;c++){ var nth3=idxOf(list[c]); if(!nth3) continue; var td3=tr.querySelector('td:nth-child('+nth3+')'); if(td3) td3.classList.add('cell-grey'); }
    }
  };

  /* ========================= 전월 미환급세액 계산 (A~K) ========================= */
  (function refundBlock(){
    function toNum(v){ return Number(String(v||'').replace(/[^\d.-]/g,'')) || 0; }
    function fmt(n){ return n.toLocaleString('ko-KR'); }
    function setVal(el,n){ el.value = fmt(n); }

    var A=document.getElementById('A'), B=document.getElementById('B'), C=document.getElementById('C'),
        D=document.getElementById('D'), E=document.getElementById('E'), F=document.getElementById('F'), G=document.getElementById('G'),
        H=document.getElementById('H'), I=document.getElementById('I'), J=document.getElementById('J'), K=document.getElementById('K');
    if(!A||!B||!C||!D||!E||!F||!G||!H||!I||!J||!K) return;

    function headerIndexByName(tableSel, headerName){
      var ths=document.querySelectorAll(tableSel+' thead th');
      for(var i=0;i<ths.length;i++){ var key=(ths[i].textContent||'').trim(); if(key===headerName) return i+1; }
      return null;
    }
    function sumColByHeader(headerName){
      var nth=headerIndexByName('#panelSummary table', headerName); if(!nth) return 0;
      var rows=document.querySelectorAll('#gridBody tr'); var sum=0;
      for(var r=0;r<rows.length;r++){ var td=rows[r].querySelector('td:nth-child('+nth+')'); if(!td) continue; sum += toNum(td.textContent); }
      return sum;
    }
    // D/I를 요약표에서 계산
    function recalcFromTable(){
      var total = sumColByHeader('징수소득세') + sumColByHeader('징수가산세') + sumColByHeader('징수농특세');
      var d = (total < 0) ? (-total) : 0;
      setVal(D, d);

      var i = sumColByHeader('조정환급세액');
      setVal(I, i);

      recalcAll();
    }

    // A~K 재계산
    function recalcAll(){
      var a=Number(String(A.value).replace(/[^\d.-]/g,''))||0,
          b=Number(String(B.value).replace(/[^\d.-]/g,''))||0,
          d=Number(String(D.value).replace(/[^\d.-]/g,''))||0,
          e=Number(String(E.value).replace(/[^\d.-]/g,''))||0,
          f=Number(String(F.value).replace(/[^\d.-]/g,''))||0,
          g=Number(String(G.value).replace(/[^\d.-]/g,''))||0,
          i=Number(String(I.value).replace(/[^\d.-]/g,''))||0;
      var c=a-b, h=c+d+e+f+g, j=h-i;
      setVal(C, Math.max(0,c));
      setVal(H, Math.max(0,h));
      setVal(J, Math.max(0,j));
    }

    // 사용자 입력 가능 칸
    [A,B,E,F,G,K].forEach(function(el){
      el.addEventListener('input', function(){
        var n=Number(String(this.value).replace(/[^\d.-]/g,''))||0;
        this.value = (this===K) ? this.value : (n ? fmt(n) : '');
        if (this!==K) recalcAll();
      });
      el.addEventListener('blur', function(){
        if (this===K) return;
        var n=Number(String(this.value).replace(/[^\d.-]/g,''))||0;
        this.value = fmt(n);
        recalcAll();
      });
    });

    // 전월(A,B) 값을 서버에서 받아와 채우기
    function prefillPrevRefund() {
      if (!PREV_REFUND_URL) return;

      var ymEl = document.getElementById('ym');
      var ym = (ymEl && ymEl.value || '').trim();
      var m  = /^(\d{4})-(\d{2})$/.exec(ym);
      if (!m) return;

      var y = +m[1], mo = +m[2] - 1;
      if (mo === 0) { y -= 1; mo = 12; }
      var prev = String(y) + '-' + ('0' + mo).slice(-2);

      function setNum(el, v){
        if (!el) return;
        var n = Number(String(v || '').replace(/[^\d.-]/g, '')) || 0;
        setVal(el, n);
      }

      fetch(PREV_REFUND_URL + '?yyyymm=' + encodeURIComponent(prev), {
        headers: { 'Accept': 'application/json' }
      })
        .then(function(r){ if(!r.ok) throw new Error(r.status); return r.json(); })
        .then(function(j){
          setNum(A, j.prevCarryJ);   // A ← 전월 J
          setNum(B, j.prevApplyK);   // B ← 전월 K
          recalcAll();
        })
        .catch(function(){ /* quiet */ });
    }

    // 귀속월 바뀔 때마다 전월값 재주입
    var ymInput = document.getElementById('ym');
    if (ymInput) {
      ['change','blur'].forEach(function(ev){
        ymInput.addEventListener(ev, function(){
          recalcFromTable();
          prefillPrevRefund();
        });
      });
    }

    // 요약표 갱신 훅(요약 로딩 후 호출됨)
    window.recalcRefundByTables = function(){
      recalcFromTable();
      prefillPrevRefund();
    };

    // 초기 1회
    window.recalcRefundByTables();
  })();

  // 첫 로드 요약 불러오기
  onReady(function(){ loadSummary(); });

  /* ========================= PDF 저장(한 장에 합치기) ========================= */
  window.exportWHtoPDF = function (encrypt, password) {
    var JsPDFCtor = window.__getJsPDFCtor();
    if (!JsPDFCtor || !window.html2canvas) {
      alert('PDF 라이브러리가 로드되지 않았습니다.\nhtml2canvas/jsPDF 스크립트를 확인하세요.');
      return;
    }

    var pdf  = new JsPDFCtor({ unit: 'mm', format: 'a4', compress: true });
    var A4W  = 210, A4H = 297, M = 10;           // 여백
    var WMM  = A4W - M * 2, HMM = A4H - M * 2;   // 가용폭/높이
    var GAP_MM = 4;                               // 섹션 간 간격
    var sectionIds = ['panelSummary', 'panelAnnex', 'refundBlock'];

    function capture(id) {
      var el = document.getElementById(id);
      if (!el) return Promise.resolve(null);
      return html2canvas(el, { scale: 2, useCORS: true, backgroundColor: '#ffffff' })
        .then(function(cv){ return cv; })
        .catch(function(){ return null; });
    }

    Promise.all(sectionIds.map(capture)).then(function(canvases){
      canvases = canvases.filter(function(c){ return c && c.width && c.height; });
      if (!canvases.length) { alert('내보낼 섹션이 없습니다.'); return; }

      // 전체 크기(px)
      var maxWpx = 0, sumHpx = 0;
      for (var i=0;i<canvases.length;i++){ maxWpx = Math.max(maxWpx, canvases[i].width); sumHpx += canvases[i].height; }

      // 스케일(px→mm)
      var sByWidth  = WMM / maxWpx;
      var sByHeight = (HMM - (canvases.length - 1) * GAP_MM) / sumHpx;
      var s = Math.min(sByWidth, sByHeight);

      // 암호(플러그인 있을 때만)
      if (encrypt) {
        try {
          if (typeof pdf.setEncryption === 'function') {
            var pwd = (password || '').trim();
            if (pwd) pdf.setEncryption({ userPassword: pwd, ownerPassword: pwd, userPermissions: ['print','copy','modify'] });
          }
        } catch (e) { /* 실패해도 평문으로 계속 */ }
      }

      // 한 장에 위→아래 배치
      var y = M;
      for (var j=0;j<canvases.length;j++){
        var cv = canvases[j];
        var wmm = cv.width  * s;
        var hmm = cv.height * s;
        var x   = M + (WMM - wmm)/2;

        pdf.addImage(cv.toDataURL('image/jpeg', 0.95), 'JPEG', x, y, wmm, hmm);
        y += hmm + (j < canvases.length - 1 ? GAP_MM : 0);
      }

      var ym = ((document.getElementById('ym')||{}).value || '').replace('-', '') || 'YYYYMM';
      pdf.save('원천징수_' + ym + '.pdf');
    })
    .catch(function(err){
      console.error('[PDF] 예기치 못한 오류:', err);
      var ym = ((document.getElementById('ym')||{}).value || '').replace('-', '') || 'YYYYMM';
      pdf.save('원천징수_' + ym + '.pdf');
    });
  };

  /* ========================= 신고/암호화 버튼 바인딩 ========================= */
  (function(){
    function toNum(v){ return Number(String(v||'').replace(/[^\d.-]/g,'')) || 0; }
    function requireYm(){
      var ymEl = document.getElementById('ym');
      var ym = (ymEl && ymEl.value || '').trim();
      if (!/^\d{4}-\d{2}$/.test(ym)) { alert('귀속월(YYYY-MM)을 먼저 입력해 주세요.'); return null; }
      return ym;
    }
    function saveJK(ym, j, k){
      if (!SAVE_REFUND_URL) return Promise.resolve(); // 저장 생략
      var url = SAVE_REFUND_URL
        + '?yyyymm=' + encodeURIComponent(ym)
        + '&jValue=' + encodeURIComponent(j)
        + '&kValue=' + encodeURIComponent(k);

      return fetch(url, { method:'POST', headers:{ 'Accept':'application/json' } })
        .then(function(r){
          if(!r.ok) throw new Error('HTTP '+r.status);
          return r.json().catch(function(){ return {}; });
        });
    }

    onReady(function(){
      var btnMake    = document.getElementById('btnMake');
      var btnEncrypt = document.getElementById('btnEncrypt');

      function handleMakeClick(e){
        if (e && e.preventDefault) e.preventDefault();
        var ym = requireYm(); if (!ym) return;
        var fileName = makeFileName(ym);

        var j = toNum((document.getElementById('J')||{}).value);
        var k = toNum((document.getElementById('K')||{}).value);

        saveJK(ym, j, k)
          .then(function(){
            alert('환급 저장 완료. PDF 생성 진행합니다.\n파일명: ' + fileName);
            if (!window.__ensurePdfLibs()) return;
            window.exportWHtoPDF(false);
          })
          .catch(function(err){
            alert('환급 저장 실패: ' + err + '\nPDF 생성은 계속 진행합니다.');
            if (!window.__ensurePdfLibs()) return;
            window.exportWHtoPDF(false);
          });
      }

      function handleEncryptClick(e){
        if (e && e.preventDefault) e.preventDefault();
        var ym = requireYm(); if (!ym) return;

        var p1El = document.getElementById('pwd1');
        var p2El = document.getElementById('pwd2');
        var p1 = (p1El && p1El.value || '').trim();
        var p2 = (p2El && p2El.value || '').trim();

        if (!p1 || !p2) { alert('비밀번호를 입력하세요.'); (p1? p2El:p1El).focus(); return; }
        if (p1 !== p2)   { alert('비밀번호가 일치하지 않습니다.'); if (p2El && p2El.focus) p2El.focus(); return; }

        var fileName = makeFileName(ym);
        var j = toNum((document.getElementById('J')||{}).value);
        var k = toNum((document.getElementById('K')||{}).value);

        saveJK(ym, j, k)
          .then(function(){
            alert('환급 저장 완료. 암호화 PDF 생성 진행합니다.\n파일명: ' + fileName);
            if (!window.__ensurePdfLibs()) return;
            window.exportWHtoPDF(true, p1);
          })
          .catch(function(err){
            alert('환급 저장 실패: ' + err + '\n암호화 PDF 생성은 계속 진행합니다.');
            if (!window.__ensurePdfLibs()) return;
            window.exportWHtoPDF(true, p1);
          });
      }

      if (btnMake)    btnMake.onclick    = handleMakeClick;
      if (btnEncrypt) btnEncrypt.onclick = handleEncryptClick;
    });
  })();

})(); // 최상위 IIFE 끝
