package org.pms.feature.voucher.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pms.feature.runpayroll.domain.SimpleResult;
import org.pms.feature.voucher.domain.VoucherProcessRequest;
import org.pms.feature.voucher.mapper.PayrollVoucherMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayrollVoucherService {

    private final PayrollVoucherMapper mapper;

    /** 미리보기: 부서별 차/대변 라인 */
    public List<Map<String, Object>> previewVoucher(String yyyymm, String payType) {
        yyyymm  = norm(yyyymm);
        payType = norm(payType);
        if (yyyymm == null || payType == null) {
            return Collections.<Map<String, Object>>emptyList();
        }
        return mapper.selectVoucherPreviewLines(yyyymm, payType);
    }

    /** 실반영: 헤더 없으면 생성, 라인 재작성 */
    @Transactional
    public SimpleResult processVoucher(VoucherProcessRequest req) {
        final String yyyymm  = norm(req.getYyyymm());
        final String payType = norm(req.getPayType());
        if (yyyymm == null)  return SimpleResult.fail("지급연월(yyyymm)이 없습니다.");
        if (payType == null) return SimpleResult.fail("급여유형(payType)이 없습니다.");

        final long wageAcct     = req.getWageAccountId()     != null ? req.getWageAccountId().longValue()     : 1001L;
        final long withholdAcct = req.getWithholdAccountId() != null ? req.getWithholdAccountId().longValue() : 2101L;

        // summary_note 토큰 (중복 방지 키)
        String token = req.getSummaryNote();
        if (token == null || token.trim().isEmpty()) {
            token = "PAYVCH-" + yyyymm + "-" + payType;
        }

        // 1) 헤더 없으면 생성
        mapper.insertVoucherIfAbsent(yyyymm, token);

        // 2) voucher_id 조회
        Long voucherId = mapper.findVoucherIdByToken(token);
        if (voucherId == null) return SimpleResult.fail("전표 헤더 생성/조회 실패");

        // 3) 라인 재작성 (기존 삭제 → 차변 → 대변)
        mapper.deleteVoucherLines(voucherId);
        int dRows = mapper.insertDebitLinesForMonth(voucherId, yyyymm, payType, wageAcct);
        int cRows = mapper.insertCreditLinesForMonth(voucherId, yyyymm, payType, withholdAcct);

        // 영향 행수 반환
        return SimpleResult.ok(dRows + cRows);
    }

    private String norm(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
        // (Java 11의 isBlank() 대신 trim().isEmpty() 사용)
    }
}
