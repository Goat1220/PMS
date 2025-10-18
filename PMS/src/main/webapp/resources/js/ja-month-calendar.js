// ===================== 日本語「月のみ」選択グリッド（依存なし） =====================
// ===================== 일본어 '달만' 선택 그리드 (의존성 없음) =====================
// #yyyymm (type="month" 또는 type="text")에 연결하여 YYYY-MM 값을 설정하는 팝업입니다.
// 입력을 다시 클릭하면 닫히는 "토글" / もう一度クリックで閉じるトグル動作に対応。
(function () {
  var MONTHS_JA = ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'];

  // 2자리 패딩 / 2桁ゼロ埋め
  function pad2(n){ return (n<10?'0':'')+n; }

  // 팝업 엘리먼트 생성 / ポップアップ要素生成
  function createPopup() {
    var pop = document.createElement('div');
    pop.className = 'ja-month-only';
    pop.style.position = 'absolute';
    pop.style.zIndex = '9999';
    pop.style.background = '#fff';
    pop.style.border = '1px solid #e5e7eb';
    pop.style.borderRadius = '10px';
    pop.style.boxShadow = '0 10px 20px rgba(0,0,0,.08)';
    pop.style.padding = '10px';
    pop.style.fontFamily = 'system-ui, -apple-system, "Segoe UI", Roboto, "Noto Sans JP", "Noto Sans KR", sans-serif';
    pop.style.userSelect = 'none';
    pop.style.minWidth = '260px';
    return pop;
  }

  // 캘린더 렌더링 / カレンダー描画
  function render(pop, state) {
    pop.innerHTML = '';

    // === 헤더(이전해/연도/다음해) / ヘッダー（前年/年/翌年） ===
    var header = document.createElement('div');
    header.style.display = 'flex';
    header.style.alignItems = 'center';
    header.style.justifyContent = 'space-between';
    header.style.margin = '4px 6px 10px';

    function navBtn(txt, title, onClick) {
      var b = document.createElement('button');
      b.type = 'button';
      b.textContent = txt;
      b.title = title;
      b.style.border = '0';
      b.style.background = 'transparent';
      b.style.cursor = 'pointer';
      b.style.fontSize = '14px';
      b.style.borderRadius = '8px';
      b.style.padding = '4px 6px';
      b.addEventListener('click', onClick);
      b.addEventListener('mouseenter', function(){ b.style.background = '#f3f4f6'; });
      b.addEventListener('mouseleave', function(){ b.style.background = 'transparent'; });
      return b;
    }

    var btnPrev = navBtn('◀', '前年へ', function () { state.year--; render(pop, state); });
    var title = document.createElement('div');
    title.textContent = state.year + '年';
    title.style.fontWeight = '600';
    var btnNext = navBtn('▶', '翌年へ', function () { state.year++; render(pop, state); });

    header.appendChild(btnPrev);
    header.appendChild(title);
    header.appendChild(btnNext);
    pop.appendChild(header);

    // === 12개월 그리드 (3x4) / 12ヶ月グリッド（3x4） ===
    var grid = document.createElement('div');
    grid.style.display = 'grid';
    grid.style.gridTemplateColumns = 'repeat(4, 1fr)';
    grid.style.gap = '8px';
    grid.style.padding = '0 6px 6px';

    var now = new Date();
    var isThisYear = (state.year === now.getFullYear());

    for (var i = 1; i <= 12; i++) {
      (function (m) {
        var btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = MONTHS_JA[m - 1];
        btn.style.padding = '10px 0';
        btn.style.border = '1px solid #eef2f7';
        btn.style.borderRadius = '10px';
        btn.style.cursor = 'pointer';
        btn.style.background = '#fff';
        btn.style.fontSize = '13px';
        btn.style.transition = 'all .12s ease';

        if (isThisYear && m === (now.getMonth() + 1)) {
          btn.style.borderColor = '#93c5fd'; // 현재 월 / 当月
        }

        btn.addEventListener('mouseenter', function(){ btn.style.background = '#f3f4f6'; });
        btn.addEventListener('mouseleave', function(){ btn.style.background = '#fff'; });

        // 월 선택 → YYYY-MM 설정 후 닫기 / 月選択 → YYYY-MM 設定して閉じる
        btn.addEventListener('click', function () {
          var ym = state.year + '-' + pad2(m);
          state.onPick(ym);
        });

        grid.appendChild(btn);
      })(i);
    }

    pop.appendChild(grid);
  }

  // === 공개 API / 公開API ===
  window.attachJaMonthOnly = function (inputSelector, options) {
    var el = (typeof inputSelector === 'string') ? document.querySelector(inputSelector) : inputSelector;
    if (!el) return;

    var opts = Object.assign({ position: 'below' }, options || {});

    // 현재 열림 여부 / 現在開いているか
    function isOpen() { return !!window.__jaMonthOnlyOpen; }

    // 열기 / 開く
    function openPopup() {
      if (isOpen()) return;            // 이미 열려있으면 무시 / 既に開いていれば何もしない
      closePopup();                    // 혹시 남은 인스턴스 정리 / 念のため掃除

      var rect = el.getBoundingClientRect();
      var pop = createPopup();

      var initYear = (function (v) {
        var m = String(v || '').match(/^(\d{4})-(\d{2})$/);
        return m ? +m[1] : (new Date()).getFullYear();
      })(el.value || el.getAttribute('value'));

      var state = {
        year: initYear,
        onPick: function (ym) {
          el.value = ym;
          el.dispatchEvent(new Event('change'));
          closePopup();
        }
      };

      render(pop, state);
      document.body.appendChild(pop);

      // 위치 / 位置
      var top = window.scrollY + rect.top + el.offsetHeight + 6;
      if (opts.position === 'above') {
        top = window.scrollY + rect.top - pop.offsetHeight - 6;
      }
      var left = window.scrollX + rect.left;
      pop.style.top = top + 'px';
      pop.style.left = left + 'px';

      // 바깥 클릭/ESC 닫기 / 外側クリック・ESCで閉じる
      setTimeout(function () {
        function outside(e) { if (!pop.contains(e.target) && e.target !== el) closePopup(); }
        function onEsc(e)    { if (e.key === 'Escape') closePopup(); }
        document.addEventListener('mousedown', outside, { once: true });
        document.addEventListener('keydown', onEsc, { once: true });
        pop._outside = outside; pop._onEsc = onEsc;
      }, 0);

      window.__jaMonthOnlyOpen = pop;
    }

    // 닫기 / 閉じる
    function closePopup() {
      var pop = window.__jaMonthOnlyOpen;
      if (pop && pop.parentNode) {
        if (pop._outside) document.removeEventListener('mousedown', pop._outside);
        if (pop._onEsc)   document.removeEventListener('keydown', pop._onEsc);
        pop.parentNode.removeChild(pop);
      }
      window.__jaMonthOnlyOpen = null;
    }

    // ==================== 트리거 바인딩 / トリガーのバインド ====================
    // 핵심: click/focus 대신 mousedown/touchstart에서만 토글 (중복 방지)
    // 重要: click/focus の代わりに mousedown/touchstart でトグル（二重発火防止）

    // 마우스 / マウス
    el.addEventListener('mousedown', function (e) {
      e.preventDefault(); // 포커스/클릭 연쇄 차단 / フォーカスとクリックの連鎖を抑止
      if (isOpen()) closePopup(); else { openPopup(); el.focus(); }
    });

    // 터치 / タッチ（モバイル）
    el.addEventListener('touchstart', function (e) {
      e.preventDefault(); // iOS에서 키보드 뜨는 것 방지 / iOS のキーボード出現を抑止
      if (isOpen()) closePopup(); else { openPopup(); try { el.focus({preventScroll:true}); } catch(_) { el.focus(); } }
    }, { passive: false });

    // 키보드 접근성: Enter/Space/ArrowDown = 토글, Escape = 닫기
    // キーボード対応: Enter/Space/ArrowDown=トグル、Escape=閉じる
    el.addEventListener('keydown', function (e) {
      if (e.key === 'Enter' || e.key === ' ' || e.key === 'ArrowDown') {
        e.preventDefault();
        if (isOpen()) closePopup(); else openPopup();
      } else if (e.key === 'Escape') {
        e.preventDefault();
        closePopup();
      }
    });

    // 스크롤/리사이즈 시 닫기 / スクロール・リサイズで閉じる
    window.addEventListener('scroll', closePopup, true);
    window.addEventListener('resize', closePopup, true);

    // 힌트 / ヒント
    el.title = '支給年月を選択（日本語・月のみ）';
    if (!el.placeholder) el.placeholder = 'YYYY-MM';

  };
})();
