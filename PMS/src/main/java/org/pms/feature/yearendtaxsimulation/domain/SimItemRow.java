package org.pms.feature.yearendtaxsimulation.domain;

import lombok.*;

/* 시뮬레이션 항목(그리드 행) / シミュレーション項目（グリッド行） */
@Data
@NoArgsConstructor   // 기본 생성자 / デフォルトコンストラクタ
@AllArgsConstructor  // 모든 필드 생성자 / 全フィールドのコンストラクタ
public class SimItemRow {
    private String itemClass;      // 항목 분류(예: 소득공제, 세액공제) / 項目分類（例：所得控除、税額控除）
    private String itemName;       // 항목명(예: 국민연금, 의료비) / 項目名（例：国民年金、医療費）
    private Long   amount;         // 실제 금액 / 実際金額
    private Long   expectedAmount; // 예상 금액 / 予想金額
}
