package org.pms.feature.yearendtaxsimulation.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.pms.feature.yearendtaxsimulation.domain.InstallmentEntry;
import org.pms.feature.yearendtaxsimulation.domain.InstallmentResponse;
import org.pms.feature.yearendtaxsimulation.domain.ResultTotal;
import org.pms.feature.yearendtaxsimulation.domain.SimHeaderMini;
import org.pms.feature.yearendtaxsimulation.domain.SimItemRow;
import org.pms.feature.yearendtaxsimulation.mapper.YearendTaxSimMapper;

/* 시뮬레이션 실행/삭제/분납 계산 서비스 / シミュレーション実行・削除・分納計算サービス */
@Service
@RequiredArgsConstructor
@Slf4j
public class YearendTaxSimCommandService {

    private final YearendTaxSimMapper mapper;              // DB 접근 매퍼 / DBアクセスマッパー
    private final YearendTaxSimQueryService query;         // 조회용 서비스 / 参照用サービス

    /**
     * 시뮬레이션 실행
     * - overwrite=true면 기존 미확정 데이터를 삭제
     * - 확정건(Y) 데이터를 복제하여 시뮬레이션(N) 생성
     * - 간이 세액결과 계산 및 결과테이블 upsert
     *
     * シミュレーション実行
     * - overwrite=true の場合、既存の未確定データを削除
     * - 確定データをコピーして未確定のシミュレーションを生成
     * - 簡易税額結果を計算して結果テーブルをアップサート
     */
    @Transactional
    public Long run(String empId, Integer baseYear, Long policyId, String runLabel, boolean overwrite) {
        // 0) 파라미터 검증 / パラメータ検証
        if (empId == null || empId.trim().isEmpty()) {
            throw new IllegalArgumentException("empId is required");
        }
        if (baseYear == null) {
            throw new IllegalArgumentException("baseYear is required");
        }

        // 1) overwrite면 해당 사번+연도 미확정 데이터 삭제 / overwriteの場合、未確定データ削除
        if (overwrite) {
            mapper.deleteUnconfirmedForEmpYear(empId, baseYear);
        }

        // 2) 헤더 생성(새로운 미확정건) / ヘッダ作成（未確定）
        SimHeaderMini header = new SimHeaderMini();
        header.setEmpId(empId);
        header.setBaseYear(baseYear);
        header.setRunLabel(runLabel);
        mapper.insertHeader(header); // selectKey로 yrtId 생성 / selectKeyでyrtId採番
        Long yrtId = header.getYrtId();

     // 3) 확정 데이터 복제 시딩 / 確定データのコピーシーディング
        List<SimItemRow> finalRows = mapper.selectFinalGrid(empId, baseYear);
        if (finalRows == null || finalRows.isEmpty()) {
            log.warn("確定データがないため、デフォルトのサンプル項目に置き換えます / 確定データが存在しないため、サンプル項目を使用します");
            seedScenarioItems(yrtId); //バックアップ用シーディング
        } else {
            for (SimItemRow row : finalRows) {
                mapper.insertItem(yrtId,
                        row.getItemClass(),
                        row.getItemName(),
                        row.getAmount(),
                        row.getExpectedAmount());
            }
            log.info("確定データ{}件複製完了 / 確定データ {}件をコピー完了", finalRows.size());
        }



        // 4) 간이 세액 결과 계산 / 簡易税額結果計算
        long taxCreditSum   = mapper.selectTaxCreditSum(empId, baseYear, yrtId);
        long standardCredit = mapper.selectStandardCredit(baseYear);
        long addNational = Math.max(0, taxCreditSum - standardCredit);
        long addLocal    = Math.round(addNational * 0.1);

        // 5) 결과 합계 upsert / 結果合計アップサート
        mapper.upsertResultTotal(yrtId, addNational, addLocal);

        // 6) 실행 ID 반환 / 実行IDを返却
        return yrtId;
    }

    /** 시뮬레이션 결과 삭제 / シミュレーション結果削除 */
    @Transactional
    public boolean delete(Long yrtId) {
        if (yrtId == null) {
            log.warn("削除要請失敗: yrtId 無し");
            return false;
        }
        try {
            int deleted = mapper.deleteByYrtId(yrtId);
            log.info("yrtId={} 削除結果{}件", yrtId, deleted);
            return deleted > 0;
        } catch (Exception e) {
            log.error("削除中に例外発生", e);
            return false;
        }
    }

    /** 분납 계산 / 分納計算 */
    @Transactional(readOnly = true)
    public InstallmentResponse installment(Long yrtId, int months, String startMonth) {
        if (yrtId == null) {
            throw new IllegalArgumentException("yrtIdが必要です。");
        }
        if (months < 2 || months > 12) {
            throw new IllegalArgumentException("분납 개월수는 2~12 범위여야 합니다。");
        }

        ResultTotal rt = query.findResultTotal(yrtId);
        if (rt == null) {
            throw new IllegalStateException("分納月数は2~12の範囲でなければなりません。");
        }

        long nat = rt.getAddNational() == null ? 0L : rt.getAddNational();
        long loc = rt.getAddLocal() == null ? 0L : rt.getAddLocal();

        List<InstallmentEntry> list = new ArrayList<>();
        YearMonth ym;
        try {
            ym = YearMonth.parse(startMonth);
        } catch (Exception e) {
            log.warn("開始月形式エラー: {}", startMonth);
            ym = YearMonth.now();
        }

        long natBase = (months <= 1) ? nat : nat / months;
        long locBase = (months <= 1) ? loc : loc / months;

        for (int i = 0; i < months; i++) {
            long natPay = (i == months - 1) ? (nat - natBase * (months - 1)) : natBase;
            long locPay = (i == months - 1) ? (loc - locBase * (months - 1)) : locBase;
            list.add(new InstallmentEntry(
                    ym.plusMonths(i).toString(), natPay, locPay, natPay + locPay
            ));
        }

        return new InstallmentResponse(list, "均等分割、残額は最後の月加算");
    }

    /* -----------------------------------------------------
     * 예시 항목 시딩 (백업용) / サンプル項目シーディング（バックアップ用）
     * ----------------------------------------------------- */
    private void seedScenarioItems(Long yrtId) {
        mapper.insertItem(yrtId, "税額控除", "子供税額控除", 500_000L, 500_000L);
        mapper.insertItem(yrtId, "税額控除", "保険料税額控除", 300_000L, 300_000L);
        mapper.insertItem(yrtId, "税額控除", "教育費税額控除", 400_000L, 400_000L);
        mapper.insertItem(yrtId, "税額控除", "寄付金税額控除", 200_000L, 200_000L);
    }
}

