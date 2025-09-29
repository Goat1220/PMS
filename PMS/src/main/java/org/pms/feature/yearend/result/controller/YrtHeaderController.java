package org.pms.feature.yearend.result.controller;

import java.util.List;

import org.pms.feature.yearend.result.domain.YrtDetailViewDTO;
import org.pms.feature.yearend.result.domain.YrtHeaderViewDTO;
import org.pms.feature.yearend.result.service.YrtDetailService;
import org.pms.feature.yearend.result.service.YrtHeaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/yearend/result/*")
@AllArgsConstructor
public class YrtHeaderController {

	private YrtHeaderService service;

	private YrtDetailService detailService;

	// 리스트 화면
	@GetMapping("/list")
	public String list(@RequestParam(value = "deptName", required = false, defaultValue="") String deptName,
			@RequestParam(value = "empName", required = false, defaultValue="") String empName, Model model) {
		List<YrtHeaderViewDTO> list = service.getList(deptName, empName);
		model.addAttribute("list", list);
		return "yearend/list"; // list.jsp // /WEB-INF/views/yearend/list.jsp
	}

	// 디테일 JSON
	@ResponseBody
	@GetMapping(value = "/detail", produces = "application/json;charset=UTF-8")
	public List<YrtDetailViewDTO> getDetail(@RequestParam int yrtId) {
		return detailService.getDetailView(yrtId);
	}

}
