package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public class UserMapper {
  public static UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setFullName(user.getFirstName()+" "+user.getLastName());
    dto.setProfilePicture(ImageMapper.toProfileDto(user.getProfileImage()));
    return dto;
  }

  public static List<UserDto> toDtoList(List<User> users) {
    return users == null ? null : users.stream().map(UserMapper::toDto).collect(Collectors.toList());
  }
}
