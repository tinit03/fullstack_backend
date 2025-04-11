package org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums;

/**
 * Enum representing the different statuses of an item.
 * <p>
 * The Status enum defines the possible states that an item can be in within the system:
 * <ul>
 *   <li><b>INACTIVE</b>: The item is inactive and is not available for purchase or bidding.</li>
 *   <li><b>ACTIVE</b>: The item is active and available for purchase or bidding.</li>
 *   <li><b>SOLD</b>: The item has been sold and is no longer available for purchase or bidding.</li>
 * </ul>
 * </p>
 */
public enum Status {

  /**
   * The item is inactive and is not available for purchase or bidding.
   */
  INACTIVE,

  /**
   * The item is active and available for purchase or bidding.
   */
  ACTIVE,

  /**
   * The item has been sold and is no longer available for purchase or bidding.
   */
  SOLD
}
