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

	
	@GetMapping("/list")
    public void list(Model model) {
        log.info("연말정산 리스트 조회");
        List<YrtHeaderVO> list=service.getList();

        // Service에서 전체 리스트 가져오기
        model.addAttribute("list", list);
    }
	
    @ResponseBody
    @GetMapping("/detail")
    public List<YrtDetailViewDTO> getDetail(@RequestParam("yrtId") int yrtId) {
        return detailService.getDetailView(yrtId);
    }
	
}
