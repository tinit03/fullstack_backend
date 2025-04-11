package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.misc.BringPostcode;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.misc.BringResponse;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Service responsible for retrieving postal code details using the Bring API. */
@Service
public class BringService {
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${bring.api.url}")
  private String bringApiUrl;

  @Value("${bring.api.uid}")
  private String bringUid;

  @Value("${bring.api.key}")
  private String bringKey;

  private final Logger logger = LoggerFactory.getLogger(BringService.class);

  /**
   * Fetches geographic and administrative information for a given postal code using the Bring
   * Postal Code Lookup API.
   *
   * @param postalCode The postal code to look up.
   * @return A {@link Location} object containing city, county, latitude, longitude, and the postal
   *     code.
   * @throws RuntimeException if the API call fails or returns an unexpected response.
   */
  public Location getLocationDetails(String postalCode) {
    // Build the full URL
    final String url = bringApiUrl + postalCode;

    // Create HttpHeaders and add the required Bring API headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Mybring-API-Uid", bringUid);
    headers.set("X-Mybring-API-Key", bringKey);
    headers.set("Accept", "application/json");
    // Create HttpEntity with the headers
    HttpEntity<String> entity = new HttpEntity<>(headers);
    logger.info(String.valueOf(entity));
    // Make the API call using exchange(), which lets you pass in the headers
    ResponseEntity<BringResponse> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, BringResponse.class);

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      BringResponse bringResponse = response.getBody();
      // Since the response is a list of strings, we want to retrieve the first element
      BringPostcode data = bringResponse.getPostalCodes().getFirst();
      Location location = new Location();
      location.setCity(data.getCity());
      location.setCounty(data.getCounty());
      location.setLatitude(data.getLatitude());
      location.setLongitude(data.getLongitude());
      location.setPostalCode(postalCode);
      return location;
    }
    throw new RuntimeException("Error retrieving location details from Bring API");
  }
}
