package org.pms.feature.runpayroll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.pms.feature.runpayroll.domain.*;
import org.pms.feature.runpayroll.service.RunPayrollService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/runpayroll/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RunPayrollController {

    private final RunPayrollService service;

    // ========== 조회 ==========
    // ========== 照会 ==========
    @GetMapping("/summary")
    public List<PayrollSummaryRow> summary(@RequestParam String yyyymm,
                                           @RequestParam(required=false) String payType,
                                           @RequestParam(required=false) String deptCode,
                                           @RequestParam(required=false) String empNo){
        // 급상여 요약 조회
        // 給与サマリーの取得
        return service.getSummary(yyyymm, payType, deptCode, empNo);
    }

    @GetMapping("/items")
    public List<EarningItemRow> items(@RequestParam String empNo,
                                      @RequestParam String yyyymm,
                                      @RequestParam(required=false) String payType){
        // 지급항목 조회
        // 支給項目の取得
        return service.getItems(empNo, yyyymm, payType);
    }

    @GetMapping("/deductions")
    public List<DeductionRow> deductions(@RequestParam String empNo,
                                         @RequestParam String yyyymm,
                                         @RequestParam(required=false) String payType){
        // 공제항목 조회
        // 控除項目の取得
        return service.getDeductions(empNo, yyyymm, payType);
    }

    // ========== 액션 ==========
    // ========== アクション ==========
    @PostMapping("/process")
    public SimpleResult process(@RequestBody BatchEmpRequest req){
        // 급상여 처리 실행
        // 給与処理の実行
        return service.processPayroll(req);
    }

    @PostMapping("/recalc-taxes")
    public SimpleResult recalcTaxes(@RequestBody BatchEmpRequest req){
        // 세금 재처리
        // 税金の再処理
        return service.recalcTaxes(req);
    }

    @PostMapping("/apply-yrt")
    public SimpleResult applyYrt(@RequestBody BatchEmpRequest req){
        // 연말정산(YRT) 반영
        // 年末調整（YRT）の反映
        return service.applyYrt(req);
    }

    @PostMapping("/confirm")
    public SimpleResult confirm(@RequestBody BatchEmpRequest req){
        // 확정 처리
        // 確定処理
        return service.confirm(req);
    }
    
    @PostMapping("/unconfirm")
    public SimpleResult unconfirm(
            @RequestParam String yyyymm,
            @RequestParam String payType,
            @RequestBody List<String> empNos) {
        // 확정 해제
        // 確定解除
        return service.unconfirmPayslips(yyyymm, payType, empNos);
    }
}
