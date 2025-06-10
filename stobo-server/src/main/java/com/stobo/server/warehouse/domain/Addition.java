package com.stobo.server.warehouse.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Entity
public class Addition {
  @Id @GeneratedValue(strategy = GenerationType.AUTO) private long id;
  @OneToOne private Item item;
  private int quantity;
  private Instant createdAt;
}
