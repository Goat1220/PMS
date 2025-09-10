package org.pms.feature.runpayroll.domain;
import lombok.Data;

@Data
public class PayrollSearchParam {
    private String yyyymm;   // ex) 2018-08
    private String payType;  // ex) SALARY
    private String deptCode; // optional
    private String empNo;    // optional
}
