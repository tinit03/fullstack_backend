package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a refresh token used for maintaining a user's session.
 * <p>
 * This entity stores a token that is issued when a user logs in or when their session is refreshed.
 * It also stores the associated user, the token's expiry date, and whether the token has been used.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

  /**
   * The unique identifier for the refresh token.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The refresh token value, typically represented as a UUID string.
   * This token is used to authenticate and refresh the user's session.
   * It must be unique for each refresh token.
   */
  @Column(nullable = false, unique = true)
  private String token; // UUID string

  /**
   * The user associated with the refresh token.
   * This field is a reference to the `User` entity, indicating which user is linked to this refresh token.
   */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  /**
   * The expiry date and time for the refresh token.
   * After this date, the token will no longer be valid.
   */
  @Column(nullable = false)
  private LocalDateTime expiryDate;

  /**
   * Indicates whether the refresh token has been used.
   * Initially, the token is not used, but it will be set to `true` once the token has been consumed for a session refresh.
   */
  @Column(nullable = false)
  private boolean used = false;

  /**
   * Checks if the refresh token has expired based on the current date and time.
   *
   * @return `true` if the token has expired, `false` otherwise.
   */
  public boolean isExpired() {
    return expiryDate.isBefore(LocalDateTime.now());
  }
}
