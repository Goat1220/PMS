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
    @GetMapping("/summary")
    public List<PayrollSummaryRow> summary(@RequestParam String yyyymm,
                                           @RequestParam(required=false) String payType,
                                           @RequestParam(required=false) String deptCode,
                                           @RequestParam(required=false) String empNo){
        return service.getSummary(yyyymm, payType, deptCode, empNo);
    }

    @GetMapping("/items")
    public List<EarningItemRow> items(@RequestParam String empNo,
                                      @RequestParam String yyyymm,
                                      @RequestParam(required=false) String payType){
        return service.getItems(empNo, yyyymm, payType);
    }

    @GetMapping("/deductions")
    public List<DeductionRow> deductions(@RequestParam String empNo,
                                         @RequestParam String yyyymm,
                                         @RequestParam(required=false) String payType){
        return service.getDeductions(empNo, yyyymm, payType);
    }

    // ========== 액션 ==========
    @PostMapping("/process")
    public SimpleResult process(@RequestBody BatchEmpRequest req){
        return service.processPayroll(req);
    }

    @PostMapping("/recalc-taxes")
    public SimpleResult recalcTaxes(@RequestBody BatchEmpRequest req){
        return service.recalcTaxes(req);
    }

    @PostMapping("/apply-yrt")
    public SimpleResult applyYrt(@RequestBody BatchEmpRequest req){
        return service.applyYrt(req);
    }

    @PostMapping("/confirm")
    public SimpleResult confirm(@RequestBody BatchEmpRequest req){
        return service.confirm(req);
    }
}
