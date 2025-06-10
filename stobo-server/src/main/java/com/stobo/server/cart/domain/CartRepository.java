package com.stobo.server.cart.domain;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

interface CartRepository extends CrudRepository<Cart, Long> {
  Optional<Cart> findByUserId(long id);
}
