package org.ntnu.idi.idatt2105.fant.org.fantorg.integration.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.JwtTokenDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserLoginDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.authentication.UserRegisterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class TestHelpers {

  private static final Logger logger = LoggerFactory.getLogger(TestHelpers.class);

  public static String loginAndGetToken(TestRestTemplate restTemplate, String baseUrl, String email, String password) {
    UserLoginDto loginDto = new UserLoginDto(email, password);
    ResponseEntity<JwtTokenDto> response = restTemplate.postForEntity(
        baseUrl + "/auth/login", loginDto, JwtTokenDto.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    String token = response.getBody().getToken();
    logger.info("User {} logged in, token: {}", email, token);
    return token;
  }

  public static void registerUser(TestRestTemplate restTemplate, String baseUrl, String email, String password, String firstName, String lastName, String address) {
    UserRegisterDto registerDto = new UserRegisterDto();
    registerDto.setEmail(email);
    registerDto.setPassword(password);
    registerDto.setFirstName(firstName);
    registerDto.setLastName(lastName);

    ResponseEntity<Void> response = restTemplate.postForEntity(baseUrl + "/auth/register", registerDto, Void.class);
    if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CONFLICT) {
      throw new IllegalStateException("Failed to register user: " + email);
    }
  }

  public static Long createCategoryAndSubcategory(TestRestTemplate restTemplate, String baseUrl, String categoryName, String subCategoryName, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // Create parent
    CategoryDto parentCategory = new CategoryDto();
    parentCategory.setName(categoryName);
    HttpEntity<?> categoryRequest = new HttpEntity<>(parentCategory, headers);
    ResponseEntity<CategoryDto> categoryResponse = restTemplate.exchange(
        baseUrl + "/categories", HttpMethod.POST, categoryRequest, CategoryDto.class);
    assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
    Long parentCategoryId = categoryResponse.getBody().getId();
    logger.info("Created category: {} with id: {}", categoryName, parentCategoryId);

    // Create sub
    SubCategoryDto subCategory = new SubCategoryDto();
    subCategory.setName(subCategoryName);
    subCategory.setParentCategoryId(parentCategoryId);
    HttpEntity<?> subCategoryRequest = new HttpEntity<>(subCategory, headers);
    ResponseEntity<CategoryDto> subCategoryResponse = restTemplate.exchange(
        baseUrl + "/categories/sub", HttpMethod.POST, subCategoryRequest, CategoryDto.class);
    assertEquals(HttpStatus.OK, subCategoryResponse.getStatusCode());
    Long subCategoryId = subCategoryResponse.getBody().getId();
    logger.info("Created subcategory: {} with id: {}", subCategoryName, subCategoryId);

    return subCategoryId;
  }
}
