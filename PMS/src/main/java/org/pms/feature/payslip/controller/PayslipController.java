package org.pms.feature.payslip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
/** 급여명세(개인) 옛 URL을 조회 화면으로 리다이렉트 / 旧URLを照会画面へリダイレクト */
public class PayslipController {

    /** seed 기준 기본 연월 / seed基準の既定年月 */
    private static final String DEFAULT_YM = "2025-09";
    /** seed 기준 기본 급여유형 / seed基準の既定支給区分 */
    private static final String DEFAULT_PAYTYPE = "SALARY";

    /**
     * /feature/payslip/view 요청을 /feature/payslip/inquiry 로 넘김
     * - empNo, periodYm 없으면 기본값 사용(empNo=E1001, periodYm=2025-09, payType=SALARY)
     * - 단일월 조회를 위해 fromYm=toYm=periodYm 로 리다이렉트
     */
    @GetMapping("/feature/payslip/view")
    public String redirectToInquiry(
            @RequestParam(required=false) String empNo,      // 사번(옵션) / 社員番号(任意)
            @RequestParam(required=false) String periodYm) { // 적용연월(옵션) / 適用年月(任意)

        // [KO] empNo 미입력 시 seed 사번(E1001) / [JA] 未入力はseed社員(E1001)
        String e  = (empNo == null || empNo.trim().isEmpty()) ? "E1001" : empNo.trim();

        // [KO] periodYm 미입력/비정상 → seed 연월(2025-09), 6자리(202509)도 허용 / [JA] 未入力・不正→2025-09、6桁も許容
        String ym = normalizeYm(periodYm);

        // [KO] 단일월 조회 + payType 함께 전달 / [JA] 単月照会＋payType付与
        return "redirect:/feature/payslip/inquiry"
             + "?empNo=" + e
             + "&fromYm=" + ym
             + "&toYm=" + ym
             + "&payType=" + DEFAULT_PAYTYPE;
    }

    /** 연월 정규화: null/blank→DEFAULT_YM, "YYYYMM"→"YYYY-MM", "YYYY-MM"은 그대로 */
    private static String normalizeYm(String s) {
        if (s == null) return DEFAULT_YM;
        String v = s.trim();
        if (v.isEmpty()) return DEFAULT_YM;
        if (v.matches("^\\d{6}$")) return v.substring(0,4) + "-" + v.substring(4,6);   // 202509 -> 2025-09
        if (v.matches("^\\d{4}-\\d{2}$")) return v;                                     // 2025-09 -> 2025-09
        return DEFAULT_YM; // 그 외는 기본값
    }
}
