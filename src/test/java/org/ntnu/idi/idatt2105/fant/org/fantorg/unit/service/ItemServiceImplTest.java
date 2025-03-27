package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.factory.TestCategoryFactory;
import org.ntnu.idi.idatt2105.fant.org.fantorg.factory.TestUserFactory;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BringService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ItemServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
  @Mock
  private ItemRepository itemRepository;
  @Mock
  private CategoryRepository categoryRepository;
  @InjectMocks
  private ItemServiceImpl itemService;

  @Mock
  private BringService bringService;
  private User seller;
  private Category category;
  private Category subCategory;

  @BeforeEach
  void setUp() {
    seller = TestUserFactory.createUser(1L);
    category = TestCategoryFactory.createCategory(1L, "Clothes");
    subCategory = TestCategoryFactory.createSubCategory(2L, "Jacket", category);

  }

  @Test
  void createItem_ShouldMapDtoAndSaveItem() {
    // Arrange
    ItemCreateDto dto = new ItemCreateDto();
    dto.setItemName("Test Item");
    dto.setDescription("Full");
    dto.setPrice(BigDecimal.valueOf(100));
    Location location = new Location();
    location.setCounty("Oslo");
    location.setLongitude("123.321");
    location.setLatitude("321.123");
    location.setCity("Oslo");
    dto.setPostalCode("0123");
    dto.setSubcategoryId(2L);
    dto.setTags(List.of("test", "bike"));

    // Mocked entity returned from the mapper
    Item mappedItem = new Item();
    mappedItem.setTitle(dto.getItemName());
    mappedItem.setDescription(dto.getDescription());
    mappedItem.setPrice(dto.getPrice());
    mappedItem.setLocation(location);
    mappedItem.setTags(dto.getTags());
    mappedItem.setSubCategory(subCategory);

    mockStatic(ItemMapper.class);
    when(ItemMapper.toItem(dto)).thenReturn(mappedItem);
    when(bringService.getLocationDetails(dto.getPostalCode())).thenReturn(location);
    when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);
    when(categoryRepository.findById(any(Long.class))).thenReturn(
        Optional.ofNullable(mappedItem.getSubCategory()));
    // Act
    Item savedItem = itemService.createItem(dto, seller);

    // Assert
    assertEquals("Test Item", savedItem.getTitle());
    assertEquals(seller, savedItem.getSeller());
    assertNotNull(savedItem.getPublishedAt());

    verify(itemRepository, times(1)).save(any(Item.class));
  }
}
