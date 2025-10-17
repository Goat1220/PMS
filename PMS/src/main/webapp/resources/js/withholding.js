(function () {
  /* ========================= 공용 유틸/전역 / 共通ユーティル/グローバル ========================= */
  
  // DOM이 준비된 후 콜백 실행 / DOMが準備された後コールバック実行
  function onReady(fn){
    // document가 완전히 로드되었는지 확인 / documentが完全にロードされたか確認
    if (document.readyState === 'complete' || document.readyState === 'interactive') {
      setTimeout(fn,0); // 즉시 실행 / すぐに実行
    }
    else {
      // DOM 로드 완료 이벤트 대기 / DOMロード完了イベント待機
      document.addEventListener('DOMContentLoaded', fn, false);
    }
  }
  
  // 자동 재계산 일시정지 플래그 (확인창 동안 true) / 自動再計算一時停止フラグ (確認ウィンドウ中true)
  window.__suspendRefundAuto = false;

  // jsPDF 생성자 감지(전역형/UMD 둘 다 지원) / jsPDF生成者検出(グローバル形/UMD両方対応)
  window.__getJsPDFCtor = function(){
    // window.jsPDF가 있으면 반환, 없으면 window.jspdf.jsPDF 시도, 둘 다 없으면 null / window.jsPDFがあれば返す、なければwindow.jspdf.jsPDFを試す、両方なければnull
    return window.jsPDF || (window.jspdf && window.jspdf.jsPDF) || null;
  };
  
  // 라이브러리 로드 확인 / ライブラリロード確認
  window.__ensurePdfLibs = function(){
    // jsPDF 생성자와 html2canvas 함수 모두 로드되었는지 확인 / jsPDF生成子とhtml2canvas関数の両方がロードされたか確認
    var ctor = window.__getJsPDFCtor();
    var ok = !!ctor && (typeof window.html2canvas === 'function');
    // 라이브러리가 없으면 경고 표시 / ライブラリがなければ警告表示
    if (!ok) alert('PDFライブラリが読み込まれていません。\nhtml2canvas/jsPDFスクリプトと読み込み順序を確認してください。');
    return ok;
  };

  // DOM에서 API URL 가져오기 / DOMからAPI URLを取得
  var root = document.querySelector('.container') || document.body;
  // data 속성에서 API URL 추출 / data属性からAPI URLを抽出
  var SUMMARY_URL     = root && root.getAttribute('data-summary-url');
  var ANNEX_URL       = root && root.getAttribute('data-annex-url');
  var GENERATE_URL    = root && root.getAttribute('data-generate-url');
  var PREV_REFUND_URL = root && root.getAttribute('data-prev-refund-url');
  var SAVE_REFUND_URL = root && root.getAttribute('data-save-refund-url');

  // 3자리 수에 쉼표 추가 (예: 1000000 → 1,000,000) / 3桁数にカンマを追加 (例: 1000000 → 1,000,000)
  function fmt3(n){
    if(n==null) return '0'; // null이면 '0' 반환 / nullなら'0'を返す
    return String(n).replace(/\B(?=(\d{3})+(?!\d))/g, ','); // 정규식으로 3자리마다 쉼표 삽입 / 正規表現で3桁ごとにカンマを挿入
  }
  
  // JSON 형식으로 데이터 가져오기 / JSON形式でデータ取得
  function fetchJson(url){
    // fetch API로 URL에서 데이터 가져오기 / fetch APIでURLからデータ取得
    return fetch(url, {headers:{'Accept':'application/json'}}).then(function(r){
      // 응답이 실패하면 에러 발생 / レスポンスが失敗したらエラー発生
      if(!r.ok) throw new Error('HTTP '+r.status);
      // JSON 형식으로 파싱 / JSON形式でパース
      return r.json();
    });
  }
  
  // ID로 입력 요소의 값 가져오기 (앞뒤 공백 제거) / IDで入力要素の値取得 (前後スペース削除)
  function val(id){
    var el=document.getElementById(id);
    return (el && typeof el.value==='string')? el.value.trim():''; // 요소 없거나 값이 문자열 아니면 빈 문자열 반환 / 要素がないか値が文字列でなければ空文字列を返す
  }
  
  // 월 더하기 (YYYY-MM 형식으로 1개월 증가) / 月を加算 (YYYY-MM形式で1ヶ月増加)
  function addMonth(ym){
    // 정규식으로 YYYY-MM 형식 파싱 / 正規表現でYYYY-MM形式をパース
    var m=/^(\d{4})-(\d{2})$/.exec((ym||'').trim());
    if(!m) return ''; // 형식이 맞지 않으면 빈 문자열 반환 / 形式が合わなければ空文字列を返す
    var y=+m[1], mo=+m[2]+1; // 연도와 월 추출, 월에 1 더하기 / 年度と月を抽出、月に1を加算
    if(mo===13){y+=1; mo=1;} // 13월이면 연도 증가, 월은 1로 리셋 / 13月なら年度増加、月は1にリセット
    return y+'-'+('0'+mo).slice(-2); // YYYY-MM 형식으로 반환 / YYYY-MM形式で返す
  }
  
  // 파일명 생성 (WH_YYYYMM.txt 형식) / ファイル名生成 (WH_YYYYMM.txt形式)
  function makeFileName(ym){
    var m=/^(\d{4})-(\d{2})$/.exec((ym||'').trim());
    return m? ('WH_'+m[1]+m[2]+'.txt'):''; // YYYY와 MM을 붙여서 파일명 생성 / YYYYとMMをくっつけてファイル名生成
  }

//  /* ========================= 상단 조회 버튼 / 上部検索ボタン ========================= */
//  onReady(function(){
//    // 조회 버튼 요소 가져오기 / 検索ボタン要素取得
//    var btnSearch=document.getElementById('btnSearch');
//    if(!btnSearch) return; // 버튼 없으면 함수 종료 / ボタンなければ関数終了
//    
//    // 버튼 클릭 이벤트 리스너 추가 / ボタンクリックイベントリスナー追加
//    btnSearch.addEventListener('click', function(e){
//      if(e&&e.preventDefault) e.preventDefault(); else e.returnValue=false; // 기본 동작 방지 / デフォルト動作を防止
//      // 현재 보이는 패널이 요약이면 loadSummary, 아니면 loadAnnex 실행 / 現在表示されるパネルがサマリーなら loadSummary、でなければloadAnnex実行
//      if(document.getElementById('panelSummary').style.display!=='none'){ loadSummary(); } else { loadAnnex(); }
//    }, false);
//  });

  /* ========================= 월 동기화/신고일 / 月同期/報告日 ========================= */
  (function autoSyncMonths(){
    // 월 입력 요소들 가져오기 / 月入力要素を取得
    var $ym=document.getElementById('ym'), // 귀속월 / 帰属月
        $pay=document.getElementById('payYm'), // 지급월 / 支給月
        $rep=document.getElementById('reportYm'), // 신고연월 / 報告年月
        $repD=document.getElementById('reportDate'); // 신고일 / 報告日
    
    if(!$ym||!$pay||!$rep) return; // 요소 없으면 함수 종료 / 要素がなければ関数終了

    // 신고일 설정 (신고연월 + '-10') / 報告日を設定 (報告年月 + '-10')
    function setReportDate(){
      if($rep && $rep.value && $repD) $repD.value = $rep.value + '-10';
    }
    
    // 귀속월에서 나머지 월 동기화 / 帰属月から他の月を同期化
    function syncFromAccrual(){
      var ym=val('ym');
      if(ym&&$pay) $pay.value=ym; // 지급월을 귀속월과 같게 설정 / 支給月を帰属月と同じに設定
      var pay=val('payYm');
      if(pay&&$rep) $rep.value=addMonth(pay); // 신고연월을 지급월 + 1월로 설정 / 報告年月を支給月 + 1月に設定
      setReportDate();
    }
    
    // 지급월에서 신고연월 동기화 / 支給月から報告年月を同期化
    function syncFromPay(){
      var pay=val('payYm');
      if(pay&&$rep) $rep.value=addMonth(pay);
      setReportDate();
    }
    
    // 신고연월에서 신고일 동기화 / 報告年月から報告日を同期化
    function syncFromReportYm(){
      setReportDate();
    }

    // 페이지 로드 시 1회 동기화 / ページ読込時に1回同期化
    syncFromAccrual();
    
    // 각 입력 요소에 이벤트 리스너 추가 (변경, 포커스 이탈, 키 입력) / 各入력要素にイベントリスナーを追加 (変更、フォーカス喪失、キー入力)
    ['change','blur','keyup'].forEach(function(ev){
      $ym.addEventListener(ev,  syncFromAccrual);
      $pay.addEventListener(ev, syncFromPay);
      $rep.addEventListener(ev, syncFromReportYm);
    });
  })();

  /* ========================= 연말정산 토글 / 年末調整トグル ========================= */
  (function initYearEndToggle(){
    // 연말정산반영 체크박스와 연말정산연도 입력창 가져오기 / 年末調整反映チェックボックスと年末調整年度入力欄を取得
    var chk=document.getElementById('opt2'), year=document.getElementById('annYear');
    if(!chk||!year) return; // 요소 없으면 함수 종료 / 要素がなければ関数終了
    
    // 동기화 함수 / 同期化関数
    function sync(){
      var on=!!chk.checked; // 체크박스 체크 여부 확인 / チェックボックスのチェック状態確認
      year.readOnly=!on; // 체크 안 되면 입력 불가능 / チェックなければ入力不可
      year.classList.toggle('is-readonly', !on); // CSS 클래스 토글 / CSSクラスを切り替え
    }
    
    // 연말정산연도 입력 시 숫자만 입력하도록 제한 (최대 4자리) / 年末調整年度入력時に数字のみ入力に制限 (最大4桁)
    year.addEventListener('input', function(){
      this.value=this.value.replace(/\D/g,'').slice(0,4); // 숫자가 아닌 것 제거, 4자리 이상 자르기 / 数字以外を削除、4桁以上をカット
    });
    
    // 체크박스 변경 이벤트 리스너 추가 / チェックボックス変更イベントリスナー追加
    chk.addEventListener('change', sync, false);
    sync(); // 초기 동기화 / 初期同期化
  })();

  /* ========================= 확정 체크박스 비활성 / 確定チェックボックス無効 ========================= */
  (function(){
    // 확정 체크박스 가져오기 / 確定チェックボックスを取得
    var cb=document.getElementById('optConfirm');
    if(!cb) return; // 요소 없으면 함수 종료 / 要素がなければ関数終了
    
    cb.disabled=true; // 클릭 불가능하게 설정 / クリック不可に設定
    cb.tabIndex=-1; // Tab 키로 포커스 불가능 / Tabキーでフォーカス不可
    
    // 부모 요소에 CSS 클래스 추가 / 親要素にCSSクラスを追加
    if(cb.parentNode&&cb.parentNode.classList){
      cb.parentNode.classList.add('is-readonly');
    }
  })();

  /* ========================= 신고파일생성 카드 접기 / 報告ファイル生成カード折りたたみ ========================= */
  (function(){
    // 신고파일생성 카드와 헤더 가져오기 / 報告ファイル生成カードとヘッダーを取得
    var card=document.getElementById('fileCard'), header=document.getElementById('fileCardHeader');
    if(!card||!header) return; // 요소 없으면 함수 종료 / 要素がなければ関数終了
    
    // 헤더 클릭 이벤트 리스너 추가 / ヘッダークリックイベントリスナー追加
    header.addEventListener('click', function(e){
      var t=e.target; // 클릭된 요소 / クリックされた要素
      // 버튼이 아닌 부분을 클릭하면 접기/펼치기 / ボタン以外の部分をクリックすると折りたたみ/展開
      while(t && t!==header){
        if(t.tagName==='BUTTON') return; // 버튼 클릭이면 함수 종료 / ボタンクリックなら関数終了
        t=t.parentNode;
      }
      card.classList.toggle('is-collapsed'); // 접기/펼치기 토글 / 折りたたみ/展開を切り替え
    });
  })();

  /* ========================= 요약 데이터 로드 / サマリーデータ読込 ========================= */
  window.loadSummary = function(){
    var ym = val('ym') || '2025-09'; // 귀속월 가져오기 (기본값 '2025-09') / 帰属月取得 (デフォルト値 '2025-09')
    
    // API로부터 요약 데이터 가져오기 / APIからサマリーデータ取得
    fetchJson(SUMMARY_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb=document.getElementById('gridBody'); // 테이블 바디 요소 / テーブルボディ要素
        if(!tb) return; // 테이블 없으면 함수 종료 / テーブルなければ関数終了
        
        // 각 행을 HTML로 변환 / 各行をHTMLに変換
        var html = rows.map(function(r,i){
          return '<tr>'
            + '<td class="center">'+(i+1)+'</td>' // 번호 / 番号
            + '<td>'+(r.incomeType||'')+'</td>' // 소득 구분 / 所得区分
            + '<td class="code">'+(r.code||'')+'</td>' // 코드 / コード
            + '<td class="num">'+(r.headCount||0)+'</td>' // 인원 / 人員
            + '<td class="num">'+fmt3(r.taxTotal)+'</td>' // 총지급액 / 総支給額
            + '<td class="num">'+fmt3(r.ntWithheld)+'</td>' // 징수농특세 / 徴収農特税
            + '<td class="num">'+fmt3(r.taxIncome)+'</td>' // 납부소득세 / 納付所得税
            + '<td class="num">'+fmt3(r.taxWithheld)+'</td>' // 징수소득세 / 徴収所得税
            + '<td class="num">'+fmt3(r.adjRefund)+'</td>' // 조정환급세액 / 調整還付税額
            + '<td class="num">'+fmt3(r.taxNt)+'</td>' // 납부농특세 / 納付農特税
            + '<td class="num">'+fmt3(r.penaltyTax)+'</td>' // 징수가산세 / 徴収加算税
          + '</tr>';
        }).join(''); // 모든 행을 합치기 / すべての行を結合
        
        tb.innerHTML = html; // 테이블에 HTML 삽입 / テーブルにHTMLを挿入
        
        // 셀 배경색 적용 / セルの背景色を適用
        if (typeof window.applyPerCellGrey === 'function') window.applyPerCellGrey();
        
        // 미환급세액 재계산 / 未還付税額を再計算
        if (window.recalcRefundByTables) window.recalcRefundByTables();
      })
      .catch(function(err){
        console.error('[SUMMARY]', err);
        alert('サマリー検索失敗: '+err.message);
      });
  };

  /* ========================= 부표 데이터 로드 / 別紙データ読込 ========================= */
  window.loadAnnex = function(){
    var ym = val('ym') || '2025-09'; // 귀속월 가져오기 / 帰属月取得
    
    // API로부터 부표 데이터 가져오기 / APIから別紙データ取得
    fetchJson(ANNEX_URL + '?applyYyyymm=' + encodeURIComponent(ym))
      .then(function(rows){
        var tb=document.getElementById('annexBody'); // 부표 테이블 바디 / 別紙テーブルボディ
        if(!tb) return;
        
        // 각 행을 HTML로 변환 / 各行をHTMLに変換
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
      .catch(function(err){
        console.error('[ANNEX]', err);
        alert('別紙検索失敗: '+err.message);
      });
  };

  /* ========================= 탭 전환 / タブ切り替え ========================= */
  function activate(which){
    // 요약/부표 탭과 패널 가져오기 / サマリー/別紙のタブとパネルを取得
	  var sTab=document.getElementById('tabSummary'), aTab=document.getElementById('tabAnnex'),
	      sPan=document.getElementById('panelSummary'), aPan=document.getElementById('panelAnnex');
	  if(!sTab||!aTab||!sPan||!aPan) return; // 요소 없으면 함수 종료 / 要素がなければ関数終了
	  
	  // which 파라미터에 따라 활성 탭/패널 변경 / whichパラメータに応じてアクティブタブ/パネルを変更
	  if(which==='summary'){
	    sTab.classList.add('active'); // 요약 탭 활성화 / サマリータブを有効化
	    aTab.classList.remove('active'); // 부표 탭 비활성화 / 別紙タブを無効化
	    sPan.style.display='block'; // 요약 패널 표시 / サマリーパネルを表示
	    aPan.style.display='none'; // 부표 패널 숨김 / 別紙パネルを非表示
	  }else{
	    aTab.classList.add('active'); // 부표 탭 활성화 / 別紙タブを有効化
	    sTab.classList.remove('active'); // 요약 탭 비활성화 / サマリータブを無効化
	    aPan.style.display='block'; // 부표 패널 표시 / 別紙パネルを表示
	    sPan.style.display='none'; // 요약 패널 숨김 / サマリーパネルを非表示
	  }
	}

  /* ========================= 데이터 생성 버튼 바인딩 / データ生成ボタンバインディング ========================= */
  (function bindDataButtons(){
    var btnLoad = document.getElementById('btnLoad'); // 데이터 생성 버튼 가져오기 / データ生成ボタンを取得
    if (!btnLoad) return; // 버튼 없으면 함수 종료 / ボタンなければ関数終了

    // 마우스 누르기 이벤트: 자동계산 일시정지 / マウス押し込みイベント: 自動計算を一時停止
    btnLoad.addEventListener('mousedown', function () {
      window.__suspendRefundAuto = true; // 자동계산 일시정지 플래그 설정 / 自動計算一時停止フラグを設定
    }, false);

    // 클릭 이벤트 / クリックイベント
    btnLoad.addEventListener('click', function(e){
      if (e) {
        e.preventDefault(); // 기본 동작 방지 / デフォルト動작を防止
        e.stopPropagation(); // 이벤트 전파 방지 / イベント伝播を防止
      }

      var ym = val('ym'); // 귀속월 가져오기 / 帰属月を取得
      if (!ym) {
        alert('帰属月(YYYY-MM)をまず入力してください。');
        window.__suspendRefundAuto = false; // 자동계산 일시정지 해제 / 自動計算一時停止を解除
        return;
      }

      // 사용자 확인 / ユーザー確認
      if (!confirm('既存に登録された資料は削除されます。削除しますか?')) {
        window.__suspendRefundAuto = false;
        return;
      }

      // 데이터 재로드 함수 / データ再読込関数
      function reload(){
        window.__suspendRefundAuto = false; // 자동계산 일시정지 해제 / 自動計算一時停止を解除
        // 현재 표시된 탭에 따라 데이터 로드 / 現在表示されているタブに応じてデータを読込
        if (document.getElementById('panelSummary').style.display !== 'none') {
          loadSummary(); // 요약 로드 / サマリーを読込
        } else {
          loadAnnex(); // 부표 로드 / 別紙を読込
        }
      }

      // 데이터 생성 API 호출 / データ生成API呼び出し
      if (GENERATE_URL) {
        fetch(GENERATE_URL + '?applyYyyymm=' + encodeURIComponent(ym), {
          method:'POST', // POST 방식 / POST方式
          headers:{ 'Accept':'application/json' }
        })
        .then(function(r){ return r.json(); }) // JSON 응답 파싱 / JSON応答をパース
        .then(function(j){
          alert((j && (j.message||'')) || '生成完了');
          window.__resetAfterRender = true; // 렌더 완료 후 초기화 표시 / レンダー完了後の初期化を表示
          reload();
        })
        .catch(function(err){
          alert('生成失敗: ' + err);
          window.__suspendRefundAuto = false;
        });
      } else {
      	 window.__suspendRefundAuto = false;
        reload();
      }
    }, false);

    // 요약 탭 클릭 이벤트 / サマリータブクリックイベント
	  var tabSummary = document.getElementById('tabSummary');
	  var tabAnnex   = document.getElementById('tabAnnex');
	  if (tabSummary) {
	    tabSummary.addEventListener('click', function(){
	      activate('summary'); // 요약 활성화 / サマリーを有効化
	      loadSummary(); // 요약 데이터 로드 / サマリーデータを読込
	    });
	  }
	  if (tabAnnex) {
	    tabAnnex.addEventListener('click', function(){
	      activate('annex'); // 부표 활성화 / 別紙を有効化
	      loadAnnex(); // 부표 데이터 로드 / 別紙データを読込
	    });
	  }
	})();

  /* ========================= 스크롤 영역 자동 높이 / スクロール領域自動高さ ========================= */
  (function(){
    // 스크롤 영역 높이 자동 조정 / スクロール領域の高さを自動調整
    function fit(){
      var sc=document.getElementById('dataScroll'); // 스크롤 영역 / スクロール領域
      if(!sc) return;
      
      var refund=document.getElementById('refundBlock'); // 미환급세액 블록 / 未還付税額ブロック
      var rect=sc.getBoundingClientRect(); // 스크롤 영역의 위치/크기 / スクロール領域の位置/サイズ
      var gap=12; // 여백 / 余白
      var underH= refund? (refund.offsetHeight+12):0; // 미환급세액 높이 / 未還付税額の高さ
      
      // 계산된 높이: 윈도우 높이 - 스크롤 영역 위치 - 미환급세액 높이 - 여백 / 計算された高さ: ウィンドウ高さ - スクロール領域位置 - 未還付税額高さ - 余白
      var h=window.innerHeight - rect.top - underH - gap;
      if(h<220) h=220; // 최소 높이 220px / 最小高さ220px
      sc.style.height=h+'px'; // 높이 적용 / 高さを適用
    }
    
    // 요소에 이벤트 리스너 추가 헬퍼 함수 / 요素にイベントリスナーを追加するヘルパー関数
    function on(el,t,fn){
      if(el&&el.addEventListener){
        el.addEventListener(t,fn,false); // 요소에 이벤트 리스너 추가 / 要素にイベントリスナーを追加
      }
    }
    
    on(window,'load',fit); // 페이지 로드 완료 시 fit 호출 / ページ読込完了時にfitを呼び出し
    on(window,'resize',fit); // 창 크기 변경 시 fit 호출 / ウィンドウサイズ変更時にfitを呼び出し
    on(document.getElementById('tabSummary'),'click',fit); // 요약 탭 클릭 시 fit 호출 / サマリータブクリック時にfitを呼び出し
    on(document.getElementById('tabAnnex'),'click',fit); // 부표 탭 클릭 시 fit 호출 / 別紙タブクリック時にfitを呼び出し
    setTimeout(fit,80); // 80ms 후 fit 호출 (초기화) / 80ms後にfitを呼び出し (初期化)
  })();

  /* ========================= 표 색칠(요약/부표) / テーブル色付け(サマリー/別紙) ========================= */
  // 코드별로 회색으로 칠할 열 목록 / コード別に灰色で塗る列リスト
  var GREY_COLUMNS_BY_CODE = {
    "A01":["調整還付税額"], 
    "A02":["調整還付税額"], 
    "A03":["徴収農特税","調整還付税額"],
    "A04":["徴収農特税","徴収所得税","調整還付税額","徴収加算税"], 
    "A05":["総支給額","調整還付税額"],
    "A06":["人員","総支給額","調整還付税額"], 
    "A10":["人員","総支給額","徴収農特税","徴収所得税","徴収加算税"],
    "A21":["徴収農特税","調整還付税額"], 
    "A22":["徴収農特税","調整還付税額"], 
    "A20":["人員","総支給額","徴収農特税","徴収所得税","徴収加算税"],
    "A25":["徴収農特税","調整還付税額"], 
    "A26":["調整還付税額"], 
    "A30":["人員","総支給額","徴収農特税","徴収所得税","徴収加算税"],
    "A41":["徴収農特税","調整還付税額"], 
    "A43":["徴収農特税","調整還付税額"], 
    "A44":["徴収農特税","調整還付税額"],
    "A42":["徴収農特税","調整還付税額"], 
    "A40":["人員","総支給額","徴収農特税","徴収所得税","徴収加算税"],
    "A48":["徴収農特税","調整還付税額"], 
    "A45":["徴収農特税","調整還付税額"], 
    "A46":["徴収農特税","調整還付税額"],
    "A47":["人員","総支給額","徴収農特税","徴収所得税","徴収加算税"], 
    "A69":["総支給額","徴収農特税"],
    "A70":["徴収農特税"], 
    "A80":["徴収農特税"], 
    "A90":["人員","総支給額"],
    "A99":["人員","総支給額","徴収農特税","徴収所得税","徴収加算税"]
  };
  
  // 항상 회색으로 칠할 열 (JSP 헤더와 정확히 일치) / 常に灰色で塗る列 (JSPヘッダーと正確に一致)
  var ALWAYS_GREY_COLUMNS = ["所得区分","コード","納付所得税","納付農特税"];
  
  // 핑크색으로 칠할 특수 코드 / ピンク色で塗る特殊コード
  var SPECIAL_PINK_CODES = ["A10","A20","A30","A40","A47","A50","A60","A69","A70","A80","A90","A99"];

  // 테이블 헤더 인덱스 맵 생성 / テーブルヘッダーインデックスマップ生成
  function buildHeaderIndexMap(tableSelector){
    var map={}; // 헤더명 → 열 번호 / ヘッダー名 → 列番号
    var ths=document.querySelectorAll(tableSelector+' thead th'); // 모든 헤더 가져오기 / すべてのヘッダーを取得
    ths.forEach(function(th,idx){
      var key=(th.textContent||'').trim(); // 헤더 텍스트 추출 / ヘッダーテキストを抽出
      if(key) map[key]=idx+1; // 맵에 추가 (1부터 시작) / マップに追加 (1から開始)
    });
    return map;
  }
  
  // 요약 테이블에 회색 배경 적용 / サマリーテーブルに灰色背景を適用
  window.applyPerCellGrey = function(){
    var headerMap=buildHeaderIndexMap('#panelSummary table'); // 헤더 맵 생성 / ヘッダーマップ生成
    var rows=document.querySelectorAll('#gridBody tr'); // 모든 행 가져오기 / すべての行を取得
    var alwaysIdx = ALWAYS_GREY_COLUMNS.map(function(h){return headerMap[h];}).filter(Boolean); // 항상 회색 열 번호 / 常に灰色の列番号
    function idxOf(h){ return headerMap[h] || null; } // 헤더명으로 열 번호 찾기 / ヘッダー名で列番号を検索

    rows.forEach(function(tr){
      // 각 행의 모든 셀에서 색상 클래스 제거 / 各行のすべてのセルから色クラスを削除
      tr.querySelectorAll('td').forEach(function(td){ td.classList.remove('cell-grey','cell-pink'); });
      
      var codeCell=tr.querySelector('td:nth-child(3)'); // 3번째 셀(코드) / 3番目のセル(コード)
      if(!codeCell) return; // 코드 셀 없으면 스킵 / コードセルなければスキップ
      var code=(codeCell.textContent||'').trim(); // 코드 추출 / コードを抽出

      // 항상 회색인 열에 색상 적용 / 常に灰色の列に色を適用
      alwaysIdx.forEach(function(nth){
        var td=tr.querySelector('td:nth-child('+nth+')');
        if(td) td.classList.add('cell-grey');
      });

      // 특수 핑크 코드인 경우 "소득 구분"과 "코드" 열을 핑크로 변경 / 特殊ピンクコードの場合「所得区分」と「コード」列をピンクに変更
      if(SPECIAL_PINK_CODES.indexOf(code)>=0){
        ["所得区分","コード"].forEach(function(h){
          var nth=idxOf(h); if(!nth) return;
          var td=tr.querySelector('td:nth-child('+nth+')');
          if(td){ td.classList.remove('cell-grey'); td.classList.add('cell-pink'); }
        });
      }
      
      // 코드별 특정 열에 회색 적용 / コード別に特定の列に灰色を適用
      (GREY_COLUMNS_BY_CODE[code]||[]).forEach(function(h){
        var nth=idxOf(h); if(!nth) return;
        var td=tr.querySelector('td:nth-child('+nth+')');
        if(td) td.classList.add('cell-grey');
      });
    });
  };

  // 부표 회색 설정 / 別紙灰色設定
  var GREY_COLUMNS_BY_CODE_ANNEX = {
    "C01":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"], 
    "C02":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"],
    "C03":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"], 
    "C05":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"],
    "C06":["徴収所得税","調整還付税額"], 
    "C07":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"],
    "C08":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"], 
    "C10":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"],
    "C11":["調整還付税額"], 
    "C12":["調整還付税額"], 
    "C13":["徴収農特税","調整還付税額"], 
    "C14":["徴収農特税","調整還付税額"],
    "C15":["徴収農特税","調整還付税額"], 
    "C16":["徴収農特税","調整還付税額"], 
    "C18":["徴収農特税","調整還付税額"],
    "C19":["徴収所得税","調整還付税額"], 
    "C20":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"], 
    "C22":["調整還付税額"],
    "C23":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"], 
    "C24":["徴収農特税","調整還付税額"], 
    "C25":["徴収農特税","調整還付税額"],
    "C26":["徴収農特税","調整還付税額"], 
    "C28":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"], 
    "C29":["徴収所得税","調整還付税額"],
    "C30":["徴収農特税","徴収所得税","総支給額","人員","徴収加算税"], 
    "C31":["調整還付税額"], 
    "C33":["調整還付税額"],
    "C34":["調整還付税額"], 
    "C36":["徴収農特税","調整還付税額"], 
    "C37":["徴収農特税","調整還付税額"], 
    "C38":["徴収農特税","調整還付税額"],
    "C39":["徴収農特税","調整還付税額"], 
    "C40":["徴収所得税","調整還付税額"], 
    "C41":["調整還付税額","徴収農特税","総支給額"],
    "C42":["調整還付税額","徴収農特税","総支給額"], 
    "C43":["調整還付税額","徴収農特税","総支給額"], 
    "C44":["調整還付税額","徴収農特税","総支給額"],
    "C45":["調整還付税額","徴収農特税","総支給額"], 
    "C46":["調整還付税額","総支給額"], 
    "C50":["人員","総支給額","徴収所得税","徴収農特税","徴収加算税"],
    "C52":["徴収農特税","調整還付税額"], 
    "C54":["徴収農特税","調整還付税額"], 
    "C55":["徴収農特税","調整還付税額"],
    "C56":["調整還付税額"], 
    "C57":["調整還付税額"], 
    "C58":["徴収農特税","調整還付税額"],
    "C70":["人員","総支給額","徴収所得税","徴収農特税","徴収加算税"], 
    "C71":["徴収農特税"], 
    "C72":["徴収農特税"],
    "C73":["徴収農特税"], 
    "C74":["徴収農特税"], 
    "C75":["徴収農特税"],
    "C76":["徴収所得税","徴収農特税","徴収加算税","調整還付税額"],
    "C81":["徴収農特税"], 
    "C82":["徴収農特税"], 
    "C83":["徴収農特税"], 
    "C84":["徴収農特税"], 
    "C85":["徴収農特税"],
    "C86":["徴収農特税"], 
    "C87":["徴収農特税"], 
    "C88":["徴収農特税"],
    "C90":["人員","総支給額","徴収所得税","徴収農特税","徴収加算税"]
  };
  
  var ALWAYS_GREY_COLUMNS_ANNEX = ["所得区分","コード","納付所得税","納付農特税"];
  var SPECIAL_PINK_CODES_ANNEX = ["C30","C50","C70","C90"];

  // 부표 테이블에 회색 배경 적용 / 別紙テーブルに灰色背景を適用
  window.applyPerCellGreyAnnex = function(){
    var headerMap=buildHeaderIndexMap('#panelAnnex table');
    var rows=document.querySelectorAll('#annexBody tr');

    var alwaysIdx=[]; 
    for(var i=0;i<ALWAYS_GREY_COLUMNS_ANNEX.length;i++){
      var h=ALWAYS_GREY_COLUMNS_ANNEX[i];
      if(headerMap[h]) alwaysIdx.push(headerMap[h]);
    }
    function idxOf(h){ return headerMap[h] ? headerMap[h] : null; }

    for(var r=0;r<rows.length;r++){
      var tr=rows[r]; 
      var tds=tr.querySelectorAll('td');
      for(var k=0;k<tds.length;k++){
        tds[k].classList.remove('cell-grey','cell-pink');
      }
      var codeCell=tr.querySelector('td:nth-child(3)');
      if(!codeCell) continue;
      var code=(codeCell.textContent || codeCell.innerText || '').replace(/^\s+|\s+$/g,'');

      for(var a=0;a<alwaysIdx.length;a++){
        var nth=alwaysIdx[a];
        var td=tr.querySelector('td:nth-child('+nth+')');
        if(td) td.classList.add('cell-grey');
      }

      for(var sp=0; sp<SPECIAL_PINK_CODES_ANNEX.length; sp++){
        if(SPECIAL_PINK_CODES_ANNEX[sp]===code){
          ["所得区分","コード"].forEach(function(hh){
            var nth2=idxOf(hh); if(!nth2) return;
            var td2=tr.querySelector('td:nth-child('+nth2+')');
            if(td2){ td2.classList.remove('cell-grey'); td2.classList.add('cell-pink'); }
          });
          break;
        }
      }

      var list=GREY_COLUMNS_BY_CODE_ANNEX[code]||[];
      for(var c=0;c<list.length;c++){
        var nth3=idxOf(list[c]);
        if(!nth3) continue;
        var td3=tr.querySelector('td:nth-child('+nth3+')');
        if(td3) td3.classList.add('cell-grey');
      }
    }
  };

  /* ========================= 전월 미환급세액 계산 (A~K) / 前月未還付税額計算 (A~K) ========================= */
  (function refundBlock(){
    // 문자열을 숫자로 변환 (쉼표 제거) / 文字列を数値に変換 (カンマ削除)
    function toNum(v){
      return Number(String(v||'').replace(/[^\d.-]/g,'')) || 0;
    }
    
    // 숫자를 포맷된 문자열로 변환 (일본어 로케일) / 数値をフォーマットされた文字列に変換 (日本語ロケール)
    function fmt(n){
      return n.toLocaleString('ja-JP');
    }
    
    // 입력 요소에 포맷된 숫자 값 설정 / 入力要素にフォーマットされた数値を設定
    function setVal(el,n){
      el.value = fmt(n);
    }

    // A~K 입력 요소 가져오기 / A~K入力要素を取得
    var A=document.getElementById('A'), B=document.getElementById('B'), C=document.getElementById('C'),
        D=document.getElementById('D'), E=document.getElementById('E'), F=document.getElementById('F'), G=document.getElementById('G'),
        H=document.getElementById('H'), I=document.getElementById('I'), J=document.getElementById('J'), K=document.getElementById('K');
    if(!A||!B||!C||!D||!E||!F||!G||!H||!I||!J||!K) return; // 요소 없으면 함수 종료 / 要素がなければ関数終了

    // 테이블 헤더명으로 열 번호 찾기 / テーブルヘッダー名で列番号を検索
    function headerIndexByName(tableSel, headerName){
      var ths=document.querySelectorAll(tableSel+' thead th');
      for(var i=0;i<ths.length;i++){
        var key=(ths[i].textContent||'').trim();
        if(key===headerName) return i+1;
      }
      return null;
    }
    
    // 특정 열의 합계 계산 / 特定の列の合計を計算
    function sumColByHeader(headerName){
      var nth=headerIndexByName('#panelSummary table', headerName);
      if(!nth) return 0;
      var rows=document.querySelectorAll('#gridBody tr');
      var sum=0;
      for(var r=0;r<rows.length;r++){
        var td=rows[r].querySelector('td:nth-child('+nth+')');
        if(!td) continue;
        sum += toNum(td.textContent);
      }
      return sum;
    }
    
    // 요약 테이블에서 D와 I 재계산 / サマリーテーブルからD とI を再計算
    function recalcFromTable(){
      var total = sumColByHeader('徴収所得税') + sumColByHeader('徴収加算税') + sumColByHeader('徴収農特税');
      var d = (total < 0) ? (-total) : 0;
      setVal(D, d);

      var i = sumColByHeader('調整還付税額');
      setVal(I, i);

      recalcAll();
    }

    // A~K 재계산 / A~Kを再計算
    function recalcAll(){
      var a=toNum(A.value), b=toNum(B.value), d=toNum(D.value), e=toNum(E.value),
          f=toNum(F.value), g=toNum(G.value), i=toNum(I.value);
      var c=a-b, h=c+d+e+f+g, j=h-i;
      setVal(C, Math.max(0,c));
      setVal(H, Math.max(0,h));
      setVal(J, Math.max(0,j));
    }

    // 사용자가 입력 가능한 칸 (A, B, E, F, G, K) / ユーザーが入力可能な場所 (A, B, E, F, G, K)
    [A,B,E,F,G,K].forEach(function(el){
    	  // 입력 이벤트: 실시간 포맷 / 入力イベント: リアルタイムフォーマット
    	  el.addEventListener('input', function(){
    	    var raw = String(this.value).replace(/[^\d.-]/g, ''); // 숫자만 추출 / 数字のみ抽出
    	    if (raw === '') {
    	      this.value = '';
    	      return;
    	    }
    	    var n = Number(raw) || 0;
    	    this.value = fmt(n); // 포맷 적용 / フォーマット適用
    	    if (this !== K) recalcAll(); // K가 아니면 재계산 / Kでなければ再計算
    	  });

    	  // 포커스 이탈 이벤트: 최종 포맷 / フォーカス喪失イベント: 最終フォーマット
    	  el.addEventListener('blur', function(){
    	    var n = Number(String(this.value).replace(/[^\d.-]/g,'')) || 0;
    	    this.value = fmt(n);
    	    if (this !== K) recalcAll();
    	  });
    	});
    
    // 전월 미환급세액 값을 서버에서 받아 채우기 / 前月未還付税額値をサーバーから受け取って埋める
    function prefillPrevRefund() {
      if (!PREV_REFUND_URL) return; // API URL 없으면 반환 / API URLなければ返す

      var ymEl = document.getElementById('ym');
      var ym = (ymEl && ymEl.value || '').trim();
      var m  = /^(\d{4})-(\d{2})$/.exec(ym);
      if (!m) return; // YYYY-MM 형식 아니면 반환 / YYYY-MM形式でなければ返す

      // 전월 계산 (현재 월 - 1) / 前月を計算 (現在の月 - 1)
      var y = +m[1], mo = +m[2] - 1;
      if (mo === 0) { y -= 1; mo = 12; }
      var prev = String(y) + '-' + ('0' + mo).slice(-2);

      function setNum(el, v){
        if (!el) return;
        var n = Number(String(v || '').replace(/[^\d.-]/g, '')) || 0;
        setVal(el, n);
      }

      // 전월 데이터 조회 / 前月データ照会
      fetch(PREV_REFUND_URL + '?yyyymm=' + encodeURIComponent(prev), {
    	  method: 'GET',
    	  headers: { 
    	    'Accept': 'application/json',
    	    'Cache-Control': 'no-cache, no-store, must-revalidate'
    	  },
    	  cache: 'no-store'
    	})
        .then(function(r){ if(!r.ok) throw new Error(r.status); return r.json(); })
        .then(function(j){
          setNum(A, j.prevCarryJ); // A ← 전월 J / A ← 前月 J
          setNum(B, j.prevApplyK); // B ← 전월 K / B ← 前月 K
          recalcAll();
        })
        .catch(function(){ /* 조용히 실패 처리 / 静かに失敗処理 */ });
    }
    
    // 수동 입력 필드 초기화 (E, F, G, K를 0으로) / 手動入力フィールドを初期化 (E, F, G, Kを0に)
    function resetManualFields(){
      [E,F,G,K].forEach(function(el){
        if (el) el.value = fmt(0);
      });
      recalcAll();
    }

    // 귀속월 변경 시 전월값 재주입 / 帰属月変更時に前月値を再注入
    var ymInput = document.getElementById('ym');
    if (ymInput) {
      ['change','blur'].forEach(function(ev){
        ymInput.addEventListener(ev, function(){
          if (window.__suspendRefundAuto) return; // 자동계산 정지 중이면 반환 / 自動計算停止中なら返す
          resetManualFields(); // 수동 필드 초기화 / 手動フィールドを初期化
          recalcFromTable();
          prefillPrevRefund();
        });
      });
    }

    // 요약 테이블 갱신 후 호출되는 훅 / サマリーテーブル更新後に呼ばれるフック
    window.recalcRefundByTables = function(){
     if (window.__suspendRefundAuto) return; // 자동계산 정지 중이면 반환 / 自動計算停止中なら返す
     
     // 렌더 완료 후 한 번만 수동 필드 초기화 / レンダー완료後1回だけ手動フィールドを初期化
     if (window.__resetAfterRender) {
       resetManualFields();
       window.__resetAfterRender = false;
     }

     recalcFromTable();
     prefillPrevRefund();
   };

    // 초기 1회 호출 / 初期1回呼び出し
    window.recalcRefundByTables();
  })();

  // 페이지 로드 시 요약 첫 로드 / ページ読込時にサマリーを初回読込
  onReady(function(){
    loadSummary();
  });

  /* ========================= PDF 저장(한 장에 합치기) / PDF保存(1ページに合成) ========================= */
  window.exportWHtoPDF = function () {
    // jsPDF 생성자 및 html2canvas 확인 / jsPDF生成子およびhtml2canvasを確認
    var JsPDFCtor = window.__getJsPDFCtor();
    if (!JsPDFCtor || !window.html2canvas) {
      alert('PDFライブラリが読み込まれていません。\nhtml2canvas/jsPDFスクリプトを確認してください。');
      return;
    }

    // PDF 인스턴스 생성 (A4 크기, 압축 활성화) / PDFインスタンス生成 (A4サイズ、圧縮有効化)
    var pdf  = new JsPDFCtor({ unit: 'mm', format: 'a4', compress: true });
    var A4W  = 210, A4H = 297, M = 10; // A4 크기 및 여백 / A4サイズおよび余白
    var WMM  = A4W - M * 2, HMM = A4H - M * 2; // 가용폭/높이 / 利用可能幅/高さ
    var GAP_MM = 4; // 섹션 간 간격 / セクション間の間隔
    var sectionIds = ['panelSummary', 'panelAnnex', 'refundBlock']; // PDF에 포함할 섹션 / PDFに含めるセクション

    // HTML을 canvas로 변환 / HTMLをcanvasに変換
    function capture(id) {
      var el = document.getElementById(id);
      if (!el) return Promise.resolve(null);
      return html2canvas(el, { scale: 2, useCORS: true, backgroundColor: '#ffffff' })
        .then(function(cv){ return cv; })
        .catch(function(){ return null; });
    }

    // 모든 섹션 캡처 후 PDF 생성 / すべてのセクションをキャプチャした後PDF生成
    Promise.all(sectionIds.map(capture)).then(function(canvases){
      // null 제거 / nullを削除
      canvases = canvases.filter(function(c){ return c && c.width && c.height; });
      if (!canvases.length) { alert('エクスポート対象セクションがありません。'); return; }

      // 전체 크기 계산 / 全体サイズ計算
      var maxWpx = 0, sumHpx = 0;
      for (var i=0;i<canvases.length;i++){
        maxWpx = Math.max(maxWpx, canvases[i].width); // 최대 폭 찾기 / 最大幅を検索
        sumHpx += canvases[i].height; // 높이 합계 / 高さを合計
      }

      // 스케일 계산 (px → mm) / スケール計算 (px → mm)
      var sByWidth  = WMM / maxWpx; // 폭 기준 스케일 / 幅ベースのスケール
      var sByHeight = (HMM - (canvases.length - 1) * GAP_MM) / sumHpx; // 높이 기준 스케일 / 高さベースのスケール
      var s = Math.min(sByWidth, sByHeight); // 더 작은 스케일 선택 / より小さいスケールを選択

      // 한 장에 위에서 아래로 배치 / 1ページに上から下へ配置
      var y = M; // 시작 Y 위치 / 開始Y位置
      for (var j=0;j<canvases.length;j++){
        var cv = canvases[j];
        var wmm = cv.width  * s; // 스케일 적용한 폭 / スケール適用した幅
        var hmm = cv.height * s; // 스케일 적용한 높이 / スケール適用した高さ
        var x   = M + (WMM - wmm)/2; // 중앙 정렬 X 위치 / 中央揃えX位置

        // 이미지를 PDF에 추가 / イメージをPDFに追加
        pdf.addImage(cv.toDataURL('image/jpeg', 0.95), 'JPEG', x, y, wmm, hmm);
        y += hmm + (j < canvases.length - 1 ? GAP_MM : 0); // 다음 이미지를 위해 Y 위치 업데이트 / 次のイメージのためにY位置を更新
      }

      // 귀속월로 파일명 생성 / 帰属月でファイル名生成
      var ym = ((document.getElementById('ym')||{}).value || '').replace('-', '') || 'YYYYMM';
      pdf.save('源泉徴収_' + ym + '.pdf'); // PDF 저장 / PDFを保存
    })
    .catch(function(err){
      console.error('[PDF] 예기치 못한 오류:', err); // 예외 로그 / 例外ログ
      var ym = ((document.getElementById('ym')||{}).value || '').replace('-', '') || 'YYYYMM';
      pdf.save('源泉徴収_' + ym + '.pdf'); // 오류 발생 후에도 PDF 저장 시도 / エラー発生後もPDF保存を試み
    });
  };

  /* ========================= 신고 버튼 바인딩 / 報告ボタンバインディング ========================= */
  (function(){
    // 문자열을 숫자로 변환 / 文字列を数値に変換
    function toNum(v){
      return Number(String(v||'').replace(/[^\d.-]/g,'')) || 0;
    }
    
    // 귀속월 필수 입력 확인 / 帰属月の必須入力確認
    function requireYm(){
      var ymEl = document.getElementById('ym');
      var ym = (ymEl && ymEl.value || '').trim();
      // YYYY-MM 형식 확인 / YYYY-MM形式を確認
      if (!/^\d{4}-\d{2}$/.test(ym)) {
        alert('帰属月(YYYY-MM)をまず入力してください。');
        return null;
      }
      return ym;
    }
    
    // J와 K 값을 서버에 저장 / JとK値をサーバーに保存
    function saveJK(ym, j, k){
      if (!SAVE_REFUND_URL) return Promise.resolve(); // 저장 API 없으면 생략 / 保存API없ければスキップ
      
      // URL에 파라미터 추가 / URLにパラメータを追加
      var url = SAVE_REFUND_URL
        + '?yyyymm=' + encodeURIComponent(ym)
        + '&jValue=' + encodeURIComponent(j)
        + '&kValue=' + encodeURIComponent(k);

      // POST 요청 / POST요청
      return fetch(url, { method:'POST', headers:{ 'Accept':'application/json' } })
        .then(function(r){
          if(!r.ok) throw new Error('HTTP '+r.status); // 요청 실패 시 에러 발생 / リクエスト失敗時にエラー発生
          return r.json().catch(function(){ return {}; }); // JSON 응답 파싱 (실패 시 빈 객체) / JSON応答をパース (失敗時は空オブジェクト)
        });
    }

    // DOM 준비 후 버튼 바인딩 / DOM준비後ボタンをバインド
    onReady(function(){
      var btnMake    = document.getElementById('btnMake'); // 신고파일생성 버튼 / 報告ファイル生成ボタン

      // 신고파일생성 버튼 클릭 이벤트 / 報告ファイル生成ボタンクリックイベント
      function handleMakeClick(e){
        if (e && e.preventDefault) e.preventDefault(); // 기본 동작 방지 / デフォルト動作を防止
        
        var ym = requireYm(); // 귀속월 확인 / 帰属月を確認
        if (!ym) return; // 귀속월 없으면 반환 / 帰属月がなければ返す
        
        var fileName = makeFileName(ym); // 파일명 생성 / ファイル名生成

        // J와 K 값 추출 / JとK値を抽出
        var j = toNum((document.getElementById('J')||{}).value);
        var k = toNum((document.getElementById('K')||{}).value);

        // J, K 값 저장 후 PDF 생성 / J、K値を保存してからPDF生成
        saveJK(ym, j, k)
          .then(function(){
            alert('環付保存完了。PDF生成を進めます。\nファイル名: ' + fileName); // 저장 완료 메시지 / 保存完了メッセージ
            if (!window.__ensurePdfLibs()) return; // PDF 라이브러리 확인 / PDFライブラリを確認
            window.exportWHtoPDF(); // PDF 생성 / PDF생成
          })
          .catch(function(err){
            alert('環付保存失敗: ' + err + '\nPDF生成は継続します。'); // 저장 실패 메시지 / 保存失敗メッセージ
            if (!window.__ensurePdfLibs()) return;
            window.exportWHtoPDF(false); // 저장 실패해도 PDF 생성 계속 / 保存失敗してもPDF생成を継続
          });
      }

      // 버튼 클릭 이벤트 바인딩 / ボタンクリックイベントをバインド
      if (btnMake)    btnMake.onclick    = handleMakeClick;
    });
  })();

})(); // 최상위 IIFE 종료 / 最上位IIFE終了