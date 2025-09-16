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
 * [KO] 급여명세 조회 화면 컨트롤러
 *  - 상단 조건(empNo/fromYm/toYm/payType/excludeZero)으로 목록 조회
 *  - 선택된 payslipId(selectedId)가 있으면 상세(PayslipSummary)도 함께 모델에 담음
 *  - 뷰: /WEB-INF/views/feature/payslip/inquiry.jsp (가정)
 *
 * [JA] 給与明細(きゅうよめいさい)照会(しょうかい)コントローラ
 *  - 条件(じょうけん)で一覧(いちらん)を取得
 *  - 選択(せんたく)された ID があれば詳細(しょうさい)も取得し Model に入(い)れる
 */
@Controller
@RequiredArgsConstructor
public class PayslipInquiryController {

    // [KO] 목록 조회(필터) 담당 서비스 / [JA] 一覧検索(いちらんけんさく)担当
    private final PayslipInquiryService inquiryService;
    // [KO] 상세 조회(요약+항목) 담당 서비스 / [JA] 詳細(しょうさい)取得担当
    private final PayslipQueryService   queryService;

    @GetMapping("/feature/payslip/inquiry")
    public String inquiry(
            // ====== 조회 조건(상단 필터) ======
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) String fromYm,  // yyyy-MM or 202509 など → normalizeYm()で整形
            @RequestParam(required = false) String toYm,    // 同上
            @RequestParam(required = false) String payType,
            @RequestParam(required = false, defaultValue = "N") String excludeZero, // "Y"=0원 제외 / "N"=포함

            // ====== 목록에서 선택된 행의 상세 보기 ======
            @RequestParam(required = false) Long selectedId,
            Model model
    ) {
        // [KO] empNo 기본값 보정 / [JA] empNo 既定値(きていち)補完(ほかん)
        if (empNo == null || empNo.trim().isEmpty()) empNo = "E0001";

        // [KO] from/to 기본값: 현재 연월 문자열(YearMonth#toString → "yyyy-MM")
        // [JA] 既定値：現在(げんざい)の年月（"yyyy-MM"）
        if (fromYm == null || fromYm.trim().isEmpty()) fromYm = java.time.YearMonth.now().toString();
        if (toYm   == null || toYm.trim().isEmpty())   toYm   = fromYm;

        // [KO] 연월 입력 다양한 형태를 허용하고 일관 포맷(yyyy-MM)으로 정규화
        // [JA] 入力(にゅうりょく)フォーマットを許容(きょよう)し、"yyyy-MM" に正規化(せいきか)
        fromYm = normalizeYm(fromYm);
        toYm   = normalizeYm(toYm);

        // [KO] 목록 데이터 바인딩
        // [JA] 一覧(いちらん)データを Model へ
        model.addAttribute("rows",
                inquiryService.search(empNo, fromYm, toYm, payType, excludeZero));

        // [KO] 선택된 행이 있으면 상세도 로딩하여 오른쪽 패널 등에서 보여주기
        // [JA] 選択(せんたく)済(ず)みなら詳細(しょうさい)を取得して右側(みぎがわ)パネル等(とう)に表示(ひょうじ)
        String empName = null;
        if (selectedId != null) {
            PayslipSummary sel = queryService.getPayslipById(selectedId);
            model.addAttribute("selected", sel);
            if (sel != null) {
                empName = sel.getEmpName(); // [KO] 화면 상단에 사원명 표시용 / [JA] 画面(がめん)見出(みだ)し用
                empNo   = sel.getEmpNo();   // [KO] 선택 상세 기준으로 empNo 동기화 / [JA] empNo 同期(どうき)
            }
        }
        model.addAttribute("empName", empName);

        // [KO] 현재 검색 조건을 그대로 모델에 넣어 폼/링크의 상태를 유지
        // [JA] 検索(けんさく)条件(じょうけん)を Model に格納(かくのう)しフォームの状態(じょうたい)を維持(いじ)
        model.addAttribute("empNo", empNo);
        model.addAttribute("fromYm", fromYm);
        model.addAttribute("toYm", toYm);
        model.addAttribute("payType", payType);
        model.addAttribute("excludeZero", excludeZero);

        // [KO] 드롭다운용 코드들(급여유형 등)
        // [JA] ドロップダウン用(よう)のコード一覧(いちらん)
        model.addAttribute("payTypeCodes", inquiryService.getPayTypeCodes());

        // [KO] 타일즈/뷰리졸버 기준 논리뷰명 / [JA] 論理(ろんり)ビュー名
        return "feature/payslip/inquiry";
    }

    /**
     * [KO] 연월 문자열 정규화 유틸
     *  - 허용: "202509" → "2025-09"
     *  - 허용: "2025-09" → "2025-09"
     *  - 그 외 입력: 현재 연월 반환
     *
     * [JA] 年月(ねんげつ)文字列(もじれつ)の正規化(せいきか)
     *  - 受入(うけい)れ: "202509" → "2025-09"
     *  - 受入: "2025-09" → そのまま
     *  - それ以外：現在(げんざい)の年月
     */
    private String normalizeYm(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.matches("^\\d{6}$")) return s.substring(0,4) + "-" + s.substring(4,6); // 202509 -> 2025-09
        if (s.matches("^\\d{4}-\\d{2}$")) return s;
        return java.time.YearMonth.now().toString();
    }
}



