<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../includes/commonform.jsp"%>
<%@ include file="../includes/table.jsp"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8" />
<title>部署</title>
<style>
/* CSS 변수 정의 / CSS 変数定義 */
:root { 
	--line: #dcdfe6;     /* 테두리 색상 / 罫線の色 */
	--bg: #f6f7fb;       /* 배경색 / 背景色 */
	--text: #303133;     /* 텍스트 색상 / テキスト色 */
	--muted: #909399;    /* 흐린 텍스트 색상 / 薄いテキスト色 */
	--blue: #409eff;     /* 주요 색상 / メインカラー */
}

/* 모든 요소에 기본 설정 적용 / すべての要素に基本設定を適用 */
* {
	box-sizing: border-box
}

/* 페이지 전체 스타일 / ページ全体のスタイル */
body {
	font-family: Segoe UI, Malgun Gothic, Apple SD Gothic Neo, sans-serif;
	color: var(--text);
	margin: 0
}

/* 전체 컨테이너 / 全体コンテナ */
.wrap {
	padding: 14px
}

/* 타이틀 영역 스타일 / タイトル領域のスタイル */
.titlebar {
	display: flex;
	justify-content: center;
	border-bottom: 1px solid var(--line);
	padding: 10px 12px;
	font-weight: 700
}

/* 검색 영역 스타일 / 検索領域のスタイル */
.searchbar {
	display: flex;
	gap: 8px;
	align-items: center;
	padding: 10px 12px;
	border-bottom: 1px solid var(--line);
	flex-wrap: wrap
}

/* 검색 필드(드롭다운, 입력창) 스타일 / 検索フィールド(ドロップダウン、入力)のスタイル */
.searchbar select, .searchbar input {
	height: 30px;
	border: 1px solid var(--line);
	padding: 0 8px;
	border-radius: 4px
}

/* 검색 입력창 가로 크기 / 検索入力欄の横幅 */
.searchbar input {
	width: 320px
}

/* 일반 버튼 스타일 / 通常ボタンのスタイル */
.btn {
	height: 30px;
	padding: 0 12px;
	border: 1px solid var(--line);
	background: #fff;
	border-radius: 4px;
	cursor: pointer
}

/* 강조 버튼 스타일 (파란색) / 強調ボタンのスタイル (青色) */
.btn.primary {
	background: var(--blue);
	border-color: var(--blue);
	color: #fff
}

/* 테이블 영역 / テーブル領域 */
.grid {
	padding: 10px 12px
}

/* 행(Row)에 마우스 오버 시 배경색 변경 / 行(Row)にマウスオーバー時に背景色を変更 */
tr:hover td {
	background: #fafafa
}

/* 하단 푸터 영역 / 下部フッター領域 */
.footer {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 8px 12px;
	border-top: 1px solid var(--line)
}

/* 푸터의 우측 섹션 / フッターの右側セクション */
.footer .left {
	margin-left: auto;
	display: flex;
	gap: 6px;
	align-items: center
}

/* 페이지 번호 링크 스타일 / ページネーションリンクのスタイル */
.pager a {
	display: inline-block;
	min-width: 24px;
	text-align: center;
	padding: 2px 6px;
	border: 1px solid var(--line);
	border-radius: 4px;
	color: var(--text);
	text-decoration: none
}

