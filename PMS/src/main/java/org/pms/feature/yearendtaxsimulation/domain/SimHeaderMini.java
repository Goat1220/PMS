package org.pms.feature.yearendtaxsimulation.domain;

import java.util.Date;
import lombok.Data;

/* 시뮬레이션 헤더 요약 정보 / シミュレーションヘッダ簡易情報 */
@Data
public class SimHeaderMini {
    private Long     yrtId;      // 연말정산 결과 ID / 年末調整結果ID
    private String   empId;      // 사원 ID / 社員ID
    private Integer  baseYear;   // 기준 연도 / 基準年
    private String   runLabel;   // 실행 라벨(메모용) / 実行ラベル（メモ用）
    private String   confirmYn;  // 확정 여부(Y/N) / 確定有無（Y/N）
    private Date     createdAt;  // 생성 일시 / 作成日時
    private Date     updatedAt;  // 수정 일시 / 更新日時
}
