package org.pms.feature.voucher.domain;

import lombok.Data;
import java.util.Date;

/**
 * 전표 마스터 엔티티  
 * 伝票マスターエンティティ  
 *
 * - 급상여 처리 후 생성되는 전표(헤더) 정보를 관리  
 * - 給与・賞与処理後に作成される伝票（ヘッダー）情報を管理
 */
@Data
public class PayVoucher {

    /** 전표ID (PK) / 伝票ID（主キー） */
    private Long voucherId;

    /** 전표번호 / 伝票番号 */
    private String voucherNo;

    /** 지급일 / 支給日 */
    private Date payDate;

    /** 적요(요약) / 摘要（サマリー） */
    private String summaryNote;

    /** 승인여부 (Y/N) / 承認有無 (Y/N) */
    private String approvedYn;
}
