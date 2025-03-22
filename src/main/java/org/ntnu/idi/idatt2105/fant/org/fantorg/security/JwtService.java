package org.ntnu.idi.idatt2105.fant.org.fantorg.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Serviced for handling business logic of JWT token.
 *
 * @author Harry Xu
 * @version 1.0
 */
@Service
public class JwtService {

    private final SecretKey key;

    /**
     * Constructs an instance of the class that creates the key for signing tokens
     */
    public JwtService() {
        byte[] keyBytes = Base64.getDecoder().decode(System.getenv("SECRET_KEY"));
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * Wrapper method for generating JWT token
     * @param userDetails User information
     * @param expirationTimeMinutes Expiration time of token
     * @return Generated token
     */
    public String generateToken(UserDetails userDetails, long expirationTimeMinutes) {
        return generateToken(new HashMap<>(), userDetails, expirationTimeMinutes);
    }

    /**
     * Generates JWT token
     * @param extraClaims Map of extra claims
     * @param userDetails User info
     * @param expirationTimeMinutes Expiration time for token
     * @return JWT token
     */
    public String generateToken(
            Map<String, Object> extraClaims, UserDetails userDetails, long expirationTimeMinutes) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + Duration.ofMinutes(expirationTimeMinutes).toMillis()))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the subject (username) from JWT token
     * @param token JWT token
     * @return The username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Applies input function on the claims. The returned type depends on the type
     * T of the input function
     * @param token Token
     * @param claimsTFunction The function applied to claims
     * @return Type of input function
     * @param <T> Type of input function
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    /**
     * Gets all claims
     * @param token Token
     * @return Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Checks if username in token checks out with the current user and
     * that the token is not expired
     * @param token Token
     * @param userDetails Details of the user trying to retrieve session
     * @return True, if username matches and token has not expired
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if token is expired
     * @param token JWT token
     * @return True, if token is expired.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts expiration data from JWT token
     * @param token JWT token
     * @return Expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
