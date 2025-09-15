
package org.pms.feature.payslip.domain;

import java.util.List;
import lombok.Data;

/*
 * [KO] 급여명세 "상세 화면"의 집약 모델
 * - 상단 헤더(사번/이름/연월/유형, 합계) + 하단 항목 리스트(지급/공제)
 * - Controller/Service에서 한 번에 반환하여 화면 바인딩 편의성 제공
 *
 * [JA] 給与明細「詳細画面」の集約モデル
 * - ヘッダー（社員番号/氏名/年月/種類、合計）+ 明細（支給/控除）
 * - 一括返却で画面バインディングを簡単に
 */
@Data
public class PayslipSummary {

    // [KO] 급여명세 ID(식별자)
    // [JA] 明細ID（識別子）
    private Long   payslipId;

    // [KO] 사번 / [JA] 社員番号
    private String empNo;

    // [KO] 성명 / [JA] 氏名
    private String empName;

    // [KO] 귀속연월(YYYYMM) / [JA] 対象年月
    private String periodYm;

    // [KO] 급여유형 코드 / [JA] 給与種類コード
    private String payType;

    // [KO] 총지급액 / [JA] 総支給額
    private Long   grossAmount;

    // [KO] 공제합계 / [JA] 控除合計
    private Long   deductionSum;

    // [KO] 실지급액(총지급액-공제합계) / [JA] 差引支給額
    private Long   netAmount;

    // [KO] 지급 항목 리스트 / [JA] 支給項目リスト
    private List<PayItem>       payItems;

    // [KO] 공제 항목 리스트 / [JA] 控除項目リスト
    private List<DeductionItem> deductionItems;
}
