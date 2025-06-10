package com.stobo.server.catalog.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.AUTO) private Long id;
  private String name;
  private String desc;
  private String number;
  private int weight;
  private Instant year;
  @Embedded private Price price;
  @OneToMany private List<Category> categories;

  public void addCategory(String name) {
    this.categories.add(new Category(name));
  }

  public void deleteCategory(String name) {
    this.categories.remove(new Category(name));
  }
}
