package org.ntnu.idi.idatt2105.fant.org.fantorg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining OpenAPI documentation settings.
 *
 * @author Harry Xu
 */
@Configuration
public class OpenApiConfig {

  /**
   * Defines the OpenAPI bean for Swagger documentation.
   *
   * @return An instance of OpenAPI with configured settings.
   */
  @Bean
  public OpenAPI defineOpenApi() {
    // Configure server information
    Server server = new Server();
    server.setUrl("http://localhost:8080");
    server.setDescription("Development");

    // Configure contact information
    Contact myContact = new Contact();
    myContact.setName("Test contact");
    myContact.setEmail("testmail@mail.com");

    // Configure general information about the API
    Info information =
        new Info()
            .title("Fant.org API")
            .version("1.0")
            .description("This API exposes endpoints for the Fant.org application.")
            .contact(myContact);

    // Return the configured OpenAPI object
    return new OpenAPI().info(information).servers(List.of(server));
  }
}
