package org.pms.feature.payslip.domain;

import lombok.Data;

/*
 *  지급 항목(수당 등) 한 줄을 표현하는 VO
 * - chkPaid, chkValid는 화면 체크박스/상태값과 연동되는 문자열("Y"/"N")을 가정
 *
 *  支給項目を表す VO
 * - chkPaid, chkValid は 画面チェック用の "Y"/"N" を想定
 */
@Data
public class PayItem {

    //  지급항목명. 예: "기본급", "식대"
    //  支給項目名
    private String itemName;

    //  실제로 지급 처리되었는지 여부("Y"/"N")
    //  実際に支給処理されたか（"Y"/"N"）
    private String chkPaid;

    //  유효 항목인지 여부("Y"/"N") — 계산 포함/제외 제어에 사용 가능
    //  有効かどうか（"Y"/"N"）— 計算の含め/除外制御
    private String chkValid;

    //  지급 금액(원)
    //  支給金額
    private Long   amount;
}


