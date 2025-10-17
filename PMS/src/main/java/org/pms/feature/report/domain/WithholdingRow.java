package org.pms.feature.report.domain;

import lombok.Data;

/**
 * [요약/부표 공통 행 구조]  
 * - 화면의 표(그리드)에서 한 행을 표현하는 DTO  
 * - 부표(Annex)와 동일 구조로, 재사용성을 높임 / 付表と同構造で再利用性を高める  
 * - 일부 항목은 DB에 직접 존재하지 않아 SELECT 시 계산 또는 0으로 채움 / 一部の項目はDBにないためSELECTで計算または0を代入
 */
@Data // getter/setter 자동 생성 / getter・setter自動生成
public class WithholdingRow {

    /** 귀속월(예: 2025-01) / 対象月 */
    private String applyYyyymm;

    /** 소득구분(예: 근로소득, 상여 등) / 所得区分（給与・賞与など） */
    private String incomeType;

    /** 코드(소득 코드나 항목 코드 등) / コード（所得コード・項目コードなど） */
    private String code;

    /** 인원 수 / 人数 */
    private Integer headCount;

    /** 총지급액 / 総支給額 */
    private Long taxTotal;

    /** 징수 농특세 / 徴収農特税       */
    private Long ntWithheld;

    /** 납부소득세 / 納付所得税       */
    private Long taxIncome;

    /** 징수가산세 / 徴収加算税       */
    private Long penaltyTax;

    /** 징수소득세 / 源泉所得税   */
    private Long taxWithheld;

    /** 조정환급세액 / 調整還付税額 */
    private Long adjRefund;

    /** 납부 농특세 / 納付農特税   */
    private Long taxNt;

    /** 행 번호(ROW_NUMBER) / 行番号 */
    private Integer no;
}
