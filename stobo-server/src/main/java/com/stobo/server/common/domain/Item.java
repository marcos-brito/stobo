package com.stobo.server.common.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
@Embeddable
public class Item {
    private long productId;
    private int quantity;

    public void add(int quantity) {
        this.quantity += quantity;
    }

    public void remove(int quantity) {
        this.quantity -= quantity;
    }
}
