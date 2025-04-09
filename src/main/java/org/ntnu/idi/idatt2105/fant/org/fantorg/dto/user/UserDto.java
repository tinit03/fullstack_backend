package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;

@Setter
@Getter
public class UserDto {
  private Long id;
  private String email;
  private String fullName;
  private String firstName;
  private String lastName;
  private ImageDto profilePicture;
  private double averageRating;
  private long totalReviews;
}
