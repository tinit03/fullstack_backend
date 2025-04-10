package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for loading user details by email.
 * Implements {@link UserDetailsService} to be used by Spring Security.
 * This service retrieves user information from the database using the provided email address.
 *
 * <p>
 * The {@link CustomUserDetailsService} class is used to fetch the user details during the authentication process.
 * It searches for the user by email and throws an exception if the email is not found.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads user details by email.
   *
   * <p>
   * This method is called during the authentication process to retrieve the user information
   * based on the provided email. If the user is not found, a {@link UsernameNotFoundException} is thrown.
   * </p>
   *
   * @param email The email of the user to be retrieved.
   * @return {@link UserDetails} containing user information.
   * @throws UsernameNotFoundException If the user with the specified email does not exist.
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      return userRepository.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("Email '" +email+"' not found"));
  }
}
