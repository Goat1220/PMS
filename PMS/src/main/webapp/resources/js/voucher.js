/* global VoucherConfig */
(function () {
  'use strict';

  // =============== 유틸 ===============
  var cfg = window.VoucherConfig || {};
  function $(sel, p) { return (p || document).querySelector(sel); }
  function $$(sel, p) { return Array.prototype.slice.call((p || document).querySelectorAll(sel)); }

  function toast(msg) { alert(msg); }

  function qs(params) {
    if (!params) return '';
    var arr = [];
    for (var k in params) {
      if (!params.hasOwnProperty(k)) continue;
      var v = params[k];
      if (v !== undefined && v !== null && String(v).trim() !== '') {
        arr.push(encodeURIComponent(k) + '=' + encodeURIComponent(v));
      }
    }
    return arr.join('&');
  }

  function getVal(el) { return el ? el.value : ''; }

  function intOrNull(v) {
    if (v === null || v === undefined || String(v).trim() === '') return null;
    var n = Number(v);
    return isNaN(n) ? null : n;
  }

  function formatAmt(n) {
    if (n === null || n === undefined) return '';
    var v = Number(n);
    if (isNaN(v)) return String(n);
    try { return v.toLocaleString('ko-KR'); } catch (e) { return String(v); }
  }

  function setBusy(isBusy) {
    var ids = ['#btnPreview', '#btnProcess'];
    ids.forEach(function (id) { var el = $(id); if (el) el.disabled = isBusy; });
    var sp = $('#loading');
    if (sp) sp.style.display = isBusy ? 'inline-block' : 'none';
  }

  // =============== 폼 ===============
  function getForm() {
    return {
      yyyymm: getVal($('#yyyymm')),
      payType: getVal($('#payType')),
      wageAccountId: intOrNull(getVal($('#wageAccountId'))),
      withholdAccountId: intOrNull(getVal($('#withholdAccountId'))),
      summaryNote: getVal($('#summaryNote')),
      approvedYn: getVal($('#approvedYn')) || 'N'
    };
  }

  function validateBase(f) {
    if (!f.yyyymm) { toast('지급연월(YYYY-MM)을 선택하세요.'); return false; }
    if (!f.payType) { toast('급여유형을 선택하세요.'); return false; }
    return true;
  }

  // =============== Ajax ===============
  function ajaxGET(url, params, onDone) {
    var full = params ? (url + '?' + qs(params)) : url;
    fetch(full, { credentials: 'same-origin' })
      .then(function (r) {
        if (!r.ok) return Promise.reject(r.statusText || ('HTTP ' + r.status));
        return r.json();
      })
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
    .then(function (r) {
      if (!r.ok) return Promise.reject(r.statusText || ('HTTP ' + r.status));
      return r.json();
    })
    .then(function (data) { if (onDone) onDone(data); })
    .catch(function (e) { toast('요청 실패: ' + e); });
  }

  // =============== 렌더 ===============
  function renderPreview(rows) {
    // rows: mapper가 반환한 [{lineSeq, accountName, drcrCode, debitAmt, creditAmt, deptName, deptId, accountId, payDate, note, approvedYn}, ...]
    var tbody = $('#tblPreview tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    var debitTot = 0;
    var creditTot = 0;

    (rows || []).forEach(function (r) {
      var tr = document.createElement('tr');

      var debit = Number(r.debitAmt || 0);
      var credit = Number(r.creditAmt || 0);
      debitTot += debit;
      creditTot += credit;

      tr.innerHTML =
        '<td style="text-align:right;">' + (r.lineSeq || '') + '</td>' +
        '<td>' + (r.deptName || '') + '</td>' +
        '<td>' + (r.accountName || '') + '</td>' +
        '<td style="text-align:center;">' + (r.drcrCode || '') + '</td>' +
        '<td style="text-align:right;">' + formatAmt(debit) + '</td>' +
        '<td style="text-align:right;">' + formatAmt(credit) + '</td>' +
        '<td class="muted">' + (r.note || '') + '</td>';
      tbody.appendChild(tr);
    });

    // 합계행
    var trSum = document.createElement('tr');
    trSum.className = 'row-total';
    trSum.innerHTML =
      '<td colspan="3" style="text-align:right;font-weight:600;">합계</td>' +
      '<td></td>' +
      '<td style="text-align:right;font-weight:600;">' + formatAmt(debitTot) + '</td>' +
      '<td style="text-align:right;font-weight:600;">' + formatAmt(creditTot) + '</td>' +
      '<td></td>';
    tbody.appendChild(trSum);

    // 차대 불일치 경고
    var warn = $('#balanceWarn');
    if (warn) {
      warn.style.display = (debitTot !== creditTot) ? 'block' : 'none';
      warn.textContent = (debitTot !== creditTot)
        ? ('⚠️ 차변(' + formatAmt(debitTot) + ')과 대변(' + formatAmt(creditTot) + ') 합계가 일치하지 않습니다.')
        : '';
    }
  }

  // =============== 동작 ===============
  function doPreview() {
    var f = getForm();
    if (!validateBase(f)) return;
    if (!cfg.preview) { toast('미리보기 API가 설정되어 있지 않습니다.'); return; }

    setBusy(true);
    ajaxGET(cfg.preview, { yyyymm: f.yyyymm, payType: f.payType }, function (rows) {
      setBusy(false);
      renderPreview(rows || []);
    });
  }

  function doProcess() {
    var f = getForm();
    if (!validateBase(f)) return;
    if (!cfg.process) { toast('처리 API가 설정되어 있지 않습니다.'); return; }

    // 기본 토큰 미입력 시 서비스에서 "PAYVCH-YYYY-MM-PAYTYPE" 생성
    var payload = {
      yyyymm: f.yyyymm,
      payType: f.payType,
      wageAccountId: f.wageAccountId,          // null이면 서비스에서 1001 사용
      withholdAccountId: f.withholdAccountId,  // null이면 서비스에서 2101 사용
      summaryNote: f.summaryNote,
      approvedYn: f.approvedYn || 'N'
    };

    if (!window.confirm('미리보기 결과대로 전표를 생성(재작성)합니다. 진행할까요?')) return;

    setBusy(true);
    ajaxPOST(cfg.process, payload, function (res) {
      setBusy(false);
      if (!res) { toast('응답이 비었습니다.'); return; }
      if (res.success) {
        var msg = res.message || '전표가 처리되었습니다.';
        if (res.affected !== null && res.affected !== undefined) msg += ' [' + res.affected + '행]';
        toast(msg);
        doPreview(); // 처리 후 미리보기 갱신
      } else {
        toast(res.message || '전표 처리에 실패했습니다.');
      }
    });
  }

  // =============== 바인딩 ===============
  function bind() {
    var btn;
    btn = $('#btnPreview'); if (btn) btn.addEventListener('click', doPreview);
    btn = $('#btnProcess'); if (btn) btn.addEventListener('click', doProcess);

    // 엔터로 미리보기
    ['#yyyymm', '#payType', '#wageAccountId', '#withholdAccountId', '#summaryNote'].forEach(function (sel) {
      var el = $(sel);
      if (!el) return;
      el.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') doPreview();
      });
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    bind();
    // 초기 자동 미리보기 원하면 주석 해제
    // doPreview();
  });
})();
