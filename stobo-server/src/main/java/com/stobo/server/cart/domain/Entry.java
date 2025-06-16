package com.stobo.server.cart.domain;

import jakarta.persistence.Embeddable;
import java.time.Instant;
import com.stobo.server.common.domain.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class Entry {
    private Item item;
    private Instant addedAt;
}
