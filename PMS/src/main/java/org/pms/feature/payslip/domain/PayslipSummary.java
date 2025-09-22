
package org.pms.feature.payslip.domain;

import java.util.List;
import lombok.Data;

/*
 *  급여명세 "상세 화면"의 집약 모델
 * - 상단 헤더(사번/이름/연월/유형, 합계) + 하단 항목 리스트(지급/공제)
 * - Controller/Service에서 한 번에 반환하여 화면 바인딩 편의성 제공
 *
 *  給与明細「詳細画面」の集約モデル
 * - ヘッダー（社員番号/氏名/年月/種類、合計）+ 明細（支給/控除）
 * - 一括返却で画面バインディングを簡単に
 */
@Data
public class PayslipSummary {

    //  급여명세 ID(식별자)
    //  明細ID（識別子）
    private Long   payslipId;

    //  사번 /  社員番号
    private String empNo;

    //  성명 /  氏名
    private String empName;

    //  귀속연월(YYYYMM) /  対象年月
    private String periodYm;

    //  급여유형 코드 /  給与種類コード
    private String payType;

    //  총지급액 /  総支給額
    private Long   grossAmount;

    //  공제합계 /  控除合計
    private Long   deductionSum;

    //  실지급액(총지급액-공제합계) /  差引支給額
    private Long   netAmount;

    //  지급 항목 리스트 /  支給項目リスト
    private List<PayItem>       payItems;

    //  공제 항목 리스트 /  控除項目リスト
    private List<DeductionItem> deductionItems;
}
