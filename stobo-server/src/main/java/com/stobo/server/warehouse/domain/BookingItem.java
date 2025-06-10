package com.stobo.server.warehouse.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Embeddable
public class BookingItem {
  @OneToOne private Item item;
  private int quantity;
}
