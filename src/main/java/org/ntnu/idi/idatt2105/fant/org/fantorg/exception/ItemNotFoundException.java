package org.ntnu.idi.idatt2105.fant.org.fantorg.exception;

public class ItemNotFoundException extends RuntimeException{

  public ItemNotFoundException(Long id) {
    super("Item not found: " + id);
  }
}
