package com.stobo.server.warehouse.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.List;
import com.stobo.server.common.domain.Item;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Entity
public class Booking {
  @Id @GeneratedValue(strategy = GenerationType.AUTO) private long id;
  @Embedded private List<Item> items;
  private Instant createdAt;
}
