// runpayroll.js
(function () {
  // DOM 헬퍼
  var $  = function (s, root) { return (root || document).querySelector(s); };
  var $$ = function (s, root) { return Array.prototype.slice.call((root || document).querySelectorAll(s)); };
  var fmt = function (n) { return (n == null || n === '') ? '' : Number(n).toLocaleString('ko-KR'); };
  var yn  = function (v) { return (v === 'Y' || v === 'N') ? v : (v ? 'Y' : 'N'); };
  function post(url, body) {
    return fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    }).then(function (r) { return r.json(); });
  }
  function flagBox(field, val, empNo) {
    var checked = yn(val) === 'Y' ? 'checked' : '';
    return '' +
      '<label class="chk">' +
      '  <input type="checkbox" class="flagchk" data-field="' + field + '" data-empno="' + (empNo || '') + '" ' + checked + '>' +
      '  <span class="sr-only">' + field + '</span>' +
      '</label>';
  }

  // JSP에서 주입한 URL들
  var urls = (window.RunPayrollConfig || {});

  // 상태
  var state = {
    yyyymm: '',
    payType: '',
    deptCode: '',
    empNo: '',
    rows: [],
    selected: null
  };

  // 초기 바인딩 및 이벤트
  function init() {
    state.yyyymm  = $('#yyyymm').value || '2018-08';
    state.payType = $('#payType').value || 'SALARY';
    state.deptCode= $('#deptCode').value.trim();
    state.empNo   = $('#empNo').value.trim();

    $('#btnSearch').addEventListener('click', onSearch);
    $('#btnReset').addEventListener('click', onReset);

    // 전체선택 동기화
    $('#chkAll').addEventListener('change', function (e) {
      var checked = e.target.checked;
      $$('#tblSummary tbody input[type="checkbox"].rowchk').forEach(function (cb) { cb.checked = checked; });
      $('#chkAllHeader').checked = checked;
    });
    $('#chkAllHeader').addEventListener('change', function (e) {
      var checked = e.target.checked;
      $$('#tblSummary tbody input[type="checkbox"].rowchk').forEach(function (cb) { cb.checked = checked; });
      $('#chkAll').checked = checked;
    });

    // 플래그 델리게이션
    $('#tblSummary tbody').addEventListener('change', function (e) {
      var t = e.target;
      if (!t.classList || !t.classList.contains('flagchk')) return;
      var field = t.getAttribute('data-field');
      var empNo = t.getAttribute('data-empno');
      var row = state.rows.find(function (x) { return (x.empNo || '') === empNo; });
      if (row) {
        row[field] = t.checked ? 'Y' : 'N';
        // 필요 시 서버 반영:
        // post('/runpayroll/api/update-flags', { empNo, field, value: row[field], yyyymm: state.yyyymm, payType: state.payType })
        //   .then(function(){ /* 재조회 등 */ });
        console.log('flag changed:', empNo, field, row[field]);
      }
    });

    // 액션 버튼
    $('#btnProcess').addEventListener('click', function () { bulkAction(urls.process, { yyyymm: state.yyyymm, payType: state.payType }); });
    $('#btnReTax').addEventListener('click', function () {
      bulkAction(urls.retax, { yyyymm: state.yyyymm, payType: state.payType }, true);
    });
    $('#btnApplyYrt').addEventListener('click', function () {
      bulkAction(urls.applyYrt, { yyyymm: state.yyyymm, splitMonths: 1 }, true);
    });
    $('#btnConfirm').addEventListener('click', function () {
      bulkAction(urls.confirm, { yyyymm: state.yyyymm, payType: state.payType, confirm: true });
    });
    $('#btnUnconfirm').addEventListener('click', function () {
      bulkAction(urls.confirm, { yyyymm: state.yyyymm, payType: state.payType, confirm: false });
    });

    // 첫 로드
    loadSummary();
  }

  function onSearch() {
    state.yyyymm  = $('#yyyymm').value || '2018-08';
    state.payType = $('#payType').value || '';
    state.deptCode= $('#deptCode').value.trim();
    state.empNo   = $('#empNo').value.trim();
    loadSummary();
  }

  function onReset() {
    $('#yyyymm').value = '2018-08';
    $('#payType').value = 'SALARY';
    $('#deptCode').value = '';
    $('#empNo').value = '';
    state = { yyyymm: '2018-08', payType: 'SALARY', deptCode: '', empNo: '', rows: [], selected: null };
    loadSummary();
  }

  function loadSummary() {
    var p = new URLSearchParams();
    p.set('yyyymm', state.yyyymm);
    if (state.payType) p.set('payType', state.payType);
    if (state.deptCode) p.set('deptCode', state.deptCode);
    if (state.empNo) p.set('empNo', state.empNo);

    fetch(urls.summary + '?' + p.toString(), { headers: { 'Accept': 'application/json' } })
      .then(function (res) { if (!res.ok) throw new Error('요약 조회 실패'); return res.json(); })
      .then(function (data) {
        state.rows = Array.isArray(data) ? data : [];
        $('#summaryCount').textContent = '총 ' + state.rows.length + ' 건';
        renderSummary();
        $('#selYyyymm').textContent = state.yyyymm;
        $('#selPayType').textContent = state.payType || 'ALL';
      })
      .catch(function (e) { alert(e.message); });
  }

  function renderSummary() {
    var tbody = $('#tblSummary tbody');
    tbody.innerHTML = '';
    state.rows.forEach(function (r, idx) {
      var tr = document.createElement('tr');
      tr.dataset.empNo = r.empNo;

      var html = '';
      html += '<td><input type="checkbox" class="rowchk" data-empno="' + (r.empNo || '') + '"></td>';
      html += '<td>' + (r.empNo || '') + '</td>';
      html += '<td>' + (r.empName || '') + '</td>';
      html += '<td>' + (r.deptName || '') + '</td>';
      html += '<td>' + (r.taxApplyType || '') + '</td>';
      html += '<td style="text-align:right;">' + (r.taxAdjustRate != null ? r.taxAdjustRate : '') + '</td>';
      html += '<td>' + (r.projectName || '') + '</td>';

      html += '<td>' + flagBox('taxCalcExemptYn',      r.taxCalcExemptYn,      r.empNo) + '</td>';
      html += '<td>' + flagBox('prorateYn',             r.prorateYn,            r.empNo) + '</td>';
      html += '<td>' + flagBox('settlementReflectYn',   r.settlementReflectYn,  r.empNo) + '</td>';
      html += '<td>' + flagBox('manufTaxExemptYn',      r.manufTaxExemptYn,     r.empNo) + '</td>';
      html += '<td>' + flagBox('overseasTaxExemptYn',   r.overseasTaxExemptYn,  r.empNo) + '</td>';
      html += '<td>' + flagBox('researcherTaxExemptYn', r.researcherTaxExemptYn,r.empNo) + '</td>';

      html += '<td style="text-align:right;">' + (r.incomeTaxReductionRate != null ? r.incomeTaxReductionRate : '') + '</td>';
      html += '<td>' + (r.personalTaxApplyType || '') + '</td>';
      html += '<td style="text-align:right;">' + (r.bonusRate != null ? r.bonusRate : '') + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.payTotAmt) + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.prevPayTotAmt) + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.dedTotAmt) + '</td>';
      html += '<td style="text-align:right;">' + fmt(r.netPayAmt) + '</td>';
      html += '<td>' + flagBox('retireYn', r.retireYn, r.empNo) + '</td>';

      tr.innerHTML = html;

      tr.addEventListener('click', function (e) {
        if (e.target && e.target.classList) {
          if (e.target.classList.contains('rowchk') || e.target.classList.contains('flagchk')) return;
        }
        $$('#tblSummary tbody tr').forEach(function (x) { x.classList.remove('active'); });
        tr.classList.add('active');
        onSelectRow(r);
      });

      tr.addEventListener('change', function (e) {
        if (e.target && e.target.classList && e.target.classList.contains('rowchk')) {
          var all = $$('#tblSummary tbody .rowchk');
          var checked = all.filter(function (c) { return c.checked; }).length;
          var allChecked = checked === all.length && all.length > 0;
          $('#chkAll').checked = allChecked;
          $('#chkAllHeader').checked = allChecked;
        }
      });

      tbody.appendChild(tr);
      if (idx === 0) { tr.classList.add('active'); onSelectRow(r); }
    });
  }

  function onSelectRow(row) {
    state.selected = row;
    $('#selEmpNo').textContent = row.empNo || '-';
    $('#selEmpNo').classList.remove('ghost');
    $('#selEmpName').textContent = row.empName || '-';
    $('#selEmpName').classList.remove('ghost');
    Promise.all([loadItems(row.empNo), loadDeds(row.empNo)]).catch(function () { });
  }

  function loadItems(empNo) {
    var p = new URLSearchParams();
    p.set('empNo', empNo);
    p.set('yyyymm', state.yyyymm);
    if (state.payType) p.set('payType', state.payType);

    return fetch(urls.items + '?' + p.toString(), { headers: { 'Accept': 'application/json' } })
      .then(function (res) { return res.ok ? res.json() : []; })
      .then(function (list) {
        var tbody = $('#tblItems tbody'); tbody.innerHTML = '';
        (Array.isArray(list) ? list : []).forEach(function (r) {
          var tr = document.createElement('tr');
          if ((r.itemName || '') === 'TOTAL') { tr.className = 'row-total'; }
          var html = '';
          html += '<td>' + (r.itemName || '') + '</td>';
          html += '<td>' + (r.nonTaxType || '') + '</td>';
          html += '<td>' + (r.previousYn || '') + '</td>';
          html += '<td style="text-align:right;">' + fmt(r.amount) + '</td>';
          tr.innerHTML = html;
          tbody.appendChild(tr);
        });
      });
  }

  function loadDeds(empNo) {
    var p = new URLSearchParams();
    p.set('empNo', empNo);
    p.set('yyyymm', state.yyyymm);
    if (state.payType) p.set('payType', state.payType);

    return fetch(urls.deductions + '?' + p.toString(), { headers: { 'Accept': 'application/json' } })
      .then(function (res) { return res.ok ? res.json() : []; })
      .then(function (list) {
        var tbody = $('#tblDeds tbody'); tbody.innerHTML = '';
        (Array.isArray(list) ? list : []).forEach(function (r) {
          var tr = document.createElement('tr');
          if ((r.deductionName || '') === 'TOTAL') { tr.className = 'row-total'; }
          var html = '';
          html += '<td>' + (r.deductionName || '') + '</td>';
          html += '<td style="text-align:right;">' + fmt(r.amount) + '</td>';
          tr.innerHTML = html;
          tbody.appendChild(tr);
        });
      });
  }

  function selectedEmpNos() {
    return $$('#tblSummary tbody .rowchk')
      .filter(function (cb) { return cb.checked; })
      .map(function (cb) { return cb.getAttribute('data-empno'); });
  }

  function bulkAction(url, basePayload, refreshRight) {
    var empNos = selectedEmpNos();
    if (empNos.length === 0) { alert('대상 사원을 선택하세요.'); return; }
    var payload = Object.assign({}, basePayload, { empNos: empNos });
    post(url, payload).then(function () {
      if (refreshRight && state.selected && state.selected.empNo) {
        loadItems(state.selected.empNo);
        loadDeds(state.selected.empNo);
      }
      loadSummary();
    });
  }

  // DOM 준비 후 초기화
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
