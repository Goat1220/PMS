package org.pms.feature.voucher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PayrollVoucherPageController {


	@GetMapping("/voucher")
    public String voucherPage(Model model) {
        // 초기값 셋팅
        model.addAttribute("defaultYyyymm", "2025-09"); // 예시
        model.addAttribute("defaultPayType", "SALARY");
        return "voucher/voucher"; // JSP 또는 Thymeleaf 뷰 경로
    }
}
