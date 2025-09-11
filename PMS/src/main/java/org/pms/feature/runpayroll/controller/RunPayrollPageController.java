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

    /**
     * 급상여 처리 메인 페이지
     * - JSP 경로 규칙: /WEB-INF/view/runpayroll/runpayroll.jsp  (예시)
     * - ViewResolver 설정 예:
     *   spring.mvc.view.prefix=/WEB-INF/view/
     *   spring.mvc.view.suffix=.jsp
     */
    @GetMapping({"/runpayroll", "/runpayroll/"})
    public String runPayrollPage(Model model) {
        // 기본값: 이번달, SALARY
        String defaultYyyymm = YearMonth.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String defaultPayType = "SALARY";

        model.addAttribute("defaultYyyymm", defaultYyyymm);
        model.addAttribute("defaultPayType", defaultPayType);

        // 필요시 화면 좌측 검색용 기본 부서코드/사번 등도 추가 가능
        // model.addAttribute("defaultDeptCode", "");
        // model.addAttribute("defaultEmpNo", "");

        // /WEB-INF/view/runpayroll/runpayroll.jsp
        return "runpayroll/list";
    }
}
