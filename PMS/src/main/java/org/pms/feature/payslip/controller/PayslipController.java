package org.pms.feature.payslip.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
/** 급여명세(개인) 옛 URL을 조회 화면으로 리다이렉트 / 旧URLを照会画面へリダイレクト */
public class PayslipController {

    /** 적용연월 포맷터(yyyy-MM) / 適用年月フォーマッタ(yyyy-MM) */
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * /feature/payslip/view 요청을 /feature/payslip/inquiry 로 넘김
     * - empNo, periodYm 없으면 기본값 사용(empNo=E0001, periodYm=本年月)
     * - 단일월 조회를 위해 fromYm=toYm=periodYm 로 리다이렉트
     */
    @GetMapping("/feature/payslip/view")
    public String redirectToInquiry(
            @RequestParam(required=false) String empNo,      // 사번(옵션) / 社員番号(任意)
            @RequestParam(required=false) String periodYm) { // 적용연월(옵션) / 適用年月(任意)

        // empNo 미입력 시 기본값 E0001 / 未入力は E0001 に置換
        String e  = (empNo == null || empNo.trim().isEmpty()) ? "E0001" : empNo.trim();

        // periodYm 미입력 시 현재 연월 / 未入力は当月（yyyy-MM）
        String ym = (periodYm == null || periodYm.trim().isEmpty())
                ? YearMonth.now().format(YM)
                : periodYm.trim();

        // 단일월 조회로 통일 / 単月照会に統一
        return "redirect:/feature/payslip/inquiry?empNo=" + e + "&fromYm=" + ym + "&toYm=" + ym;
    }
}

