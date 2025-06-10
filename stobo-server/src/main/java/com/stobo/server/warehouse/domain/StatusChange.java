package com.stobo.server.warehouse.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Entity
public class StatusChange {
  @Id @GeneratedValue(strategy = GenerationType.AUTO) private long id;
  private ItemStatus kind;
  private String reason;
  private Instant changedAt;
}
