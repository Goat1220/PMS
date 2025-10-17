package org.pms.feature.report.service;

import java.math.BigDecimal; // 큰 숫자를 정확하게 다루기 위한 라이브러리 / 大きな数字を正確に扱うためのライブラリ
import java.util.Arrays; // 배열을 다루기 위한 유틸리티 / 配列を扱うためのユーティリティ
import java.util.HashMap; // 키-값 쌍으로 데이터를 저장 / キー値のペアでデータを保存
import java.util.LinkedHashMap; // 삽입 순서를 유지하는 맵 / 挿入順序を保持するマップ
import java.util.List; // 리스트 데이터 타입 / リストデータ型
import java.util.Map; // 맵 데이터 타입 / マップデータ型

import org.pms.feature.report.domain.AnnexRow; // 부표 행 데이터 클래스 / 別紙行データクラス
import org.pms.feature.report.domain.WithholdingRow; // 원천징수 행 데이터 클래스 / 源泉徴収行データクラス
import org.pms.feature.report.domain.WithholdingSearch; // 원천징수 검색 조건 클래스 / 源泉徴収検索条件クラス
import org.pms.feature.report.mapper.WithholdingReportMapper; // DB와 통신하는 매퍼 인터페이스 / DBと通信するマッパーインターフェース
import org.springframework.stereotype.Service; // 이 클래스가 서비스임을 표시하는 어노테이션 / このクラスがサービスであることを示すアノテーション
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 처리를 위한 어노테이션 / トランザクション処理のためのアノテーション

import lombok.RequiredArgsConstructor; // 생성자 자동 생성 어노테이션 / コンストラクタ自動生成アノテーション

@Service // 이 클래스는 비즈니스 로직을 처리하는 서비스 계층 / このクラスはビジネスロジックを処理するサービス層です
@RequiredArgsConstructor // mapper를 생성자 주입으로 자동 설정 / mapperをコンストラクタ注入で自動設定
public class WithholdingReportService {

    private final WithholdingReportMapper mapper; // DB 접근 객체 / DBアクセスオブジェクト

