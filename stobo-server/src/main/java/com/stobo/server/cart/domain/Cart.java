package com.stobo.server.cart.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.stobo.server.common.domain.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long userId;
    @Embedded
    private List<Entry> entries = new ArrayList<Entry>();

    public void add(final long productId, final int quantity) {
        this.findItem(productId).ifPresentOrElse(entry -> entry.getItem().add(quantity), () -> {
            Item item = new Item(productId, quantity);
            Entry entry = new Entry(item, Instant.now());

            this.entries.add(entry);
        });
    }

    public void remove(final long productId, final int quantity) {
        this.findItem(productId).ifPresent(entry -> {
            Item item = entry.getItem();
            item.remove(quantity);

            if (item.getQuantity() <= 0)
                this.clearItem(productId);
        });
    }

    public void clearItem(final long productId) {
        this.entries.removeIf(entry -> entry.getItem().getProductId() == productId);
    }

    public void clear() {
        this.entries.clear();
    }

    public Optional<Entry> findItem(final long productId) {
        for (final Entry entry : this.entries)
            if (entry.getItem().getProductId() == productId)
                return Optional.of(entry);

        return Optional.empty();
    }
}
