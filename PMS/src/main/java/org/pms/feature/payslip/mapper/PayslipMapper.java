package org.pms.feature.payslip.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.payslip.domain.*;

// XML namespace는 반드시 아래와 같아야 함 / XML の namespace は必ず下記と一致
// <mapper namespace="org.pms.feature.payslip.mapper.PayslipMapper">
public interface PayslipMapper {

    // =========================
    // 개인 요약 조회(사번+연월) / 個人サマリー取得（社員番号+年月）
    // XML: <select id="selectPayslipSummary"> で #{empNo}, #{ym} を使用
    // => @Param 이름과 XML의 #{...} 이름이 동일 / @Param 名とXMLの#{...}名を一致
    // =========================
    PayslipSummary selectPayslipSummary(
            @Param("empNo") String empNo,
            @Param("ym")    String periodYm
    );

    // 지급 항목 조회(payslipId 기준) / 支給項目一覧（payslipId基準）
    // XML: <select id="selectPayItems"> #{payslipId}
    List<PayItem> selectPayItems(@Param("payslipId") Long payslipId);

    // 공제 항목 조회(payslipId 기준) / 控除項目一覧（payslipId基準）
    // XML: <select id="selectDeductionItems"> #{payslipId}
    List<DeductionItem> selectDeductionItems(@Param("payslipId") Long payslipId);

    // =========================
    // 목록/검색 조회(inquiry 화면) / 一覧検索（inquiry画面）
    // excludeZero: "Y"=0원 제외 / "N"=포함（XML에서 분기）
    // =========================
    List<PayslipListRow> selectPayslipList(
            @Param("empNo")       String empNo,
            @Param("fromYm")      String fromYm,
            @Param("toYm")        String toYm,
            @Param("payType")     String payType,
            @Param("excludeZero") String excludeZero
    );

    // payslipId로 단건 요약 / ID指定サマリー取得
    PayslipSummary selectPayslipSummaryById(@Param("payslipId") Long payslipId);

    // 급여유형 코드 목록(드롭다운용) / 給与種類コード一覧（ドロップダウン用）
    List<PayTypeCode> selectPayTypeCodes();
}
