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

    // 1. Create test user
    User user = new User();
    user.setEmail("test@fant.org");
    user.setPassword(passwordEncoder.encode("password"));
    user.setFirstName("Test");
    user.setLastName("User");
    user.setRole(Role.USER);
    userRepository.save(user);

    // 2. Create parent category
    Category parentCategory = new Category();
    parentCategory.setCategoryName("Clothes");
    categoryRepository.save(parentCategory);

    // 3. Create subcategory (e.g. Jackets)
    Category subCategory = new Category();
    subCategory.setCategoryName("Jackets");
    subCategory.setParentCategory(parentCategory);
    categoryRepository.save(subCategory);

    Category shoes = new Category();
    shoes.setCategoryName("Shoes");
    shoes.setParentCategory(parentCategory);
    categoryRepository.save(shoes);

    // === PARENT CATEGORY: Electronics ===
    Category electronics = new Category();
    electronics.setCategoryName("Electronics");
    categoryRepository.save(electronics);

    Category phones = new Category();
    phones.setCategoryName("Phones");
    phones.setParentCategory(electronics);
    categoryRepository.save(phones);

    Category laptops = new Category();
    laptops.setCategoryName("Laptops");
    laptops.setParentCategory(electronics);
    categoryRepository.save(laptops);

    // === PARENT CATEGORY: Sports ===
    Category sports = new Category();
    sports.setCategoryName("Sports");
    categoryRepository.save(sports);

    Category bikes = new Category();
    bikes.setCategoryName("Bikes");
    bikes.setParentCategory(sports);
    categoryRepository.save(bikes);

    Category balls = new Category();
    balls.setCategoryName("Balls");
    balls.setParentCategory(sports);
    categoryRepository.save(balls);

    // 4. Create test item in subcategory
    Item item = new Item();
    item.setTitle("Winter Jacket");
    item.setDescription("Insulated winter jacket in great condition");
    item.setSubCategory(subCategory); // Set to subcategory
    item.setPrice(new BigDecimal("499.99"));
    item.setPublishedAt(LocalDateTime.now());
    item.setTags(List.of("jacket", "winter", "clothes"));
    item.setSeller(user);
    item.setLocation(new Location("Oslo", "0170"));
    itemRepository.save(item);

    System.out.println("Inserted test data.");
  }
}
