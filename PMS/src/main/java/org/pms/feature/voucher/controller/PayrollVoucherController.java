package org.pms.feature.voucher.controller;

import java.util.List;
import java.util.Map;

import org.pms.feature.runpayroll.domain.SimpleResult;
import org.pms.feature.voucher.domain.VoucherProcessRequest;
import org.pms.feature.voucher.service.PayrollVoucherService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/runpayroll/api/voucher")
@RequiredArgsConstructor
public class PayrollVoucherController {

    private final PayrollVoucherService service;

    /** 전표 미리보기 */
    @GetMapping(value = "/preview", produces = "application/json;charset=UTF-8")
    public List<Map<String, Object>> previewVoucher(
            @RequestParam String yyyymm,
            @RequestParam String payType) {
        return service.previewVoucher(yyyymm, payType);
    }

    /** 전표 실반영 */
    @PostMapping(value = "/process", consumes = "application/json", produces = "application/json;charset=UTF-8")
    public SimpleResult processVoucher(@RequestBody VoucherProcessRequest req) {
        return service.processVoucher(req);
    }
}
