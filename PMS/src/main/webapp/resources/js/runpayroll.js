/* global RunPayrollConfig */
(function () {
  'use strict';

  // ===================== 공통 =====================
  var cfg = window.RunPayrollConfig || {};
  function $(sel, p) { return (p || document).querySelector(sel); }
  function $$(sel, p) { return Array.prototype.slice.call((p || document).querySelectorAll(sel)); }

  function toast(msg) { alert(msg); }

  function qs(params) {
    var sp = [];
    if (!params) return '';
    for (var k in params) {
      if (!params.hasOwnProperty(k)) continue;
      var v = params[k];
      if (v !== undefined && v !== null && String(v).trim() !== '') {
        sp.push(encodeURIComponent(k) + '=' + encodeURIComponent(v));
      }
    }
    return sp.join('&');
  }

  function getVal(el) { return el ? el.value : ''; }

  function getForm() {
    return {
      yyyymm: getVal($('#yyyymm')) || '',
      payType: getVal($('#payType')) || '',
      deptCode: getVal($('#deptCode')) || '',
      empNo: getVal($('#empNo')) || ''
    };
  }

  function getSelectedEmpNos() {
    var arr = [];
    $$('#tblSummary tbody input[name="empCheck"]:checked').forEach(function (chk) {
      arr.push(chk.getAttribute('data-empno'));
    });
    return arr;
  }

  function setButtonsDisabled(disabled) {
    ['#btnProcess', '#btnReTax', '#btnApplyYrt', '#btnConfirm', '#btnUnconfirm', '#btnSearch', '#btnReset']
      .forEach(function (id) {
        var el = $(id);
        if (el) el.disabled = disabled;
      });
  }

  function ajaxGET(url, params, onDone) {
    var full = params ? (url + '?' + qs(params)) : url;
    fetch(full, { credentials: 'same-origin' })
      .then(function (r) { return r.ok ? r.json() : Promise.reject(r.statusText); })
      .then(function (data) { if (onDone) onDone(data); })
      .catch(function (e) { toast('요청 실패: ' + e); });
  }

  function ajaxPOST(url, payload, onDone) {
    fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      credentials: 'same-origin',
      body: JSON.stringify(payload || {})
    })
      .then(function (r) { return r.ok ? r.json() : Promise.reject(r.statusText); })
      .then(function (data) { if (onDone) onDone(data); })
      .catch(function (e) { toast('요청 실패: ' + e); });
  }

  function formatAmt(n) {
    if (n === null || n === undefined) return '';
    var v = Number(n);
    if (isNaN(v)) return String(n);
    try { return v.toLocaleString('ko-KR'); } catch (e) { return String(v); }
  }

  function safeVal(v) {
    return (v === null || v === undefined) ? '' : String(v);
  }

  function yn(v) {
    return (String(v || '').toUpperCase() === 'Y') ? 'checked' : '';
  }

  // ===================== 렌더: 요약 =====================
  function renderSummary(rows) {
    var tbody = $('#tblSummary tbody');
    tbody.innerHTML = '';
    var cnt = 0;
    (rows || []).forEach(function (row) {
      cnt++;
      var tr = document.createElement('tr');
      tr.setAttribute('data-empno', row.empNo || '');
      tr.setAttribute('data-empname', row.empName || '');

      var html = ''
        + '<td><input type="checkbox" name="empCheck" data-empno="' + (row.empNo || '') + '" aria-label="선택"></td>'
        + '<td class="emp-no">' + (row.empNo || '') + '</td>'
        + '<td>' + (row.empName || '') + '</td>'
        + '<td>' + (row.deptName || '') + '</td>'
        + '<td>' + (row.taxApplyType || '-') + '</td>'

        + '<td><input type="number" step="0.001" name="taxAdjustRate" value="' + safeVal(row.taxAdjustRate) + '" style="width:90px"></td>'
        + '<td><input type="text" name="projectName" value="' + safeVal(row.projectName) + '" style="width:120px"></td>'

        + '<td class="center"><input type="checkbox" name="taxCalcExemptYn" ' + yn(row.taxCalcExemptYn) + '></td>'
        + '<td class="center"><input type="checkbox" name="prorateYn" ' + yn(row.prorateYn) + '></td>'
        + '<td class="center"><input type="checkbox" name="settlementReflectYn" ' + yn(row.settlementReflectYn) + '></td>'
        + '<td class="center"><input type="checkbox" name="manufTaxExemptYn" ' + yn(row.manufTaxExemptYn) + '></td>'
        + '<td class="center"><input type="checkbox" name="overseasTaxExemptYn" ' + yn(row.overseasTaxExemptYn) + '></td>'
        + '<td class="center"><input type="checkbox" name="researcherTaxExemptYn" ' + yn(row.researcherTaxExemptYn) + '></td>'

        + '<td><input type="number" step="0.001" name="incomeTaxReductionRate" value="' + safeVal(row.incomeTaxReductionRate) + '" style="width:90px"></td>'
        + '<td><input type="text" name="personalTaxApplyType" value="' + safeVal(row.personalTaxApplyType) + '" style="width:110px"></td>'
        + '<td><input type="number" step="0.001" name="bonusRate" value="' + safeVal(row.bonusRate) + '" style="width:90px"></td>'

        + '<td style="text-align:right">' + formatAmt(row.payTotAmt) + '</td>'
        + '<td style="text-align:right">' + formatAmt(row.prevPayTotAmt) + '</td>'
        + '<td style="text-align:right">' + formatAmt(row.dedTotAmt) + '</td>'
        + '<td style="text-align:right">' + formatAmt(row.netPayAmt) + '</td>'
        + '<td class="center"><input type="checkbox" name="retiredYn" ' + yn(row.retireYn) + '></td>'; 

      tr.innerHTML = html;

      tr.addEventListener('click', function (ev) {
        if (ev.target && ev.target.name === 'empCheck') return;
        $$('#tblSummary tbody tr').forEach(function (r) { r.classList.remove('active'); });
        tr.classList.add('active');
        loadDetails(row.empNo);
      });

      tbody.appendChild(tr);
    });
    var sc = $('#summaryCount');
    if (sc) sc.textContent = '총 ' + cnt + '명';
    var chkAllHeader = $('#chkAllHeader');
    if (chkAllHeader) { chkAllHeader.checked = false; chkAllHeader.indeterminate = false; }
    bindRowCheckSync();
  }

  // ===================== 렌더: 상세 =====================
  function renderItems(rows) {
    var tbody = $('#tblItems tbody');
    tbody.innerHTML = '';
    (rows || []).forEach(function (r) {
      var tr = document.createElement('tr');
      var isTotal = (r.itemName === 'TOTAL');
      if (isTotal) tr.classList.add('row-total');
      tr.innerHTML = ''
        + '<td>' + (r.itemName || '') + '</td>'
        + '<td>' + (r.nonTaxType || '') + '</td>'
        + '<td>' + (r.previousYn || '') + '</td>'
        + '<td style="text-align:right">' + formatAmt(r.amount) + '</td>';
      tbody.appendChild(tr);
    });
  }

  function renderDeds(rows) {
    var tbody = $('#tblDeds tbody');
    tbody.innerHTML = '';
    (rows || []).forEach(function (r) {
      var tr = document.createElement('tr');
      var isTotal = (r.deductionName === 'TOTAL');
      if (isTotal) tr.classList.add('row-total');
      tr.innerHTML = ''
        + '<td>' + (r.deductionName || '') + '</td>'
        + '<td style="text-align:right">' + formatAmt(r.amount) + '</td>';
      tbody.appendChild(tr);
    });
  }

  // ===================== 데이터 로드 =====================
  function doSearch() {
    var f = getForm();
    var params = {
      yyyymm: f.yyyymm,
      payType: f.payType,
      deptCode: f.deptCode,
      empNo: f.empNo
    };
    setButtonsDisabled(true);
    ajaxGET(cfg.summary, params, function (rows) {
      renderSummary(rows || []);
      var selY = $('#selYyyymm'); if (selY) selY.textContent = f.yyyymm || '-';
      var selP = $('#selPayType'); if (selP) selP.textContent = f.payType || '-';
      var selNo = $('#selEmpNo'); if (selNo) selNo.textContent = '-';
      var selNm = $('#selEmpName'); if (selNm) selNm.textContent = '-';
      renderItems([]);
      renderDeds([]);
      setButtonsDisabled(false);
    });
  }

  function loadDetails(empNo) {
    var f = getForm();
    var elNo = $('#selEmpNo'); if (elNo) elNo.textContent = empNo || '-';
    var tr = $('#tblSummary tbody tr[data-empno="' + empNo + '"]');
    var nm = tr ? tr.getAttribute('data-empname') : '-';
    var elNm = $('#selEmpName'); if (elNm) elNm.textContent = nm || '-';
    var elY = $('#selYyyymm'); if (elY) elY.textContent = f.yyyymm || '-';
    var elP = $('#selPayType'); if (elP) elP.textContent = f.payType || '-';

    var baseParams = { empNo: empNo, yyyymm: f.yyyymm, payType: f.payType };
    ajaxGET(cfg.items, baseParams, renderItems);
    ajaxGET(cfg.deductions, baseParams, renderDeds);
  }

  // ===================== EmpFlag 수집 =====================
  function collectFlags(selectedSet) {
    var flags = [];
    $$('#tblSummary tbody tr[data-empno]').forEach(function (tr) {
      var empNo = tr.getAttribute('data-empno');
      if (!empNo || (selectedSet && !selectedSet.has(empNo))) return;

      function get(name) { return tr.querySelector('[name="' + name + '"]'); }
      function val(name) {
        var el = get(name);
        if (!el) return null;
        if (el.type === 'checkbox') return el.checked ? 'Y' : 'N';
        return (el.value || '').trim();
      }

      flags.push({
        empNo: empNo,
        taxAdjustRate:            val('taxAdjustRate'),
        projectName:              val('projectName'),
        taxCalcExemptYn:          val('taxCalcExemptYn'),
        prorateYn:                val('prorateYn'),
        settlementReflectYn:      val('settlementReflectYn'),
        manufTaxExemptYn:         val('manufTaxExemptYn'),
        overseasTaxExemptYn:      val('overseasTaxExemptYn'),
        researcherTaxExemptYn:    val('researcherTaxExemptYn'),
        incomeTaxReductionRate:   val('incomeTaxReductionRate'),
        personalTaxApplyType:     val('personalTaxApplyType'),
        bonusRate:                val('bonusRate'),
        retiredYn:                val('retiredYn')
      });
    });
    return flags;
  }


  // ===================== 공통 결과 처리 =====================
  function handleSimpleResult(res, fallbackMsg) {
    if (!res) { toast('응답이 비었습니다.'); return; }
    if (res.success) {
      var msg = (res.message || fallbackMsg);
      if (res.affected !== null && res.affected !== undefined) msg += ' [' + res.affected + '건]';
      toast(msg);
      doSearch();
    } else {
      toast(res.message || '처리에 실패했습니다.');
    }
  }

  // ===================== 액션 =====================
  function doProcessPayroll() {
	  var f = getForm();
	  var empNos = getSelectedEmpNos();
	  if (!f.yyyymm) { toast('지급연월을 선택하세요.'); return; }
	  if (!f.payType) { toast('급여유형을 선택하세요.'); return; }
	  if (empNos.length === 0) { toast('대상 사원을 선택하세요.'); return; }

	  var selected = new Set(empNos);
	  var payload = {
	    yyyymm: f.yyyymm,
	    payType: f.payType,
	    empNos: empNos,
	    flags: collectFlags(selected) 
	  };

	  setButtonsDisabled(true);
	  ajaxPOST(cfg.process, payload, function (res) {
	    setButtonsDisabled(false);
	    handleSimpleResult(res, '급상여 처리가 완료되었습니다.');
	  });
	}

  function doReTax() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    if (!f.yyyymm) { toast('지급연월을 선택하세요.'); return; }
    if (!f.payType) { toast('급여유형을 선택하세요.'); return; }
    if (empNos.length === 0) { toast('대상 사원을 선택하세요.'); return; }

    var payload = { yyyymm: f.yyyymm, payType: f.payType, empNos: empNos };
    setButtonsDisabled(true);
    ajaxPOST(cfg.retax, payload, function (res) {
      setButtonsDisabled(false);
      handleSimpleResult(res, '세금 재처리가 완료되었습니다.');
    });
  }

  function doApplyYrt() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    if (!f.yyyymm) { toast('지급연월을 선택하세요.'); return; }
    if (!f.payType) { toast('급여유형을 선택하세요.'); return; }
    if (empNos.length === 0) { toast('대상 사원을 선택하세요.'); return; }

    var payload = { yyyymm: f.yyyymm, payType: f.payType, empNos: empNos };
    setButtonsDisabled(true);
    ajaxPOST(cfg.applyYrt, payload, function (res) {
      setButtonsDisabled(false);
      handleSimpleResult(res, '정산세금 반영이 완료되었습니다.');
    });
  }

  function doConfirm() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    if (!f.yyyymm) { toast('지급연월을 선택하세요.'); return; }
    if (!f.payType) { toast('급여유형을 선택하세요.'); return; }
    if (empNos.length === 0) { toast('대상 사원을 선택하세요.'); return; }
    if (!window.confirm('선택 사원의 급여명세를 확정하시겠습니까? 확정 후에는 급상여 처리가 불가합니다.')) { return; }

    var payload = { yyyymm: f.yyyymm, payType: f.payType, empNos: empNos };
    setButtonsDisabled(true);
    ajaxPOST(cfg.confirm, payload, function (res) {
      setButtonsDisabled(false);
      handleSimpleResult(res, '확정되었습니다.');
    });
  }

  function doUnconfirm() {
	  var f = getForm();
	  var empNos = getSelectedEmpNos();
	  if (!cfg.unconfirm) { toast('확정해제 API가 설정되어 있지 않습니다. (RunPayrollConfig.unconfirm)'); return; }
	  if (!f.yyyymm) { toast('지급연월을 선택하세요.'); return; }
	  if (!f.payType) { toast('급여유형을 선택하세요.'); return; }
	  if (empNos.length === 0) { toast('대상 사원을 선택하세요.'); return; }
	  if (!window.confirm('선택 사원의 확정을 해제하시겠습니까?')) { return; }

	  // 컨트롤러: @RequestParam yyyymm, payType + @RequestBody List<String>
		// empNos
	  var url = cfg.unconfirm + '?' + qs({ yyyymm: f.yyyymm, payType: f.payType });

	  setButtonsDisabled(true);
	  fetch(url, {
	    method: 'POST',
	    headers: { 'Content-Type': 'application/json;charset=UTF-8' },
	    credentials: 'same-origin',
	    body: JSON.stringify(empNos) // ← Body에는 배열만
	  })
	  .then(function (r) { return r.ok ? r.json() : Promise.reject(r.statusText); })
	  .then(function (res) {
	    setButtonsDisabled(false);
	    handleSimpleResult(res, '확정해제되었습니다.');
	  })
	  .catch(function (e) {
	    setButtonsDisabled(false);
	    toast('요청 실패: ' + e);
	  });
	}


  // ===================== 이벤트 바인딩 =====================
  function bindEvents() {
    var btn;
    btn = $('#btnSearch');   if (btn) btn.addEventListener('click', doSearch);
    btn = $('#btnReset');    if (btn) btn.addEventListener('click', function () {
      var d = $('#deptCode'); if (d) d.value = '';
      var e = $('#empNo');    if (e) e.value = '';
      doSearch();
    });

    btn = $('#btnProcess');  if (btn) btn.addEventListener('click', doProcessPayroll);
    btn = $('#btnReTax');    if (btn) btn.addEventListener('click', doReTax);
    btn = $('#btnApplyYrt'); if (btn) btn.addEventListener('click', doApplyYrt);
    btn = $('#btnConfirm');  if (btn) btn.addEventListener('click', doConfirm);
    btn = $('#btnUnconfirm');if (btn) btn.addEventListener('click', doUnconfirm);
    btn = $('#btnUnconfirm');if (btn) btn.addEventListener('click', doUnconfirm);
    btn = $('#btnSearchEmp');if (btn) btn.addEventListener('click', openEmployeePopup);
    
    // cfg.unconfirm이 없으면 버튼 비활성화
    if (!cfg.unconfirm) {
      var unBtn = $('#btnUnconfirm');
      if (unBtn) { unBtn.disabled = true; unBtn.title = 'unconfirm API 미설정'; }
    }

    var chkAll = $('#chkAll');
    var chkAllHeader = $('#chkAllHeader');
    function syncAll(checked) {
      $$('#tblSummary tbody input[name="empCheck"]').forEach(function (chk) { chk.checked = checked; });
    }
    if (chkAll) {
      chkAll.addEventListener('change', function (e) {
        var checked = e.target.checked;
        syncAll(checked);
        if (chkAllHeader) chkAllHeader.checked = checked;
      });
    }
    if (chkAllHeader) {
      chkAllHeader.addEventListener('change', function (e) {
        var checked = e.target.checked;
        syncAll(checked);
        if (chkAll) chkAll.checked = checked;
      });
    }
  }
  
  function bindRowCheckSync() {
	  var header = $('#chkAllHeader');
	  var all = $$('#tblSummary tbody input[name="empCheck"]');
	  if (!header || all.length === 0) return;

	  all.forEach(function (chk) {
	    chk.addEventListener('change', function () {
	      var allChecked = all.every(function (c) { return c.checked; });
	      var anyChecked = all.some(function (c) { return c.checked; });
	      header.indeterminate = !allChecked && anyChecked;
	      header.checked = allChecked;
	    });
	  });
	}

