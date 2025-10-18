/* global RunPayrollConfig */
(function () {
  'use strict';

  // ===================== 共通 / 공통 =====================
  // cfg: 서버에서 내려준 API 엔드포인트/설정 객체를 참조합니다. / サーバから渡されるAPIエンドポイント/設定オブジェクトを参照
  var cfg = window.RunPayrollConfig || {};

  // 단일 요소 선택 헬퍼 / 単一要素の取得ヘルパー
  function $(sel, p) { return (p || document).querySelector(sel); }

  // 다중 요소 선택 헬퍼(Array로 변환) / 複数要素の取得（配列化）
  function $$(sel, p) { return Array.prototype.slice.call((p || document).querySelectorAll(sel)); }

  // 간단 토스트(알림) / 簡易トースト（アラート）
  function toast(msg) { alert(msg); }

  // 객체 -> 쿼리스트링 변환 / オブジェクト→クエリ文字列へ変換
  function qs(params) {
    var sp = [];
    if (!params) return '';
    for (var k in params) {
      if (!params.hasOwnProperty(k)) continue;
      var v = params[k];
      // 빈 값/공백은 제외 / 空値・空白は除外
      if (v !== undefined && v !== null && String(v).trim() !== '') {
        sp.push(encodeURIComponent(k) + '=' + encodeURIComponent(v));
      }
    }
    return sp.join('&');
  }

  // 안전하게 value 가져오기 / 安全に value を取得
  function getVal(el) { return el ? el.value : ''; }

  // 검색 폼 값 수집 / 検索フォーム値の収集
  function getForm() {
    return {
      yyyymm: getVal($('#yyyymm')) || '',
      payType: getVal($('#payType')) || '',
      deptCode: getVal($('#deptCode')) || '',
      empNo: getVal($('#empNo')) || ''
    };
  }

  // 선택된 사번 리스트 수집 / 選択された社員番号の収集
  function getSelectedEmpNos() {
    var arr = [];
    $$('#tblSummary tbody input[name="empCheck"]:checked').forEach(function (chk) {
      arr.push(chk.getAttribute('data-empno'));
    });
    return arr;
  }

  // 주요 버튼 활성/비활성 전환 / 主要ボタンの活性/非活性切替
  function setButtonsDisabled(disabled) {
    ['#btnProcess', '#btnReTax', '#btnApplyYrt', '#btnConfirm', '#btnUnconfirm', '#btnSearch', '#btnReset']
      .forEach(function (id) {
        var el = $(id);
        if (el) el.disabled = disabled;
      });
  }

  // GET 요청 헬퍼 / GET リクエストのヘルパー
  function ajaxGET(url, params, onDone) {
    var full = params ? (url + '?' + qs(params)) : url;
    fetch(full, { credentials: 'same-origin' })
      .then(function (r) { return r.ok ? r.json() : Promise.reject(r.statusText); })
      .then(function (data) { if (onDone) onDone(data); })
      .catch(function (e) { toast('リクエスト失敗: ' + e); });
  }

  // POST 요청 헬퍼(JSON) / POST リクエスト（JSON）
  function ajaxPOST(url, payload, onDone) {
    fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      credentials: 'same-origin',
      body: JSON.stringify(payload || {})
    })
      .then(function (r) { return r.ok ? r.json() : Promise.reject(r.statusText); })
      .then(function (data) { if (onDone) onDone(data); })
      .catch(function (e) { toast('リクエスト失敗: ' + e); });
  }

  // 숫자 포맷(금액) / 数値フォーマット（金額）
  function formatAmt(n) {
    if (n === null || n === undefined) return '';
    var v = Number(n);
    if (isNaN(v)) return String(n);
    try { return v.toLocaleString('ja-JP'); } catch (e) { return String(v); }
  }

  // null/undefined 방지 값 변환 / null/undefined 回避の値変換
  function safeVal(v) {
    return (v === null || v === undefined) ? '' : String(v);
  }

  // 'Y' 체크박스 변환 / 'Y'→チェックON
  function yn(v) {
    return (String(v || '').toUpperCase() === 'Y') ? 'checked' : '';
  }

  // ✅ 전체 선택/체크박스 초기화 / 全選択・チェック状態の初期化
  function resetSelection() {
    var chkAll = $('#chkAll');
    var chkAllHeader = $('#chkAllHeader');
    if (chkAll) chkAll.checked = false;
    if (chkAllHeader) { chkAllHeader.checked = false; chkAllHeader.indeterminate = false; }
    $$('#tblSummary tbody input[name="empCheck"]').forEach(function (c) { c.checked = false; });
  }

  // ===================== レンダリング: サマリー / 렌더링: 요약 =====================
  // 요약 테이블 바디 렌더 / サマリーテーブル本体の描画
  function renderSummary(rows) {
    var tbody = $('#tblSummary tbody');
    tbody.innerHTML = '';
    var cnt = 0;
    (rows || []).forEach(function (row) {
      cnt++;
      var tr = document.createElement('tr');
      tr.setAttribute('data-empno', row.empNo || '');
      tr.setAttribute('data-empname', row.empName || '');

      // 각 열 HTML 구성 / 各列のHTML構築
      var html = ''
        + '<td><input type="checkbox" name="empCheck" data-empno="' + (row.empNo || '') + '" aria-label="選択"></td>'
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

      // 행 클릭 시 상세 로드(체크박스 클릭은 제외) / 行クリックで明細読込（チェックは除外）
      tr.addEventListener('click', function (ev) {
        if (ev.target && ev.target.name === 'empCheck') return;
        $$('#tblSummary tbody tr').forEach(function (r) { r.classList.remove('active'); });
        tr.classList.add('active');
        loadDetails(row.empNo);
      });

      tbody.appendChild(tr);
    });
    // 합계 인원 표시 / 合計人数の表示
    var sc = $('#summaryCount');
    if (sc) sc.textContent = '合計 ' + cnt + '名';
    // 헤더 체크 초기화 / ヘッダーチェック初期化
    var chkAllHeader = $('#chkAllHeader');
    if (chkAllHeader) { chkAllHeader.checked = false; chkAllHeader.indeterminate = false; }
    // 행 체크 이벤트 동기화 핸들러 바인딩 / 行チェックの同期ハンドラをバインド
    bindRowCheckSync();
  }

  // ===================== レンダリング: 詳細 / 렌더링: 상세(지급) =====================
  // 지급 항목 테이블 렌더 / 支給項目テーブルの描画
  function renderItems(rows) {
    var tbody = $('#tblItems tbody');
    tbody.innerHTML = '';
    (rows || []).forEach(function (r) {
      var tr = document.createElement('tr');
      var isTotal = (r.itemName === 'TOTAL'); // 합계행 여부 / 合計行判定
      if (isTotal) tr.classList.add('row-total');
      tr.innerHTML = ''
        + '<td>' + (r.itemName || '') + '</td>'
        + '<td>' + (r.nonTaxType || '') + '</td>'
        + '<td>' + (r.previousYn || '') + '</td>'
        + '<td style="text-align:right">' + formatAmt(r.amount) + '</td>';
      tbody.appendChild(tr);
    });
  }

  // 공제 항목 테이블 렌더 / 控除項目テーブルの描画
  function renderDeds(rows) {
    var tbody = $('#tblDeds tbody');
    tbody.innerHTML = '';
    (rows || []).forEach(function (r) {
      var tr = document.createElement('tr');
      var isTotal = (r.deductionName === 'TOTAL'); // 합계행 여부 / 合計行判定
      if (isTotal) tr.classList.add('row-total');
      tr.innerHTML = ''
        + '<td>' + (r.deductionName || '') + '</td>'
        + '<td style="text-align:right">' + formatAmt(r.amount) + '</td>';
      tbody.appendChild(tr);
    });
  }

  // ===================== データ読込 / 데이터 로드 =====================
  // 검색 실행: 리스트/상세 초기화 후 호출 / 検索実行：一覧・明細を初期化して呼び出し
  function doSearch() {
    var f = getForm();
    var params = {
      yyyymm: f.yyyymm,
      payType: f.payType,
      deptCode: f.deptCode,
      empNo: f.empNo
    };
    // 선택 상태 초기화 / 選択状態の初期化
    resetSelection();

    setButtonsDisabled(true);
    ajaxGET(cfg.summary, params, function (rows) {
      renderSummary(rows || []);
      // 선택된 컨텍스트(상단 표시) 갱신 / 選択中コンテキスト（上部表示）の更新
      var selY = $('#selYyyymm'); if (selY) selY.textContent = f.yyyymm || '-';
      var selP = $('#selPayType'); if (selP) selP.textContent = f.payType || '-';
      var selNo = $('#selEmpNo'); if (selNo) selNo.textContent = '-';
      var selNm = $('#selEmpName'); if (selNm) selNm.textContent = '-';
      // 상세 테이블 초기화 / 明細テーブルの初期化
      renderItems([]);
      renderDeds([]);
      setButtonsDisabled(false);
    });
  }

  // 특정 사번 상세 로드(지급/공제) / 指定社員番号の明細読込（支給/控除）
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

  // ===================== EmpFlag 収集 / EmpFlag 수집 =====================
  // 선택(또는 전체) 행에서 입력/체크 상태를 EmpFlag 배열로 구성 / 選択（または全行）の入力/チェック状態を EmpFlag 配列に整形
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

  // ===================== 共通結果処理 / 공통 결과 처리 =====================
  // SimpleResult 표준 처리(토스트 → 재조회) / SimpleResult 標準処理（トースト→再検索）
  function handleSimpleResult(res, fallbackMsg) {
    if (!res) { toast('応答が空です。'); return; }
    if (res.success) {
      var msg = (res.message || fallbackMsg);
      if (res.affected !== null && res.affected !== undefined) msg += ' [' + res.affected + '件]';
      toast(msg);
      // 성공 시 선택 상태 초기화 + 재조회 / 成功時に選択初期化＋再検索
      resetSelection();
      doSearch();
    } else {
      toast(res.message || '処理に失敗しました。');
      // 실패 시도 초기화(운영방침에 따라 제거 가능) / 失敗時も初期化（運用方針に応じて削除可）
      resetSelection();
    }
  }

  // ===================== アクション / 액션 =====================
  // 급여 처리 실행 / 給与処理実行
  function doProcessPayroll() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    // 유효성 검사 / 入力チェック
    if (!f.yyyymm) { toast('支給年月を選択してください。'); return; }
    if (!f.payType) { toast('給与区分を選択してください。'); return; }
    if (empNos.length === 0) { toast('対象社員を選択してください。'); return; }

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
      handleSimpleResult(res, '給与処理が完了しました。');
    });
  }

  // 세금 재처리 / 税金再処理
  function doReTax() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    if (!f.yyyymm) { toast('支給年月を選択してください。'); return; }
    if (!f.payType) { toast('給与区分を選択してください。'); return; }
    if (empNos.length === 0) { toast('対象社員を選択してください。'); return; }

    var payload = { yyyymm: f.yyyymm, payType: f.payType, empNos: empNos };
    setButtonsDisabled(true);
    ajaxPOST(cfg.retax, payload, function (res) {
      setButtonsDisabled(false);
      handleSimpleResult(res, '税金の再処理が完了しました。');
    });
  }

  // YRT 반영 / 年末調整(YRT)反映
  function doApplyYrt() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    if (!f.yyyymm) { toast('支給年月を選択してください。'); return; }
    if (!f.payType) { toast('給与区分を選択してください。'); return; }
    if (empNos.length === 0) { toast('対象社員を選択してください。'); return; }

    var payload = { yyyymm: f.yyyymm, payType: f.payType, empNos: empNos };
    setButtonsDisabled(true);
    ajaxPOST(cfg.applyYrt, payload, function (res) {
      setButtonsDisabled(false);
      handleSimpleResult(res, '年末調整の反映が完了しました。');
    });
  }

  // 확정 처리 / 確定処理
  function doConfirm() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    if (!f.yyyymm) { toast('支給年月を選択してください。'); return; }
    if (!f.payType) { toast('給与区分を選択してください。'); return; }
    if (empNos.length === 0) { toast('対象社員を選択してください。'); return; }
    if (!window.confirm('選択した社員の給与明細を確定しますか？ 確定後は給与処理を行えません。')) { return; }

    var payload = { yyyymm: f.yyyymm, payType: f.payType, empNos: empNos };
    setButtonsDisabled(true);
    ajaxPOST(cfg.confirm, payload, function (res) {
      setButtonsDisabled(false);
      handleSimpleResult(res, '確定しました。');
    });
  }

  // 확정 해제 / 確定解除
  function doUnconfirm() {
    var f = getForm();
    var empNos = getSelectedEmpNos();
    if (!cfg.unconfirm) { toast('確定解除APIが設定されていません。（RunPayrollConfig.unconfirm）'); return; }
    if (!f.yyyymm) { toast('支給年月を選択してください。'); return; }
    if (!f.payType) { toast('給与区分を選択してください。'); return; }
    if (empNos.length === 0) { toast('対象社員を選択してください。'); return; }
    if (!window.confirm('選択した社員の確定を解除しますか？')) { return; }

    // 컨트롤러 시그니처에 맞춰 QueryString + Body 전달 / コントローラのシグネチャに合わせて QueryString + Body を送信
    // @RequestParam yyyymm, payType + @RequestBody List<String> empNos
    var url = cfg.unconfirm + '?' + qs({ yyyymm: f.yyyymm, payType: f.payType });

    setButtonsDisabled(true);
    fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      credentials: 'same-origin',
      body: JSON.stringify(empNos) // ← Body는 배열のみ / Body は配列のみ
    })
    .then(function (r) { return r.ok ? r.json() : Promise.reject(r.statusText); })
    .then(function (res) {
      setButtonsDisabled(false);
      handleSimpleResult(res, '確定を解除しました。');
    })
    .catch(function (e) {
      setButtonsDisabled(false);
      toast('リクエスト失敗: ' + e);
      // 실패 시 선택 초기화(선택) / 失敗時の選択初期化（任意）
      resetSelection();
    });
  }

  // ===================== イベント・バインド / 이벤트 바인딩 =====================
  function bindEvents() {
    var btn;
    // 조회 버튼 / 検索ボタン
    btn = $('#btnSearch');   if (btn) btn.addEventListener('click', doSearch);
    // 초기화 버튼(검색조건 리셋 + 재조회) / リセットボタン（検索条件クリア＋再検索）
    btn = $('#btnReset');    if (btn) btn.addEventListener('click', function () {
      var d = $('#deptCode'); if (d) d.value = '';
      var e = $('#empNo');    if (e) e.value = '';
      var e2 = $('#empName'); if (e2) e2.value = '';
      var e3 = $('#deptName');if (e3) e3.value = '';
      resetSelection();
      doSearch();
    });

    // 액션 버튼들 / アクションボタン
    btn = $('#btnProcess');  if (btn) btn.addEventListener('click', doProcessPayroll);
    btn = $('#btnReTax');    if (btn) btn.addEventListener('click', doReTax);
    btn = $('#btnApplyYrt'); if (btn) btn.addEventListener('click', doApplyYrt);
    btn = $('#btnConfirm');  if (btn) btn.addEventListener('click', doConfirm);
    btn = $('#btnUnconfirm');if (btn) btn.addEventListener('click', doUnconfirm);
    btn = $('#btnUnconfirm');if (btn) btn.addEventListener('click', doUnconfirm);
    // 팝업 열기 / ポップアップ起動
    btn = $('#btnSearchEmp');if (btn) btn.addEventListener('click', openEmployeePopup);
    btn = $('#btnSearchDep');if (btn) btn.addEventListener('click', openDepartmentPopup);

    // unconfirm 미설정 시 버튼 비활성 / unconfirm 未設定の場合はボタン無効化
    if (!cfg.unconfirm) {
      var unBtn = $('#btnUnconfirm');
      if (unBtn) { unBtn.disabled = true; unBtn.title = 'unconfirm API 未設定'; }
    }

    // 전체선택 체크박스 동기화 / 全選択チェックの同期
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

  // 행 개별 체크 변화에 따라 헤더 상태(체크/불확정) 갱신 / 行個別チェックに応じてヘッダー状態（チェック/不確定）更新
  function bindRowCheckSync() {
    var header = $('#chkAllHeader');
    var all = $$('#tblSummary tbody input[name="empCheck"]');
    if (!header || all.length === 0) return;

    all.forEach(function (chk) {
      chk.addEventListener('change', function () {
        var allChecked = all.every(function (c) { return c.checked; });
        var anyChecked = all.some(function (c) { return c.checked; });
        header.indeterminate = !allChecked && anyChecked; // 일부만 체크 / 一部のみチェック
        header.checked = allChecked; // 전부 체크 / 全てチェック
      });
    });
  }

  // ポップアップから呼び出すコールバック / 팝업 콜백(사원 선택)
  window.onEmployeePicked = function(row) {
    document.getElementById('deptCode').value= row.deptCode || '';
    document.getElementById('deptName').value= row.deptName || '';
    document.getElementById('empNo').value   = row.empNo || '';
    document.getElementById('empName').value = row.empName || '';
  };

  // 社員検索ポップアップを開く / 사원 검색 팝업 열기
  function openEmployeePopup() {
    const w = 1100, h = 700;
    const x = (screen.availWidth  - w) / 2;
    const y = (screen.availHeight - h) / 2;
    window.open(
      'popups/employees',  // JSP パス / JSP 경로
      'empPopup',
      `width=${w},height=${h},left=${x},top=${y},resizable=yes,scrollbars=yes`
    );
  }

  // 部署ポップアップのコールバック / 부서 팝업 콜백
  window.onDepartmentPicked = function(row) {
    document.getElementById('deptCode').value= row.deptCode || '';
    document.getElementById('deptName').value= row.deptName || '';
  };

  // 部署検索ポップアップを開く / 부서 검색 팝업 열기
  function openDepartmentPopup() {
    const w = 1100, h = 700;
    const x = (screen.availWidth  - w) / 2;
    const y = (screen.availHeight - h) / 2;
    window.open(
      'popups/departments',  // JSP パス / JSP 경로
      'depPopup',
      `width=${w},height=${h},left=${x},top=${y},resizable=yes,scrollbars=yes`
    );
  }

  // ===================== 初期化 / 초기화 =====================
  // DOM 로드 완료 시 이벤트 바인드 & 최초 조회 / DOM 読込完了時にイベントをバインドし初回検索
  document.addEventListener('DOMContentLoaded', function () {
    bindEvents();
    doSearch();
  });
})();
