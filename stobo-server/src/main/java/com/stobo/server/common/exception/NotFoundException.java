package com.stobo.server.common.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String entity, String id) {
    super(String.format("%s with id \"%s\" not found", entity, id));
  }
}
