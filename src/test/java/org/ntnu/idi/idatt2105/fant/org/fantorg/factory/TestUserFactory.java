package org.ntnu.idi.idatt2105.fant.org.fantorg.factory;

import java.lang.reflect.Field;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;

public class TestUserFactory {
  public static User createUser(Long id) {
    User user = new User();
    user.setEmail("test@fant.org");
    user.setPassword("password");
    user.setFirstName("test");
    user.setLastName("User");
    user.setRole(Role.USER);

    if (id != null) {
      try {
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return user;
  }
}
