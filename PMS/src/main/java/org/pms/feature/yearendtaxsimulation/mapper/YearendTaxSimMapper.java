package org.pms.feature.yearendtaxsimulation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.pms.feature.yearendtaxsimulation.domain.*;

public interface YearendTaxSimMapper {

    // 최종 탭(확정) 그리드
    List<SimItemRow> selectFinalGrid(@Param("empId") String empId,
                                     @Param("baseYear") Integer baseYear);

    // 시뮬 탭(미확정 최신 or 특정 실행) 그리드
    List<SimItemRow> selectSimGrid(@Param("empId") String empId,
                                   @Param("baseYear") Integer baseYear,
                                   @Param("yrtId") Long yrtId);

    // 최신 헤더(확정/미확정 플래그로 선택)
    SimHeaderMini selectLatestHeader(@Param("empId") String empId,
                                     @Param("baseYear") Integer baseYear,
                                     @Param("confirmedOnly") boolean confirmedOnly);

    // 분납용 합계
    ResultTotal selectResultTotal(@Param("yrtId") Long yrtId);

    /* ===== 시뮬 간이 구현(테스트용) ===== */
    int deleteUnconfirmedForEmpYear(@Param("empId") String empId,
                                    @Param("baseYear") Integer baseYear);

    void insertHeader(SimHeaderMini header); // selectKey로 yrtId 채움

    int insertItem(@Param("yrtId") Long yrtId,
                   @Param("cls") String cls,
                   @Param("name") String name,
                   @Param("amt") Long amt,
                   @Param("exp") Long exp);

    int upsertResultTotal(@Param("yrtId") Long yrtId,
                          @Param("nat") Long nat,
                          @Param("loc") Long loc);

    int deleteByYrtId(@Param("yrtId") Long yrtId);

    /* ===== 세금적용결과 판정 ===== */
    Long selectTaxCreditSum(@Param("empId") String empId,
                            @Param("baseYear") Integer baseYear,
                            @Param("yrtId") Long yrtId);

    Long selectStandardCredit(@Param("baseYear") Integer baseYear);
}
