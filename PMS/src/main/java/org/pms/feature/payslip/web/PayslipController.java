package org.pms.feature.payslip.web;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PayslipController {
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    // 예전 북마크/메뉴가 /view 여도 깨지지 않게 inquiry로 넘김
    @GetMapping("/feature/payslip/view")
    public String redirectToInquiry(@RequestParam(required=false) String empNo,
                                    @RequestParam(required=false) String periodYm) {
        String e  = (empNo == null || empNo.trim().isEmpty()) ? "E0001" : empNo.trim();
        String ym = (periodYm == null || periodYm.trim().isEmpty())
                ? YearMonth.now().format(YM) : periodYm.trim();
        return "redirect:/feature/payslip/inquiry?empNo="+e+"&fromYm="+ym+"&toYm="+ym;
    }
}