/* 현재 페이지 번호 스타일 / 現在のページ番号のスタイル */
.pager a.on {
	background: var(--blue);
	color: #fff;
	border-color: var(--blue)
}
</style>
</head>
<body>
	<div class="wrap">

		<!-- 타이틀 영역 / タイトル領域 -->
		<div class="titlebar">
			<div id="popupTitle">部署</div>
		</div>

		<!-- 검색 조건 영역: 부서명 / 부서코드 / 検索条件領域: 部署名 / 部署コード -->
		<div class="searchbar">
			<!-- 검색 기준 선택 드롭다운 / 検索基準選択ドロップダウン -->
			<select id="by">
				<option value="deptName">部署名</option>
				<option value="deptCode">部署コード</option>
			</select>
			<!-- 검색 키워드 입력창 / 検索キーワード入力欄 -->
			<input id="keyword" type="text" placeholder="%" />
			<!-- 검색 버튼 (파란색) / 検索ボタン (青色) -->
			<button id="btnSearch" class="btn primary">検索</button>
		</div>

		<!-- 사용 여부 필터 영역 / 使用状況フィルター領域 -->
		<div class="searchbar">
			<!-- 사용 여부 라벨 / 使用状況ラベル -->
			<span class="muted" style="margin-left: 12px;">使用有無</span>
			<!-- 사용 여부 드롭다운 선택 / 使用有無ドロップダウン選択 -->
			<select id="selUse" title="使用有無">
				<option value="ALL">全体</option>
				<option value="Y">使用</option>
				<option value="N">未使用</option>
			</select>
		</div>

		<!-- 데이터 테이블 / データテーブル -->
		<div class="grid">
			<table id="grid">
				<!-- 테이블 헤더 (제목행) / テーブルヘッダー (タイトル行) -->
				<thead>
					<tr id="gridHead"></tr>
				</thead>
				<!-- 테이블 바디 (데이터 행) / テーブルボディ (データ行) -->
				<tbody id="gridBody"></tbody>
			</table>
		</div>

		<!-- 하단: 조회 건수 및 페이지 / 下部: 検索件数およびページ -->
		<div class="footer">
			<div class="left">
				<!-- 조회 건수 설정 / 検索件数設定 -->
				<span>表示件数</span>
				<!-- 페이지당 표시할 행의 개수 선택 드롭다운 / 1ページあたり表示する行数選択ドロップダウン -->
				<select id="pageSize">
					<option>10</option>
					<option>20</option>
					<option selected>50</option>
					<option>100</option>
				</select>
				<!-- 페이지 번호 링크들이 표시되는 영역 / ページ番号リンクが表示される領域 -->
				<div class="pager" id="pagerNumbers"></div>
			</div>
		</div>
	</div>

	<!-- 공통 팝업 자바스크립트 라이브러리 로드 / 共通ポップアップJavaScriptライブラリの読み込み -->
	<script src="<c:url value='/resources/js/popup-common.js'/>"></script>
	<script>
		// 팝업 설정 객체 정의 / ポップアップ設定オブジェクト定義
		window.POPUP_CONFIG = {
			// 팝업 타이틀 / ポップアップタイトル
			title : '部署',
			// API 엔드포인트 (데이터를 가져올 서버 주소) / APIエンドポイント (データを取得するサーバーアドレス)
			api : '<c:url value="/api/popups/departments"/>',
			// 페이지당 표시할 기본 건수 / ページあたり表示するデフォルト件数
			pageSize : 50,
			// 검색 기준 옵션 목록 / 検索基準オプションリスト
			byOptions : [ {
				value : 'deptName',
				label : '部署名'
			}, {
				value : 'deptCode',
				label : '部署コード'
			} ],
			// 테이블 열 정의 (각 컬럼의 이름, 크기 등) / テーブル列定義 (各列の名前、サイズなど)
			columns : [ {
				key : '__seq',
				name : 'No',
				width : 56
			}, {
				key : 'deptCode',
				name : '部署コード',
				width : 80
			}, {
				key : 'deptName',
				name : '部署名'
			}, {
				key : 'startDate',
				name : '開始日',
				width : 100,
				format : 'date'
			}, {
				key : 'endDate',
				name : '終了日',
				width : 100,
				format : 'date'
			}, {
				key : 'memo',
				name : '備考'
			} ],
			// 테이블 행 더블클릭 시 실행되는 콜백 함수 / テーブル行ダブルクリック時に実行されるコールバック関数
			onRowClick : function(row) {
				// 부모 창이 존재하고 콜백 함수가 있으면 호출 / 親ウィンドウが存在し、コールバック関数がある場合は呼び出し
				if (window.opener
						&& typeof window.opener.onDepartmentPicked === 'function') {
					window.opener.onDepartmentPicked(row);
				}
				// 현재 팝업 창 닫기 / 現在のポップアップウィンドウを閉じる
				window.close();
			}

		};

		// 사용 여부 드롭다운 변경 시 즉시 데이터 재조회 / 使用有無ドロップダウン変更時にすぐデータを再検索
		document.getElementById('selUse').addEventListener('change',
				function() {
					// POPUP 객체의 search 메서드 호출 (검색 실행) / POPUPオブジェクトのsearchメソッド呼び出し (検索実行)
					if (window.POPUP && POPUP.search)
						POPUP.search();
					else
						document.getElementById('btnSearch').click();
				});

		// 검색 입력창에서 엔터 키 입력 시 검색 실행 / 検索入力欄でEnterキー入力時に検索実行
		document.getElementById('keyword').addEventListener('keydown',
				function(e) {
					if (e.key === 'Enter')
						document.getElementById('btnSearch').click();
				});

		// 팝업 초기화 (공통 팝업 라이브러리의 init 메서드 호출) / ポップアップ初期化 (共通ポップアップライブラリのinitメソッド呼び出し)
		POPUP.init();
	</script>
	<script>
		// DOM 로드 완료 후 테이블 기능 활성화 / DOMロード完了後テーブル機能を有効化
		document.addEventListener('DOMContentLoaded', function() {
			// 테이블 정렬 기능 활성화 (클릭하면 해당 열로 정렬) / テーブルソート機能を有効化 (クリックするとその列でソート)
			enableSort('#grid');
			// 테이블 페이징 기능 활성화 (1페이지에 20개 행 표시) / テーブルページング機能を有効化 (1ページに20行表示)
			enablePaging('#grid', 20);
		});
	</script>
</body>
</html>