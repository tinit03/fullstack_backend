package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatProfileDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

/**
 * Utility class for converting between User entities and their corresponding DTOs.
 *
 * <p>The UserMapper class provides methods to map User entities to UserDto objects for general user
 * information, and ChatProfileDto objects for representing a user in the context of a chat.
 */
public class UserMapper {

  /**
   * Converts a User entity to a UserDto.
   *
   * <p>This method maps the essential user details (id, email, full name, first name, last name,
   * and profile picture) to a UserDto, which is used to represent user information in a response.
   *
   * @param user The User entity to be converted.
   * @return The UserDto containing the user details.
   */
  public static UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setFullName(user.getFirstName() + " " + user.getLastName());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setProfilePicture(ImageMapper.toDto(user.getProfileImage()));
    return dto;
  }

  /**
   * Converts a User entity to a ChatProfileDto.
   *
   * <p>This method is specifically designed to map user information (full name and profile picture
   * URL) for the purpose of displaying a user's profile in the context of a chat.
   *
   * @param user The User entity to be converted.
   * @return The ChatProfileDto containing the user’s profile details for chat purposes.
   */
  public static ChatProfileDto toChatProfileDto(User user) {
    ChatProfileDto dto = new ChatProfileDto();
    dto.setFullName(user.getFirstName() + " " + user.getLastName());

    if (user.getProfileImage() != null) {
      dto.setUrl(ImageMapper.toDto(user.getProfileImage()).getUrl());
    }
    return dto;
  }

  /**
   * Converts a list of User entities to a list of UserDto objects.
   *
   * <p>This method converts each User entity in the list into a corresponding UserDto and returns a
   * list of UserDto objects.
   *
   * @param users The list of User entities to be converted.
   * @return The list of UserDto objects containing user details.
   */
  public static List<UserDto> toDtoList(List<User> users) {
    return users == null
        ? null
        : users.stream().map(UserMapper::toDto).collect(Collectors.toList());
  }
}
