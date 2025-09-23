// org.pms.feature.searchpopup.controller.SearchPopupPageController
package org.pms.feature.searchpopup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//org.pms.feature.searchpopup.controller.SearchPopupPageController
@Controller
@RequestMapping("/popups")
public class SearchPopupPageController {
 @GetMapping("/employees")   public String emp()  { return "popups/employees"; }
 @GetMapping("/departments") public String dept() { return "popups/departments"; }
}
