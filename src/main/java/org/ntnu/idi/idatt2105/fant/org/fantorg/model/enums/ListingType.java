package org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums;

/**
 * Enum representing the different types of listings for an item.
 *
 * <p>The ListingType enum defines the method through which an item is being sold:
 *
 * <ul>
 *   <li><b>DIRECT</b>: The item is being sold at a fixed price.
 *   <li><b>BID</b>: The item is being sold through a bidding process, where the highest bid wins.
 *   <li><b>CONTACT</b>: The item is listed, but the seller prefers to be contacted directly for
 *       more details or price negotiation.
 * </ul>
 */
public enum ListingType {

  /** The item is being sold at a fixed price. */
  DIRECT,

  /** The item is being sold through a bidding process. */
  BID,

  /**
   * The item is listed for sale, but the seller prefers to be contacted for further details or
   * negotiation.
   */
  CONTACT
}
