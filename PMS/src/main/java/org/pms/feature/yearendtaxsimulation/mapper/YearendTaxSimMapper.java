package org.pms.feature.yearendtaxsimulation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.yearendtaxsimulation.domain.*;

/* 연말정산 시뮬레이션용 MyBatis 매퍼 인터페이스 / 年末調整シミュレーション用 MyBatis マッパー */
public interface YearendTaxSimMapper {

    /* 최종 탭 그리드 조회 / 最終タブのグリッド取得 */
    List<SimItemRow> selectFinalGrid(@Param("empId") String empId,
                                     @Param("baseYear") Integer baseYear);

    /* 시뮬 탭 그리드 조회(특정 실행ID 기준) / シミュタブのグリッド取得（実行ID基準） */
    List<SimItemRow> selectSimGrid(@Param("empId") String empId,
                                   @Param("baseYear") Integer baseYear,
                                   @Param("yrtId") Long yrtId);

    /* 최신 헤더 조회(확정만 여부 선택) / 最新ヘッダ取得（確定のみ選択可） */
    SimHeaderMini selectLatestHeader(@Param("empId") String empId,
                                     @Param("baseYear") Integer baseYear,
                                     @Param("confirmedOnly") boolean confirmedOnly);

    /* 결과 합계 조회(국세/지방세) / 結果合計取得（国税/地方税） */
    ResultTotal selectResultTotal(@Param("yrtId") Long yrtId);

    /* 특정 사원·연도의 미확정 데이터 삭제 / 特定社員・年の未確定データ削除 */
    int deleteUnconfirmedForEmpYear(@Param("empId") String empId,
                                    @Param("baseYear") Integer baseYear);

    /* 헤더 입력( selectKey 로 yrtId 세팅) / ヘッダ挿入（selectKeyでyrtId設定） */
    void insertHeader(SimHeaderMini header); // selectKey로 yrtId 채움 / selectKeyでyrtId付与

    /* 항목 삽입(분류/이름/금액/예상) / 項目挿入（分類/名称/金額/予想） */
    int insertItem(@Param("yrtId") Long yrtId,
                   @Param("cls") String cls,
                   @Param("name") String name,
                   @Param("amt") Long amt,
                   @Param("exp") Long exp);

    /* 결과 합계 업서트(국세/지방세) / 結果合計のアップサート（国税/地方税） */
    int upsertResultTotal(@Param("yrtId") Long yrtId,
                          @Param("nat") Long nat,
                          @Param("loc") Long loc);

    /* 실행ID로 전체 삭제 / 実行IDで全削除 */
    int deleteByYrtId(@Param("yrtId") Long yrtId);

    /* 세액공제 합계(시뮬 결과 기준) / 税額控除合計（シミュ結果基準） */
    Long selectTaxCreditSum(@Param("empId") String empId,
                            @Param("baseYear") Integer baseYear,
                            @Param("yrtId") Long yrtId);

    /* 표준세액공제(연도 기준) / 標準税額控除（年基準） */
    Long selectStandardCredit(@Param("baseYear") Integer baseYear);
}

