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

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/voucher/api",  produces = MediaType.APPLICATION_JSON_VALUE)
public class PayrollVoucherController {

    private final PayrollVoucherService service;
    
    @GetMapping("/view")
    public List<VoucherLedgerRow> getView(
            @RequestParam("yyyymm") String yyyymm,
            @RequestParam(value = "payType", required = false) String payType
    ) {
    	VoucherProcessRequest req = new VoucherProcessRequest(); 
    	req.setYyyymm(yyyymm);
    	req.setPayType(payType);
    	req.setCleanupExisting(false);
    	return service.previewVoucherAccrual(req);
    }
    
    /** 전표 실반영 */
    @PostMapping(value = "/process", consumes = "application/json", produces = "application/json;charset=UTF-8")
    public SimpleResult processVoucher(@RequestBody VoucherProcessRequest req) {
        return service.processVoucher(req);
    }
    
    /** 전표 기초자료생성(미리보기) - 발생(#ACCRUAL)만
     *  - cleanupExisting=true 이면 기존 발생 전표(PAYVCH-YYYY-MM-PAYTYPE#ACCRUAL) 삭제 후 미리보기
     *  - empNos가 있으면 해당 사번만 대상으로 집계
     *  - DB INSERT/UPDATE 없음(삭제 제외)
     */
    @PostMapping(value = "/basegenerate", consumes = "application/json", produces = "application/json;charset=UTF-8")
    public List<VoucherLedgerRow> previewAccrual(@RequestBody VoucherProcessRequest req) {
    	req.setCleanupExisting(true);
        return service.previewVoucherAccrual(req);
    }
}
