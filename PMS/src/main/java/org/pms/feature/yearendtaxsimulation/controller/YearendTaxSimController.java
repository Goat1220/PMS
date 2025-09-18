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

/** KO: 화면/API 컨트롤러 / JP: 画面・APIコントローラ */
@Controller
@RequestMapping("/feature/yearend-tax-simulation")
@RequiredArgsConstructor
@Slf4j
public class YearendTaxSimController {

    private final YearendTaxSimQueryService query;
    private final YearendTaxSimCommandService command;

    /** 베이스 경로로 들어오면 /view로 리다이렉트 */
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/feature/yearend-tax-simulation/view";
    }

    /** 진단용: 매핑/스캔 확인 */
    @GetMapping("/ping") @ResponseBody
    public String ping() { return "pong"; }

    /** 화면 진입: "최종" 탭 활성 */
    @GetMapping("/view")
    public String view(@RequestParam(required=false) Integer baseYear,
                       @RequestParam(required=false) String empId,
                       Model model) {
        int defaultYear = Year.now().minusYears(1).getValue();
        int y = (baseYear == null) ? defaultYear : baseYear;
        String e = (empId == null ? "" : empId.trim());

        model.addAttribute("baseYear", y);
        model.addAttribute("empId", e);
        model.addAttribute("bizPlace", "본사");

        List<SimItemRow> finalList = (e.isEmpty())
                ? java.util.Collections.emptyList()
                : query.loadFinalGrid(e, y);
        model.addAttribute("finalList", finalList);

        SimHeaderMini simHead = (e.isEmpty()) ? null : query.latestHeader(e, y, false);
        model.addAttribute("simHeader", simHead);

        String taxApply = (e.isEmpty()) ? "미판정" : query.computeTaxApplyResult(e, y, null);
        model.addAttribute("taxApplyResult", taxApply);

        return "feature/yearendtaxsimulation/view";
    }

    /* ===== Ajax APIs ===== */

    // 산출근거(시뮬 탭 조회)
    @GetMapping("/api/sim") @ResponseBody
    public List<SimItemRow> getSim(@RequestParam String empId,
                                   @RequestParam Integer baseYear,
                                   @RequestParam(required=false) Long yrtId){
        return query.loadSimGrid(empId, baseYear, yrtId);
    }

    // 시뮬레이션 처리
    @PostMapping("/api/simulate")
    public ResponseEntity<?> simulate(@RequestParam String empId,
                                      @RequestParam Integer baseYear,
                                      @RequestParam(required=false) Long policyId,
                                      @RequestParam(required=false) String runLabel,
                                      @RequestParam(defaultValue="false") boolean overwrite) {
        Long yrtId = command.run(empId, baseYear, policyId, runLabel, overwrite);
        return ResponseEntity.ok(yrtId);
    }

    // 시뮬레이션 결과 삭제
    @DeleteMapping("/api/simulate")
    public ResponseEntity<?> delete(@RequestParam Long yrtId){
        boolean ok = command.delete(yrtId);
        return ok ? ResponseEntity.ok().build()
                  : ResponseEntity.badRequest().body("확정건은 삭제 불가");
    }

    // 납부 특례(분납) 시뮬레이션
    @PostMapping("/api/installment-simulate") @ResponseBody
    public InstallmentResponse installment(@RequestParam Long yrtId,
                                           @RequestParam Integer months,
                                           @RequestParam String startMonth){
        return command.installment(yrtId, months, startMonth);
    }

    // 세금적용결과(표준/특별) 판정
    @GetMapping("/api/tax-apply-result") @ResponseBody
    public String taxApplyResult(@RequestParam String empId,
                                 @RequestParam Integer baseYear,
                                 @RequestParam(required=false) Long yrtId) {
        return query.computeTaxApplyResult(empId, baseYear, yrtId);
    }
}

