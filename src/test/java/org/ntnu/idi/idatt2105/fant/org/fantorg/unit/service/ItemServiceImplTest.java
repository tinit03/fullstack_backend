package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchFilter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchResponse;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.item.ItemNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BookmarkRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BookmarkService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BringService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ItemServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.ItemFacetCountUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

  @Mock private ItemRepository itemRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private CloudinaryService cloudinaryService;

  @Mock private BookmarkRepository bookmarkRepository;

  @Mock private BookmarkService bookmarkService;

  @Mock private ImageRepository imageRepository;

  @Mock private BringService bringService;

  @Mock private ItemFacetCountUtil facetUtil;

  @InjectMocks private ItemServiceImpl itemService;

  private User seller;
  private User buyer;
  private Category parentCategory; // Parent for the subcategory.
  private Category subCategory; // Subcategory with a non-null parent.
  private Item sampleItem;
  private final String validImageUrl = "https://example.com/image.jpg";

  @BeforeEach
  public void setUp() {
    seller = new User();
    ReflectionTestUtils.setField(seller, "id", 1L);
    seller.setFirstName("Seller");
    seller.setLastName("User");
    seller.setEmail("seller@example.com");

    buyer = new User();
    ReflectionTestUtils.setField(buyer, "id", 2L);
    buyer.setFirstName("Buyer");
    buyer.setLastName("User");
    buyer.setEmail("buyer@example.com");

    parentCategory = new Category();
    parentCategory.setCategoryName("Parent Category");
    ReflectionTestUtils.setField(parentCategory, "categoryId", 100L);

    subCategory = new Category();
    subCategory.setCategoryName("Sub Category");
    subCategory.setParentCategory(parentCategory);
    ReflectionTestUtils.setField(subCategory, "categoryId", 200L);

    sampleItem = new Item();
    ReflectionTestUtils.setField(sampleItem, "itemId", 10L);
    sampleItem.setTitle("Test Item");
    sampleItem.setDescription("A test item description");
    sampleItem.setPrice(new BigDecimal("100.00"));
    sampleItem.setSeller(seller);
    sampleItem.setStatus(Status.ACTIVE);
    sampleItem.setSubCategory(subCategory);
    sampleItem.setLocation(new Location("1234", "TestCounty", "TestCity", "0.0", "0.0"));
    sampleItem.setListingType(ListingType.BID);
    sampleItem.setCondition(Condition.NEW);
  }

  @Test
  public void testCreateItem_NoImages() {

    ItemCreateDto createDto = new ItemCreateDto();
    createDto.setItemName("Test Item");
    createDto.setDescription("Test item description");
    createDto.setPrice(new BigDecimal("100.00"));
    createDto.setSubcategoryId(subCategory.getCategoryId());
    createDto.setPostalCode("1234");
    createDto.setImages(null);

    when(categoryRepository.findById(subCategory.getCategoryId()))
        .thenReturn(Optional.of(subCategory));
    // Simulate BringService call
    when(bringService.getLocationDetails("1234"))
        .thenReturn(new Location("1234", "TestCounty", "TestCity", "0.0", "0.0"));

    when(itemRepository.save(any(Item.class)))
        .thenAnswer(
            invocation -> {
              Item i = invocation.getArgument(0);
              ReflectionTestUtils.setField(i, "itemId", 10L);
              return i;
            });

    Item created = itemService.createItem(createDto, seller);

    assertThat(created.getItemId()).isEqualTo(10L);
    assertThat(created.getTitle()).isEqualTo("Test Item");
    assertThat(created.getSeller()).isEqualTo(seller);
    // Since images are null, we expect the images list to be empty
    assertThat(created.getImages()).isEmpty();
  }

  @Test
  public void testCreateItem_WithImages() throws IOException {

    ItemCreateDto createDto = new ItemCreateDto();
    createDto.setItemName("Test Item With Image");
    createDto.setDescription("Item with image");
    createDto.setPrice(new BigDecimal("150.00"));
    createDto.setSubcategoryId(subCategory.getCategoryId());
    createDto.setPostalCode("1234");

    ImageItemUploadDto imageDto = new ImageItemUploadDto();
    imageDto.setUrl(validImageUrl);
    imageDto.setCaption("Test Caption");
    createDto.setImages(Collections.singletonList(imageDto));

    when(categoryRepository.findById(subCategory.getCategoryId()))
        .thenReturn(Optional.of(subCategory));
    when(bringService.getLocationDetails("1234"))
        .thenReturn(new Location("1234", "TestCounty", "TestCity", "0.0", "0.0"));
    when(itemRepository.save(any(Item.class)))
        .thenAnswer(
            invocation -> {
              Item i = invocation.getArgument(0);
              ReflectionTestUtils.setField(i, "itemId", 11L);
              return i;
            });

    // We want to simulate cloudinary upload too.
    Map<String, String> uploadResult = Map.of("url", validImageUrl, "public_id", "image_public_id");
    when(cloudinaryService.uploadBase64Image(validImageUrl)).thenReturn(uploadResult);

    when(imageRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Item created = itemService.createItem(createDto, seller);

    assertThat(created.getItemId()).isEqualTo(11L);
    assertThat(created.getTitle()).isEqualTo("Test Item With Image");
    assertThat(created.getImages()).hasSize(1);
    Image createdImage = created.getImages().get(0);
    assertThat(createdImage.getUrl()).isEqualTo(validImageUrl);
    assertThat(createdImage.getPublicId()).isEqualTo("image_public_id");
    verify(cloudinaryService, times(1)).uploadBase64Image(validImageUrl);
  }

  @Test
  public void testUpdateItem_Success() throws IOException {
    Image existingImage = new Image();
    existingImage.setUrl("https://old.example.com/old.jpg");
    existingImage.setPublicId("old_public_id");
    sampleItem.setImages(Collections.singletonList(existingImage));

    when(itemRepository.findById(20L)).thenReturn(Optional.of(sampleItem));
    when(categoryRepository.findById(subCategory.getCategoryId()))
        .thenReturn(Optional.of(subCategory));
    when(bringService.getLocationDetails("1234"))
        .thenReturn(new Location("1234", "TestCounty", "TestCity", "0.0", "0.0"));
    when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ItemEditDto updateDto = new ItemEditDto();
    updateDto.setItemName("Updated Test Item");
    updateDto.setDescription("Updated description");
    updateDto.setPrice(new BigDecimal("120.00"));
    updateDto.setSubcategoryId(subCategory.getCategoryId());
    updateDto.setPostalCode("1234");

    // For images, simulate a new image (since publicId is null, it's new)
    ImageEditDto imageEditDto = new ImageEditDto();
    imageEditDto.setUrl(validImageUrl);
    imageEditDto.setCaption("Updated caption");
    updateDto.setImages(List.of(imageEditDto));

    // Stub cloudinaryService.uploadBase64Image
    Map<String, String> uploadResult =
        Map.of("url", validImageUrl, "public_id", "new_image_public_id");
    when(cloudinaryService.uploadBase64Image(validImageUrl)).thenReturn(uploadResult);
    when(imageRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

    Item updated = itemService.updateItem(20L, updateDto, seller);

    assertThat(updated.getTitle()).isEqualTo("Updated Test Item");
    assertThat(updated.getDescription()).isEqualTo("Updated description");
    assertThat(updated.getPrice()).isEqualByComparingTo(new BigDecimal("120.00"));
    assertThat(updated.getImages()).hasSize(1);
  }

  @Test
  public void testChangeStatus() {
    ReflectionTestUtils.setField(sampleItem, "itemId", 30L);
    sampleItem.setSeller(seller);
    when(itemRepository.findById(30L)).thenReturn(Optional.of(sampleItem));
    when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
    // Check if the status has changed.
    Item changed = itemService.changeStatus(30L, Status.SOLD, seller);
    assertThat(changed.getStatus()).isEqualTo(Status.SOLD);
  }

  @Test
  public void testDeleteItem_Success() {
    // Simulate that the seller owns the sample item
    ReflectionTestUtils.setField(sampleItem, "itemId", 40L);
    sampleItem.setSeller(seller);
    // Assume item has images
    sampleItem.setImages(Collections.emptyList());
    when(itemRepository.findById(40L)).thenReturn(Optional.of(sampleItem));

    // Call deleteItem
    itemService.deleteItem(40L, seller);
    // Check that it delete is called once.
    verify(itemRepository, times(1)).delete(sampleItem);
  }

  @Test
  public void testGetItemById_NotFound() {
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(999L));
  }

  @Test
  public void testGetItemByIdBookmarked() {
    ReflectionTestUtils.setField(sampleItem, "itemId", 50L);
    when(itemRepository.findById(50L)).thenReturn(Optional.of(sampleItem));
    // Simulate that bookmarkService.isBookmarked returns true
    when(bookmarkService.isBookmarked(any(User.class), eq(50L))).thenReturn(true);

    var dto = itemService.getItemByIdBookmarked(50L, buyer);
    assertThat(dto.getIsBookmarked()).isTrue();
    // Since buyer is not the seller:
    assertThat(dto.getIsOwner()).isFalse();
  }

  @Test
  public void testGetAllItems() {
    // Create a list with one item.
    ReflectionTestUtils.setField(sampleItem, "itemId", 60L);
    sampleItem.setStatus(Status.ACTIVE);
    List<Item> items = Collections.singletonList(sampleItem);
    Page<Item> page = new PageImpl<>(items);

    when(itemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    Page<ItemDto> dtoPage = itemService.getAllItems(Pageable.unpaged(), Status.ACTIVE, buyer);
    assertThat(dtoPage.getTotalElements()).isEqualTo(1);
  }

  @Test
  public void testSearchItems() {
    ReflectionTestUtils.setField(sampleItem, "itemId", 70L);
    List<Item> items = Collections.singletonList(sampleItem);
    Page<Item> page = new PageImpl<>(items);

    when(itemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    // Stub the facet counts to return empty maps.
    when(facetUtil.getEnumFacetCounts(any(), eq("condition"), any(Class.class)))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getBooleanFacetCounts(any(), eq("forSale"))).thenReturn(Collections.emptyMap());
    when(facetUtil.getStringFacetCounts(any(), eq("location.county")))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getLongFacetCounts(any(), eq("subCategory.parentCategory.id")))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getLongFacetCounts(any(), eq("subCategory.id")))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getPublishedTodayFacetCounts(any())).thenReturn(Collections.emptyMap());

    ItemSearchFilter filter = new ItemSearchFilter();
    ItemSearchResponse response = itemService.searchItems(filter, Pageable.unpaged(), buyer);
    assertThat(response.getItems().getTotalElements()).isEqualTo(1);
  }

  @Test
  public void testGetItemsBySeller() {
    ReflectionTestUtils.setField(sampleItem, "itemId", 80L);
    sampleItem.setSeller(seller);
    List<Item> items = Collections.singletonList(sampleItem);
    Page<Item> page = new PageImpl<>(items);

    when(itemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    Page<ItemDto> dtoPage = itemService.getItemsBySeller(seller, Status.ACTIVE, Pageable.unpaged());
    assertThat(dtoPage.getTotalElements()).isEqualTo(1);
  }

  @Test
  public void testSearchItems_WithAllFilters() {
    ItemSearchFilter filter = new ItemSearchFilter();
    filter.setKeyword("vintage classic");
    filter.setCategoryId("1,2");
    filter.setSubCategoryId("3,4");
    filter.setCondition("NEW,GOOD");
    filter.setCounty("oslo,bergen");
    filter.setMinPrice(50.0);
    filter.setMaxPrice(200.0);
    filter.setForSale(true);
    filter.setOnlyToday(true);

    List<Item> items = Collections.singletonList(sampleItem);
    Page<Item> page = new PageImpl<>(items);

    when(itemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    when(facetUtil.getEnumFacetCounts(any(), eq("condition"), eq(Condition.class)))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getBooleanFacetCounts(any(), eq("forSale"))).thenReturn(Collections.emptyMap());
    when(facetUtil.getStringFacetCounts(any(), eq("location.county")))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getLongFacetCounts(any(), eq("subCategory.parentCategory.id")))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getLongFacetCounts(any(), eq("subCategory.id")))
        .thenReturn(Collections.emptyMap());
    when(facetUtil.getPublishedTodayFacetCounts(any())).thenReturn(Collections.emptyMap());

    ItemSearchResponse response = itemService.searchItems(filter, Pageable.unpaged(), buyer);
    assertThat(response.getItems().getTotalElements()).isEqualTo(1);
  }
}
