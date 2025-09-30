package org.pms.feature.payslip.controller;

import lombok.RequiredArgsConstructor;

import org.pms.feature.payslip.domain.PayslipSummary;
import org.pms.feature.payslip.service.PayslipInquiryService;
import org.pms.feature.payslip.service.PayslipQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * 급여명세 조회 화면 컨트롤러 / 給与明細照会コントローラ
 * - 상단 조건(empNo/fromYm/toYm/payType/excludeZero)으로 목록 조회 / 条件で一覧取得
 * - 선택된 payslipId가 있으면 상세(PayslipSummary)도 모델에 포함 / 選択IDがあれば詳細もModelに格納
 * - 뷰 경로(가정): /WEB-INF/views/feature/payslip/inquiry.jsp / ビュー仮: 上記
 */
@Controller
@RequiredArgsConstructor
public class PayslipInquiryController {

    // 목록 조회(필터) 서비스 / 一覧検索サービス
    private final PayslipInquiryService inquiryService;
    // 상세 조회(요약+항목) 서비스 / 詳細取得サービス
    private final PayslipQueryService   queryService;

    @GetMapping("/feature/payslip/inquiry")
    public String inquiry(
            // ====== 조회 조건(상단 필터) / 照会条件（上部フィルタ） ======
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) String fromYm,  
            @RequestParam(required = false) String toYm,    
            @RequestParam(required = false) String payType,
            @RequestParam(required = false, defaultValue = "N") String excludeZero,

            // ====== 목록에서 선택된 행의 상세 / 一覧の選択行の詳細 ======
            @RequestParam(required = false) Long selectedId,
            Model model
    ) {
        // [KO] empNo 기본값: seed 사번(E1001) / [JA] empNo既定値：seed社員(E1001)
        if (empNo == null || empNo.trim().isEmpty()) empNo = "E1001";

        // [KO] from/to 기본값: seed 기준 "2025-09" / [JA] from/to既定値：seed基準"2025-09"
        if (fromYm == null || fromYm.trim().isEmpty()) fromYm = "2025-09";
        if (toYm   == null || toYm.trim().isEmpty())   toYm   = "2025-09";

        // [KO] payType: 첫 진입은 SALARY, 조회 버튼 눌렀을 땐 빈값("")도 허용 / 
        // [JA] payType：初回はSALARY、検索ボタン押下時は空文字も許容
        if (payType == null) payType = "SALARY"; // null → SALARY
        // 단, ""(빈 문자열)은 그대로 두어 전체 조회 가능하게 한다.

        // 다양한 연월 입력 허용 → "yyyy-MM"로 정규화 / 多様な入力を許容→"yyyy-MM"に正規化
        fromYm = normalizeYm(fromYm);
        toYm   = normalizeYm(toYm);

        // 목록 데이터 바인딩 / 一覧データのバインド
        model.addAttribute("rows",
                inquiryService.search(empNo, fromYm, toYm, payType, excludeZero));

        // 선택된 행이 있으면 상세도 로딩 / 選択行があれば詳細も取得
        String empName = null;
        if (selectedId != null) {
            PayslipSummary sel = queryService.getPayslipById(selectedId);
            model.addAttribute("selected", sel);
            if (sel != null) {
                empName = sel.getEmpName(); // 화면 상단 표시용 / 画面上部表示用
                empNo   = sel.getEmpNo();   // empNo 동기화 / 同期
            }
        }
        model.addAttribute("empName", empName);

        // 현재 검색 조건을 모델에 유지 / 検索条件をModelに保持
        model.addAttribute("empNo", empNo);
        model.addAttribute("fromYm", fromYm);
        model.addAttribute("toYm", toYm);
        model.addAttribute("payType", payType);
        model.addAttribute("excludeZero", excludeZero);

        // 드롭다운용 코드(급여유형 등) / ドロップダウン用コード
        model.addAttribute("payTypeCodes", inquiryService.getPayTypeCodes());

        // 논리 뷰명 반환 / 論理ビュー名
        return "feature/payslip/inquiry";
    }

    /** 연월 문자열 정규화 / 年月文字列の正規化 */
    private String normalizeYm(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.matches("^\\d{6}$")) return s.substring(0,4) + "-" + s.substring(4,6);
        if (s.matches("^\\d{4}-\\d{2}$")) return s;
        return java.time.YearMonth.now().toString();
    }
}

