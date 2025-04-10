package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

/**
 * DTO representing the response from the Bring postcode API.
 * <p>
 * This class is used to map the response from the Bring service, which provides a list of postal codes
 * along with related geographic information (city, county, latitude, longitude). The class contains a list
 * of {@link BringPostcode} objects, each representing a single postcode with its associated geographic details.
 * </p>
 */
@Getter
public class BringResponse {

  /**
   * A list of postal codes along with their associated geographic details.
   * <p>
   * This list contains {@link BringPostcode} objects, each of which holds information such as the city,
   * county, latitude, and longitude for a specific postcode. The list is mapped from the JSON field
   * "postal_codes" in the response from the Bring API.
   * </p>
   */
  @JsonProperty("postal_codes")
  private List<BringPostcode> postalCodes;
}

