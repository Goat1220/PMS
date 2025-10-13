package org.pms.feature.report.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.WithholdingSearch;
import org.pms.feature.report.service.WithholdingReportService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class WithholdingReportController {

	private final WithholdingReportService service;

    /** 페이지 */
	@GetMapping("/report/withholding")
	public String page(HttpServletRequest req, Model model) {
	    String ctx = req.getContextPath();
	    model.addAttribute("apiSummary",  ctx + "/api/report/withholding");
	    model.addAttribute("apiAnnex",    ctx + "/api/report/withholding/annex");
	    model.addAttribute("apiGenerate", ctx + "/api/report/withholding/generate"); // 선택(데이터생성)
	    return "report/withholding";
	}


    /** 요약 API */
	  @GetMapping("/api/report/withholding")
	    @ResponseBody
	    public List<WithholdingRow> summary(@RequestParam String applyYyyymm) {
	        WithholdingSearch s = new WithholdingSearch();
	        s.setApplyYyyymm(applyYyyymm);
	        return service.getSummary(s);
	    }

    /** 부표 API */
	   @GetMapping("/api/report/withholding/annex")
	    @ResponseBody
	    public List<AnnexRow> annex(@RequestParam String applyYyyymm) {
	        WithholdingSearch s = new WithholdingSearch();
	        s.setApplyYyyymm(applyYyyymm);
	        return service.getAnnex(s);
	    }
	   
	   /** 데이터 생성(더미) */
	   @PostMapping(value="/api/report/withholding/generate",
	             produces = MediaType.APPLICATION_JSON_VALUE)   // ★ JSON 강제
	@ResponseBody
	public Map<String, Object> generate(@RequestParam String applyYyyymm) {
	    Map<String, Object> res = new HashMap<>();
	    res.put("ok", true);
	    return res;
	}
	   
	    // ===========================
	    // 전월 미환급세액 조회  / 저장
	    // ===========================
	   @GetMapping("/api/report/withholding/refund-prev")
	   @ResponseBody
	   public Map<String, Number> prevRefund(@RequestParam String yyyymm) {
		    Map<String, Number> res = new HashMap<>();
		    res.put("prevCarryJ", service.findPrevJ(yyyymm)); // 전월 J
		    res.put("prevApplyK", service.findPrevK(yyyymm)); // 전월 K
		    return res;
	   }

	   @PostMapping(value="/api/report/withholding/refund-save",
	             produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> saveRefund(
	        @RequestParam String yyyymm,
	        @RequestParam BigDecimal jValue,
	        @RequestParam BigDecimal kValue) {

	    service.saveRefund(yyyymm, jValue, kValue);

	    Map<String, Object> res = new HashMap<String, Object>();
	    res.put("ok", true);
	    res.put("message", "saved");
	    return res;
	}


	}
