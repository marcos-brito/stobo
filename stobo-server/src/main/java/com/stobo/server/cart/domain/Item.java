package com.stobo.server.cart.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
public class Item {
  @Id private long productId;
  private int quantity;
  private Instant addedAt;

  public void add(int quantity) { this.quantity += quantity; }

  public void remove(int quantity) { this.quantity -= quantity; }
}
