package org.pms.feature.runpayroll.domain;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class SimpleResult {
    private boolean success;
    private int affected;
    private String message;

    public static SimpleResult ok(int affected){ return new SimpleResult(true, affected, null); }
    public static SimpleResult fail(String msg){ return new SimpleResult(false, 0, msg); }
}