    /** 요약 데이터 조회(+ A01 주입, + 모든 가감계/총합계 자동 합산) / サマリーデータ조회(+ A01注입、+ すべての加減計/総合計自動合算) */
    public List<WithholdingRow> getSummary(WithholdingSearch cond) {
        // DB에서 요약 데이터 조회 / DBからサマリーデータを検索
        List<WithholdingRow> rows = mapper.selectSummary(cond);

        // 코드별로 행을 맵에 저장 (나중에 쉽게 찾기 위함) / コード別に行をマップに保存 (後で簡単に検索するため)
        Map<String, WithholdingRow> byCode = new HashMap<String, WithholdingRow>();
        for (WithholdingRow r : rows) {
            byCode.put(r.getCode(), r); // 코드를 키로, 행 데이터를 값으로 저장 / コードをキーとして、行データを値として保存
        }

        // 1) 상단 실제 원장 합계 주입: A01 / 1) 上部実際元帳合計注入: A01
        // 전표에서 차변 합계 조회 / 伝票から借方合計を検索
        BigDecimal totalPayment = mapper.sumVoucherDebitByMonth(cond.getApplyYyyymm());
       
        // 징수된 소득세 합계 조회 / 徴収された所得税合計を検索
        BigDecimal withheldTax  = mapper.sumWithheldTaxByMonthByPrefix(cond.getApplyYyyymm());
        
        // 해당 월의 직원 수 조회 / 該当月の従業員数を検索
        Integer headCount = mapper.countHeadsByMonth(cond.getApplyYyyymm());
        if (headCount == null) headCount = 0; // null이면 0으로 설정 / nullなら0に設定
        
        // A01 행 찾기 (맵에서 조회) / A01行を検索 (マップから)
        WithholdingRow a01 = byCode.get("A01");
        
        // A01이 존재하면 데이터 입력 / A01が存在すればデータを入力
        if (a01 != null) {
        	a01.setHeadCount(headCount); // 직원 수 설정 / 従業員数を設定
            a01.setTaxTotal(nL(totalPayment)); // 총지급액 설정 / 総支給額を設定
            a01.setTaxWithheld(nL(withheldTax)); // 징수세액 설정 / 徴収税額を設定
            a01.setTaxIncome(0L); // 납부소득세는 0으로 설정 / 納付所得税は0に設定
        }


        // 2) 섹션별 가감계 매핑 (LinkedHashMap: 선언 순서 유지) / 2) セクション別加減計マッピング (LinkedHashMap: 宣言順序を保持)
        Map<String, List<String>> groups = new LinkedHashMap<String, List<String>>();
        groups.put("A10", Arrays.asList("A01","A02","A03","A04","A05","A06")); // A10(소계) = A01~A06 합산 / A10(小計) = A01~A06合算
        groups.put("A20", Arrays.asList("A21","A22")); // A20(소계) = A21~A22 합산 / A20(小計) = A21~A22合算
        groups.put("A30", Arrays.asList("A25","A26")); // A30(소계) = A25~A26 합산 / A30(小計) = A25~A26合算
        groups.put("A40", Arrays.asList("A41","A42")); // A40(소계) = A41~A42 합산 / A40(小計) = A41~A42合算
        groups.put("A47", Arrays.asList("A45","A46","A48")); // A47(소계) = A45~A48 합산 / A47(小計) = A45~A48合算

        // 3) 각 가감계 코드에 열별 합산 반영 / 3) 各加減計コードに列別合算を反映
        for (Map.Entry<String, List<String>> e : groups.entrySet()) {
            String parent = e.getKey(); // 부모 코드 (예: A10) / 親コード (例: A10)
            List<String> children = e.getValue(); // 자식 코드들 (예: A01, A02, ...) / 子コード (例: A01、A02、...)

            // 부모 코드의 행 찾기 / 親コードの行を検索
            WithholdingRow tgt = byCode.get(parent);
            if (tgt == null) continue; // 부모가 없으면 스킵 / 親がなければスキップ

            // 자식들의 값을 합산하기 위한 변수들 / 子の値を合算するための変数
            long head=0, tot=0, ntw=0, taxInc=0, pen=0, taxWh=0, adj=0, nt=0;

            // 모든 자식 행들을 반복하면서 값 합산 / すべての子行を反復しながら値を合算
            for (String child : children) {
                WithholdingRow r = byCode.get(child); // 자식 행 찾기 / 子行を検索
                if (r == null) continue; // 자식이 없으면 스킵 / 子がなければスキップ
                
                // 각 필드의 값을 합산 / 各フィールドの値を合算
                head += n(r.getHeadCount()); // 인원 합산 / 人員を合算
                tot  += n(r.getTaxTotal()); // 총지급액 합산 / 総支給額を合算
                ntw  += n(r.getNtWithheld()); // 징수농특세 합산 / 徴収農特税を合算
                taxInc += n(r.getTaxIncome()); // 납부소득세 합산 / 納付所得税を合算
                pen  += n(r.getPenaltyTax()); // 가산세 합산 / 加算税を合算
                taxWh += n(r.getTaxWithheld()); // 징수소득세 합산 / 徴収所得税を合算
                adj  += n(r.getAdjRefund()); // 조정환급세액 합산 / 調整還付税額を合算
                nt   += n(r.getTaxNt()); // 납부농특세 합산 / 納付農特税を合算
            }

            // 합산된 값들을 부모 행에 설정 / 合算された値を親行に設定
            tgt.setHeadCount((int) head); // 인원 설정 / 人員を設定
            tgt.setTaxTotal(tot); // 총지급액 설정 / 総支給額を設定
            tgt.setNtWithheld(ntw); // 징수농특세 설정 / 徴収農特税を設定
            tgt.setTaxWithheld(taxWh); // 징수소득세 설정 / 徴収所得税を設定
            tgt.setAdjRefund(adj); // 조정환급세액 설정 / 調整還付税額を設定
            tgt.setTaxIncome(taxWh - adj); // 납부소득세 = 징수소득세 - 조정환급세액 / 納付所得税 = 徴収所得税 - 調整還付税額
            tgt.setPenaltyTax(pen); // 가산세 설정 / 加算税を設定
            tgt.setTaxNt(nt); // 납부농특세 설정 / 納付農特税を設定
        }

        // 4) 총합계 A99 = 가감계(소계) 라인만 합산 / 4) 総合計 A99 = 加減計(小計)行のみ合算
        WithholdingRow a99 = byCode.get("A99"); // 총합계 행 찾기 / 総合計行を検索
        if (a99 != null) {
            // 가감계 부모 코드 목록만 더한다 (전체 라인을 다 더하면 안됨) / 加減計親コードのみを加算 (全行を加算するとNG)
            List<String> totalParents = Arrays.asList("A10","A20","A30","A40","A47"); // 5개의 소계만 / 5つの小計のみ
            
            // 합산 변수들 초기화 / 合算変数を初期化
            long head=0, tot=0, ntw=0, pen=0, taxWh=0, adj=0, nt=0;

            // 각 소계 행의 값들을 합산 / 各小計行の値を合算
            for (String p : totalParents) {
                WithholdingRow r = byCode.get(p); // 소계 행 찾기 / 小計行を検索
                if (r == null) continue; // 없으면 스킵 / なければスキップ
                
                // 각 필드 합산 / 各フィールドを合算
                head += n(r.getHeadCount()); // 인원 / 人員
                tot  += n(r.getTaxTotal()); // 총지급액 / 総支給額
                ntw  += n(r.getNtWithheld()); // 징수농특세 / 徴収農特税
                pen  += n(r.getPenaltyTax()); // 가산세 / 加算税
                taxWh += n(r.getTaxWithheld()); // 징수소득세 / 徴収所得税
                adj  += n(r.getAdjRefund()); // 조정환급세액 / 調整還付税額
                nt   += n(r.getTaxNt()); // 납부농특세 / 納付農特税
            }
            
            // 합산된 값들을 A99에 설정 / 合算された値をA99に設定
            a99.setHeadCount((int) head); // 인원 설정 / 人員を設定
            a99.setTaxTotal(tot); // 총지급액 설정 / 総支給額を設定
            a99.setNtWithheld(ntw); // 징수농특세 설정 / 徴収農特税を設定
            a99.setPenaltyTax(pen); // 가산세 설정 / 加算税を設定
            a99.setTaxWithheld(taxWh); // 징수소득세 설정 / 徴収所得税を設定
            a99.setAdjRefund(adj); // 조정환급세액 설정 / 調整還付税額を設定
            a99.setTaxNt(nt); // 납부농특세 설정 / 納付農特税を設定
            a99.setTaxIncome(taxWh - adj); // 납부소득세 = 징수소득세 - 조정환급세액 / 納付所得税 = 徴収所得税 - 調整還付税額
        }

        // 최종적으로 계산된 모든 행을 반환 / 最終的に計算されたすべての行を返す
        return rows;
    }

