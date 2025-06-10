package com.stobo.server.cart.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.Test;

public class CartTests {
    @Test
    public void addsItem() {
        Cart cart = new Cart();

        cart.add(1, 2);

        assertEquals(2, cart.findItem(1).orElseThrow().getQuantity());
    }

    @Test
    public void removesItem() {
        Cart cart = new Cart();

        cart.add(1, 3);
        cart.remove(1, 2);

        assertEquals(1, cart.findItem(1).orElseThrow().getQuantity());
    }

    @Test
    public void clearsIfQuantityIsZeroOrLess() {
        Cart cart = new Cart();

        cart.add(1, 1);
        cart.remove(1, 2);

        assertEquals(Optional.empty(), cart.findItem(1));
    }

    @Test
    public void clearsItem() {
        Cart cart = new Cart();

        cart.add(1, 5);
        cart.clearItem(1);

        assertEquals(Optional.empty(), cart.findItem(1));
    }

    @Test
    public void clearsCart() {
        Cart cart = new Cart();

        cart.add(1, 3);
        cart.add(2, 4);
        cart.clear();

        assertEquals(0, cart.getItems().size());
    }
}
