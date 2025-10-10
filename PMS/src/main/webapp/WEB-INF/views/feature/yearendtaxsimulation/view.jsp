<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSP 설정 / JSP 設定 --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%-- JSTL Core 태그 / JSTL Core タグ --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%-- 숫자·날짜 포맷 / 数値・日付フォーマット --%>


<style>
.common-btn {
	padding: 8px 16px;
	margin: 4px;
	border: none;
	border-radius: 4px;
	background-color: #4CAF50;
	color: white;
	cursor: pointer;
}

.common-btn:hover {
	background-color: #45a049;
}
</style>
<script>
	/**
	 * 공용 버튼 생성 / 共通ボタン生成
	 * @param {string} label  버튼 텍스트 / ボタン表示
	 * @param {string} onClickFn 전역 함수명 / クリック時の関数名
	 * @returns {HTMLButtonElement}
	 */
	function createCommonButton(label, onClickFn) {
		const btn = document.createElement("button");
		btn.type = "button";
		btn.className = "common-btn";
		btn.textContent = label;
		btn.setAttribute("onclick", onClickFn + "()");
		return btn;
	}
</script>


<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-grid.css" />
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/ag-grid-community/styles/ag-theme-alpine.css" />
<script
	src="https://cdn.jsdelivr.net/npm/ag-grid-community/dist/ag-grid-community.min.noStyle.js"></script>
<script>
	/**
	 * 공용 그리드 초기화(기본) / 共通グリッド初期化（基本）
	 * @param {Array} columnDefs
	 * @param {Array} rowData
	 */
	function initCommonGrid(columnDefs, rowData) {
		const gridOptions = {
			columnDefs : columnDefs,
			rowData : rowData || [],
			defaultColDef : {
				sortable : true,
				filter : true,
				resizable : true
			}
		};
		new agGrid.Grid(document.querySelector("#commonGrid"), gridOptions);
		return gridOptions;
	}
</script>
<!-- (숨김) AG Grid 컨테이너(레이아웃 영향 제거) / 非表示コンテナ（レイアウト非影響） -->
<div id="commonGrid" class="ag-theme-alpine"
	style="height: 0; width: 100%; overflow: hidden;"></div>

<style>
/* ===== 기본 레이아웃 / 基本レイアウト ===== */
html, body {
	margin: 0;
	background: #ffffff;
	color: #222;
	font-family: system-ui, Segoe UI, Apple SD Gothic Neo, Malgun Gothic,
		sans-serif;
}

.container {
	width: 100%;
	max-width: 1280px;
	margin: 0 auto;
	padding: 16px;
} /* 중앙 고정 폭 / 中央固定幅 */
.page-title {
	font-size: 18px;
	font-weight: 700;
} /* 페이지 제목 / ページタイトル */

/* ===== 제목 오른쪽 엑셀 버튼 정렬 / タイトル右側のボタン配置 ===== */
.page-header {
	display: flex;
	align-items: center;
	gap: 12px;
	margin: 6px 0 14px 0; /* 기존 제목 마진 대체 / 既存マージンの置換 */
}

#titleBtnAreaExcel {
	margin-left: auto; /* 오른쪽 정렬 / 右端寄せ */
	display: flex;
	gap: 8px;
	white-space: nowrap; /* 줄바꿈 방지 / 折り返し防止 */
}

@media ( max-width : 980px) {
	.page-header {
		flex-wrap: wrap;
	}
	#titleBtnAreaExcel {
		width: 100%;
		justify-content: flex-end;
	}
}

/* ===== 검색바 / 検索バー ===== */
.searchbar {
	padding: 10px 0;
	border-bottom: 1px solid #e5e7eb;
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	align-items: center;
}

.field {
	display: flex;
	gap: 6px;
	align-items: center;
} /* 라벨+입력 묶음 / ラベル＋入力 */
.field input[type=text], .field select {
	height: 30px;
	padding: 0 8px;
	border: 1px solid #d9dce3;
	border-radius: 4px;
	background: #fff;
}

.w-yr {
	width: 90px
}

.w-emp {
	width: 120px
}

.w-mid {
	width: 180px
}

.w-biz {
	width: 140px
} /* 폭 프리셋 / 幅プリセット */
.spacer {
	flex: 1 1 auto;
} /* 오른쪽 버튼 밀어내기 / 右側ボタン押し出し */
.btn {
	height: 32px;
	padding: 0 12px;
	border: 1px solid #d0d5dd;
	background: #fff;
	border-radius: 4px;
	cursor: pointer;
}

