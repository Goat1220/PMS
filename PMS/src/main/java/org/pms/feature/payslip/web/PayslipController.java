package org.pms.feature.payslip.web;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
/**
 * 급여명세(개인) 화면용 뷰 엔드포인트를 과거 호환을 위해 조회(inquiry)로 리다이렉트하는 컨트롤러
 * － 한국어 : 예전 메뉴/북마크가 /view 를 가리켜도 실제 조회는 /inquiry 로 통일
 * － 日本語 : 旧ブックマークやメニューが /view を指していても、実際の照会は /inquiry に統一するためのリダイレクト用コントローラ
 */
public class PayslipController {

    /** 
     * 적용연월 포맷터(예: 2025-09)
     * － 한국어 : 기본 yyyy-MM 형식으로 현재 연월 또는 전달받은 연월을 문자열로 변환
     * － 日本語 : yyyy-MM 形式で当月または受け取った年月を文字列化するフォーマッタ
     */
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 과거 URL(/feature/payslip/view) 요청을 실제 조회 URL(/feature/payslip/inquiry)로 넘김
     * － 한국어 :
     *   - empNo/periodYm 파라미터가 비어 있으면 안전한 기본값(empNo=E0001, periodYm=오늘의 연월)으로 대체
     *   - fromYm/toYm 을 동일한 값으로 지정하여 단일월 조회가 되도록 리다이렉트
     * － 日本語 :
     *   - empNo / periodYm が未指定・空白なら安全なデフォルト(empNo=E0001, periodYm=本日の年月)に置換
     *   - fromYm / toYm を同一値にして単月の照会となるようにリダイレクト
     */
    @GetMapping("/feature/payslip/view")
    public String redirectToInquiry(
            @RequestParam(required=false) String empNo,      // 한국어: 사번(옵션) / 日本語: 社員番号(任意)
            @RequestParam(required=false) String periodYm) { // 한국어: 적용연월(옵션, yyyy-MM 권장) / 日本語: 適用年月(任意, yyyy-MM 推奨)

        // 한국어: empNo 미입력/공백 → 기본값 "E0001"
        // 日本語: empNo 未入力/空白 → デフォルト "E0001"
        String e  = (empNo == null || empNo.trim().isEmpty()) ? "E0001" : empNo.trim();

        // 한국어: periodYm 미입력/공백 → 현재 연월(yyyy-MM)로 대체
        // 日本語: periodYm 未入力/空白 → 本年月(yyyy-MM)に置換
        String ym = (periodYm == null || periodYm.trim().isEmpty())
                ? YearMonth.now().format(YM)
                : periodYm.trim();

        // 한국어: 단일월 조회를 위해 fromYm=toYm=ym 으로 리다이렉트
        // 日本語: 単月照会になるよう fromYm=toYm=ym でリダイレクト
        return "redirect:/feature/payslip/inquiry?empNo=" + e + "&fromYm=" + ym + "&toYm=" + ym;
    }
}
