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


    // === Additional Items ===
    Item dessertItem = new Item();
    dessertItem.setTitle("Delicious Dessert");
    dessertItem.setDescription("Sweet and tasty dessert for food lovers");
    dessertItem.setSubCategory(laptops); // Place under an existing subcategory
    dessertItem.setPrice(new BigDecimal("99.99"));
    dessertItem.setPublishedAt(LocalDateTime.now().minusDays(2));
    dessertItem.setLocation(new Location("0150", "Oslo", "Oslo", "59.9139", "10.7522"));
    dessertItem.setTags(List.of("dessert", "sweet", "food"));
    dessertItem.setSeller(user);
    dessertItem.setListingType(ListingType.DIRECT);
    dessertItem.setStatus(Status.ACTIVE);
    dessertItem.setCondition(Condition.NEW);
    dessertItem.setForSale(true);
    itemRepository.save(dessertItem);

    Item sheepItem = new Item();
    sheepItem.setTitle("Wooly Sheep");
    sheepItem.setDescription("Friendly sheep available for petting or wool");
    sheepItem.setSubCategory(balls); // Example category
    sheepItem.setPrice(new BigDecimal("3000"));
    sheepItem.setPublishedAt(LocalDateTime.now().minusDays(1));
    sheepItem.setLocation(new Location("5000", "Vestland", "Bergen", "60.39299", "5.32415"));
    sheepItem.setTags(List.of("sheep", "wool", "animal"));
    sheepItem.setSeller(userB);
    sheepItem.setListingType(ListingType.BID);
    sheepItem.setStatus(Status.ACTIVE);
    sheepItem.setCondition(Condition.GOOD);
    sheepItem.setForSale(true);
    itemRepository.save(sheepItem);

    Item shoesItem = new Item();
    shoesItem.setTitle("Sporty Shoes");
    shoesItem.setDescription("Durable and stylish shoes for running and gym");
    shoesItem.setSubCategory(shoes);
    shoesItem.setPrice(new BigDecimal("399.99"));
    shoesItem.setPublishedAt(LocalDateTime.now().minusHours(5));
    shoesItem.setLocation(new Location("6000", "Møre og Romsdal", "Ålesund", "62.4722", "6.1499"));
    shoesItem.setTags(List.of("shoes", "sports", "running"));
    shoesItem.setSeller(userA);
    shoesItem.setListingType(ListingType.DIRECT);
    shoesItem.setStatus(Status.ACTIVE);
    shoesItem.setCondition(Condition.NEW);
    shoesItem.setForSale(true);
    itemRepository.save(shoesItem);

    Item bikeItem = new Item();
    bikeItem.setTitle("Mountain Bike");
    bikeItem.setDescription("Used mountain bike in great condition");
    bikeItem.setSubCategory(bikes);
    bikeItem.setPrice(new BigDecimal("1200"));
    bikeItem.setPublishedAt(LocalDateTime.now().minusDays(4));
    bikeItem.setLocation(new Location("7000", "Trøndelag", "Trondheim", "63.4305", "10.3951"));
    bikeItem.setTags(List.of("bike", "mountain", "cycling"));
    bikeItem.setSeller(userA);
    bikeItem.setListingType(ListingType.BID);
    bikeItem.setStatus(Status.ACTIVE);
    bikeItem.setCondition(Condition.ACCEPTABLE);
    bikeItem.setForSale(true);
    itemRepository.save(bikeItem);

    // === Images for new items ===
    List<Image> newImages = new ArrayList<>();
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/food/dessert.jpg", "samples/food/dessert", "Yummy dessert", dessertItem));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/sheep.jpg", "samples/sheep", "Wooly sheep", sheepItem));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/ecommerce/shoes.png", "samples/ecommerce/shoes", "Sporty shoes", shoesItem));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/bike.jpg", "samples/bike", "Mountain bike", bikeItem));
    imageRepository.saveAll(newImages);

    dessertItem.setImages(List.of(newImages.get(0)));
    sheepItem.setImages(List.of(newImages.get(1)));
    shoesItem.setImages(List.of(newImages.get(2)));
    bikeItem.setImages(List.of(newImages.get(3)));

    itemRepository.saveAll(List.of(dessertItem, sheepItem, shoesItem, bikeItem));
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
