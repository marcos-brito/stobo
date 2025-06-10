package com.stobo.server.cart.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long userId;
    @Embedded
    private List<Item> items = new ArrayList<Item>();

    public void add(final long productId, final int quantity) {
        this.findItem(productId).ifPresentOrElse(item -> item.add(quantity), () -> {
            final Item item = Item.builder().productId(productId).quantity(quantity).build();

            this.items.add(item);
        });
    }

    public void remove(final long productId, final int quantity) {
        this.findItem(productId).ifPresent(item -> {
            item.remove(quantity);

            if (item.getQuantity() <= 0)
                this.clearItem(productId);
        });
    }

    public void clearItem(final long productId) {
        this.items.removeIf(item -> item.getProductId() == productId);
    }

    public void clear() {
        this.items.clear();
    }

    public Optional<Item> findItem(final long productId) {
        for (final Item item : this.items)
            if (item.getProductId() == productId)
                return Optional.of(item);

        return Optional.empty();
    }
}
