package org.pms.feature.yearendtaxsimulation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 분납 스케줄의 한 행(월별 금액) / 分納スケジュールの1行（各月の金額） */
@Data
@NoArgsConstructor   // 기본 생성자 / デフォルトコンストラクタ
@AllArgsConstructor  // 모든 필드 생성자 / 全フィールドのコンストラクタ
public class InstallmentEntry {
    private String yyyymm; // 대상 연월(예: 2025-03) / 対象年月（例：2025-03）
    private Long national; // 국세 금액 / 国税金額
    private Long local;    // 지방세 금액 / 地方税金額
    private Long total;    // 합계 금액(국세+지방세) / 合計金額（国税+地方税）
}
