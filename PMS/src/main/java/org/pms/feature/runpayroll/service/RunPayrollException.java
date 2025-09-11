package org.pms.feature.runpayroll.service;

public class RunPayrollException extends RuntimeException {
    public RunPayrollException(String message) { super(message); }
    public RunPayrollException(String message, Throwable cause) { super(message, cause); }
}
