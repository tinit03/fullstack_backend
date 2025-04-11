package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;

/**
 * Data Transfer Object (DTO) representing the user details.
 * <p>
 * This class is used to transfer the necessary information related to a user's profile, such as
 * personal details, profile picture, rating information, and more.
 * </p>
 */
@Setter
@Getter
public class UserDto {

  /**
   * The unique identifier for the user.
   * <p>
   * This field represents the unique ID assigned to the user in the system.
   * </p>
   */
  private Long id;

  /**
   * The email address associated with the user's account.
   * <p>
   * This field contains the email used for authentication and notifications.
   * </p>
   */
  private String email;

  /**
   * The full name of the user.
   * <p>
   * This field represents the full name of the user, typically a combination of first and last names.
   * </p>
   */
  private String fullName;

  /**
   * The first name of the user.
   * <p>
   * This field contains the user's first name.
   * </p>
   */
  private String firstName;

  /**
   * The last name of the user.
   * <p>
   * This field contains the user's last name.
   * </p>
   */
  private String lastName;

  /**
   * The profile picture of the user.
   * <p>
   * This field contains an object that represents the user's profile picture,
   * including details such as the URL of the image.
   * </p>
   */
  private ImageDto profilePicture;

  /**
   * The average rating of the user.
   * <p>
   * This field represents the average rating of the user, based on the reviews they've received.
   * </p>
   */
  private double averageRating;

  /**
   * The total number of reviews the user has received.
   * <p>
   * This field tracks the total number of reviews the user has been given by others.
   * </p>
   */
  private long totalReviews;
}
