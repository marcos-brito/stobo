package com.stobo.server.warehouse.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
public class Item {
  @Id @GeneratedValue(strategy = GenerationType.AUTO) private long id;
  @Id private long productId;
  private ItemStatus status;
  private int quantity;

  public void addSupply(int increment) { this.quantity += increment; }

  public void deleteSupply(int decrement) { this.quantity -= decrement; }

  public boolean hasSupply() { return this.quantity > 0; }

  public boolean canSupply(int demand) {
    return this.quantity >= demand && this.isActive();
  }

  public boolean isActive() { return this.status == ItemStatus.ACTIVE; }

  public void activate() { this.status = ItemStatus.ACTIVE; }

  public void inactivate() { this.status = ItemStatus.INACTIVE; }
}
