package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.RefreshToken;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.RefreshTokenRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.RefreshTokenServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private RefreshTokenServiceImpl refreshTokenService;

  private User sampleUser;
  private RefreshToken sampleToken;

  @BeforeEach
  public void setUp() {
    // Set up a sample user.
    sampleUser = new User();


    sampleUser.setEmail("john.doe@example.com");
    sampleUser.setPassword("old_password");
    sampleUser.setRole(Role.USER);
    ReflectionTestUtils.setField(sampleUser, "id", 10L);

    sampleToken = new RefreshToken();
    sampleToken.setToken(UUID.randomUUID().toString());
    sampleToken.setUser(sampleUser);
    sampleToken.setExpiryDate(LocalDateTime.now().plusDays(7));
    sampleToken.setUsed(false);
  }

  @Test
  public void testCreateToken_WhenTokenAlreadyExists() {
    when(refreshTokenRepository.findByUser(eq(sampleUser))).thenReturn(Optional.of(sampleToken));
    when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    String oldToken = sampleToken.getToken();
    RefreshToken result = refreshTokenService.createToken(sampleUser);

    //Check if the existing token is updated (new token string, new expiry, and not used).
    assertThat(result.getToken()).isNotEqualTo(oldToken);
    assertThat(result.getExpiryDate()).isAfter(LocalDateTime.now().plusDays(6));
    assertThat(result.isUsed()).isFalse();
    verify(refreshTokenRepository).findByUser(eq(sampleUser));
    verify(refreshTokenRepository).save(any(RefreshToken.class));
  }

  @Test
  public void testCreateToken_WhenNoTokenExists() {
    // Simulate that there is no existing token for the user
    when(refreshTokenRepository.findByUser(eq(sampleUser))).thenReturn(Optional.empty());
    when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenAnswer(invocation -> {
          RefreshToken token = invocation.getArgument(0);
          // Simulate setting an id after save
          token.setToken(token.getToken()); // token remains same
          return token;
        });

    // Create a new token
    RefreshToken result = refreshTokenService.createToken(sampleUser);

    // Check if the token is valid
    assertThat(result).isNotNull();
    assertThat(result.getUser()).isEqualTo(sampleUser);
    assertThat(result.getToken()).isNotBlank();
    verify(refreshTokenRepository).findByUser(eq(sampleUser));
    verify(refreshTokenRepository).save(any(RefreshToken.class));
  }

  @Test
  public void testGetByToken_Success() {
    when(refreshTokenRepository.findByToken(eq(sampleToken.getToken()))).thenReturn(Optional.of(sampleToken));

    RefreshToken result = refreshTokenService.getByToken(sampleToken.getToken());

    assertThat(result).isEqualTo(sampleToken);
    verify(refreshTokenRepository).findByToken(eq(sampleToken.getToken()));
  }

  @Test
  public void testGetByToken_TokenNotFound() {
    when(refreshTokenRepository.findByToken(any(String.class))).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> refreshTokenService.getByToken("nonexistent"));

    verify(refreshTokenRepository).findByToken("nonexistent");
  }

  @Test
  public void testValidateToken_Success() {
    when(refreshTokenRepository.findByToken(eq(sampleToken.getToken()))).thenReturn(Optional.of(sampleToken));

    RefreshToken result = refreshTokenService.validateToken(sampleToken.getToken());

    assertThat(result).isEqualTo(sampleToken);
    verify(refreshTokenRepository).findByToken(eq(sampleToken.getToken()));
  }

  @Test
  public void testValidateToken_Expired() {
    // Simulate an expired token
    sampleToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
    when(refreshTokenRepository.findByToken(eq(sampleToken.getToken()))).thenReturn(Optional.of(sampleToken));

    // When the token is validated, then it should delete the token and throw an exception.
    assertThatThrownBy(() -> refreshTokenService.validateToken(sampleToken.getToken()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Refresh token is expired");

    verify(refreshTokenRepository).findByToken(eq(sampleToken.getToken()));
    verify(refreshTokenRepository).delete(sampleToken);
  }

  @Test
  public void testRevokeToken() {
    //When revokeToken is called.
    refreshTokenService.revokeToken(sampleUser);

    // Then we should expect the repository to delete by user and then flush
    verify(refreshTokenRepository).deleteByUser(eq(sampleUser));
    verify(refreshTokenRepository).flush();
  }


  @Test
  public void testPurgeExpiredTokens() {
    //We don't need to do anything, because it will call upon itself in any time
    doNothing().when(refreshTokenRepository).deleteAllByExpiryDateBefore(any(LocalDateTime.class));
    refreshTokenService.purgeExpiredTokens();
    verify(refreshTokenRepository).deleteAllByExpiryDateBefore(any(LocalDateTime.class));
  }
}
