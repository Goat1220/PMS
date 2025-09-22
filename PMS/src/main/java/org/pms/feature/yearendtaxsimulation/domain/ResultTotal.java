package org.pms.feature.yearendtaxsimulation.domain;

import lombok.Data;

/* 연말정산 결과 합계(추가 납부 세액) / 年末調整結果の合計（追加納付税額） */
@Data
public class ResultTotal {
    private Long addNational; // 추가 납부 국세 / 追加納付 国税
    private Long addLocal;    // 추가 납부 지방세 / 追加納付 地方税
}
