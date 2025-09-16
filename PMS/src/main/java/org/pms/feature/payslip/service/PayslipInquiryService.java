package org.pms.feature.payslip.service;

import lombok.RequiredArgsConstructor;
import org.pms.feature.payslip.domain.PayslipListRow;
import org.pms.feature.payslip.mapper.PayslipMapper;
import org.pms.feature.payslip.domain.PayTypeCode;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * [KO] 급여명세 조회 전용 서비스 (목록)
 * - Controller 등에서 의존하는 읽기 전용 유스케이스.
 * - DB 접근은 Mapper에 위임. 여기서는 "입력값 검증/흐름 제어" 중심.
 *
 * [JA] 給与(きゅうよ)明細(めいさい)の一覧(いちらん)取得(しゅとく)サービス（リスト用）
 * - Controller から呼(よ)ばれる読み取り専用(せんよう)ユースケース。
 * - DB へのアクセスは Mapper に委譲(いじょう)。ここでは入力値(にゅうりょくち)チェックや流れの制御(せいぎょ)に集中(しゅうちゅう)。
 */
@Service
@RequiredArgsConstructor // [KO] 생성자 주입 자동 생성 / [JA] コンストラクタ注入(ちゅうにゅう)を自動生成(じどうせいせい)
public class PayslipInquiryService {

    // [KO] MyBatis Mapper 의존성 (생성자 주입됨)
    // [JA] MyBatis の Mapper 依存性(いぞんせい)（コンストラクタで注入）
    private final PayslipMapper mapper;

    /**
     * [KO] 급여명세 목록 조회
     *  - empNo: 사번
     *  - fromYm ~ toYm: 조회 기간(YYYYMM)
     *  - payType: 급여유형(정기/비정기 등 코드값)
     *  - excludeZero: 0원 내역 제외 여부("Y"/"N")
     *
     * [JA] 給与明細(きゅうよめいさい)リスト取得
     *  - empNo: 社員番号(しゃいんばんごう)
     *  - fromYm ~ toYm: 期間(きかん)（YYYYMM）
     *  - payType: 給与種類(しゅるい)コード
     *  - excludeZero: 金額(きんがく)0の明細(めいさい)を除外(じょがい)するか（"Y"/"N"）
     */
    public List<PayslipListRow> search(String empNo, String fromYm, String toYm,
                                       String payType, String excludeZero) {
        // [KO] 실제 쿼리는 Mapper에 위임. 서비스는 파라미터를 전달하고 결과를 그대로 반환.
        // [JA] 実際(じっさい)の SQL は Mapper に任(まか)せ、引数(ひきすう)を渡(わた)して結果(けっか)を返(かえ)すだけ。
        return mapper.selectPayslipList(empNo, fromYm, toYm, payType, excludeZero);
    }

    /**
     * [KO] 급여유형 코드 목록 조회 (드롭다운 등 UI용)
     * [JA] 給与種類(しゅるい)コード一覧(いちらん)取得（ドロップダウン等(など)の UI 用(よう)）
     */
    public List<PayTypeCode> getPayTypeCodes() {
        return mapper.selectPayTypeCodes();
    }
}
