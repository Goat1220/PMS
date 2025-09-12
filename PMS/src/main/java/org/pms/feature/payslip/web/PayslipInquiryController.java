package org.pms.feature.payslip.web;

import lombok.RequiredArgsConstructor;
import org.pms.feature.payslip.application.PayslipInquiryService;
import org.pms.feature.payslip.application.PayslipQueryService;
import org.pms.feature.payslip.domain.PayslipSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PayslipInquiryController {

    private final PayslipInquiryService inquiryService;
    private final PayslipQueryService   queryService;

    @GetMapping("/feature/payslip/inquiry")
    public String inquiry(
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) String fromYm,
            @RequestParam(required = false) String toYm,
            @RequestParam(required = false) String payType,
            // 체크박스 미체크 시 파라미터가 아예 안 오므로 "N" 기본값
            @RequestParam(required = false, defaultValue = "N") String excludeZero,
            // 선택행 없을 수 있으니 래퍼(Long)
            @RequestParam(required = false) Long selectedId,
            Model model
    ) {
        if (empNo == null || empNo.trim().isEmpty()) empNo = "E0001";
        if (fromYm == null || fromYm.trim().isEmpty()) fromYm = java.time.YearMonth.now().toString();
        if (toYm   == null || toYm.trim().isEmpty())   toYm   = fromYm;

        fromYm = normalizeYm(fromYm);
        toYm   = normalizeYm(toYm);

        // 좌측 목록
        model.addAttribute("rows",
                inquiryService.search(empNo, fromYm, toYm, payType, excludeZero));

        // 우측 상세 + 상단 사원 이름/사번 표시
        String empName = null;
        if (selectedId != null) {
            PayslipSummary sel = queryService.getPayslipById(selectedId);
            model.addAttribute("selected", sel);
            if (sel != null) {
                empName = sel.getEmpName(); // 이름
                empNo   = sel.getEmpNo();   // 사번(동기화)
            }
        }
        model.addAttribute("empName", empName); // null이면 JSP에서 빈칸

        // 조회조건 echo-back
        model.addAttribute("empNo", empNo);
        model.addAttribute("fromYm", fromYm);
        model.addAttribute("toYm", toYm);
        model.addAttribute("payType", payType);
        model.addAttribute("excludeZero", excludeZero);

        // ⬇️ 급상여종류 드롭다운 데이터
        model.addAttribute("payTypeCodes", inquiryService.getPayTypeCodes());

        return "feature/payslip/inquiry";
    }

    private String normalizeYm(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.matches("^\\d{6}$")) return s.substring(0,4) + "-" + s.substring(4,6); // 202509 -> 2025-09
        if (s.matches("^\\d{4}-\\d{2}$")) return s;
        return java.time.YearMonth.now().toString();
    }
}

