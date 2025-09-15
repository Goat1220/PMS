package org.pms.feature.payslip.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pms.feature.payslip.domain.*;
import org.pms.feature.payslip.infra.PayslipMapper;

/*
 * [KO] 급여명세 상세 조회 서비스
 * - "요약 + 항목(지급/공제)"을 한 번에 구성해서 반환.
 * - 읽기 전용 트랜잭션으로 성능/일관성 확보.
 *
 * [JA] 給与明細(きゅうよめいさい)詳細(しょうさい)取得サービス
 * - 「サマリー + 明細項目(めいさいこうもく：支給/控除)」をまとめて組立(くみた)てて返(かえ)す。
 * - 読(よ)み取り専用(せんよう)トランザクションで整合性(せいごうせい)と性能(せいのう)を確保(かくほ)。
 */
@Service
@RequiredArgsConstructor
public class PayslipQueryService {

    // [KO] DB 접근을 담당하는 MyBatis Mapper
    // [JA] DB アクセスを担(にな)う MyBatis の Mapper
    private final PayslipMapper mapper;

    /**
     * [KO] 사번 + 귀속연월로 급여명세 조회
     *  1) 요약 조회 → 없으면 null 반환 (컨트롤러에서 404 처리 등)
     *  2) 요약의 payslipId로 지급항목/공제항목 조회
     *  3) 요약 객체에 항목 세팅 후 반환
     *
     * [JA] 社員番号 + 対象(たいしょう)年月(ねんげつ)で明細(めいさい)取得
     *  1) サマリーを取得 → 無(な)ければ null（Controller 側(がわ)で 404 など）
     *  2) サマリーの payslipId で 支給(しきゅう)/控除(こうじょ)項目(こうもく)を取得
     *  3) サマリーに一覧(いちらん)をセットして返却(へんきゃく)
     */
    @Transactional(readOnly = true) // [KO] 읽기 전용 → 변경 감지/플러시 비용 절감 / [JA] 読取専用で無駄(むだ)なコスト削減(さくげん)
    public PayslipSummary getPayslip(String empNo, String periodYm) {
        // [KO] 1) 요약 조회
        // [JA] 1) サマリー取得
        PayslipSummary summary = mapper.selectPayslipSummary(empNo, periodYm);
        if (summary == null) return null; // [KO] 없으면 그대로 반환 / [JA] 無ければ null

        // [KO] 2) 항목 조회 (지급/공제)
        // [JA] 2) 明細(めいさい)項目（支給/控除）を取得
        List<PayItem> payItems = mapper.selectPayItems(summary.getPayslipId());
        List<DeductionItem> dedItems = mapper.selectDeductionItems(summary.getPayslipId());

        // [KO] 3) 요약 객체에 세팅
        // [JA] 3) サマリーにセット
        summary.setPayItems(payItems);
        summary.setDeductionItems(dedItems);
        return summary;
    }

    /**
     * [KO] payslipId로 급여명세 상세 조회
     *  - 위 메서드와 동일한 흐름이지만, 식별자를 직접 받는 경우.
     *
     * [JA] payslipId で明細(めいさい)取得
     *  - 上(うえ)のメソッドと流(なが)れは同(おな)じだが、識別子(しきべつし)を直接(ちょくせつ)受(う)け取(と)るパターン。
     */
    @Transactional(readOnly = true)
    public PayslipSummary getPayslipById(Long payslipId) {
        if (payslipId == null) return null; // [KO] 방어 코드 / [JA] ガード節(せつ)

        PayslipSummary summary = mapper.selectPayslipSummaryById(payslipId);
        if (summary == null) return null;

        List<PayItem> payItems = mapper.selectPayItems(payslipId);
        List<DeductionItem> dedItems = mapper.selectDeductionItems(payslipId);

        summary.setPayItems(payItems);
        summary.setDeductionItems(dedItems);
        return summary;
    }
}


