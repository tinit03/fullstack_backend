package org.ntnu.idi.idatt2105.fant.org.fantorg.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for security settings. Whitelists certain url's. The rest
 * are blacklisted and return a status code 403 FORBIDDEN when accessed without authentication.
 *
 * @author Harry Xu
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthorizationFilter jwtAuthFilter;

    /**
     * Configures security filter chain.
     *
     * @param http
     *            HttpSecurity object
     * @return SecurityFilterChain object
     * @throws Exception
     *             If an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(
                                                "/forgotPassword/**",
                                                "/auth/login",
                                                "/auth/register",
                                                "/auth/renewToken",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .sessionManagement(
                        manager -> manager.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
