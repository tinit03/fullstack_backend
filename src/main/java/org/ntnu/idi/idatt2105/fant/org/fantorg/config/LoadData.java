package org.ntnu.idi.idatt2105.fant.org.fantorg.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
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

  private final ImageRepository imageRepository;
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

    User a = new User();
    a.setEmail("b@b");
    a.setPassword(passwordEncoder.encode("password"));
    a.setFirstName("Test");
    a.setLastName("User");
    a.setRole(Role.USER);
    userRepository.save(a);

    User otherUser = new User();
    otherUser.setEmail("a@a");
    otherUser.setPassword(passwordEncoder.encode("password"));
    otherUser.setFirstName("Test");
    otherUser.setLastName("User");
    otherUser.setRole(Role.USER);
    userRepository.save(otherUser);

    // 2. Create parent category
    Category parentCategory = new Category();
    Image clothesImage = new Image();
    clothesImage.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1743697236/clothes-hanger-svgrepo-com_lnddra.svg");
    clothesImage.setPublicId("clothes-hanger-svgrepo-com_lnddra");
    parentCategory.setImage(clothesImage);
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
    Image imageElectronics = new Image();
    imageElectronics.setPublicId("computer-svgrepo-com_gz7m1c");
    imageElectronics.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1743697442/computer-svgrepo-com_gz7m1c.svg");
    electronics.setImage(imageElectronics);
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
    Image imageSport = new Image();
    imageSport.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1743698155/ymlpxut6wpax101shhqw.svg");
    imageSport.setPublicId("ymlpxut6wpax101shhqw");
    sports.setImage(imageSport);
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
    item.setLocation(new Location("7010", "Trøndelag","Trondheim", "63.4305", "10.3951"));
    item.setTags(List.of("jacket", "winter", "clothes"));
    item.setSeller(user);
    item.setListingType(ListingType.BID);
    item.setStatus(Status.ACTIVE);
    item.setCondition(Condition.ACCEPTABLE);
    item.setForSale(false);
    itemRepository.save(item);

    Item item1 = new Item();
    item1.setTitle("Cat");
    item1.setDescription("Super cool cat");
    item1.setSubCategory(balls); // Set to subcategory
    item1.setPrice(new BigDecimal("1500"));
    item1.setPublishedAt(LocalDateTime.now());
    item1.setLocation(new Location("7010", "Trøndelag","Trondheim", "63.4305", "10.3951"));
    item1.setTags(List.of("animal", "cat", "feline"));
    item1.setSeller(user);
    item1.setListingType(ListingType.BID);
    item1.setStatus(Status.ACTIVE);
    item1.setCondition(Condition.ACCEPTABLE);
    item1.setForSale(false);
    itemRepository.save(item1);

    List<Image> images = new ArrayList<>();
    Image image = new Image();
    image.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/people/boy-snow-hoodie.jpg");
    image.setPublicId("samples/people/boy-snow-hoodie");
    image.setCaption("winter jacket");
    image.setItem(item);
    images.add(image);
    imageRepository.saveAll(images);
    item.setImages(images);
    List<Image> images1 = new ArrayList<>();
    Image image1 = new Image();
    image1.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/animals/cat.jpg");
    image1.setPublicId("samples/animals/cat");
    image1.setCaption("cool cat");
    image1.setItem(item1);
    images1.add(image1);
    imageRepository.saveAll(images1);
    item1.setImages(images1);

    System.out.println("Inserted test data.");
  }
}
