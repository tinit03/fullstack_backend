package org.ntnu.idi.idatt2105.fant.org.fantorg.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.ChatMessage;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatMessageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ChatRoomServiceImpl;
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
  private final ChatRoomServiceImpl chatRoomServiceImpl;
  private final ChatMessageRepository chatMessageRepository;


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

    User userA = new User();
    userA.setEmail("a@a");
    userA.setPassword(passwordEncoder.encode("password"));
    userA.setFirstName("Test");
    userA.setLastName("User");
    userA.setRole(Role.USER);
    userRepository.save(userA);

    User userB = new User();
    userB.setEmail("b@b");
    userB.setPassword(passwordEncoder.encode("password"));
    userB.setFirstName("Test");
    userB.setLastName("User");
    userB.setRole(Role.USER);
    userRepository.save(userB);

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
    item.setLocation(new Location("7010", "Trøndelag","Trondheim", "63.4305", "10.3951"));
    item.setTags(List.of("jacket", "winter", "clothes"));
    item.setSeller(user);
    item.setListingType(ListingType.BID);
    item.setStatus(Status.ACTIVE);
    item.setCondition(Condition.ACCEPTABLE);
    item.setForSale(false);
    itemRepository.save(item);

    Item item1 = new Item();
    item1.setTitle("Winter Jacket");
    item1.setDescription("Insulated winter jacket in great condition");
    item1.setSubCategory(subCategory); // Set to subcategory
    item1.setPrice(new BigDecimal("499.99"));
    item1.setPublishedAt(LocalDateTime.now());
    item1.setLocation(new Location("7010", "Trøndelag","Trondheim", "63.4305", "10.3951"));
    item1.setTags(List.of("jacket", "winter", "clothes"));
    item1.setSeller(user);
    item1.setListingType(ListingType.BID);
    item1.setStatus(Status.ACTIVE);
    item1.setCondition(Condition.ACCEPTABLE);
    item1.setForSale(false);
    itemRepository.save(item1);

    List<Image> images = new ArrayList<>();
    Image image = new Image();
    image.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1743251398/qpsm7x97gizcivhstmob.png");
    image.setPublicId("qpsm7x97gizcivhstmob");
    image.setCaption("AON-netter");
    image.setItem(item);
    images.add(image);
    imageRepository.saveAll(images);
    item.setImages(images);

    // 5: Create test chat room
    String chatId = chatRoomServiceImpl.getChatRoomId(userA.getEmail(), userB.getEmail(), item.getItemId(), true).orElseThrow();
    ChatMessage chatMessage = ChatMessage.builder()
        .sender(userA)
        .recipient(userB)
        .item(item)
        .content("first message")
        .chatId(chatId)
        .timestamp(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .build();

    chatMessageRepository.save(chatMessage);

    String otherChatId = chatRoomServiceImpl.getChatRoomId(userA.getEmail(), user.getEmail(), item1.getItemId(), true).orElseThrow();
    ChatMessage otherChatMessage = ChatMessage.builder()
        .sender(userA)
        .recipient(user)
        .item(item1)
        .content("other message")
        .chatId(otherChatId)
        .timestamp(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .build();

    chatMessageRepository.save(otherChatMessage);

    String anotherChatId = chatRoomServiceImpl.getChatRoomId(user.getEmail(), userA.getEmail(), item1.getItemId(), true).orElseThrow();
    ChatMessage antotherChatMessage = ChatMessage.builder()
        .sender(user)
        .recipient(userA)
        .item(item1)
        .content("antother messgae")
        .chatId(anotherChatId)
        .timestamp(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .build();

    chatMessageRepository.save(antotherChatMessage);

    System.out.println("Inserted test data.");
  }
}
