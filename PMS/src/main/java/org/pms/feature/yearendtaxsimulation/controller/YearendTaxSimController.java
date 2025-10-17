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
@RequiredArgsConstructor
@Slf4j
public class YearendTaxSimController {

    private final YearendTaxSimQueryService query;     // 조회 전용 서비스 / 参照用サービス
    private final YearendTaxSimCommandService command; // 실행·삭제 등 변경 서비스 / 実行・削除サービス

    /** 기본 진입 시 /view로 이동 / ベースパスに来たら /viewへリダイレクト */
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/feature/yearend-tax-simulation/view";
    }

    /** 매핑 확인용 핑 테스트 / 確認用ピンテスト */
    @GetMapping("/ping") @ResponseBody
    public String ping() { return "pong"; }

    /**
     * 초기 화면 로드 (기본 연도: 전년도)
     * 初期画面ロード（既定年：前年）
     */
    @GetMapping("/view")
    public String view(@RequestParam(required=false) Integer baseYear,
                       @RequestParam(required=false) String empId,
                       Model model) {

        int defaultYear = Year.now().minusYears(1).getValue(); // 작년 기본값
        int y = (baseYear == null) ? defaultYear : baseYear;
        String e = (empId == null ? "" : empId.trim());

        model.addAttribute("baseYear", y);
        model.addAttribute("empId", e);
        model.addAttribute("bizPlace", "本社");

        // 최종 탭 데이터 (확정건 우선, 없으면 미확정건 fallback)
        List<SimItemRow> finalList = (e.isEmpty())
                ? java.util.Collections.emptyList()
                : query.loadFinalGrid(e, y);
        model.addAttribute("finalList", finalList);

        // 최신 헤더 정보
        SimHeaderMini simHead = (e.isEmpty()) ? null : query.latestHeader(e, y, false);
        model.addAttribute("simHeader", simHead);

        // 세액적용결과
        String taxApply = (e.isEmpty()) ? "未判定" : query.computeTaxApplyResult(e, y, null);
        model.addAttribute("taxApplyResult", taxApply);

        return "feature/yearendtaxsimulation/view";
    }

    /* ===================================================== */
    /* =============== Ajax APIs =========================== */
    /* ===================================================== */

    /** ① 최종 탭(확정건) 조회 / 最終タブ（確定データ）取得 */
    @GetMapping(value = "/api/final", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<SimItemRow> getFinal(@RequestParam String empId,
                                     @RequestParam Integer baseYear) {
        return query.loadFinalGrid(empId, baseYear);
    }

    /** ② 시뮬레이션 탭(미확정건) 조회 / シミュレーションタブ（未確定データ）取得 */
    @GetMapping(value = "/api/sim", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<SimItemRow> getSim(@RequestParam String empId,
                                   @RequestParam Integer baseYear,
                                   @RequestParam(required=false) Long yrtId){
        return query.loadSimGrid(empId, baseYear, yrtId);
    }

    
    /** ③ 시뮬레이션 실행 / シミュレーション実行 */
    @PostMapping("/api/simulate")
    public ResponseEntity<?> simulate(@RequestParam String empId,
                                      @RequestParam Integer baseYear,
                                      @RequestParam(required=false) Long policyId,
                                      @RequestParam(required=false) String runLabel,
                                      @RequestParam(defaultValue="false") boolean overwrite) {

        // ⚠️ 시뮬레이션 시 실제 DB에 반영되지 않도록 차단
        log.info("[simulate] 연말정산 시뮬레이션 호출은 무시됩니다. DB에는 아무 변화 없음。");
        
        // 필요하다면 아래처럼 임시 응답도 가능
        return ResponseEntity.ok("SIMULATION_DISABLED");

        // 원래 코드 (임시로 주석처리)
        // Long yrtId = command.run(empId, baseYear, policyId, runLabel, overwrite);
        // return ResponseEntity.ok(yrtId);
    }

    /** ⑤ 납부 특례(분납) 계산 / 納付特例（分納）計算 */
    @PostMapping(value = "/api/installment", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public InstallmentResponse installment(@RequestParam Long yrtId,
                                           @RequestParam Integer months,
                                           @RequestParam String startMonth) {
        return command.installment(yrtId, months, startMonth);
    }

    /** ⑥ 세액적용결과 판정 조회 / 税額適用結果判定取得 */
    @GetMapping(value = "/api/tax-apply-result", produces = "text/plain; charset=UTF-8")
    @ResponseBody
    public String taxApplyResult(@RequestParam String empId,
                                 @RequestParam Integer baseYear,
                                 @RequestParam(required=false) String yrtIdRaw) {
        Long yrtId = null;
        try {
            if (yrtIdRaw != null && !yrtIdRaw.isEmpty()) {
                yrtId = Long.valueOf(yrtIdRaw.replaceAll("\\D", ""));
            }
        } catch (Exception e) {
            log.warn("yrtId 파싱 실패: {}", yrtIdRaw);
        }
        return query.computeTaxApplyResult(empId, baseYear, yrtId);
    }
}
