package org.pms.feature.yearend.controller;

import java.util.List;

import org.pms.feature.yearend.domain.YrtDetailViewDTO;
import org.pms.feature.yearend.domain.YrtHeaderVO;
import org.pms.feature.yearend.service.YrtDetailService;
import org.pms.feature.yearend.service.YrtHeaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/yearend/*")
@AllArgsConstructor
public class YrtHeaderController {

	private YrtHeaderService service;
	
    private YrtDetailService detailService;

	
 // 리스트 화면
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("list", service.getList());
        return "/yearend/list"; // /WEB-INF/views/yearend/list.jsp
    }

    // 디테일 JSON
    @ResponseBody
    @GetMapping(value = "/detail", produces = "application/json;charset=UTF-8")
    public List<YrtDetailViewDTO> getDetail(@RequestParam int yrtId) {
        return detailService.getDetailView(yrtId);
    }

}
