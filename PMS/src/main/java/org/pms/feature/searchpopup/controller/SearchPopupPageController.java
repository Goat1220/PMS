// org.pms.feature.searchpopup.controller.SearchPopupPageController
package org.pms.feature.searchpopup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * [검색 팝업 페이지 컨트롤러]  
 * - 사원 / 부서 검색 팝업 화면 이동 담당  
 * - API가 아닌 JSP 페이지 경로를 반환함  
 * - 検索ポップアップ画面用コントローラ（JSP画面遷移）
 */
@Controller
@RequestMapping("/popups") // 기본 경로 / ベースパス
public class SearchPopupPageController {

    /** 
     * 사원 검색 팝업 페이지 / 社員検索ポップアップ画面  
     * - JSP: /WEB-INF/view/popups/employees.jsp  
     */
    @GetMapping("/employees")
    public String emp() {
        return "popups/employees"; // 뷰 이름 반환 / ビュー名を返す
    }

    /** 
     * 부서 검색 팝업 페이지 / 部署検索ポップアップ画面  
     * - JSP: /WEB-INF/view/popups/departments.jsp  
     */
    @GetMapping("/departments")
    public String dept() {
        return "popups/departments"; // 뷰 이름 반환 / ビュー名を返す
    }
}
