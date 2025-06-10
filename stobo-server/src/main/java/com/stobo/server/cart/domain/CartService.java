package com.stobo.server.cart.domain;

import com.stobo.server.common.exception.NotFoundException;
import com.stobo.server.common.id.OpaqueId;
import com.stobo.server.warehouse.domain.WarehouseService;
import com.stobo.server.warehouse.exception.ItemUnavailable;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
  private final WarehouseService warehouseService;
  private final CartRepository cartRepository;

  CartService(WarehouseService warehouseService,
              CartRepository cartRepository) {
    this.warehouseService = warehouseService;
    this.cartRepository = cartRepository;
  }

  public Cart findCartByUserId(String userId) {
    return OpaqueId.decodeLong(userId)
        .map(this::findOrCreate)
        .orElseThrow(() -> new NotFoundException("cart", userId));
  }

  public Cart clearCart(String userId) {
    return this.findAnd(userId, cart -> cart.clear());
  }

  @Transactional
  public Cart addItem(String userId, String productId, int quantity) {
    com.stobo.server.warehouse.domain.Item item =
        this.warehouseService.findItemById(productId);

    if (!item.canSupply(quantity))
      throw new ItemUnavailable("");

    return this.findAnd(userId,
                        cart -> cart.add(item.getProductId(), quantity));
  }

  @Transactional
  public Cart removeItem(String userId, String productId, int quantity) {
    com.stobo.server.warehouse.domain.Item item =
        this.warehouseService.findItemById(productId);

    return this.findAnd(userId,
                        cart -> cart.remove(item.getProductId(), quantity));
  }

  @Transactional
  public Cart clearItem(String userId, String productId) {
    com.stobo.server.warehouse.domain.Item item =
        this.warehouseService.findItemById(productId);

    return this.findAnd(userId, cart -> cart.clearItem(item.getProductId()));
  }

  @Transactional
  private Cart findAnd(String userId, Consumer<Cart> consumer) {
    Cart cart = this.findCartByUserId(userId);

    consumer.accept(cart);
    return this.cartRepository.save(cart);
  }

  private Cart findOrCreate(long userId) {
    return this.cartRepository.findByUserId(userId).orElseGet(
        () -> this.create(userId));
  }

  private Cart create(long userId) {
    Cart cart = new Cart();

    return this.cartRepository.save(cart);
  }
}
