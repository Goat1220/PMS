package org.pms.feature.runpayroll.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.runpayroll.domain.DeductionRow;
import org.pms.feature.runpayroll.domain.EarningItemRow;
import org.pms.feature.runpayroll.domain.EmpFlag;
import org.pms.feature.runpayroll.domain.PayrollSummaryRow;
import org.pms.feature.runpayroll.domain.YrtAdjustment;

import java.util.List;
import java.util.Map;

@Mapper
public interface RunPayrollMapper {

    // ===== 조회 =====
    // ===== 照会 =====
    List<PayrollSummaryRow> selectPayrollSummary(
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType,
            @Param("deptCode") String deptCode,
            @Param("empNo") String empNo);

    List<EarningItemRow> selectEarningItems(
            @Param("empNo") String empNo,
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType);

    List<DeductionRow> selectDeductions(
            @Param("empNo") String empNo,
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType);

    // ===== 공통 유틸 =====
    // ===== 共通ユーティリティ =====
    Long findEmpIdByEmpNo(@Param("empNo") String empNo);
    // 사번으로 emp_id 조회
    // 社員番号から emp_id を取得

    int ensurePayslipExists(@Param("empId") Long empId,
                            @Param("yyyymm") String yyyymm,
                            @Param("payType") String payType);
    // payslip 존재 보장(없으면 생성) - 영향 행 수 반환
    // payslip の存在を保証（無ければ作成）— 影響件数を返却

    Long ensurePayslip(@Param("empId") Long empId,
                       @Param("yyyymm") String yyyymm,
                       @Param("payType") String payType);
    // payslip 존재 보장 후 payslip_id 반환
    // payslip の存在を保証し、payslip_id を返却

    Long sumEarningItems(@Param("payslipId") Long payslipId);
    // 지급항목 합계
    // 支給項目合計

    Long sumDeductionItems(@Param("payslipId") Long payslipId);
    // 공제항목 합계
    // 控除項目合計

    int updatePayslipNet(@Param("payslipId") Long payslipId,
                         @Param("net") Long net);
    // 실지급액(net) 업데이트
    // 実支給額(net) を更新

    // 요약행 플래그 저장 (emp_tax_profile upsert 등)
    // サマリー行フラグの保存（emp_tax_profile のUPSERT など）
    int upsertPayslipFlags(@Param("empId") Long empId,
                           @Param("yyyymm") String yyyymm,
                           @Param("payType") String payType,
                           @Param("f") EmpFlag f);

    // 확정 상태 체크/변경
    // 確定状態の確認/変更
    int countConfirmedPayslips(@Param("yyyymm") String yyyymm,
                               @Param("payType") String payType,
                               @Param("empNos") List<String> empNos);
    // 선택 대상 중 확정된 payslip 개수
    // 選択対象のうち確定済み payslip 件数

    int confirmPayslips(@Param("yyyymm") String yyyymm,
                        @Param("payType") String payType,
                        @Param("empNos") List<String> empNos);
    // payslip 확정 처리
    // payslip の確定処理

    int unconfirmPayslips(
            @Param("yyyymm") String yyyymm,
            @Param("payType") String payType,
            @Param("empNos") List<String> empNos
    );
    // payslip 확정 해제
    // payslip の確定解除

    // ===== 월집계(pay_month_summary) =====
    // ===== 月次集計（pay_month_summary） =====
    Map<String, Object> calcMonthRollup(@Param("yyyymm") String yyyymm,
                                        @Param("payType") String payType);
    // 해당 연월/유형의 집계값 계산(미리보기용 맵)
    // 当該年月/区分の集計値を計算（プレビュー用のマップ）

    int upsertMonthlySummaryMonth(@Param("payType") String payType,
                                  @Param("yyyymm") String yyyymm,
                                  @Param("totalPayAmt") Long totalPayAmt,
                                  @Param("prevPaidAmt") Long prevPaidAmt,
                                  @Param("totalDedAmt") Long totalDedAmt,
                                  @Param("netPayAmt") Long netPayAmt);
    // 월별 합계 업서트
    // 月次合計をUPSERT

    // ===== 전표 처리(pay_voucher, pay_voucher_line) =====
    // ===== 伝票処理（pay_voucher, pay_voucher_line） =====
    Integer hasVoucherByToken(@Param("token") String token);
    // 토큰으로 전표 존재 여부 확인
    // トークンで伝票の有無を確認

    int insertVoucherHeaderByToken(@Param("yyyymm") String yyyymm,
                                   @Param("token") String token);
    // 토큰 기준 전표헤더 생성
    // トークン基準で伝票ヘッダを作成

    Long findVoucherIdByToken(@Param("token") String token);
    // 토큰으로 voucher_id 조회
    // トークンから voucher_id を取得

    int upsertVoucherLinesForPayslip(@Param("payslipId") Long payslipId,
                                     @Param("voucherId") Long voucherId,
                                     @Param("wageAccountId") Long wageAccountId,
                                     @Param("withholdAccountId") Long withholdAccountId);
    // payslip 기반 전표 라인 업서트(임금/원천징수 계정 반영)
    // payslip に基づく伝票行のUPSERT（賃金/源泉徴収勘定を反映）

    // ===== 세금 재처리 =====
    // ===== 税金再処理 =====
    int deleteTaxDeductions(@Param("payslipId") Long payslipId);
    // 기존 세금 공제항목 삭제
    // 既存の税控除項目を削除

    int insertTaxDeduction(@Param("payslipId") Long payslipId,
                           @Param("code") String code,
                           @Param("name") String name,
                           @Param("amount") Long amount);
    // 세금 공제항목 추가
    // 税控除項目を追加

    // ===== YRT 반영 =====
    // ===== YRT 反映 =====
    YrtAdjustment findLatestYrtAdjustment(@Param("empId") Long empId,
                                          @Param("baseYear") Integer baseYear);
    // 최신 연말정산 결과 조회(사원/연도별)
    // 最新の年末調整結果を取得（社員/年度別）

    int insertEarningItem(@Param("payslipId") Long payslipId,
                          @Param("itemName") String itemName,
                          @Param("amount") Long amount);
    // 지급항목 추가
    // 支給項目を追加

    int insertDeductionItem(@Param("payslipId") Long payslipId,
                            @Param("code") String code,
                            @Param("name") String name,
                            @Param("amount") Long amount);
    // 공제항목 추가
    // 控除項目を追加

    int updateEmpRetiredYn(@Param("empId") Long empId,
                           @Param("yyyymm") String yyyymm,
                           @Param("retiredYn") String retiredYn);
    // 퇴직 여부 업데이트
    // 退職有無を更新

    // 월별 합계 업서트
    // 月次合計 UPSERT
    int upsertMonthSummary(@Param("yyyymm") String yyyymm,
                           @Param("payType") String payType);
}
