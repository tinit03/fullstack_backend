package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public interface UserService {


  User findByEmail(String email);
}
