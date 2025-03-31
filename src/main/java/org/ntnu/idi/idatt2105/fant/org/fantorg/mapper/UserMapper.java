package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatProfileDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public class UserMapper {
  public static UserDto toDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setFullName(user.getFirstName()+" "+user.getLastName());
    return dto;
  }

  public static ChatProfileDto toChatProfileDto(User user) {
   return ChatProfileDto.builder()
       .url("test")
       .fullName(user.getFirstName() + " " + user.getLastName())
       .build();
  }
}
