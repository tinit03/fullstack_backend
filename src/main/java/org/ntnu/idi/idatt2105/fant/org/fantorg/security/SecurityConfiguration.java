package org.ntnu.idi.idatt2105.fant.org.fantorg.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for security settings in the application. This class defines the security
 * filter chain, authentication provider, and other relevant security mechanisms such as JWT
 * authorization, password encoding, and cross-origin request handling.
 *
 * <p>It defines the following key features:
 *
 * <ul>
 *   <li>Whitelisting specific URLs (such as login, password reset, and API documentation)
 *   <li>JWT Authorization filter setup to ensure security and authentication for other requests
 *   <li>Password encoding using BCryptPasswordEncoder
 *   <li>Handling CORS (Cross-Origin Resource Sharing) configuration for local development
 * </ul>
 *
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthorizationFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * <p>This method sets up the following security measures:
   *
   * <ul>
   *   <li>Disables CSRF protection
   *   <li>Enables CORS with default settings
   *   <li>Defines authorized HTTP request matchers (such as open APIs for authentication and
   *       documentation)
   *   <li>Enforces authentication for all other requests
   *   <li>Disables session creation, ensuring stateless authentication
   *   <li>Injects a custom JWT filter for authorization
   * </ul>
   *
   * @param http HttpSecurity object for configuring HTTP security settings
   * @return SecurityFilterChain configured security filter chain
   * @throws Exception if an error occurs during security configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .headers(
            httpSecurityHeadersConfigurer -> {
              httpSecurityHeadersConfigurer.frameOptions(
                  HeadersConfigurer.FrameOptionsConfig::disable);
            })
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/forgotPassword/**",
                        "/auth/**",
                        "/categories/**",
                        "/ws/**",
                        "ws/**",
                        "/items/**",
                        "/swagger-ui/**",
                        "/index.html",
                        "/v3/api-docs*/**",
                        "/h2-console/**",
                        "/webjars/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Provides the password encoder used to encode passwords.
   *
   * <p>This method returns a BCryptPasswordEncoder instance for encoding passwords.
   *
   * @return PasswordEncoder instance for encoding passwords
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the authentication provider to use the custom user details service and the BCrypt
   * password encoder.
   *
   * @return AuthenticationProvider configured to authenticate users based on a custom user details
   *     service and password encoder
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  /**
   * Configures the authentication manager for the application.
   *
   * @param config AuthenticationConfiguration object for configuring the authentication manager
   * @return AuthenticationManager instance used to manage authentication in the application
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Configures the CORS (Cross-Origin Resource Sharing) settings.
   *
   * <p>This method sets up the allowed origin patterns, HTTP methods, headers, and credentials to
   * enable CORS for local development and testing.
   *
   * @return CorsConfigurationSource configuration for handling CORS requests
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:*"));
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}
