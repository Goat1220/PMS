package org.pms.feature.voucher.controller;

import java.util.List;
import java.util.Map;

import org.pms.feature.runpayroll.domain.SimpleResult;
import org.pms.feature.voucher.domain.VoucherLedgerRow;
import org.pms.feature.voucher.domain.VoucherProcessRequest;
import org.pms.feature.voucher.service.PayrollVoucherService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 급상여 전표 처리 컨트롤러
 * 給与・賞与の伝票処理コントローラ
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/voucher/api",  produces = MediaType.APPLICATION_JSON_VALUE)
public class PayrollVoucherController {

    // 서비스 의존성 주입
    // サービスの依存性注入
    private final PayrollVoucherService service;
    
    /**
     * 전표 미리보기 조회 (발생/분개 산출 결과)
     * 伝票プレビューの取得（発生/仕訳の試算結果）
     */
    @GetMapping("/view")
    public List<VoucherLedgerRow> getView(
            @RequestParam("yyyymm") String yyyymm,               // 적용 연월 (YYYY-MM)
            // 適用年月 (YYYY-MM)
            @RequestParam(value = "payType", required = false) String payType // 급상여 구분 (옵션)
            // 給与種別（任意）
    ) {
    	VoucherProcessRequest req = new VoucherProcessRequest(); 
    	req.setYyyymm(yyyymm);
    	req.setPayType(payType);
    	req.setCleanupExisting(false); // 기존 데이터 삭제 없이 프리뷰
    	// 既存データを削除せずプレビュー
    	return service.previewVoucherAccrual(req);
    }
    
    /**
     * 전표 실반영
     * 伝票の本反映
     */
    @PostMapping(value = "/process", consumes = "application/json", produces = "application/json;charset=UTF-8")
    public SimpleResult processVoucher(@RequestBody VoucherProcessRequest req) {
        // 계산 결과를 실제 전표/전표라인에 반영
        // 試算結果を実際の伝票/伝票行へ反映
        return service.processVoucher(req);
    }
    
    /**
     * 전표 기초자료생성(미리보기) - 발생(#ACCRUAL)만
     * 伝票の基礎データ生成（プレビュー）- 発生（#ACCRUAL）のみ
     *
     * - cleanupExisting=true 이면 기존 발생 전표(PAYVCH-YYYY-MM-PAYTYPE#ACCRUAL) 삭제 후 미리보기
     * - cleanupExisting=true の場合、既存の発生伝票（PAYVCH-YYYY-MM-PAYTYPE#ACCRUAL）を削除してからプレビュー
     *
     * - empNos가 있으면 해당 사번만 대상으로 집계
     * - empNos が指定されていれば、その社員のみ集計対象
     *
     * - DB INSERT/UPDATE 없음(삭제 제외)
     * - DB への INSERT/UPDATE は実施しない（削除を除く）
     */
    @PostMapping(value = "/basegenerate", consumes = "application/json", produces = "application/json;charset=UTF-8")
    public List<VoucherLedgerRow> previewAccrual(@RequestBody VoucherProcessRequest req) {
    	req.setCleanupExisting(true); // 프리뷰 전 기존 발생 데이터 정리
    	// プレビュー前に既存の発生データをクリーンアップ
        return service.previewVoucherAccrual(req);
    }
}