.btn.primary {
	background: #2f74ff;
	border-color: #2f74ff;
	color: #fff;
	font-weight: 600;
}

.btn.alt {
	background: #3d4f91;
	border-color: #3d4f91;
	color: #fff;
}

.btn.small {
	height: 30px;
	padding: 0 10px;
}

/* ===== 탭 / タブ ===== */
.tabs {
	display: flex;
	gap: 6px;
	margin: 12px 0 0 0;
}

.tab {
	padding: 6px 10px;
	border: 1px solid #e5e7eb;
	background: #f6f7fb;
	border-bottom: none;
	border-radius: 6px 6px 0 0;
	cursor: pointer;
}
/* 활성 탭 컬러 = 공통 버튼 컬러(#4CAF50) / アクティブ色＝共通ボタン色 */
.tab.active {
	background: #4CAF50;
	border-color: #4CAF50;
	color: #fff;
	font-weight: 600;
}

.panel {
	border-top: 1px solid #e5e7eb;
	padding: 10px 0 18px 0;
} /* 탭 내용 / タブ内容 */
.meta {
	color: #777;
	font-size: 12.5px;
	margin: 6px 0 4px 0;
} /* 메타 정보 / メタ情報 */

/* ===== 표 / テーブル ===== */
table.grid {
	width: 100%;
	border-collapse: collapse;
	margin-top: 6px;
}

table.grid th, table.grid td {
	border: 1px solid #e5e7eb;
	padding: 8px 10px;
	background: #fff;
}

table.grid th {
	background: #f4f6fa;
	text-align: left;
}

td.right {
	text-align: right
}

.field-year>span {
	color: #e11d48;
	font-weight: 700;
} /* 연도 라벨 강조 / 年度ラベル強調 */
.readonly-gray {
	background: #f3f4f6 !important;
	color: #777 !important;
	border-color: #e5e7eb !important;
} /* 읽기전용 스타일 / 読取専用スタイル */

/* ===== 사원 입력(하늘색) / 社員入力（空色） ===== */
.input-wrap.sky {
	background: #e6f3ff;
	border: 1px solid #b6d9ff;
	border-radius: 4px;
	position: relative;
	display: inline-flex;
	align-items: center;
	height: 30px;
	padding-right: 28px;
}

.input-wrap.sky input {
	background: transparent !important;
	border: 0 !important;
	outline: none;
	height: 100%;
	padding: 0 8px;
	width: 180px;
}

.input-wrap.sky .icon-btn {
	position: absolute;
	right: 6px;
	top: 50%;
	transform: translateY(-50%);
	border: 0;
	background: transparent;
	font-size: 14px;
	opacity: .75;
	cursor: pointer;
	padding: 0;
	width: 22px;
	height: 22px;
	line-height: 22px;
}

.input-wrap.sky:focus-within {
	box-shadow: 0 0 0 2px rgba(45, 120, 255, .15);
	border-color: #8fc0ff;
}
</style>

