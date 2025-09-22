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
            @RequestParam(required = false) String fromYm,  // yyyy-MM 또는 202509 → normalizeYm() / yyyy-MMや202509 → 正規化
            @RequestParam(required = false) String toYm,    // 동일 / 同様
            @RequestParam(required = false) String payType,
            @RequestParam(required = false, defaultValue = "N") String excludeZero, // "Y"=0원 제외 / "N"=포함

            // ====== 목록에서 선택된 행의 상세 / 一覧の選択行の詳細 ======
            @RequestParam(required = false) Long selectedId,
            Model model
    ) {
        // empNo 기본값 보정(E0001) / empNo既定値補完
        if (empNo == null || empNo.trim().isEmpty()) empNo = "E0001";

        // from/to 기본값: 현재 연월("yyyy-MM") / 既定値：現在の年月
        if (fromYm == null || fromYm.trim().isEmpty()) fromYm = java.time.YearMonth.now().toString();
        if (toYm   == null || toYm.trim().isEmpty())   toYm   = fromYm;

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

    /**
     * 연월 문자열 정규화 유틸 / 年月文字列の正規化ユーティリティ
     * - 허용: "202509" → "2025-09"
     * - 허용: "2025-09" → "2025-09"
     * - 그 외: 현재 연월 반환 / それ以外：現在の年月
     */
    private String normalizeYm(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.matches("^\\d{6}$")) return s.substring(0,4) + "-" + s.substring(4,6); // 202509 -> 2025-09
        if (s.matches("^\\d{4}-\\d{2}$")) return s;
        return java.time.YearMonth.now().toString();
    }
}
