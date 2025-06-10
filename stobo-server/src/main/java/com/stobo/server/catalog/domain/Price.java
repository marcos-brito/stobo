package com.stobo.server.catalog.domain;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Embeddable
public class Price {
  private String currency;
  private long cents;
}
