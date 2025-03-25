package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class Location {
  private String postalCode;
  private String city;
//  private double latitude;
//  private double longitude;
}