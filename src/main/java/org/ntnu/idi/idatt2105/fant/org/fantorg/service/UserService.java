package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

/** Service interface for user-related operations. */
public interface UserService {

  /**
   * Finds a user by their unique ID.
   *
   * @param id The ID of the user.
   * @return The corresponding UserDto.
   */
  UserDto findById(Long id);

  /**
   * Finds a user entity by email address.
   *
   * @param email The email of the user.
   * @return The User entity.
   */
  User findByEmail(String email);

  /**
   * Retrieves all users in the system.
   *
   * @return A list of UserDto objects.
   */
  List<UserDto> findAll();

  /**
   * Updates the profile picture of a user.
   *
   * @param url The new profile picture URL.
   * @param user The user whose profile picture is being updated.
   * @return The updated User entity.
   */
  User updateProfilePicture(String url, User user);

  /**
   * Updates the user's password.
   *
   * @param user The user whose password is to be updated.
   * @param dto DTO containing the old and new passwords.
   */
  void updatePassword(User user, UpdatePasswordDto dto);

  /**
   * Deletes a user by their ID.
   *
   * @param id The ID of the user to delete.
   */
  void deleteUser(Long id);
}
