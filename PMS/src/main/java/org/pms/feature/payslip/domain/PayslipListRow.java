package org.pms.feature.payslip.domain;

import lombok.Data;

/*
 *  급여명세 "목록 화면" 한 행(Row)을 표현
 * - 합계/요약 중심 필드로 구성 (상세 항목은 PayslipSummary에서 별도 제공)
 *
 *  給与明細「一覧画面」の1行を表現
 * - 合計/サマリー中心のフィールド構成
 */
@Data
public class PayslipListRow {

    //  급여명세 ID(식별자). 상세 조회 시 키
    //  明細ID（識別子）
    private Long   payslipId;

    //  급여유형 코드값. 예: "REG"(정기), "IRR"(비정기)
    //  給与種類コード
    private String payType;

    //  귀속연월(YYYYMM). 예: "202508"
    //  対象年月（YYYYMM）
    private String periodYm;

    //  총지급액(공제 전)
    //  総支給額（控除前）
    private Long   grossAmount;

    //  전월/이전 지급 누계 등 의미로 사용 가능(프로젝트 정의에 따름)
    //  前月/以前支給の累計など（定義はPJ依存）
    private Long   prevPaidAmount;

    //  공제합계
    //  控除合計
    private Long   deductionSum;

    //  실지급액(= 총지급액 - 공제합계)
    //  差引支給額
    private Long   netAmount;

    //  급여유형 명칭(코드명 매핑 결과). 예: "정기급여"
    //  種類名（コード→名称の解決結果）
    private String payTypeName;
}
