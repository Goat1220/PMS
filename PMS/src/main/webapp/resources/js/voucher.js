/* global VoucherConfig */
(function () {
  'use strict';

  // =============== 유틸 / ユーティリティ ===============
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

  // =============== 폼 / フォーム ===============
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
    if (!f.yyyymm) { toast('支給年月(YYYY-MM)を選択してください。'); return false; }
    if (!f.payType) { toast('給与区分を選択してください。'); return false; }
    return true;
  }

  // =============== Ajax / 通信 ===============
  function ajaxGET(url, params, onDone) {
    var full = params ? (url + '?' + qs(params)) : url;
    fetch(full, { credentials: 'same-origin' })
      .then(function (r) {
        if (!r.ok) return Promise.reject(r.statusText || ('HTTP ' + r.status));
        return r.json();
      })
      .then(function (data) { if (onDone) onDone(data); })
      .catch(function (e) { toast('リクエスト失敗: ' + e); setBusy(false); });
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
    .catch(function (e) { toast('リクエスト失敗: ' + e); setBusy(false); });
  }

  // =============== 렌더 / レンダリング ===============
  /**
   * rows: VoucherLedgerRow[]
   * 매핑: JSP 헤더 순서에 정확히 맞춤 / マッピング: JSPヘッダー順に正確に整列
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
        '<td class="right">' + (r.displaySeq || '') + '</td>' +                              // # / 行番号
        '<td>' + (r.accountName || '') + '</td>' +                                        // 계정과목(현재 account_id) / 勘定科目（現在は account_id）
        '<td class="center">' + (r.drcrName || r.drcrCode || '') + '</td>' +              // 차대구분 / 借貸区分
        '<td class="right">' + formatAmt(debit) + '</td>' +                               // 차변금액 / 借方金額
        '<td class="right">' + formatAmt(credit) + '</td>' +                              // 대변금액 / 貸方金額
        '<td>' + (r.occurDeptName || '') + '</td>' +                                      // 발생부서 / 発生部門
        '<td>' + (r.targetCode || '') + '</td>' +                                         // 발생원천 / 発生元
        '<td>' + (r.payDateStr || r.occurDate || '') + '</td>' +                          // 지급일 / 支給日
        '<td>' + (r.summaryNote || '') + '</td>' +                                        // 적요 / 摘要
        '<td>' + (r.voucherNo || '') + '</td>' +                                          // 전표형번호(표시용) / 伝票形式番号(表示用)
        '<td>' + (r.voucherNo || '') + '</td>' +                                          // 전표내부코드(추후 voucherId 매핑 권장) / 伝票内部コード（将来的にvoucherId紐付け推奨）
        '<td>' + (r.approvedYn || '') + '</td>' +                                         // 승인여부 / 承認有無
        '<td class="right">' + (r.lineSeq || '') + '</td>' +                              // 순번 / 行順
        '<td>' + (r.accountCode || '') + '</td>' +                                        // 계정내부코드 / 勘定内部コード
        '<td>' + (r.drcrCode || '') + '</td>' +                                           // 차대구분코드 / 借貸区分コード
        '<td>' + (r.occurDeptCode || '') + '</td>' +                                      // 발생부서코드 / 発生部門コード
        '<td>' + (r.costTypeCode || '') + '</td>' +                                       // 비용구분코드 / 費用区分コード
        '<td>' + ((r.processFlag === 0 || r.processFlag === 1) ? r.processFlag : '') + '</td>' + // 처리구분 / 処理区分
        '<td>' + (r.targetCode || '') + '</td>';                                          // 전표처리대상자코드 / 伝票処理対象者コード

      tbody.appendChild(tr);
    });

    // 합계행(표 하단) / 合計行（表下部）
    var trSum = document.createElement('tr');
    trSum.className = 'row-total';
    trSum.innerHTML =
      '<td colspan="3" class="right strong">合計</td>' +
      '<td></td>' +
      '<td class="right strong">' + formatAmt(debitTot) + '</td>' +
      '<td class="right strong">' + formatAmt(creditTot) + '</td>' +
      '<td colspan="13"></td>';
    tbody.appendChild(trSum);

    // 상단 합계 입력창 반영 / 上部 合計入力欄へ反映
    var sumDebit = $('#sumDebit'); if (sumDebit) sumDebit.value = formatAmt(debitTot);
    var sumCredit = $('#sumCredit'); if (sumCredit) sumCredit.value = formatAmt(creditTot);
  }

  // =============== 동작 / アクション ===============
  function doPreview() {
    var f = getForm();
    if (!validateBase(f)) return;
    if (!cfg.view) { toast('プレビューAPIが設定されていません。'); return; }

    setBusy(true);
    ajaxGET(cfg.view, { yyyymm: f.yyyymm, payType: f.payType }, function (rows) {
      setBusy(false);
      renderPreview(rows || []);
    });
  }

  function doProcess() {
    var f = getForm();
    if (!validateBase(f)) return;
    if (!cfg.process) { toast('処理APIが設定されていません。'); return; }

    var payload = {
      yyyymm: f.yyyymm,
      payType: f.payType,
      wageAccountId: f.wageAccountId,
      withholdAccountId: f.withholdAccountId,
      summaryNote: f.summaryNote,
      approvedYn: f.approvedYn || 'N'
    };

    if (!window.confirm('プレビュー結果に基づき伝票を生成（再作成）します。続行しますか？')) return;

    setBusy(true);
    ajaxPOST(cfg.process, payload, function (res) {
      setBusy(false);
      if (!res) { toast('応答が空です。'); return; }
      if (res.success) {
        var msg = res.message || '伝票が処理されました。';
        if (res.affected !== null && res.affected !== undefined) msg += ' [' + res.affected + '行]';
        toast(msg);
        doPreview(); // 처리 후 갱신 / 処理後の再読込
      } else {
        toast(res.message || '伝票処理に失敗しました。');
      }
    });
  }
  
  // 기초자료생성 / 基礎データ生成
  function doBaseGenerate() {
	    var f = getForm();
	    if (!validateBase(f)) return;
	    var url = cfg.baseGenerate || cfg.view; // 별도 설정 없으면 preview 재사용 / 個別設定が無ければプレビューAPI再利用
	    if (!url) { toast('基礎データ生成APIが設定されていません。'); return; }

	    if (!window.confirm('既存の発生伝票を削除し、基礎データを再生成（プレビュー）します。続行しますか？')) return;

	    var payload = {
	      yyyymm: f.yyyymm,
	      payType: f.payType,
	      wageAccountId: f.wageAccountId,
	      withholdAccountId: f.withholdAccountId,
	      accruedAccountId: f.accruedAccountId,
	      cleanupExisting: true,        // ← 강제 삭제 / 強制削除
	      empNos: f.empNos
	    };

	    setBusy(true);
	    ajaxPOST(url, payload, function (rows) {
	      setBusy(false);
	      toast('基礎データを再生成しました。');
	      renderPreview(rows || []);
	    });
	  }

  
  // =============== 바인딩 / バインド ===============
  function bind() {
    var btn;
    btn = $('#btnBaseGenerate');  if (btn) btn.addEventListener('click', doBaseGenerate); 
    btn = $('#btnProcess'); if (btn) btn.addEventListener('click', doProcess);

    // 엔터로 미리보기 / Enterでプレビュー
    ['#yyyymm', '#payType', '#wageAccountId', '#withholdAccountId', '#summaryNote'].forEach(function (sel) {
      var el = $(sel);
      if (!el) return;
      el.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') doPreview();
      });
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    // VoucherConfig 안전 확인 / VoucherConfigの安全確認
    if (!cfg || !cfg.view) {
      console.warn('VoucherConfig.preview が空です'); // 개발자 콘솔용 / 開発者コンソール用
    }
    bind();
    // 필요 시 자동 미리보기 / 必要に応じて自動プレビュー
    doPreview();
  });
})();
