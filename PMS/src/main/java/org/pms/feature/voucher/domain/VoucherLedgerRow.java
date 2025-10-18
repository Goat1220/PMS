package org.pms.feature.voucher.domain;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 전표 원장 행 (Voucher Ledger Row)  
 * 伝票元帳行（Voucher Ledger Row）  
 *
 * - 전표 미리보기 및 전표처리 화면에서 표시되는 데이터 구조  
 * - 伝票プレビューおよび伝票処理画面で表示されるデータ構造  
 */
@Getter
@Setter
@ToString
public class VoucherLedgerRow {

    /** 화면 번호 / 画面表示番号 */
	private String displaySeq;

    /** 계정과목 / 勘定科目名 */
    private String accountName;

    /** 차대구분(차변/대변 라벨) / 借方・貸方区分（ラベル） */
    private String drcrName;

    /** 차변금액 / 借方金額 */
    private BigDecimal debitAmt;

    /** 대변금액 / 貸方金額 */
    private BigDecimal creditAmt;

    /** 발생부서 / 発生部署 */
    private String occurDeptName;

    /** 발생일자 (yyyy-MM-dd) / 発生日付 (yyyy-MM-dd) */
    private String occurDate;

    /** 지급일 (yyyy-MM-dd) / 支給日 (yyyy-MM-dd) */
    private String payDateStr;

    /** 적요 / 摘要（説明・備考） */
    private String summaryNote;

    /** 전표발행번호(전표내부코드) / 伝票発行番号（伝票内部コード） */
    private String voucherNo;

    /** 승인여부(Y/N) / 承認有無 (Y/N) */
    private String approvedYn;

    /** 순번 / 行順序番号 */
    private Integer lineSeq;

    /** 계정내부코드 / 勘定内部コード */
    private String accountCode;

    /** 차대구분코드(D/C) / 借貸区分コード (D/C) */
    private String drcrCode;

    /** 발생부서코드 / 発生部署コード */
    private String occurDeptCode;

    /** 비용구분코드 / 費用区分コード */
    private String costTypeCode;

    /** 처리구분 (예: 승인여부 기반 0/1) / 処理区分（例：承認状態に基づく 0/1） */
    private Integer processFlag;

    /** 전표처리대상자코드 (예: source_dept_code 등) / 伝票処理対象コード（例：source_dept_codeなど） */
    private String targetCode;
}
