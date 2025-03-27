package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
@Getter
public class BringResponse {
  @JsonProperty("postal_codes")
  private List<BringPostcode> postalCodes;
}

