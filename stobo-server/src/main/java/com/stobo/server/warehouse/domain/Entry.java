package com.stobo.server.warehouse.domain;

import com.stobo.server.common.domain.Item;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Item item;
    private Status status;

    public void addSupply(int increment) {
        this.item.add(increment);
    }

    public void deleteSupply(int decrement) {
        this.item.remove(decrement);
    }

    public boolean hasSupply() {
        return this.item.getQuantity() > 0;
    }

    public boolean canSupply(int demand) {
        return this.item.getQuantity() >= demand && this.isActive();
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void inactivate() {
        this.status = Status.INACTIVE;
    }
}
