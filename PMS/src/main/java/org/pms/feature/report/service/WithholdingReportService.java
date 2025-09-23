package org.pms.feature.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.pms.feature.report.domain.AnnexRow;
import org.pms.feature.report.domain.WithholdingRow;
import org.pms.feature.report.domain.WithholdingSearch;
import org.pms.feature.report.mapper.WithholdingReportMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithholdingReportService {

    private final WithholdingReportMapper mapper;

    public List<WithholdingRow> getSummary(WithholdingSearch s) {
        return mapper.selectSummary(s);
    }

    public List<AnnexRow> getAnnex(WithholdingSearch s) {
        return mapper.selectAnnex(s);
    }
}