<div class="container">

	<!-- 제목 + 우측 엑셀 버튼 / タイトル＋右側エクセルボタン -->
	<div class="page-header">
		<div class="page-title">연말정산시뮬레이션(개인원본)</div>
		<div id="titleBtnAreaExcel"></div>
	</div>

	<input type="hidden" id="yrtId" value="${simHeader.yrtId}" />

	<!-- ===== 검색 영역 / 検索エリア ===== -->
	<div class="searchbar">
		<div class="field field-year">
			<span>정산연도</span> <input id="baseYear" class="w-yr" type="text"
				value="${baseYear}" placeholder="YYYY">
		</div>

		<div class="field">
			<span>사원</span> <span class="input-wrap sky"> <input
				id="empName" type="text" value="${empName}" placeholder="사원 이름"
				aria-label="사원 이름">
				<button type="button" class="icon-btn" aria-label="사원 검색"
					onclick="openEmployeePopup()">🔍</button>
			</span>
		</div>

		<div class="field">
			<span>사번</span> <input id="empNo" class="w-emp readonly-gray"
				type="text" value="${empId}" placeholder="사번" readonly>
				  <input type="hidden" id="empId" value="${empId}">
		</div>

		<div class="field">
			<span>정산사업장</span> <select id="bizPlace" class="w-biz readonly-gray"
				disabled>
				<option>본사</option>
			</select>
		</div>

		<div class="field" style="gap: 12px; color: #888;">
			<label><input type="checkbox" disabled> 개인마감</label> <label><input
				type="checkbox" disabled> 담당자마감</label> <label><input
				type="checkbox" checked disabled> 정산대상자</label>
		</div>

		<div class="field">
			<span>조회구분</span> <select id="searchType" class="w-emp readonly-gray"
				disabled>
				<option selected>정산</option>
			</select>
		</div>

		<div class="field">
			<span>세금적용결과</span> <input id="taxApplyResult"
				class="w-biz readonly-gray" type="text"
				value="${empty taxApplyResult ? '표준세액공제' : taxApplyResult}" readonly>
		</div>

		<div class="spacer"></div>

		<!-- 공용 버튼 주입(검색바) / 共通ボタン注入（検索バー） -->
		<div class="field" id="opsBtnArea" style="gap: 8px;"></div>
	</div>

	<!-- 공용 버튼 + 엑셀 버튼 주입 / 共通ボタン＋エクセルボタン注入 -->
	<script>
		(function mountOps() {
			var area = document.getElementById('opsBtnArea');
			if (area && !area.dataset.inited) {
				area.dataset.inited = '1';
				area.appendChild(createCommonButton('산출근거', 'onReason'));
				area.appendChild(createCommonButton('정산시뮬레이션처리', 'onSim'));
				area.appendChild(createCommonButton('정산시뮬레이션결과삭제', 'onDelete'));
				area.appendChild(createCommonButton('납부특례세액시뮬레이션처리',
						'onInstallment'));
			}

			// 제목 오른쪽 엑셀 버튼 / タイトル右側のエクセルボタン
			var titleArea = document.getElementById('titleBtnAreaExcel');
			if (titleArea
					&& !titleArea
							.querySelector('button[data-role="export-csv"]')) {
				var btn = createCommonButton('엑셀 추출', 'onExportCsv');
				btn.dataset.role = 'export-csv';
				titleArea.appendChild(btn);
			}
		})();
	</script>

	<!-- ===== 탭 / タブ ===== -->
	<div class="tabs">
		<div id="tab-final" class="tab active" onclick="showTab('final')">최종</div>
		<div id="tab-sim" class="tab" onclick="showTab('sim')">시뮬레이션</div>
	</div>

	<!-- 최종 탭 / 最終タブ -->
	<div id="panel-final" class="panel">
		<div class="meta">
			실행라벨: <strong><c:out
					value="${simHeader != null ? simHeader.runLabel : '-'}" /></strong> · 기준연도:
			<strong><c:out value="${baseYear}" /></strong> · 생성/갱신:
			<c:out value="${simHeader != null ? simHeader.updatedAt : '-'}" />
			· 확정여부:
			<c:out value="${simHeader != null ? simHeader.confirmYn : '-'}" />
		</div>

		<table class="grid">
			<thead>
				<tr>
					<th style="width: 22%">정산항목분류</th>
					<th style="width: 38%">정산항목</th>
					<th style="width: 20%">금액</th>
					<th style="width: 20%">예상적용금액</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="row" items="${finalList}">
					<tr>
						<td>${row.itemClass}</td>
						<td>${row.itemName}</td>
						<td class="right"><fmt:formatNumber value="${row.amount}" /></td>
						<td class="right"><fmt:formatNumber
								value="${row.expectedAmount}" /></td>
					</tr>
				</c:forEach>
				<c:if test="${empty finalList}">
					<tr>
						<td colspan="4" style="text-align: center; color: #777;">데이터가
							없습니다</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>

	<!-- 시뮬레이션 탭 / シミュレーションタブ -->
	<div id="panel-sim" class="panel" style="display: none;">
		<table class="grid">
			<thead>
				<tr>
					<th>정산항목분류</th>
					<th>정산항목</th>
					<th>금액</th>
					<th>예상적용금액</th>
				</tr>
			</thead>
			<tbody id="simBody">
				<tr>
					<td colspan="4" style="text-align: center;">버튼으로 조회하세요</td>
				</tr>
			</tbody>
		</table>
		<div id="installmentBox" style="margin-top: 10px;"></div>
	</div>

