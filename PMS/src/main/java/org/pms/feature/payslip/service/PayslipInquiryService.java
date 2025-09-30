package org.pms.feature.payslip.service;

import lombok.RequiredArgsConstructor;
import org.pms.feature.payslip.domain.PayslipListRow;
import org.pms.feature.payslip.mapper.PayslipMapper;
import org.pms.feature.payslip.domain.PayTypeCode;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 급여명세 목록 조회 전용 서비스 / 給与明細一覧取得サービス
 * - Controller 등에서 호출되는 읽기 전용 유스케이스 / コントローラから呼ばれる読み取り専用ユースケース
 * - DB 접근은 Mapper에 위임 / DBアクセスはMapperへ委譲
 * - 이 레이어는 입력값 검증/흐름 제어에 집중 / このレイヤは入力値チェック・フロー制御に集中
 */
@Service
@RequiredArgsConstructor // 생성자 주입 자동 / コンストラクタ注入を自動生成
public class PayslipInquiryService {

    // MyBatis Mapper 의존성 / MyBatis Mapper依存
    private final PayslipMapper mapper;

    /**
     * 급여명세 목록 조회 / 給与明細リスト取得
     * - empNo: 사번 / 社員番号
     * - fromYm ~ toYm: 기간(YYYYMM) / 期間(YYYYMM)
     * - payType: 급여유형 코드 / 給与種類コード
     * - excludeZero: 0원 내역 제외 여부("Y"/"N") / 金額0の明細除外("Y"/"N")
     */
    public List<PayslipListRow> search(String empNo, String fromYm, String toYm,
                                       String payType, String excludeZero) {
        // 실제 쿼리는 Mapper에 위임하고 결과 반환 / 実SQLはMapperに任せて結果を返す
        return mapper.selectPayslipList(empNo, fromYm, toYm, payType, excludeZero);
    }

    /**
     * 급여유형 코드 목록(드롭다운 등 UI용) / 給与種類コード一覧（ドロップダウン等のUI用）
     */
    public List<PayTypeCode> getPayTypeCodes() {
        return mapper.selectPayTypeCodes();
    }
}
