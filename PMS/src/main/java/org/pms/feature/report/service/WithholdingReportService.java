package org.pms.feature.report.service;

import lombok.RequiredArgsConstructor;
import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.WithholdingSearch;
import org.pms.feature.report.mapper.WithholdingReportMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * [서비스 레이어]
 * - 트랜잭션/로깅/후처리 등을 넣기 좋은 자리입니다.
 * - 지금은 단순히 매퍼를 호출만 합니다.
 */
@Service
@RequiredArgsConstructor
public class WithholdingReportService {

    private final WithholdingReportMapper mapper;

    /** 요약 데이터 조회 */
    public List<WithholdingRow> getSummary(WithholdingSearch cond) {
        return mapper.selectSummary(cond);
    }

    /** 부표 데이터 조회 */
    public List<AnnexRow> getAnnex(WithholdingSearch cond) {
        return mapper.selectAnnex(cond);
    }
}
