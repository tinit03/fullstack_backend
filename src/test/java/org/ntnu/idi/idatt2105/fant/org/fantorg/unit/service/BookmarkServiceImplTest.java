package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bookmark;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BookmarkRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.BookmarkServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private ItemRepository itemRepository;

  @Mock private BookmarkRepository bookmarkRepository;

  @InjectMocks private BookmarkServiceImpl bookmarkService;

  // Sample entities for testing.
  private User seller;
  private User buyer;
  private Item item;

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

    item = new Item();
    ReflectionTestUtils.setField(item, "itemId", 10L);
    item.setTitle("Test Item");
    item.setStatus(Status.ACTIVE);
    item.setSeller(seller);
    item.setPublishedAt(LocalDateTime.now());
  }

  @Test
  public void testBookmarkItem_Success() {
    when(userRepository.findById(eq(buyer.getId()))).thenReturn(Optional.of(buyer));

    when(itemRepository.findById(eq(item.getItemId()))).thenReturn(Optional.of(item));
    when(bookmarkRepository.existsByUserAndItem(eq(buyer), eq(item))).thenReturn(false);

    when(bookmarkRepository.save(any(Bookmark.class)))
        .thenAnswer(
            invocation -> {
              Bookmark bm = invocation.getArgument(0);
              return bm;
            });

    bookmarkService.bookmarkItem(buyer, item.getItemId());

    verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
  }

  @Test
  public void testBookmarkItem_CannotBookmarkOwnItem() {
    item.setSeller(buyer);

    when(itemRepository.findById(eq(item.getItemId()))).thenReturn(Optional.of(item));
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          bookmarkService.bookmarkItem(buyer, item.getItemId());
        });
  }

  @Test
  public void testBookmarkItem_AlreadyBookmarked() {
    when(userRepository.findById(eq(buyer.getId()))).thenReturn(Optional.of(buyer));
    when(itemRepository.findById(eq(item.getItemId()))).thenReturn(Optional.of(item));
    when(bookmarkRepository.existsByUserAndItem(eq(buyer), eq(item))).thenReturn(true);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          bookmarkService.bookmarkItem(buyer, item.getItemId());
        });
  }

  @Test
  public void testRemoveBookmark_Success() {
    when(userRepository.findById(eq(buyer.getId()))).thenReturn(Optional.of(buyer));
    when(itemRepository.findById(eq(item.getItemId()))).thenReturn(Optional.of(item));

    bookmarkService.removeBookmark(buyer, item.getItemId());

    verify(bookmarkRepository, times(1)).deleteByUserAndItem(eq(buyer), eq(item));
  }

  @Test
  public void testIsBookmarked_ReturnsTrue() {
    when(userRepository.findById(eq(buyer.getId()))).thenReturn(Optional.of(buyer));
    when(itemRepository.findById(eq(item.getItemId()))).thenReturn(Optional.of(item));
    when(bookmarkRepository.existsByUserAndItem(eq(buyer), eq(item))).thenReturn(true);

    boolean result = bookmarkService.isBookmarked(buyer, item.getItemId());
    assertThat(result).isTrue();
  }

  @Test
  public void testGetBookmarkedItems() {
    Bookmark bookmark =
        Bookmark.builder().user(buyer).item(item).bookmarkedAt(LocalDateTime.now()).build();

    Page<Bookmark> page = new PageImpl<>(Collections.singletonList(bookmark));
    when(bookmarkRepository.findByUserAndItem_Status(
            eq(buyer), eq(Status.ACTIVE), any(Pageable.class)))
        .thenReturn(page);

    Page<Item> itemsPage = bookmarkService.getBookmarkedItems(buyer, Pageable.unpaged());
    assertThat(itemsPage.getTotalElements()).isEqualTo(1);
    assertThat(itemsPage.getContent().get(0).getItemId()).isEqualTo(item.getItemId());
  }
}
