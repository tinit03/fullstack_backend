package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * DTO representing a postcode and its related geographic information.
 *
 * <p>This class is used to map the response from an external service, such as Bring, which provides
 * postcode and geographical information. The class includes details about the city, county,
 * latitude, and longitude associated with a postcode.
 */
@Getter
public class BringPostcode {

  /**
   * The name of the city associated with the postcode.
   *
   * <p>This field represents the city (e.g., "Oslo") for the given postcode. It is annotated with
   * {@link JsonProperty} to map the corresponding JSON field from the external API response.
   */
  @JsonProperty("city")
  private String city;

  /**
   * The name of the county associated with the postcode.
   *
   * <p>This field represents the county (e.g., "Viken") for the given postcode. It is annotated
   * with {@link JsonProperty} to map the corresponding JSON field from the external API response.
   */
  @JsonProperty("county")
  private String county;

  /**
   * The latitude of the location corresponding to the postcode.
   *
   * <p>This field contains the latitude (e.g., "59.9127") for the geographical location of the
   * postcode. It is annotated with {@link JsonProperty} to map the corresponding JSON field from
   * the external API response.
   */
  @JsonProperty("latitude")
  private String latitude;

  /**
   * The longitude of the location corresponding to the postcode.
   *
   * <p>This field contains the longitude (e.g., "10.7461") for the geographical location of the
   * postcode. It is annotated with {@link JsonProperty} to map the corresponding JSON field from
   * the external API response.
   */
  @JsonProperty("longitude")
  private String longitude;
}
