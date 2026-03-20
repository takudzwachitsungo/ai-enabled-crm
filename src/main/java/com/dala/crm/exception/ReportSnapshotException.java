package com.dala.crm.exception;

/**
 * Base exception type for reporting module operations.
 */
public class ReportSnapshotException extends RuntimeException {

    public ReportSnapshotException(String message) {
        super(message);
    }
}
