package org.pms.feature.payslip.domain;

import lombok.Data;

/*
 *  급여유형 코드 마스터 (드롭다운/검증에 사용)
 * - code: 내부 코드값, name: 표시용 명칭
 *
 *  給与種類コード・マスタ（ドロップダウン/検証用）
 * - code: 内部コード、name: 画面表示名
 */
@Data
public class PayTypeCode {

    //  코드값(예: "REG", "IRR")
    //  コード値
    private String code;

    //  코드명(예: "정기급여", "비정기급여")
    //  名称
    private String name;
}
