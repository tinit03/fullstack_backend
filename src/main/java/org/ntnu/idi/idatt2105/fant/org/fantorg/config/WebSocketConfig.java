package org.ntnu.idi.idatt2105.fant.org.fantorg.config;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for setting up WebSocket messaging using STOMP protocol.
 *
 * <p>This class enables the use of WebSocket message broker, defines the endpoint for
 * WebSocket communication, configures the message broker, and sets up message converters
 * for JSON serialization and deserialization.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * Registers the STOMP endpoint used by clients to connect to the WebSocket.
   *
   * @param registry the {@link StompEndpointRegistry} to register endpoints with
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("http://localhost:5173");
  }

  /**
   * Configures the message broker options including destination prefixes and simple broker.
   *
   * @param registry the {@link MessageBrokerRegistry} used to configure broker settings
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/user");
    registry.setUserDestinationPrefix("/user");
  }

  /**
   * Configures the message converters for use in WebSocket messaging.
   * This method sets up a Jackson-based message converter for JSON content.
   *
   * @param messageConverters the list of configured {@link MessageConverter}s
   * @return {@code false} to indicate that default converters should be added as well
   */
  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    resolver.setDefaultMimeType(APPLICATION_JSON);

    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setObjectMapper(new ObjectMapper());
    converter.setContentTypeResolver(resolver);

    messageConverters.add(converter);
    return false;
  }
}
