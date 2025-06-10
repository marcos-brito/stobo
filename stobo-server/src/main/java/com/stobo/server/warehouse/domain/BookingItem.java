package com.stobo.server.warehouse.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Embeddable
public class BookingItem {
  @Id private long productId;
  private int quantity;
}