// 팝업에서 호출할 콜백 (이미 popup-common.js에서 window.opener.onEmployeePicked 호출)
// function onEmployeePicked(row) {
// console.log('선택된 사원:', row);
// // 예시: 화면의 입력칸에 채우기
// document.getElementById('empNo').value = row.empNo || '';
// document.getElementById('empName').value = row.empName || '';
// document.getElementById('deptName').value= row.deptName || '';
// // 필요 시 추가 필드들 매핑
// }
//  
  window.onEmployeePicked = function(row) {
    console.log("화면 업데이트");
    document.getElementById('empNo').value   = row.empNo || '';
    document.getElementById('empName').value = row.empName || '';
    document.getElementById('deptName').value= row.deptName || '';
    };

  // 사원 검색 팝업 열기
  function openEmployeePopup() {
    const w = 1100, h = 700;
    const x = (screen.availWidth  - w) / 2;
    const y = (screen.availHeight - h) / 2;
    window.open(
      'popups/employees',  // JSP 경로
      'empPopup',
      `width=${w},height=${h},left=${x},top=${y},resizable=yes,scrollbars=yes`
    );
  }
  
  
  // ===================== 초기화 =====================
  document.addEventListener('DOMContentLoaded', function () {
    bindEvents();
    doSearch();
  });
})();
