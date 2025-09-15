package org.pms.feature.payslip.domain;

import lombok.Data;

/*
 * [KO] 급여명세 "목록 화면" 한 행(Row)을 표현
 * - 합계/요약 중심 필드로 구성 (상세 항목은 PayslipSummary에서 별도 제공)
 *
 * [JA] 給与明細「一覧画面」の1行を表現
 * - 合計/サマリー中心のフィールド構成
 */
@Data
public class PayslipListRow {

    // [KO] 급여명세 ID(식별자). 상세 조회 시 키
    // [JA] 明細ID（識別子）
    private Long   payslipId;

    // [KO] 급여유형 코드값. 예: "REG"(정기), "IRR"(비정기)
    // [JA] 給与種類コード
    private String payType;

    // [KO] 귀속연월(YYYYMM). 예: "202508"
    // [JA] 対象年月（YYYYMM）
    private String periodYm;

    // [KO] 총지급액(공제 전)
    // [JA] 総支給額（控除前）
    private Long   grossAmount;

    // [KO] 전월/이전 지급 누계 등 의미로 사용 가능(프로젝트 정의에 따름)
    // [JA] 前月/以前支給の累計など（定義はPJ依存）
    private Long   prevPaidAmount;

    // [KO] 공제합계
    // [JA] 控除合計
    private Long   deductionSum;

    // [KO] 실지급액(= 총지급액 - 공제합계)
    // [JA] 差引支給額
    private Long   netAmount;

    // [KO] 급여유형 명칭(코드명 매핑 결과). 예: "정기급여"
    // [JA] 種類名（コード→名称の解決結果）
    private String payTypeName;
}
