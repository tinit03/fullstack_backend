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
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;
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
import org.springframework.context.annotation.Profile;
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
  @Profile("test")
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
    a.setEmail("JohnDoe@mail.com");
    a.setPassword(passwordEncoder.encode("password"));
    a.setFirstName("John");
    a.setLastName("Doe");
    a.setRole(Role.USER);
    userRepository.save(a);

    User otherUser = new User();
    otherUser.setEmail("alicesmith@mail.com");
    otherUser.setPassword(passwordEncoder.encode("password"));
    otherUser.setFirstName("Alice");
    otherUser.setLastName("Smith");
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

    Image booksImage = new Image();
    booksImage.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1744108933/ctxhoaldixtbr7jlsnwg.svg");
    booksImage.setPublicId("ctxhoaldixtbr7jlsnwg");
    Category books = new Category();
    books.setCategoryName("Books");
    books.setImage(booksImage);
    categoryRepository.save(books);

    Category fiction = new Category();
    fiction.setCategoryName("Fiction");
    fiction.setParentCategory(books);
    categoryRepository.save(fiction);

    Image furnitureImage = new Image();
    furnitureImage.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1744108930/uhdqgdilcpgrojap0nor.svg");
    furnitureImage.setPublicId("uhdqgdilcpgrojap0nor");
    Category furniture = new Category();
    furniture.setCategoryName("Furniture");
    furniture.setImage(furnitureImage);
    categoryRepository.save(furniture);

    Category chairs = new Category();
    chairs.setCategoryName("Chairs");
    chairs.setParentCategory(furniture);
    categoryRepository.save(chairs);

    Image toysImage = new Image();
    toysImage.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1744109544/uuaa9dnsdsvyoedrncrt.svg");
    toysImage.setPublicId("uuaa9dnsdsvyoedrncrt");
    Category toys = new Category();
    toys.setCategoryName("Toys");
    toys.setImage(toysImage);
    categoryRepository.save(toys);

    Category actionFigures = new Category();
    actionFigures.setCategoryName("Action Figures");
    actionFigures.setParentCategory(toys);
    categoryRepository.save(actionFigures);

    Image appliancesImage = new Image();
    appliancesImage.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1744109547/dnhcpvfffwih5vk1rrg4.svg");
    appliancesImage.setPublicId("dnhcpvfffwih5vk1rrg4");
    Category appliances = new Category();
    appliances.setCategoryName("Home Appliances");
    appliances.setImage(appliancesImage);
    categoryRepository.save(appliances);

    Category fridges = new Category();
    fridges.setCategoryName("Refrigerators");
    fridges.setParentCategory(appliances);
    categoryRepository.save(fridges);

    Image vehiclesImage = new Image();
    vehiclesImage.setUrl("https://res.cloudinary.com/desnhobcx/image/upload/v1744109550/folw0fsqqyqs5pnrkp9y.svg");
    vehiclesImage.setPublicId("folw0fsqqyqs5pnrkp9y");
    Category vehicles = new Category();
    vehicles.setCategoryName("Vehicles");
    vehicles.setImage(vehiclesImage);
    categoryRepository.save(vehicles);

    Category cars = new Category();
    cars.setCategoryName("Cars");
    cars.setParentCategory(vehicles);
    categoryRepository.save(cars);


    Item item = new Item();
    item.setTitle("Winter Jacket");
    item.setDescription("Insulated winter jacket in great condition");
    item.setSubCategory(subCategory); // Set to subcategory
    item.setPrice(new BigDecimal("0"));
    item.setPublishedAt(LocalDateTime.now());
    item.setLocation(new Location("7010", "Trøndelag","Trondheim", "63.4305", "10.3951"));
    item.setTags(List.of("jacket", "winter", "clothes"));
    item.setSeller(user);
    item.setListingType(ListingType.CONTACT);
    item.setStatus(Status.ACTIVE);
    item.setCondition(Condition.ACCEPTABLE);
    item.setForSale(false);
    itemRepository.save(item);

    Item item1 = new Item();
    item1.setTitle("Cat");
    item1.setDescription("Super cool cat");
    item1.setSubCategory(balls); // Set to subcategory
    item1.setPrice(new BigDecimal("0"));
    item1.setPublishedAt(LocalDateTime.now());
    item1.setLocation(new Location("7010", "Trøndelag","Trondheim", "63.4305", "10.3951"));
    item1.setTags(List.of("animal", "cat", "feline"));
    item1.setSeller(user);
    item1.setListingType(ListingType.CONTACT);
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
    sheepItem.setSeller(otherUser);
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
    shoesItem.setSeller(a);
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
    bikeItem.setSeller(a);
    bikeItem.setListingType(ListingType.BID);
    bikeItem.setStatus(Status.ACTIVE);
    bikeItem.setCondition(Condition.ACCEPTABLE);
    bikeItem.setForSale(true);
    itemRepository.save(bikeItem);

    Item book = new Item();
    book.setTitle("Fantastic Novel");
    book.setDescription("A gripping fictional tale");
    book.setSubCategory(fiction);
    book.setPrice(new BigDecimal("19.99"));
    book.setPublishedAt(LocalDateTime.now().minusHours(1));
    book.setLocation(new Location("0000", "Oslo", "Oslo", "59.9139", "10.7522"));
    book.setTags(List.of("book", "fiction", "novel"));
    book.setSeller(user);
    book.setListingType(ListingType.DIRECT);
    book.setStatus(Status.ACTIVE);
    book.setCondition(Condition.NEW);
    book.setForSale(true);
    itemRepository.save(book);

    Item chair = new Item();
    chair.setTitle("Ergonomic Chair");
    chair.setDescription("Comfortable and stylish chair");
    chair.setSubCategory(chairs);
    chair.setPrice(new BigDecimal("89.99"));
    chair.setPublishedAt(LocalDateTime.now().minusHours(2));
    chair.setLocation(new Location("1111", "Oslo", "Oslo", "59.9139", "10.7522"));
    chair.setTags(List.of("chair", "furniture", "comfort"));
    chair.setSeller(otherUser);
    chair.setListingType(ListingType.DIRECT);
    chair.setStatus(Status.ACTIVE);
    chair.setCondition(Condition.LIKE_NEW);
    chair.setForSale(true);
    itemRepository.save(chair);

    Item figures = new Item();
    figures.setTitle("Action Figure Set");
    figures.setDescription("Collectible action figures for enthusiasts");
    figures.setSubCategory(actionFigures);
    figures.setPrice(new BigDecimal("59.99"));
    figures.setPublishedAt(LocalDateTime.now().minusHours(3));
    figures.setLocation(new Location("2222", "Oslo", "Oslo", "59.9139", "10.7522"));
    figures.setTags(List.of("action", "toys", "figures"));
    figures.setSeller(a);
    figures.setListingType(ListingType.DIRECT);
    figures.setStatus(Status.ACTIVE);
    figures.setCondition(Condition.NEW);
    figures.setForSale(true);
    itemRepository.save(figures);

    Item fridge = new Item();
    fridge.setTitle("Modern Refrigerator");
    fridge.setDescription("Energy-efficient and spacious refrigerator");
    fridge.setSubCategory(fridges);
    fridge.setPrice(new BigDecimal("799.99"));
    fridge.setPublishedAt(LocalDateTime.now().minusDays(1));
    fridge.setLocation(new Location("3333", "Oslo", "Oslo", "59.9139", "10.7522"));
    fridge.setTags(List.of("fridge", "appliance", "kitchen"));
    fridge.setSeller(otherUser);
    fridge.setListingType(ListingType.DIRECT);
    fridge.setStatus(Status.ACTIVE);
    fridge.setCondition(Condition.LIKE_NEW);
    fridge.setForSale(true);
    itemRepository.save(fridge);

    Item car = new Item();
    car.setTitle("Electric Car");
    car.setDescription("Latest model electric car with modern features");
    car.setSubCategory(cars);
    car.setPrice(new BigDecimal("29999.99"));
    car.setPublishedAt(LocalDateTime.now().minusDays(2));
    car.setLocation(new Location("4444", "Oslo", "Oslo", "59.9139", "10.7522"));
    car.setTags(List.of("car", "electric", "vehicle"));
    car.setSeller(user);
    car.setListingType(ListingType.BID);
    car.setStatus(Status.ACTIVE);
    car.setCondition(Condition.NEW);
    car.setForSale(true);
    itemRepository.save(car);

    // === Images for new items ===
    List<Image> newImages = new ArrayList<>();
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/food/dessert.jpg", "samples/food/dessert", "Yummy dessert", dessertItem));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651143/samples/sheep.jpg", "samples/sheep", "Wooly sheep", sheepItem));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/ecommerce/shoes.png", "samples/ecommerce/shoes", "Sporty shoes", shoesItem));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1742651144/samples/bike.jpg", "samples/bike", "Mountain bike", bikeItem));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1744111775/llyfzr98jt9os9xjfk9e.jpg", "llyfzr98jt9os9xjfk9e", "Catcher in the rye", book));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1744111895/iexwpih72nkt0j89qvvu.jpg", "iexwpih72nkt0j89qvvu", "Black ergonomic Chair", chair));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1744112006/ybrvxvud75ok5z1ped0l.png", "ybrvxvud75ok5z1ped0l", "Cool action figure", figures));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1744112148/qfvpj9qafdqtjnffskwf.jpg", "qfvpj9qafdqtjnffskwf", "Modern Refrigerator", fridge));
    newImages.add(new Image(null, "https://res.cloudinary.com/desnhobcx/image/upload/v1744112263/rmxgjymbt92xbfmq8doq.png", "rmxgjymbt92xbfmq8doq", "Electric Car Image", car));
    imageRepository.saveAll(newImages);

    dessertItem.setImages(List.of(newImages.get(0)));
    sheepItem.setImages(List.of(newImages.get(1)));
    shoesItem.setImages(List.of(newImages.get(2)));
    bikeItem.setImages(List.of(newImages.get(3)));
    book.setImages(List.of(newImages.get(4)));
    figures.setImages(List.of(newImages.get(5)));
    fridge.setImages(List.of(newImages.get(6)));
    car.setImages(List.of(newImages.get(7)));

    itemRepository.saveAll(List.of(dessertItem, sheepItem, shoesItem, bikeItem));
    // 5: Create test chat room
    String chatId = chatRoomServiceImpl.getChatRoomId(a.getEmail(), otherUser.getEmail(), item.getItemId(), true).orElseThrow();
    ChatMessage chatMessage = ChatMessage.builder()
        .sender(a)
        .recipient(otherUser)
        .item(item)
        .content("first message")
        .chatId(chatId)
        .timestamp(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .type(MessageType.NORMAL)
        .build();

    chatMessageRepository.save(chatMessage);

    String otherChatId = chatRoomServiceImpl.getChatRoomId(a.getEmail(), user.getEmail(), item1.getItemId(), true).orElseThrow();
    ChatMessage otherChatMessage = ChatMessage.builder()
        .sender(a)
        .recipient(user)
        .item(item1)
        .content("other message")
        .chatId(otherChatId)
        .timestamp(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .type(MessageType.NORMAL)
        .build();

    chatMessageRepository.save(otherChatMessage);

    String anotherChatId = chatRoomServiceImpl.getChatRoomId(user.getEmail(), a.getEmail(), item1.getItemId(), true).orElseThrow();
    ChatMessage antotherChatMessage = ChatMessage.builder()
        .sender(user)
        .recipient(a)
        .item(item1)
        .content("another messgae")
        .chatId(anotherChatId)
        .timestamp(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        .type(MessageType.NORMAL)
        .build();

    chatMessageRepository.save(antotherChatMessage);

    System.out.println("Inserted test data.");
  }
}
