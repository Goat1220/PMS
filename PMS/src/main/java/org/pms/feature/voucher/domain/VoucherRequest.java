package org.pms.feature.voucher.domain;

import lombok.Data;

/**
 * 전표 요청 DTO  
 * 伝票リクエストDTO  
 *
 * - 전표(분개) 처리 시 조회 조건으로 사용되는 데이터 구조  
 * - 伝票（仕訳）処理時に検索条件として使用されるデータ構造  
 */
@Data
public class VoucherRequest {

    /** 적용연월 / 適用年月 */
    private String yyyymm;

    /** 급여유형 (SALARY/BONUS) / 給与タイプ（SALARY/BONUS） */
    private String payType;

    /** 회계단위 / 会計単位 */
    private String orgUnit;

    /** 급여작업 / 給与作業区分 */
    private String jobType;

    /** 대상자 범위 (재직/퇴직) / 対象者範囲（在職／退職） */
    private String empTarget;

    /** 처리구분 (정상/조정) / 処理区分（正常／調整） */
    private String procKind;
}
