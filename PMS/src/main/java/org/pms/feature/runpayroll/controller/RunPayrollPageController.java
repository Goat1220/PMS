package org.pms.feature.runpayroll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class RunPayrollPageController {

    @GetMapping({"/runpayroll", "/runpayroll/"})
    public String runPayrollPage(Model model) {
        // 기본값: 이번달, SALARY
        String defaultYyyymm = YearMonth.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String defaultPayType = "SALARY";

        model.addAttribute("defaultYyyymm", defaultYyyymm);
        model.addAttribute("defaultPayType", defaultPayType);

        return "runpayroll/list";
    }
}
