package org.ntnu.idi.idatt2105.fant.org.fantorg.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadData implements CommandLineRunner {
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;

  private final PasswordEncoder passwordEncoder;

  /**
   * Callback used to run the bean.
   *
   * @param args incoming main method arguments
   * @throws Exception on error
   */
  @Override
  public void run(String... args) throws Exception {

    // Create test user
    User user = new User();
    user.setEmail("test@fant.org");
    user.setPassword(passwordEncoder.encode("password"));
    user.setFirstName("Test");
    user.setLastName("User");
    //TODO: Fjerne brukernavn, vi ønsker heller å bruke email som identifikator
    user.setUsername("username");
    user.setRole(Role.USER);
    userRepository.save(user);


    // add category
    Category category = new Category(null, "SPORT");
    category = categoryRepository.save(category);

    // Create test item

    Item item = new Item();
    item.setTitle("Test Bike from Trondheim");
    item.setBriefDescription("A fast and stylish test bike.");
    item.setFullDescription("A great test bike, slightly used.");
    item.setCategory(category); // if category is an entity
    item.setPrice(new BigDecimal("199.99"));
    item.setPublishedAt(LocalDateTime.now());
    item.setTags(List.of("bike", "test", "sport"));
    item.setSeller(user);
    item.setLocation(new Location("Trondheim", "7010"));

    itemRepository.save(item);

    System.out.println("Inserted test data.");
  }
}
