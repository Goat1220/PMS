// src/main/java/org/pms/feature/report/domain/WithholdingSearch.java
package org.pms.feature.report.domain;

import lombok.Data;

/**
 * [검색 파라미터]
 * - 화면에서 '귀속월(YYYY-MM)'만 넘겨 받는 간단 검색용 DTO
 * - 필요하면 필드를 더 추가해도 됩니다(예: incomeType 등)
 */
@Data
public class WithholdingSearch {
    /** 예: "2025-09" */
    private String applyYyyymm;
}