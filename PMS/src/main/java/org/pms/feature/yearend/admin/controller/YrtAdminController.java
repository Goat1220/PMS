package org.pms.feature.yearend.admin.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.pms.feature.yearend.admin.domain.*;
import org.pms.feature.yearend.admin.service.YrtAdminService;

@Controller
@RequestMapping("/yearend/admin")
@RequiredArgsConstructor
public class YrtAdminController {

    private final YrtAdminService service;

    // 진입점: /yearend/admin/list
    @GetMapping("/list")
    public String list(@ModelAttribute("cond") AdminSearchCond cond, Model model) {
        List<AdminRowDTO> list = service.findRows(cond);
        model.addAttribute("list", list);
        return "yearend/admin";   // /WEB-INF/views/yearend/admin.jsp
    }

}
