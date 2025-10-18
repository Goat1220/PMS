package org.pms.feature.voucher.domain;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 전표 라인 엔티티  
 * 伝票明細エンティティ  
 *
 * - 전표(PayVoucher)의 상세 행 정보를 관리  
 * - 伝票（PayVoucher）の明細行情報を管理  
 */
@Data
public class PayVoucherLine {

    /** 전표행ID (PK) / 伝票行ID（主キー） */
    private Long lineId;

    /** 전표ID (FK) / 伝票ID（外部キー） */
    private Long voucherId;

    /** 행 순번 / 行番号 */
    private Integer lineSeq;

    /** 계정과목ID / 勘定科目ID */
    private Long accountId;

    /** 차대구분(D/C) / 借方・貸方区分 (D/C) */
    private String drcrCode;

    /** 부서ID / 部署ID */
    private Long deptId;

    /** 발생원천 부서ID (선택사항) / 発生元部署ID（任意） */
    private Long sourceDeptId;

    /** 비용구분 코드 / 費用区分コード */
    private String costTypeCode;

    /** 차변금액 / 借方金額 */
    private BigDecimal debitAmt;

    /** 대변금액 / 貸方金額 */
    private BigDecimal creditAmt;

    /** 적요 / 摘要 */
    private String note;

    /** 지급일 (yyyy-MM-dd) / 支給日 (yyyy-MM-dd) */
    private String payDate;
}
