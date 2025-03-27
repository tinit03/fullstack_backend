package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BringPostcode {
  @JsonProperty("city")
  private String city;

  @JsonProperty("county")
  private String county;

  @JsonProperty("latitude")
  private String latitude;

  @JsonProperty("longitude")
  private String longitude;
}
