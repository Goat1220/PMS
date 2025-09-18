package org.pms.feature.yearendtaxsimulation.domain;

import lombok.*;

/** KO: 화면 표 1행 / JP: 画面の1行 */
@Data @NoArgsConstructor @AllArgsConstructor
public class SimItemRow {
    private String itemClass;      // 분류 / 区分
    private String itemName;       // 항목 / 項目名
    private Long   amount;         // 금액 / 金額
    private Long   expectedAmount; // 예상적용금액 / 予定適用額
}
