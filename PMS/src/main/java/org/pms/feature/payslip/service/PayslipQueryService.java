package org.pms.feature.payslip.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.payslip.domain.*;
import org.pms.feature.payslip.mapper.PayslipMapper;

/*
 * 급여명세 상세 조회 서비스 / 給与明細詳細取得サービス
 * - "요약 + 항목(지급/공제)"을 한 번에 구성해 반환 / 「サマリー＋明細（支給/控除）」をまとめて返却
 * - 읽기 전용 트랜잭션으로 일관성/성능 확보 / 読み取り専用トランザクションで整合性/性能を確保
 */
@Service
@RequiredArgsConstructor
public class PayslipQueryService {

    // DB 접근을 담당하는 MyBatis Mapper / DBアクセスを担う MyBatis Mapper
    private final PayslipMapper mapper;

    /**
     * 사번 + 귀속연월로 급여명세 조회 / 社員番号＋対象年月で明細取得
     * 1) 요약 조회 → 없으면 null / サマリー取得→無ければnull
     * 2) 요약의 payslipId로 지급/공제 항목 조회 / payslipIdで支給・控除項目を取得
     * 3) 요약 객체에 항목 세팅 후 반환 / サマリーに該当項目をセットして返却
     */
    @Transactional(readOnly = true) // 읽기 전용 / 読み取り専用
    public PayslipSummary getPayslip(String empNo, String periodYm) {
        // 1) 요약 / サマリー
        PayslipSummary summary = mapper.selectPayslipSummary(empNo, periodYm);
        if (summary == null) return null;

        // 2) 항목 조회 / 明細項目取得
        List<PayItem>        payItems = mapper.selectPayItems(summary.getPayslipId());         // 지급 / 支給
        List<DeductionItem>  dedItems = mapper.selectDeductionItems(summary.getPayslipId());   // 공제 / 控除

        // 3) 세팅 후 반환 / セットして返却
        summary.setPayItems(payItems);
        summary.setDeductionItems(dedItems);
        return summary;
    }

    /**
     * payslipId로 급여명세 상세 조회 / payslipId指定で明細取得
     * - 흐름은 위와 동일, 식별자를 직접 받는 경우 / 上記と同様で識別子を直接受け取るパターン
     */
    @Transactional(readOnly = true)
    public PayslipSummary getPayslipById(Long payslipId) {
        if (payslipId == null) return null; // 방어 로직 / ガード

        PayslipSummary summary = mapper.selectPayslipSummaryById(payslipId);
        if (summary == null) return null;

        List<PayItem>        payItems = mapper.selectPayItems(payslipId);
        List<DeductionItem>  dedItems = mapper.selectDeductionItems(payslipId);

        summary.setPayItems(payItems);
        summary.setDeductionItems(dedItems);
        return summary;
    }
}

