package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the location of an item.
 * <p>
 * This embedded entity is used to store the location details of an item in the system.
 * The location includes:
 * <ul>
 *   <li><b>postalCode</b>: The postal code of the location.</li>
 *   <li><b>county</b>: The county or region where the item is located.</li>
 *   <li><b>city</b>: The city where the item is located.</li>
 *   <li><b>latitude</b>: The latitude coordinate of the item's location.</li>
 *   <li><b>longitude</b>: The longitude coordinate of the item's location.</li>
 * </ul>
 * </p>
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class Location {

  /**
   * The postal code of the item's location.
   */
  private String postalCode;

  /**
   * The county or region of the item's location.
   */
  private String county;

  /**
   * The city of the item's location.
   */
  private String city;

  /**
   * The latitude coordinate of the item's location.
   */
  private String latitude;

  /**
   * The longitude coordinate of the item's location.
   */
  private String longitude;
}
