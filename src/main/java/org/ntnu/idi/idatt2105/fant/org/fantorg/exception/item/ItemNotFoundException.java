package org.ntnu.idi.idatt2105.fant.org.fantorg.exception.item;

/**
 * Exception thrown when an item with a specific ID is not found.
 *
 * <p>This exception is typically thrown when an operation attempts to access or perform actions on
 * an item that does not exist in the system.
 */
public class ItemNotFoundException extends RuntimeException {

  /**
   * Constructs a new {@code ItemNotFoundException} with a detailed message.
   *
   * <p>The message indicates the ID of the item that was not found.
   *
   * @param id The ID of the item that was not found.
   */
  public ItemNotFoundException(Long id) {
    super("Item not found: " + id);
  }
}
