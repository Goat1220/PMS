package org.pms.feature.runpayroll.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import org.pms.feature.runpayroll.domain.*;
import org.pms.feature.runpayroll.service.RunPayrollService;

@Controller
@RequestMapping(value = "/runpayroll", method = RequestMethod.GET)
public class RunPayrollController {

    @Autowired
    private RunPayrollService service;

    /** 화면 진입 */
	@GetMapping("/list")
    public String view(Model model) {
        model.addAttribute("defaultYyyymm", "2018-08");
        model.addAttribute("defaultPayType", "SALARY");
        return "runpayroll/list";
    }

    /** 메인 그리드 */
    @GetMapping("/api/summary")
    @ResponseBody
    public List<PayrollRow> summary(@RequestParam String yyyymm,
                                    @RequestParam(required=false) String payType,
                                    @RequestParam(required=false) String deptCode,
                                    @RequestParam(required=false) String empNo) {
        PayrollSearchParam p = new PayrollSearchParam();
        p.setYyyymm(yyyymm);
        p.setPayType(payType);
        p.setDeptCode(deptCode);
        p.setEmpNo(empNo);
        return service.getPayrollSummary(p);
    }

    /** 지급 항목(우측 패널) */
    @GetMapping("/api/items")
    @ResponseBody
    public List<PayslipItemRow> items(@RequestParam String empNo,
                                      @RequestParam String yyyymm,
                                      @RequestParam(required=false) String payType) {
        return service.getPayslipItems(empNo, yyyymm, payType);
    }

    /** 공제 항목(우측 패널) */
    @GetMapping("/api/deductions")
    @ResponseBody
    public List<PayslipDeductionRow> deductions(@RequestParam String empNo,
                                                @RequestParam String yyyymm,
                                                @RequestParam(required=false) String payType) {
        return service.getPayslipDeductions(empNo, yyyymm, payType);
    }
}
