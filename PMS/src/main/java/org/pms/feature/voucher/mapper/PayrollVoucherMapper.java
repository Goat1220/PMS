package org.pms.feature.voucher.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface PayrollVoucherMapper {

    /** 미리보기(차/대변 라인) */
    List<Map<String,Object>> selectVoucherPreviewLines(@Param("yyyymm") String yyyymm,
                                                       @Param("payType") String payType);

    /** 전표 헤더: 토큰(summary_note)으로 없으면 INSERT */
    int insertVoucherIfAbsent(@Param("yyyymm") String yyyymm,
                              @Param("token") String token);

    /** 토큰으로 voucher_id 조회 */
    Long findVoucherIdByToken(@Param("token") String token);

    /** 기존 라인 삭제 */
    int deleteVoucherLines(@Param("voucherId") Long voucherId);

    /** 차변 라인 INSERT (부서별 지급 합계) */
    int insertDebitLinesForMonth(@Param("voucherId") Long voucherId,
                                 @Param("yyyymm") String yyyymm,
                                 @Param("payType") String payType,
                                 @Param("accountId") Long accountId);

    /** 대변 라인 INSERT (부서별 공제 합계) */
    int insertCreditLinesForMonth(@Param("voucherId") Long voucherId,
                                  @Param("yyyymm") String yyyymm,
                                  @Param("payType") String payType,
                                  @Param("accountId") Long accountId);
}
