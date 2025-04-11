package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for representing the profile information of a user in a chat.
 * <p>
 * This DTO encapsulates the profile details of a user such as the URL to the profile picture
 * and the user's full name.
 * </p>
 *
 * @author Harry Xu
 */
@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatProfileDto {

  /**
   * The URL of the user's profile picture.
   * <p>
   * This field holds the link to the image that represents the user's profile in the chat.
   * </p>
   */
  private String url;

  /**
   * The full name of the user.
   * <p>
   * This field holds the full name of the user associated with the profile.
   * </p>
   */
  private String fullName;
}
