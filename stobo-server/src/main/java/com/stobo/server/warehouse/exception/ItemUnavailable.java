package com.stobo.server.warehouse.exception;

public class ItemUnavailable extends RuntimeException {
    public ItemUnavailable(String message) {
        super(message);
    }
}