    /** null-safe long 변환 (Number 타입을 long으로) / null-safe long変換 (Number型をlongに) */
    private long n(Number v) {
        return (v == null) ? 0L : v.longValue(); // null이면 0L, 아니면 long으로 변환 / nullなら0L、そうでなければlongに変換
    }
    
    /** BigDecimal → long (null 안전) / BigDecimal → long (null安全) */
    private long nL(BigDecimal v) {
        return (v == null) ? 0L : v.longValue(); // null이면 0L, 아니면 long으로 변환 / nullなら0L、そうでなければlongに変換
    }

    /** 부표 데이터 조회 (변경 없음) / 別紙データ조회 (変更なし) */
    public List<AnnexRow> getAnnex(WithholdingSearch cond) {
        // DB에서 부표 데이터를 조회해서 그대로 반환 / DBから別紙データを検索して返す
        return mapper.selectAnnex(cond);
    }
    
    /** (써야할때 써) 요약/부표 대상 데이터 집계·저장 로직 / (使う時に使う) サマリー/別紙対象データ集計·保存ロジック */
    public void generate(String applyYyyymm) {
    	  // TODO: 요약/부표 대상 데이터 집계·저장 로직 (나중에 구현 필요) / TODO: サマリー/別紙対象データ集計·保存ロジック (後で実装必要)
    	}
    
    // ===========================
    // 전월 미환급세액 조회 / 저장
    // 前月未還付税額 조회 / 保存
    // ===========================
    
    /** 전월 J(차월이월환급세액) 조회 / 前月 J(翌月繰越還付税額) 조회 */
    public BigDecimal findPrevJ(String yyyymm) {
        // DB에서 전월 J 값 조회 / DBから前月 J値を検索
        BigDecimal v = mapper.selectPrevJ(yyyymm);
        // null이면 ZERO(0) 반환, 아니면 그 값 반환 / nullならZERO(0)を返す、そうでなければその値を返す
        return (v == null) ? BigDecimal.ZERO : v;
    }

    /** 전월 K(환급신청금액) 조회 / 前月 K(還付申請金額) 조会 */
    public BigDecimal findPrevK(String yyyymm) {
        // DB에서 전월 K 값 조회 / DBから前月 K値を検索
        BigDecimal v = mapper.selectPrevK(yyyymm);
        // null이면 ZERO(0) 반환, 아니면 그 값 반환 / nullならZERO(0)を返す、そうでなければその値を返す
        return (v == null) ? BigDecimal.ZERO : v;
    }
   

    /** 이번달 J/K 저장 (데이터 생성 버튼에서 호출) / 今月 J/K 保存 (データ生成ボタンから呼び出し) */
    @Transactional // 이 메서드 실행 중 에러 발생하면 롤백 / このメソッド実行中にエラー発생したらロールバック
    public void saveRefund(String yyyymm, BigDecimal jValue, BigDecimal kValue) {
        // J값이 null이면 ZERO로 설정 / J値がnullなら ZEROに設定
        if (jValue == null) jValue = BigDecimal.ZERO;
        
        // K값이 null이면 ZERO로 설정 / K値がnullなら ZEROに設定
        if (kValue == null) kValue = BigDecimal.ZERO;
        
        // DB에 저장 또는 업데이트 / DBに保存または更新
        mapper.upsertRefund(yyyymm, jValue, kValue);
    }
}