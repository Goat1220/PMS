package org.pms.feature.yearendtaxsimulation.controller;

import java.time.Year;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.pms.feature.yearendtaxsimulation.domain.*;
import org.pms.feature.yearendtaxsimulation.service.*;

/** 화면·API 컨트롤러 / 画面・APIコントローラ */
@Controller
@RequestMapping("/feature/yearend-tax-simulation")
@RequiredArgsConstructor // 생성자 주입 자동 / コンストラクタ注入を自動生成
@Slf4j // 로그 사용 가능 / ログ出力が可能
public class YearendTaxSimController {

    // 조회 전용 서비스 / 参照用サービス
    private final YearendTaxSimQueryService query;
    // 실행/삭제 등 변경 서비스 / 実行・削除などの変更サービス
    private final YearendTaxSimCommandService command;

    /** 베이스 경로 접근 시 /view로 이동 / ベースパスに来たら /view へリダイレクト */
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/feature/yearend-tax-simulation/view";
    }

    /** 간이 진단: 매핑 확인 / 簡易診断：マッピング確認 */
    @GetMapping("/ping") @ResponseBody
    public String ping() { return "pong"; }

    /**
     * 초기 화면(최종 탭). 기본연도 = 현재연도 - 1
     * 初期画面（最終タブ）。既定年 = 現在年 - 1
     */
    @GetMapping("/view")
    public String view(@RequestParam(required=false) Integer baseYear,
                       @RequestParam(required=false) String empId,
                       Model model) {
        int defaultYear = Year.now().minusYears(1).getValue(); // 기본연도(작년) / 既定年（前年）
        int y = (baseYear == null) ? defaultYear : baseYear;    // 사용할 연도 / 使用年
        String e = (empId == null ? "" : empId.trim());         // 사원ID 정리 / 社員IDの整形

        model.addAttribute("baseYear", y);     // 화면 바인딩: 기준연도 / 画面バインド：基準年
        model.addAttribute("empId", e);        // 화면 바인딩: 사원ID / 画面バインド：社員ID
        model.addAttribute("bizPlace", "본사"); // 사업장 기본값 / 事業所の既定値

        // 사원ID 없으면 빈 목록, 있으면 최종 그리드 조회 / 社員IDなし→空、あり→最終グリッド取得
        List<SimItemRow> finalList = (e.isEmpty())
                ? java.util.Collections.emptyList()
                : query.loadFinalGrid(e, y);
        model.addAttribute("finalList", finalList);

        // 최신 헤더 정보 / 最新ヘッダ情報
        SimHeaderMini simHead = (e.isEmpty()) ? null : query.latestHeader(e, y, false);
        model.addAttribute("simHeader", simHead);

        // 세금적용결과: 사원 없으면 '미판정' / 税適用結果：社員なしは「未判定」
        String taxApply = (e.isEmpty()) ? "미판정" : query.computeTaxApplyResult(e, y, null);
        model.addAttribute("taxApplyResult", taxApply);

        return "feature/yearendtaxsimulation/view"; // 뷰 경로 / ビューのパス
    }

    /* ===== Ajax APIs ===== */
    // 화면 비동기 요청 처리 / 画面の非同期要求を処理

    /** 산출근거(시뮬 탭) 조회 / 算出根拠（シミュレーションタブ）取得 */
    @GetMapping(value = "/api/sim", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<SimItemRow> getSim(@RequestParam String empId,
                                   @RequestParam Integer baseYear,
                                   @RequestParam(required=false) Long yrtId){
        return query.loadSimGrid(empId, baseYear, yrtId);
    }

    /**
     * 시뮬레이션 실행 / シミュレーション実行
     */
    @PostMapping("/api/simulate")
    public ResponseEntity<?> simulate(@RequestParam String empId,
                                      @RequestParam Integer baseYear,
                                      @RequestParam(required=false) Long policyId,
                                      @RequestParam(required=false) String runLabel,
                                      @RequestParam(defaultValue="false") boolean overwrite) {
        Long yrtId = command.run(empId, baseYear, policyId, runLabel, overwrite);
        return ResponseEntity.ok(yrtId);
    }

    /** 시뮬레이션 결과 삭제 / シミュレーション結果の削除 */
    @DeleteMapping(value = "/api/simulate", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> delete(@RequestParam Long yrtId) {
        boolean ok = command.delete(yrtId);
        return ok 
            ? ResponseEntity.ok("삭제 완료")  
            : ResponseEntity.badRequest().body("확정건은 삭제 불가");
    }


    /** 납부 특례(분납) 시뮬레이션 / 納付特例（分納）シミュレーション */
    @PostMapping(value = "/api/installment", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public InstallmentResponse installment(@RequestParam Long yrtId,
                                           @RequestParam Integer months,
                                           @RequestParam String startMonth) {
        return command.installment(yrtId, months, startMonth);
    }


    /** 세금적용결과 판정 조회 / 税適用結果の判定取得 */
    @GetMapping(value = "/api/tax-apply-result", produces = "text/plain; charset=UTF-8")
    @ResponseBody
    public String taxApplyResult(@RequestParam String empId,
                                 @RequestParam Integer baseYear,
                                 @RequestParam(required=false) String yrtIdRaw) {
        Long yrtId = null;
        try {
            if (yrtIdRaw != null && !yrtIdRaw.isEmpty()) {
                // "<Long>68</Long>" → 68 로 정제 / XML形式の文字列から数値を抽出
                yrtId = Long.valueOf(yrtIdRaw.replaceAll("\\D", ""));
            }
        } catch (Exception e) {
            log.warn("yrtId 파싱 실패: {}", yrtIdRaw);
        }

        return query.computeTaxApplyResult(empId, baseYear, yrtId);
    }
}
