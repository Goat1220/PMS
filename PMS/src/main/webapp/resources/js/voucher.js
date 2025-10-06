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
    ['#btnBaseGenerate', '#btnProcess'].forEach(function (id) {
      var el = $(id);
      if (el) el.disabled = isBusy;
    });
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
      .catch(function (e) { toast('요청 실패: ' + e); setBusy(false); });
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
    .catch(function (e) { toast('요청 실패: ' + e); setBusy(false); });
  }

  // =============== 렌더 ===============
  /**
   * rows: VoucherLedgerRow[]
   * 매핑: JSP 헤더 순서에 정확히 맞춤
   */
  function renderPreview(rows) {
    var tbody = $('#tblVoucher tbody');              
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
        '<td class="right">' + (r.displaySeq || '') + '</td>' +                              // #
        '<td>' + (r.accountName || '') + '</td>' +                                        // 계정과목(현재 account_id)
        '<td class="center">' + (r.drcrName || r.drcrCode || '') + '</td>' +              // 차대구분
        '<td class="right">' + formatAmt(debit) + '</td>' +                               // 차변금액
        '<td class="right">' + formatAmt(credit) + '</td>' +                              // 대변금액
        '<td>' + (r.occurDeptName || '') + '</td>' +                                      // 발생부서
        '<td>' + (r.targetCode || '') + '</td>' +                                         // 발생원천
        '<td>' + (r.payDateStr || r.occurDate || '') + '</td>' +                          // 지급일
        '<td>' + (r.summaryNote || '') + '</td>' +                                        // 적요
        '<td>' + (r.voucherNo || '') + '</td>' +                                          // 전표형번호(표시용)
        '<td>' + (r.voucherNo || '') + '</td>' +                                          // 전표내부코드(추후 voucherId 매핑 권장)
        '<td>' + (r.approvedYn || '') + '</td>' +                                         // 승인여부
        '<td class="right">' + (r.lineSeq || '') + '</td>' +                              // 순번
        '<td>' + (r.accountCode || '') + '</td>' +                                        // 계정내부코드
        '<td>' + (r.drcrCode || '') + '</td>' +                                           // 차대구분코드
        '<td>' + (r.occurDeptCode || '') + '</td>' +                                      // 발생부서코드
        '<td>' + (r.costTypeCode || '') + '</td>' +                                       // 비용구분코드
        '<td>' + ((r.processFlag === 0 || r.processFlag === 1) ? r.processFlag : '') + '</td>' + // 처리구분
        '<td>' + (r.targetCode || '') + '</td>';                                          // 전표처리대상자코드

      tbody.appendChild(tr);
    });

    // 합계행(표 하단)
    var trSum = document.createElement('tr');
    trSum.className = 'row-total';
    trSum.innerHTML =
      '<td colspan="3" class="right strong">합계</td>' +
      '<td></td>' +
      '<td class="right strong">' + formatAmt(debitTot) + '</td>' +
      '<td class="right strong">' + formatAmt(creditTot) + '</td>' +
      '<td colspan="13"></td>';
    tbody.appendChild(trSum);

    // 상단 합계 입력창 반영
    var sumDebit = $('#sumDebit'); if (sumDebit) sumDebit.value = formatAmt(debitTot);
    var sumCredit = $('#sumCredit'); if (sumCredit) sumCredit.value = formatAmt(creditTot);
  }

  // =============== 동작 ===============
  function doPreview() {
    var f = getForm();
    if (!validateBase(f)) return;
    if (!cfg.view) { toast('미리보기 API가 설정되어 있지 않습니다.'); return; }

    setBusy(true);
    ajaxGET(cfg.view, { yyyymm: f.yyyymm, payType: f.payType }, function (rows) {
      setBusy(false);
      renderPreview(rows || []);
    });
  }

  function doProcess() {
    var f = getForm();
    if (!validateBase(f)) return;
    if (!cfg.process) { toast('처리 API가 설정되어 있지 않습니다.'); return; }

    var payload = {
      yyyymm: f.yyyymm,
      payType: f.payType,
      wageAccountId: f.wageAccountId,
      withholdAccountId: f.withholdAccountId,
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
        doPreview(); // 처리 후 갱신
      } else {
        toast(res.message || '전표 처리에 실패했습니다.');
      }
    });
  }
  
  //기초자료생성
  function doBaseGenerate() {
	    var f = getForm();
	    if (!validateBase(f)) return;
	    var url = cfg.baseGenerate || cfg.view; // 별도 설정 없으면 preview 재사용
	    if (!url) { toast('기초자료생성 API가 설정되어 있지 않습니다.'); return; }

	    if (!window.confirm('기존 발생 전표를 삭제하고 기초자료를 새로 생성(미리보기)합니다. 진행할까요?')) return;

	    var payload = {
	      yyyymm: f.yyyymm,
	      payType: f.payType,
	      wageAccountId: f.wageAccountId,
	      withholdAccountId: f.withholdAccountId,
	      accruedAccountId: f.accruedAccountId,
	      cleanupExisting: true,        // ← 강제 삭제
	      empNos: f.empNos
	    };

	    setBusy(true);
	    ajaxPOST(url, payload, function (rows) {
	      setBusy(false);
	      toast('기초자료가 재생성되었습니다.');
	      renderPreview(rows || []);
	    });
	  }

  
  // =============== 바인딩 ===============
  function bind() {
    var btn;
    btn = $('#btnBaseGenerate');  if (btn) btn.addEventListener('click', doBaseGenerate); 
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
    // VoucherConfig 안전 확인
    if (!cfg || !cfg.view) {
      console.warn('VoucherConfig.preview 가 비어 있음');
    }
    bind();
    // 필요 시 자동 미리보기
    doPreview();
  });
})();
