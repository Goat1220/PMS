package org.pms.feature.payslip.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.payslip.domain.*;

// [KO] XML namespace는 반드시 아래와 같아야 함
// [JA] XML の namespace は必(かなら)ず下(した)と一致(いっち)
// <mapper namespace="org.pms.feature.payslip.infra.PayslipMapper">

public interface PayslipMapper {

    // =========================
    // [KO] 개인 상세 조회용 (사번+연월)
    // [JA] 個人サマリー取得（社員番号 + 年月）
    // XML: <select id="selectPayslipSummary"> で #{empNo}, #{ym} を使用
    // => @Param 이름과 XML의 #{...} 이름이 정확히 동일해야 함
    // =========================
    PayslipSummary selectPayslipSummary(
            @Param("empNo") String empNo,
            @Param("ym")    String periodYm
    );

    // [KO] 지급 항목 조회 (payslipId 기준)
    // [JA] 支給項目一覧
    // XML: <select id="selectPayItems"> #{payslipId}
    List<PayItem> selectPayItems(@Param("payslipId") Long payslipId);

    // [KO] 공제 항목 조회 (payslipId 기준)
    // [JA] 控除項目一覧
    // XML: <select id="selectDeductionItems"> #{payslipId}
    List<DeductionItem> selectDeductionItems(@Param("payslipId") Long payslipId);

    // =========================
    // [KO] 목록/검색 조건 기반 조회 (inquiry 화면)
    // [JA] 一覧検索
    // - excludeZero: "Y" → 0원 항목 제외 / "N" → 포함 (XML에서 조건 분기)
    // =========================
    List<PayslipListRow> selectPayslipList(
            @Param("empNo")       String empNo,
            @Param("fromYm")      String fromYm,
            @Param("toYm")        String toYm,
            @Param("payType")     String payType,
            @Param("excludeZero") String excludeZero
    );

    // [KO] payslipId로 단건 요약 조회
    // [JA] ID 指定サマリー取得
    PayslipSummary selectPayslipSummaryById(@Param("payslipId") Long payslipId);

    // [KO] 급여유형 코드 목록 (드롭다운용)
    // [JA] 給与種類コード一覧
    List<PayTypeCode> selectPayTypeCodes();
}


