package org.pms.feature.yearendtaxsimulation.domain;

import java.util.Date;
import lombok.Data;

/** KO: 실행 헤더 요약 / JP: 実行ヘッダー概要 */
@Data
public class SimHeaderMini {
    private Long     yrtId;
    private String   empId;
    private Integer  baseYear;
    private String   runLabel;
    private String   confirmYn; // Y/N
    private Date     createdAt;
    private Date     updatedAt;
}
