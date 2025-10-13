package org.pms.feature.report.domain;

import lombok.Data;

/**
 * [요약/부표 공통 행 구조]
 * 화면 그리드가 요구하는 컬럼 세트입니다.
 * - 팀 스키마에는 없는 값(납부소득세, 징수가산세 등)은 SELECT에서 계산/0으로 채워서 매핑합니다.
 */
@Data
public class WithholdingRow {
    private String applyYyyymm;   // 귀속월 (YYYY-MM)
    private String incomeType;    // 소득구분 (예: 근로소득)
    private String code;          // 코드
    private Integer headCount;    // 인원
    private Long taxTotal;        // 총지급액

    private Long ntWithheld;      // 징수농특세 (스키마 분리값 없으면 tax_nt로 동치/또는 0)
    private Long taxIncome;       // 납부소득세 = 징수소득세 - 조정환급세액 (계산식)
    private Long penaltyTax;      // 징수가산세 (스키마에 없어서 일단 0)
    private Long taxWithheld;     // 징수소득세 (스키마의 tax_income을 원천징수액으로 사용)
    private Long adjRefund;       // 조정환급세액
    private Long taxNt;           // 납부농특세 (스키마 tax_nt)

    private Integer no;           // 행번호(ROW_NUMBER)
}
