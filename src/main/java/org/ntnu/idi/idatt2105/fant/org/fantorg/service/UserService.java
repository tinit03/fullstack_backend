package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UpdatePasswordDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public interface UserService {
  User findById(Long id);
  User findByEmail(String email);
  List<User> findAll();
  User updateProfilePicture(String url, User user);

  void updatePassword(User user, UpdatePasswordDto dto);
}
