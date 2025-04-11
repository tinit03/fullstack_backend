package org.ntnu.idi.idatt2105.fant.org.fantorg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Application file that runs the app. */
@SpringBootApplication
public class Application {

  /**
   * Main method which acts as the gateway to the app.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
