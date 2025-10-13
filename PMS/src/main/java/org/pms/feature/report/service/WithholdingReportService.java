package org.pms.feature.report.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.WithholdingSearch;
import org.pms.feature.report.mapper.WithholdingReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithholdingReportService {

    private final WithholdingReportMapper mapper;

    /** 요약 데이터 조회(+ A01 주입, + 모든 가감계/총합계 자동 합산) */
    public List<WithholdingRow> getSummary(WithholdingSearch cond) {
        List<WithholdingRow> rows = mapper.selectSummary(cond);

        // 코드 → 행 매핑
        Map<String, WithholdingRow> byCode = new HashMap<String, WithholdingRow>();
        for (WithholdingRow r : rows) {
            byCode.put(r.getCode(), r);
        }

        // 1) 상단 실제 원장 합계 주입: A01
        BigDecimal totalPayment = mapper.sumTotalPaymentByMonth(cond.getApplyYyyymm());
        BigDecimal withheldTax  = mapper.sumWithheldTaxByMonthByPrefix(cond.getApplyYyyymm());
  
        Integer headCount = mapper.countHeadsByMonth(cond.getApplyYyyymm());
        if (headCount == null) headCount = 0;
        
        WithholdingRow a01 = byCode.get("A01");
        if (a01 != null) {
        	a01.setHeadCount(headCount);
            a01.setTaxTotal(nL(totalPayment));
            a01.setTaxWithheld(nL(withheldTax));
            a01.setTaxIncome(0L);
        }


        // 2) 섹션별 가감계 매핑 (LinkedHashMap: 선언 순서 유지)
        Map<String, List<String>> groups = new LinkedHashMap<String, List<String>>();
        groups.put("A10", Arrays.asList("A01","A02","A03","A04","A05","A06"));
        groups.put("A20", Arrays.asList("A21","A22"));
        groups.put("A30", Arrays.asList("A25","A26"));
        groups.put("A40", Arrays.asList("A41","A42"));
        groups.put("A47", Arrays.asList("A45","A46","A48"));

        // 3) 각 가감계 코드에 열별 합산 반영
        for (Map.Entry<String, List<String>> e : groups.entrySet()) {
            String parent = e.getKey();
            List<String> children = e.getValue();

            WithholdingRow tgt = byCode.get(parent);
            if (tgt == null) continue;

            long head=0, tot=0, ntw=0, taxInc=0, pen=0, taxWh=0, adj=0, nt=0;

            for (String child : children) {
                WithholdingRow r = byCode.get(child);
                if (r == null) continue;
                head += n(r.getHeadCount());
                tot  += n(r.getTaxTotal());
                ntw  += n(r.getNtWithheld());
                taxInc += n(r.getTaxIncome());
                pen  += n(r.getPenaltyTax());
                taxWh += n(r.getTaxWithheld());
                adj  += n(r.getAdjRefund());
                nt   += n(r.getTaxNt());
            }

            tgt.setHeadCount((int) head);
            tgt.setTaxTotal(tot);
            tgt.setNtWithheld(ntw);
            tgt.setTaxWithheld(taxWh);
            tgt.setAdjRefund(adj);
            tgt.setTaxIncome(taxWh - adj); // 규정식
            tgt.setPenaltyTax(pen);
            tgt.setTaxNt(nt);
        }

     // 4) 총합계 A99 = 가감계(소계) 라인만 합산
        WithholdingRow a99 = byCode.get("A99");
        if (a99 != null) {
            // 가감계 부모 코드 목록만 더한다
            List<String> totalParents = Arrays.asList("A10","A20","A30","A40","A47");
            long head=0, tot=0, ntw=0, pen=0, taxWh=0, adj=0, nt=0;

            for (String p : totalParents) {
                WithholdingRow r = byCode.get(p);
                if (r == null) continue;
                head += n(r.getHeadCount());
                tot  += n(r.getTaxTotal());
                ntw  += n(r.getNtWithheld());
                pen  += n(r.getPenaltyTax());
                taxWh += n(r.getTaxWithheld());
                adj  += n(r.getAdjRefund());
                nt   += n(r.getTaxNt());
            }
            a99.setHeadCount((int) head);
            a99.setTaxTotal(tot);
            a99.setNtWithheld(ntw);
            a99.setPenaltyTax(pen);
            a99.setTaxWithheld(taxWh);
            a99.setAdjRefund(adj);
            a99.setTaxNt(nt);
            a99.setTaxIncome(taxWh - adj); // 규정식
        }


        return rows;
    }

    /** null-safe long 변환 */
    private long n(Number v) { return (v == null) ? 0L : v.longValue(); }
    /** BigDecimal → long (null 안전) */
    private long nL(BigDecimal v) { return (v == null) ? 0L : v.longValue(); }

    /** 부표 데이터 조회 (변경 없음) */
    public List<AnnexRow> getAnnex(WithholdingSearch cond) {
        return mapper.selectAnnex(cond);
    }
    /** (써야할때 써) 요약/부표 대상 데이터 집계·저장 로직 */
    public void generate(String applyYyyymm) {
    	  // TODO: 요약/부표 대상 데이터 집계·저장 로직
    	}
    
    // ===========================
    // 전월 미환급세액 조회 / 저장
    // ===========================
    /** 전월 J(차월이월환급세액) 조회 */
    public BigDecimal findPrevJ(String yyyymm) {
        BigDecimal v = mapper.selectPrevJ(yyyymm);  // ★ mapper에서 BigDecimal로 받기
        return (v == null) ? BigDecimal.ZERO : v;
    }

    /** 전월 K(환급신청금액) 조회 */
    public BigDecimal findPrevK(String yyyymm) {
        BigDecimal v = mapper.selectPrevK(yyyymm);  // ★ mapper에서 BigDecimal로 받기
        return (v == null) ? BigDecimal.ZERO : v;
    }
   

    /** 이번달 J/K 저장 (데이터 생성 버튼에서 호출) */
    @Transactional
    public void saveRefund(String yyyymm, BigDecimal jValue, BigDecimal kValue) {
        if (jValue == null) jValue = BigDecimal.ZERO;
        if (kValue == null) kValue = BigDecimal.ZERO;
        mapper.upsertRefund(yyyymm, jValue, kValue); // ★ upsert 호출
    }
}
