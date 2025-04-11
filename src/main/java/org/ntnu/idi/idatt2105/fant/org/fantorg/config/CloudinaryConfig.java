package org.ntnu.idi.idatt2105.fant.org.fantorg.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Config file for image upload service. */
@Configuration
public class CloudinaryConfig {

  @Value("${cloudinary.cloud-name}")
  private String cloudName;

  @Value("${cloudinary.api-key}")
  private String apiKey;

  @Value("${cloudinary.api-secret}")
  private String apiSecret;

  /**
   * Creates a bean for the cloudinary service.
   *
   * @return Service bean.
   */
  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true));
  }
}
