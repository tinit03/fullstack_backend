package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user.UserRegisterDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  public void registerUser(UserRegisterDto dto) {
    if(userRepository.existsByEmail(dto.getEmail())){
      throw new IllegalArgumentException("Email already in use!");
    }

    User user = new User();
    user.setRole(Role.USER);
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setAddress(dto.getAddress());

    userRepository.save(user);
  }
}
