package com.stobo.server.catalog.exception;

public class NoAssociatedCategory extends RuntimeException {
  public NoAssociatedCategory() {
    super("product should have at least one category");
  }
}
