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

/** 원천징수 보고서 화면 및 API를 관리하는 컨트롤러 / 源泉徴収レポート画面とAPIのコントローラ */
@Controller
@RequiredArgsConstructor   // final 필드를 자동 주입 / finalフィールドを自動注入
@RequestMapping("")         // 기본 경로 / ベースパス
public class WithholdingReportController {

    /** 서비스 객체 주입 / サービスオブジェクトの注入 */
	private final WithholdingReportService service;

    /** 
     * 보고서 페이지 이동 / レポートページ表示  
     * - 프론트에서 사용할 API 주소를 model에 담아서 JSP로 보냄  
     * - return 값은 JSP 파일 경로 (View 이름)
     */
	@GetMapping("/report/withholding")
	public String page(HttpServletRequest req, Model model) {
	    String ctx = req.getContextPath(); // 현재 프로젝트의 contextPath를 가져옴 / 現在のコンテキストパスを取得
	    model.addAttribute("apiSummary",  ctx + "/api/report/withholding");        // 요약 데이터 API / 概要データAPI
	    model.addAttribute("apiAnnex",    ctx + "/api/report/withholding/annex");  // 부표 데이터 API / 付表データAPI
	    model.addAttribute("apiGenerate", ctx + "/api/report/withholding/generate");// 더미 데이터 생성 API / ダミーデータ生成API
	    return "report/withholding";  // JSP 뷰 이름 반환 / JSPビュー名を返す
	}

    /** 
     * 요약 데이터 조회 API / 概要データ取得API  
     * - 요청 파라미터로 월(applyYyyymm)을 받아 해당 월의 요약 데이터를 반환
     */
	@GetMapping("/api/report/withholding")
	@ResponseBody   // 반환값을 JSON 형태로 응답 / 戻り値をJSONで返す
	public List<WithholdingRow> summary(@RequestParam String applyYyyymm) {
	    WithholdingSearch s = new WithholdingSearch(); // 검색조건 객체 생성 / 検索条件オブジェクト作成
	    s.setApplyYyyymm(applyYyyymm);                 // 월 설정 / 月を設定
	    return service.getSummary(s);                  // 서비스 호출 → DB 조회 / サービス呼出し→DB取得
	}

    /** 
     * 부표 데이터 조회 API / 付表データ取得API  
     * - 월별 상세(개인별) 데이터를 조회
     */
	@GetMapping("/api/report/withholding/annex")
	@ResponseBody
	public List<AnnexRow> annex(@RequestParam String applyYyyymm) {
	    WithholdingSearch s = new WithholdingSearch(); // 검색조건 객체 / 検索条件オブジェクト
	    s.setApplyYyyymm(applyYyyymm);
	    return service.getAnnex(s); // 서비스에서 annex 데이터 조회 / サービスから付表データを取得
	}

    /** 
     * 더미 데이터 생성 API / ダミーデータ生成API  
     * - 실제 로직 없이 “ok:true” JSON 반환  
     * - 화면 테스트용
     */
	@PostMapping(value="/api/report/withholding/generate",
	             produces = MediaType.APPLICATION_JSON_VALUE)  // 응답 타입 JSON / 応答タイプJSON
	@ResponseBody
	public Map<String, Object> generate(@RequestParam String applyYyyymm) {
	    Map<String, Object> res = new HashMap<>(); // 응답용 Map / 応答用Map
	    res.put("ok", true);                      // 성공 여부 / 成功フラグ
	    return res;                               // JSON 반환 / JSONを返す
	}

    // ===========================
    // 전월 미환급세액 조회 / 저장
    // 前月未還付税額の取得・保存
    // ===========================

    /** 
     * 전월 J,K 조회 / 前月J,K取得  
     * - 전달의 미환급 세액(J: 이월, K: 환급신청)을 불러옴
     */
	@GetMapping("/api/report/withholding/refund-prev")
	@ResponseBody
	public Map<String, Number> prevRefund(@RequestParam String yyyymm) {
	    Map<String, Number> res = new HashMap<>();
	    res.put("prevCarryJ", service.findPrevJ(yyyymm)); // 전월 J값 / 前月J値
	    res.put("prevApplyK", service.findPrevK(yyyymm)); // 전월 K값 / 前月K値
	    return res;  // JSON 응답 / JSON応答
	}

    /** 
     * 미환급세액 저장 API / 未還付税額保存API  
     * - 전달받은 J, K 값을 DB에 저장(업데이트 or 삽입)
     */
	@PostMapping(value="/api/report/withholding/refund-save",
	             produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> saveRefund(
	        @RequestParam String yyyymm,       // 대상 월 / 対象月
	        @RequestParam BigDecimal jValue,   // J값 / J値
	        @RequestParam BigDecimal kValue) { // K값 / K値

	    service.saveRefund(yyyymm, jValue, kValue); // 서비스에서 저장 처리 / サービスで保存処理

	    Map<String, Object> res = new HashMap<>();   // 응답 Map / 応答Map
	    res.put("ok", true);                         // 성공 여부 / 成功フラグ
	    res.put("message", "saved");                 // 메시지 / メッセージ
	    return res;                                  // JSON 응답 / JSON応答
	}
}
