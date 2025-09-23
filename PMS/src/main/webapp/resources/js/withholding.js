(function () {
  var root = document.querySelector('.container');
  var SUMMARY_URL = root.getAttribute('data-summary-url');
  var ANNEX_URL   = root.getAttribute('data-annex-url');

  function fmt(n){
    if (n == null) return '0';
    return String(n).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  function fetchJson(url){
    return fetch(url, { headers: { 'Accept': 'application/json' } })
      .then(function(r){
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      });
  }
  
  // --- "YYYY-MM" → "WH_YYYYMM.txt" 유틸 ---
  function makeFileName(ym) {
    const m = /^(\d{4})-(\d{2})$/.exec((ym || '').trim());
    return m ? `WH_${m[1]}${m[2]}.txt` : '';
  }
   //비밀번호 체크
  document.getElementById('btnMake').addEventListener('click', function () {
	  var p1 = document.getElementById('pwd1').value.trim();
	  var p2 = document.getElementById('pwd2').value.trim();
	  if (p1 !== p2) {
	    alert('비밀번호가 일치하지 않습니다.');
	    return;
	  }
	  
      const ym =
          document.getElementById('ym')?.value?.trim() ||
          document.getElementById('reportYm')?.value?.trim() ||
          '';
        const fileName = makeFileName(ym);
        console.log("fileName" + fileName);
	  // TODO: 실제 생성 로직 호출
	  alert('생성 준비 완료(샘플).');
	});


  // --- 암호화 버튼: 동일하게 파일명 계산해서 사용 ---
  document.getElementById('btnEncrypt')?.addEventListener('click', function () {
    const ym =
      document.getElementById('ym')?.value?.trim() ||
      document.getElementById('reportYm')?.value?.trim() ||
      '';
    const fileName = makeFileName(ym);
    if (!fileName) {
      alert('귀속월(YYYY-MM)을 먼저 입력해 주세요.');
      return;
    }


  // 요약
  function loadSummary(){
    var ym = (document.getElementById('ym').value || '2025-05');
    fetchJson(SUMMARY_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb = document.getElementById('gridBody');
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

  // 부표
  function loadAnnex(){
    var ym = (document.getElementById('ym').value || '2025-05');
    fetchJson(ANNEX_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb = document.getElementById('annexBody');
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

  function activate(which){
    var sTab = document.getElementById('tabSummary');
    var aTab = document.getElementById('tabAnnex');
    var sPan = document.getElementById('panelSummary');
    var aPan = document.getElementById('panelAnnex');
    if (which === 'summary'){
      sTab.classList.add('active'); aTab.classList.remove('active');
      sPan.style.display = 'block'; aPan.style.display = 'none';
    } else {
      aTab.classList.add('active'); sTab.classList.remove('active');
      aPan.style.display = 'block'; sPan.style.display = 'none';
    }
  }

  document.getElementById('btnLoad').onclick = function(){
    if (document.getElementById('panelSummary').style.display !== 'none') loadSummary();
    else loadAnnex();
  };
  document.getElementById('tabSummary').onclick = function(){ activate('summary'); loadSummary(); };
  document.getElementById('tabAnnex').onclick   = function(){ activate('annex');   loadAnnex();   };

  document.addEventListener('DOMContentLoaded', loadSummary);
})();
