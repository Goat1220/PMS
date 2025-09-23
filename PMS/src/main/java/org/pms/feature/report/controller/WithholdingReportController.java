package org.pms.feature.report.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import org.pms.feature.report.domain.*;
import org.pms.feature.report.service.WithholdingReportService;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class WithholdingReportController {

	private final WithholdingReportService service;

    /** 페이지 */
	  @GetMapping("/report/withholding")
	    public String page() {
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
	}
