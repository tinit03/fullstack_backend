package org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums;

/**
 * Enum representing the status of a bid in the system.
 * <p>
 * The BidStatus enum contains three possible states for a bid:
 * <ul>
 *   <li><b>ACCEPTED</b>: The bid has been accepted.</li>
 *   <li><b>PENDING</b>: The bid is still pending and hasn't been accepted or rejected.</li>
 *   <li><b>REJECTED</b>: The bid has been rejected.</li>
 * </ul>
 * </p>
 */
public enum BidStatus {

  /**
   * Represents a bid that has been accepted.
   */
  ACCEPTED,

  /**
   * Represents a bid that is pending approval.
   */
  PENDING,

  /**
   * Represents a bid that has been rejected.
   */
  REJECTED
}
