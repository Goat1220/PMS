package org.pms.feature.voucher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

/**
 * 급상여 전표 페이지 컨트롤러  
 * 給与・賞与伝票ページのコントローラ  
 * 
 * - JSP(View) 렌더링 전 초기값 설정  
 * - JSP(View) レンダリング前に初期値を設定
 */
@Controller
@RequiredArgsConstructor
public class PayrollVoucherPageController {

    /**
     * 전표처리 페이지 호출  
     * 伝票処理ページの呼び出し
     *
     * @param model JSP에 전달할 데이터 모델  
     *              JSP に渡すデータモデル
     * @return "voucher/voucher" — 전표처리 페이지 경로  
     *         "voucher/voucher" — 伝票処理ページのパス
     */
	@GetMapping("/voucher")
    public String voucherPage(Model model) {
        model.addAttribute("defaultYyyymm", "2025-09"); // 기본 연월 설정 / デフォルト年月の設定
        model.addAttribute("defaultPayType", "SALARY"); // 급상여 종류 기본값 / 給与区分のデフォルト値
        return "voucher/voucher"; // JSP 또는 Thymeleaf 뷰 경로 / JSP または Thymeleaf のビュー・パス
    }
}
