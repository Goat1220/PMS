package org.pms.feature.report.domain;

import lombok.Data;

/**
 * [부표 행 데이터 클래스]  
 * - 부표(詳細表) 한 줄을 표현하는 DTO  
 * - WithholdingRow와 구조를 맞춰 화면 재사용 / 画面の再利用のため同構造に設定
 * - 기존 코드 호환용으로 타입을 분리 / 既存コード互換のため分離
 */
@Data  // getter/setter 자동 생성 / getter・setter自動生成
public class AnnexRow {

    /** 적용 연월(예: 202501) / 適用年月 */
    private String applyYyyymm;

    /** 소득 구분(예: 급여, 상여 등) / 所得区分（給与・賞与など） */
    private String incomeType;

    /** 코드(세목, 분류 등) / コード（科目・分類など） */
    private String code;

    /** 인원 수 / 人数 */
    private Integer headCount;

    /** 세액 합계 / 税額合計 */
    private Long taxTotal;

    /** 비과세 원천징수액 / 非課税源泉徴収額 */
    private Long ntWithheld;

    /** 과세 소득 금액 / 課税所得金額 */
    private Long taxIncome;

    /** 가산세 등 벌과금 / 加算税・罰金等 */
    private Long penaltyTax;

    /** 원천징수세액 / 源泉徴収税額 */
    private Long taxWithheld;

    /** 조정·환급 금액 / 調整・還付金額 */
    private Long adjRefund;

    /** 비과세 세액 / 非課税税額 */
    private Long taxNt;

    /** 순번 (행 번호용) / 行番号 */
    private Integer no;
}
