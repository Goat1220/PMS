package org.pms.feature.runpayroll.domain;

import lombok.Data;

@Data
public class PayrollSummaryRow {
    private String empNo;                 
    // 사번  
    // 社員番号

    private String empName;               
    // 사원명  
    // 氏名

    private String deptName;              
    // 부서명  
    // 部署名

    private String taxApplyType;          
    // 세금 적용 유형  
    // 税適用タイプ

    private String taxAdjustRate;         
    // 세액 조정율  
    // 税額調整率

    private String projectName;           
    // 프로젝트명  
    // プロジェクト名

    private String taxCalcExemptYn;       
    // 세금 계산 제외 여부 (Y/N)  
    // 税計算除外（Y/N）

    private String prorateYn;             
    // 일할 계산 여부 (Y/N)  
    // 日割計算（Y/N）

    // XML 별칭에 맞춘 필드명들  
    // XML エイリアスに合わせたフィールド名
    private String settlementReflectYn;   
    // 정산(연말정산) 반영 여부 (기존 settlementYn 과 다름)  
    // 年末調整反映（旧 settlementYn とは異なる）

    private String manufTaxExemptYn;      
    // 생산직 비과세 여부 (기존 nonTaxProdYn 과 다름)  
    // 生産職非課税（旧 nonTaxProdYn とは異なる）

    private String overseasTaxExemptYn;   
    // 국외 근로 비과세 여부 (기존 foreignWorkYn 과 다름)  
    // 海外勤務非課税（旧 foreignWorkYn とは異なる）

    private String researcherTaxExemptYn; 
    // 연구원 비과세 여부 (기존 researcherYn 과 다름)  
    // 研究員非課税（旧 researcherYn とは異なる）

    private String incomeTaxReductionRate;
    // 소득세 감면율  
    // 所得税減免率

    private String personalTaxApplyType;  
    // 세금 적용(개인) 유형  
    // 個人税適用タイプ

    private String bonusRate;             
    // 상여율  
    // 賞与率

    private Long payTotAmt;               
    // 지급 총액  
    // 支給総額

    private Long prevPayTotAmt;           
    // 기지급 총액  
    // 既支給総額

    private Long dedTotAmt;               
    // 공제 총액  
    // 控除総額

    private Long netPayAmt;               
    // 실지급액  
    // 実支給額

    private String retireYn;              
    // 퇴직 여부 (Y/N)  
    // 退職有無（Y/N）
}
