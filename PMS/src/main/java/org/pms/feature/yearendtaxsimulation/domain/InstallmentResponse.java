package org.pms.feature.yearendtaxsimulation.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 분납 시뮬레이션 응답 데이터 / 分納シミュレーション応答データ */
@Data
@NoArgsConstructor   // 기본 생성자 / デフォルトコンストラクタ
@AllArgsConstructor  // 모든 필드 생성자 / 全フィールドのコンストラクタ
public class InstallmentResponse {
    private List<InstallmentEntry> schedule; // 월별 분납 스케줄 목록 / 月別分納スケジュール一覧
    private String note;                     // 비고·설명 / 備考・説明
}
