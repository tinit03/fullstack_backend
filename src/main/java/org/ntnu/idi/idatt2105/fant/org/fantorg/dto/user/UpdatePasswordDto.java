package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing the data required to update a user's password.
 * <p>
 * This class is used to transfer the necessary information for updating a user's password.
 * It contains the current password of the user as well as the new password that the user wants to set.
 * </p>
 */
@Getter
@Setter
public class UpdatePasswordDto {

  /**
   * The current password of the user.
   * <p>
   * This field is required to verify the user's identity before allowing the password update.
   * </p>
   */
  @NotBlank(message = "Current password is required")
  private String currentPassword;

  /**
   * The new password that the user wants to set.
   * <p>
   * This field is required to specify the new password the user intends to use for future logins.
   * </p>
   */
  @NotBlank(message = "New password is required")
  private String newPassword;
}
