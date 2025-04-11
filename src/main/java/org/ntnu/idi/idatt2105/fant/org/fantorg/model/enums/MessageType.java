package org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums;

/**
 * Enum representing the different types of messages that can be exchanged in the system.
 * <p>
 * The MessageType enum defines the nature or context of a message sent between users, which helps to categorize and handle messages appropriately:
 * <ul>
 *   <li><b>NORMAL</b>: A standard message, typically for casual communication or general discussions.</li>
 *   <li><b>BID</b>: A message related to a bid, such as a new bid placed or a bid status update.</li>
 *   <li><b>PURCHASE</b>: A message concerning the purchase of an item, typically sent after an order or transaction is made.</li>
 *   <li><b>STATUS_CHANGED</b>: A message notifying the user about a change in the status of an item, order, or other entities.</li>
 * </ul>
 * </p>
 */
public enum MessageType {

  /**
   * A standard message, typically for casual communication or general discussions.
   */
  NORMAL,

  /**
   * A message related to a bid, such as a new bid placed or a bid status update.
   */
  BID,

  /**
   * A message concerning the purchase of an item, typically sent after an order or transaction is made.
   */
  PURCHASE,

  /**
   * A message notifying the user about a change in the status of an item, order, or other entities.
   */
  STATUS_CHANGED
}
