package org.pms.feature.report.domain;

import lombok.Data;

/**
 * [검색 파라미터 DTO]  
 * - 원천징수 보고서 조회 시, 화면에서 전달되는 조건을 담음  
 * - 현재는 ‘귀속월(YYYY-MM)’만 사용 / 現在は「対象月(YYYY-MM)」のみ使用  
 */
@Data  // getter/setter 자동 생성 / getter・setter自動生成
public class WithholdingSearch {

    /** 귀속월 (예: "2025-09") / 対象月（例："2025-09"） */
    private String applyYyyymm;
}
