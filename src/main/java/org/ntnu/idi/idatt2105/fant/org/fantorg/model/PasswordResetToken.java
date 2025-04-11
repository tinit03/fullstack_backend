package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Represents a token used for resetting a user's password.
 *
 * <p>This entity stores a token that is generated when a user requests a password reset. It also
 * stores the associated user, the token's expiry date, and whether the token has been used.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

  /** The unique identifier for the password reset token. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The password reset token value, which is used to verify the user and allow resetting the
   * password. This field must be unique for each token.
   */
  @Column(nullable = false, unique = true)
  private String token;

  /**
   * The user associated with the password reset token. This field is a reference to the `User`
   * entity, indicating which user's password can be reset using this token.
   */
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /** The expiry date and time for the token. After this date, the token will no longer be valid. */
  @Column(nullable = false)
  private LocalDateTime expiryDate;

  /**
   * Indicates whether the token has been used for resetting the password. Initially, the token is
   * not used, but it will be set to `true` once the password has been reset.
   */
  @Column(name = "used", nullable = false)
  private boolean used = false;

  /**
   * Checks if the token has expired based on the current date and time.
   *
   * @return `true` if the token has expired, `false` otherwise.
   */
  public boolean isExpired() {
    return expiryDate.isBefore(LocalDateTime.now());
  }
}
