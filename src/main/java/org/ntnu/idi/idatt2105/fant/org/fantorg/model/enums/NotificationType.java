package org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums;

/**
 * Enum representing the different types of notifications that can be sent to users.
 *
 * <p>The NotificationType enum defines the context or event that triggers a notification for a
 * user. These events are critical actions or updates that the user needs to be informed about:
 *
 * <ul>
 *   <li><b>NEW_BID</b>: A notification indicating that a new bid has been placed on an item.
 *   <li><b>BID_ACCEPTED</b>: A notification indicating that a bid on an item has been accepted.
 *   <li><b>ITEM_SOLD</b>: A notification indicating that an item has been sold successfully.
 *   <li><b>ITEM_SOLD_ELSE</b>: A notification indicating that an item has been sold, but not to the
 *       current user.
 *   <li><b>MESSAGE_RECEIVED</b>: A notification indicating that the user has received a new
 *       message.
 * </ul>
 */
public enum NotificationType {

  /** A notification indicating that a new bid has been placed on an item. */
  NEW_BID,

  /** A notification indicating that a bid on an item has been accepted. */
  BID_ACCEPTED,

  /** A notification indicating that an item has been sold successfully. */
  ITEM_SOLD,

  /** A notification indicating that an item has been sold, but not to the current user. */
  ITEM_SOLD_ELSE,

  /** A notification indicating that the user has received a new message. */
  MESSAGE_RECEIVED
}
