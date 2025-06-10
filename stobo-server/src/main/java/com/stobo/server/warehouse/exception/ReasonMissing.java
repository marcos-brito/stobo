package com.stobo.server.warehouse.exception;

public class ReasonMissing extends RuntimeException {
    public ReasonMissing() {
        super("missing the reason for the status change");
    }
}
