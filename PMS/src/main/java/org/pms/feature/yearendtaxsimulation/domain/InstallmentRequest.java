package org.pms.feature.yearendtaxsimulation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 분납 시뮬레이션 요청 데이터 / 分納シミュレーション要求データ */
@Data
@NoArgsConstructor   // 기본 생성자 / デフォルトコンストラクタ
@AllArgsConstructor  // 모든 필드 생성자 / 全フィールドのコンストラクタ
public class InstallmentRequest {
    private Long yrtId;       // 연말정산 결과 ID / 年末調整結果ID
    private Integer months;   // 분납 개월 수 / 分納月数
    private String startMonth; // 시작 연월(예: 2025-03) / 開始年月（例：2025-03）
}