</div>
<!-- /.container -->

<script>
/* ===== JS 유틸 / JSユーティリティ ===== */
var CTX = window.location.origin;
function ctx() { return CTX; }
function emp() {
  var el = document.getElementById('empNo');
  return el ? el.value.trim() : '';
}
function yrt() {
  return document.getElementById('yrtId').value;
}
function validYear(y) {
  return /^\d{4}$/.test(y) && (+y >= 2000 && +y <= 2100);
}
function fmt(n) {
  if (n == null) return '';
  return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/* 초기 연도 보정 / 初期年度補正 */
(function() {
  var v = document.getElementById('baseYear').value;
  if (!/^\d{4}$/.test(v)) {
    var d = new Date();
    document.getElementById('baseYear').value = d.getFullYear() - 1;
  }
})();

/* 탭 전환 / タブ切替 */
function showTab(t) {
  document.getElementById('tab-final').classList.remove('active');
  document.getElementById('tab-sim').classList.remove('active');
  document.getElementById('panel-final').style.display = (t === 'final') ? 'block' : 'none';
  document.getElementById('panel-sim').style.display = (t === 'sim') ? 'block' : 'none';
  document.getElementById('tab-' + t).classList.add('active');
}

/* 사원 검색(외부 기능 연동) / 社員検索（外部機能連携） */
function onClickEmpSearch() {
  if (typeof window.openEmpSearch === 'function') {
    window.openEmpSearch(function(res) {
      if (!res) return;
      setEmp(res.name, res.id);
    });
  } else {
    alert('사원 검색은 외부 기능입니다.');
  }
}

/* 사원 세팅 / 社員セット */
function setEmp(name, id) {
  var nameEl = document.getElementById('empName');
  var idEl = document.getElementById('empId');
  if (nameEl) nameEl.value = name || '';
  if (idEl) idEl.value = id || '';
}

/* 사원 선택 여부 검사 / 社員選択チェック */
function needEmp() {
  if (!emp()) {
    alert('선택된 사원이 없습니다.');
    return true;
  }
  return false;
}

/* 산출근거 조회 → 시뮬 탭 / 参照取得 → シミュタブ表示 */
function onReason() {
  if (needEmp()) return;
  var y = document.getElementById('baseYear').value.trim();
  if (!validYear(y)) {
    alert('정산연도는 2000~2100의 4자리 숫자');
    return;
  }
  var url = ctx() + '/feature/yearend-tax-simulation/api/sim?empId='
          + encodeURIComponent(emp()) + '&baseYear='
          + encodeURIComponent(y);
  fetch(url)
    .then(function(r) { return r.ok ? r.json() : Promise.reject(r); })
    .then(function(rows) {
      renderSim(rows);
      showTab('sim');
      return refreshTaxApplyResult();
    })
    .catch(function() {
      alert('산출근거 조회 실패 또는 API 미구현');
    });
}

/* ✅ 시뮬레이션 실행(덮어쓰기) / シミュレーション実行（上書き） */
function onSim() {
  if (needEmp()) return;
  var y = document.getElementById('baseYear').value.trim();
  if (!validYear(y)) {
    alert('정산연도 형식 오류');
    return;
  }
  if (!confirm('기존 결과를 삭제하고 새로 생성하시겠습니까?')) return;

  var url = ctx() + '/feature/yearend-tax-simulation/api/simulate';
  var form = new URLSearchParams({
    empId: emp(),
    baseYear: y,
    overwrite: 'true'
  });

  fetch(url, { method: 'POST', body: form })
    .then(function(r) { return r.ok ? r.text() : Promise.reject(); })
    .then(function(yrtIdRaw) {
      // 숫자만 추출
      var cleanYrtId = yrtIdRaw.replace(/\D/g, '');
      document.getElementById('yrtId').value = cleanYrtId;
      onReason();
      refreshTaxApplyResult(cleanYrtId);
    })
    .catch(function() {
      alert('시뮬레이션 처리 실패 또는 API 미구현');
    });
}

/* ✅ 시뮬레이션 결과 삭제 / シミュレーション結果削除 */
function onDelete() {
  var id = yrt();
  if (!id) {
    alert('삭제할 실행이 없습니다.');
    return;
  }
  if (!confirm('기존 시뮬레이션 결과를 삭제하시겠습니까?')) return;

  fetch(ctx() + '/feature/yearend-tax-simulation/api/simulate?yrtId=' + encodeURIComponent(id), {
      method: 'DELETE'
  })
  .then(function(r) {
      if (r.ok) return r.text();
      return r.text().then(function(t){ throw new Error(t || '삭제 실패'); });
  })
  .then(function() {
      document.getElementById('simBody').innerHTML =
          '<tr><td colspan="4" style="text-align:center;">삭제됨</td></tr>';
      document.getElementById('yrtId').value = '';
      refreshTaxApplyResult();
      alert('삭제 완료되었습니다.');
  })
  .catch(function(err) {
      alert(err.message || '삭제 실패 또는 API 미구현');
  });
}

/* ✅ 분납 시뮬레이션 실행 / 分納シミュレーション実行 */
function onInstallment() {
  var id = yrt();
  if (!id) {
    alert('시뮬레이션 실행이 없습니다.');
    return;
  }
  var months = prompt('분납 개월수(2~3):', '2');
  var start = prompt('시작월(YYYY-MM):', new Date().toISOString().slice(0, 7));
  var form = new URLSearchParams({
    yrtId : id,
    months : months,
    startMonth : start
  });

  // ✅ 여기 경로를 수정해야 함
  fetch(ctx() + '/feature/yearend-tax-simulation/api/installment', {
      method : 'POST',
      body : form
  })
  .then(function(r) { return r.ok ? r.json() : Promise.reject(r); })
  .then(renderInstallment)
  .catch(function() {
      alert('분납 시뮬레이션 실패 또는 API 미구현');
  });
  showTab('sim');
}


/* 시뮬 표 렌더링 / シミュ表レンダリング */
function renderSim(rows) {
  var tb = document.getElementById('simBody');
  tb.innerHTML = '';
  if (!rows || rows.length === 0) {
    tb.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#777;">데이터 없음</td></tr>';
    return;
  }
  rows.forEach(function(r) {
    tb.insertAdjacentHTML('beforeend',
      '<tr>'
      + '<td>' + (r.itemClass || '') + '</td>'
      + '<td>' + (r.itemName || '') + '</td>'
      + '<td class="right">' + fmt(r.amount) + '</td>'
      + '<td class="right">' + fmt(r.expectedAmount) + '</td>'
      + '</tr>'
    );
  });
}

/* 분납 결과 렌더링 / 分納結果レンダリング */
function renderInstallment(res) {
  var box = document.getElementById('installmentBox');
  if (!res || !res.schedule) {
    box.innerHTML = '';
    return;
  }
  var html = '<h4 style="margin:10px 0 6px 0;">분납 스케줄</h4>'
           + '<table class="grid"><tr><th>월</th><th>국세</th><th>지방세</th><th>합계</th></tr>';
  res.schedule.forEach(function(s) {
    html += '<tr><td>' + s.yyyymm + '</td>'
         + '<td class="right">' + fmt(s.national) + '</td>'
         + '<td class="right">' + fmt(s.local) + '</td>'
         + '<td class="right">' + fmt(s.total) + '</td></tr>';
  });
  html += '</table>';
  box.innerHTML = html;
}

/* 세금적용결과 갱신 / 税適用結果更新 */
function refreshTaxApplyResult(yrtId) {
  var y = document.getElementById('baseYear').value.trim();
  if (!validYear(y) || !emp()) {
    document.getElementById('taxApplyResult').value = '미판정';
    return;
  }
  var q = new URLSearchParams({
    empId : emp(),
    baseYear : y
  });
  if (yrtId) q.append('yrtId', yrtId);

  return fetch(ctx() + '/feature/yearend-tax-simulation/api/tax-apply-result?' + q.toString())
    .then(function(r) { return r.ok ? r.text() : Promise.reject(r); })
    .then(function(t) {
      document.getElementById('taxApplyResult').value = t || '미판정';
    })
    .catch(function() {
      document.getElementById('taxApplyResult').value = '미판정';
    });
}
</script>


<script>
	/**
	 * 표를 CSV로 추출(UTF-8 BOM) / テーブルCSV出力（UTF-8 BOM）
	 * @param {HTMLTableElement} tbl 대상 테이블 / 対象テーブル
	 * @param {string} filename 파일명 / ファイル名
	 */
	function exportTableToCsv(tbl, filename) {
		if (!tbl) {
			alert('내보낼 표를 찾지 못했습니다.');
			return;
		}

		// 행 데이터 수집 / 行データ収集
		var rows = Array.prototype.slice.call(tbl.querySelectorAll('tr'));
		var csvLines = rows.map(
				function(tr) {
					var cells = Array.prototype.slice.call(tr
							.querySelectorAll('th,td'));
					return cells.map(
							function(td) {
								// 셀 텍스트 정제 / セル文字列整形（改行→空白、前後トリム）
								var text = (td.innerText || '').replace(
										/\r?\n|\r/g, ' ').trim();
								// CSV 안전 처리: "로 감싸고 내부 "는 ""로 / CSV安全化："で囲み内部"は""
								text = '"' + text.replace(/"/g, '""') + '"';
								return text;
							}).join(',');
				}).join('\r\n');

		// UTF-8 BOM 추가(엑셀 인코딩 대응) / BOM付与（Excel対策）
		var blob = new Blob([ "\uFEFF" + csvLines ], {
			type : 'text/csv;charset=utf-8;'
		});
		var a = document.createElement('a');
		a.href = URL.createObjectURL(blob);
		a.download = filename || 'export.csv';
		document.body.appendChild(a);
		a.click();
		setTimeout(function() {
			URL.revokeObjectURL(a.href);
			a.remove();
		}, 0);
	}

	/**
	 * 활성 탭의 grid를 CSV 저장 / アクティブタブのgridをCSV保存
	 */
	function onExportCsv() {
		// 활성 탭 판별 / アクティブタブ判定
		var isFinalActive = document.getElementById('tab-final').classList
				.contains('active');

		// 보이는 표 선택 / 可視テーブル選択
		var tbl = document
				.querySelector(isFinalActive ? '#panel-final table.grid'
						: '#panel-sim table.grid');

		// 파일명: yearend_YYYY_[final|simulation]_yyyyMMdd_HHmmss.csv
		var baseYearEl = document.getElementById('baseYear');
		var y = (baseYearEl && baseYearEl.value ? baseYearEl.value : '').trim()
				|| 'YEAR';
		var tabLabel = isFinalActive ? 'final' : 'simulation';
		var now = new Date();
		var ts = now.getFullYear()
				+ String(now.getMonth() + 1).padStart(2, '0')
				+ String(now.getDate()).padStart(2, '0') + '_'
				+ String(now.getHours()).padStart(2, '0')
				+ String(now.getMinutes()).padStart(2, '0')
				+ String(now.getSeconds()).padStart(2, '0');
		var fname = 'yearend_' + y + '_' + tabLabel + '_' + ts + '.csv';

		exportTableToCsv(tbl, fname);
	}
</script>
<script>
/* 사원 선택 콜백 / 社員選択コールバック */
window.onEmployeePicked = function(row) {
  // 1️ 선택한 사원 정보 세팅
  document.getElementById('empNo').value = row.empNo || '';
  document.getElementById('empName').value = row.empName || '';

  // 2️ 연도 확인
  var year = document.getElementById('baseYear').value;
  if (!year || !/^\d{4}$/.test(year)) {
    var d = new Date();
    year = d.getFullYear();
    document.getElementById('baseYear').value = year;
  }

  // 3️ 자동 조회 트리거 실행
  console.log("사원 선택됨 → 자동 조회 시작:", row.empNo, year);
  onReason();  // 산출근거 조회 (시뮬탭 자동 로드)
};


	// 사원 검색 팝업 열기
	// 사원 검색 팝업 열기 (ES5 버전) / 社員検索ポップアップを開く（ES5版）
	function openEmployeePopup() {
		// var 사용(ES5) / var を使用（ES5）
		var w = 1100;
		var h = 700;

		// 가운데 배치 / 画面中央に配置
		var x = Math.round((screen.availWidth - w) / 2);
		var y = Math.round((screen.availHeight - h) / 2);

		// window.open 의 3번째 인자는 "콤마로 이어진 문자열"이어야 함
		// window.open 第3引数は「カンマ区切りの文字列」
		var features = 'width=' + w + ',height=' + h + ',left=' + x + ',top='
				+ y + ',resizable=yes,scrollbars=yes';

		window.open('/popups/employees',  // JSP 경로
			      'empPopup', features);
	}
</script>