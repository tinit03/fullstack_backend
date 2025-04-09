package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ReviewMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ReviewRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ReviewServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @InjectMocks
  private ReviewServiceImpl reviewService;

  private User buyer;
  private User seller;
  private Order sampleOrder;
  private Review sampleReview;

  private Item sampleItem;
  private ReviewCreateDto reviewCreateDto;

  @BeforeEach
  public void setUp() {
    buyer = new User();
    ReflectionTestUtils.setField(buyer, "id", 1L);
    buyer.setEmail("buyer@example.com");
    buyer.setFirstName("Buyer");
    buyer.setLastName("User");

    seller = new User();
    ReflectionTestUtils.setField(seller, "id", 2L);
    seller.setEmail("seller@example.com");
    seller.setFirstName("Seller");
    seller.setLastName("User");

    sampleItem = new Item();
    ReflectionTestUtils.setField(sampleItem, "itemId", 10L);
    sampleItem.setSeller(seller);

    sampleOrder = new Order();
    sampleOrder.setId(100L);
    sampleOrder.setBuyer(buyer);
    sampleOrder.setItem(sampleItem);

    sampleReview = new Review();
    sampleReview.setId(10L);
    sampleReview.setRating(4);

    sampleReview.setOrder(sampleOrder);

    reviewCreateDto = new ReviewCreateDto();
    reviewCreateDto.setRating(5);
    reviewCreateDto.setComment("Excellent service");
  }


  @Test
  public void testCreateReview_Success() {
    when(orderRepository.findById(eq(sampleOrder.getId())))
        .thenReturn(Optional.of(sampleOrder));

    when(reviewRepository.existsByOrder(sampleOrder)).thenReturn(false);

    Review reviewEntity = ReviewMapper.toEntity(reviewCreateDto);
    reviewEntity.setOrder(sampleOrder);

    when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

    reviewService.createReview(buyer, sampleOrder.getId(), reviewCreateDto);


    verify(orderRepository).findById(eq(sampleOrder.getId()));
    verify(reviewRepository).save(any(Review.class));
  }

  @Test
  public void testCreateReview_OrderNotFound() {
    when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
        reviewService.createReview(buyer, 999L, reviewCreateDto)
    );
    assertThat(exception.getMessage()).contains("Order not found");
  }

  @Test
  public void testCreateReview_NotBuyer() {
    sampleOrder.setBuyer(seller);
    when(orderRepository.findById(eq(sampleOrder.getId())))
        .thenReturn(Optional.of(sampleOrder));

    SecurityException exception = assertThrows(SecurityException.class, () ->
        reviewService.createReview(buyer, sampleOrder.getId(), reviewCreateDto)
    );
    assertThat(exception.getMessage()).contains("You are not allowed to review");
  }

  @Test
  public void testCreateReview_DuplicateReview() {
    when(orderRepository.findById(eq(sampleOrder.getId())))
        .thenReturn(Optional.of(sampleOrder));
    when(reviewRepository.existsByOrder(sampleOrder)).thenReturn(true);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        reviewService.createReview(buyer, sampleOrder.getId(), reviewCreateDto)
    );
    assertThat(exception.getMessage()).contains("already has a review");
  }



  @Test
  public void testGetReviewsForSeller() {
    List<Review> reviews = Arrays.asList(sampleReview);
    Page<Review> reviewPage = new PageImpl<>(reviews);
    when(reviewRepository.findAllByOrder_Item_Seller_Id(eq(seller.getId()), any(Pageable.class)))
        .thenReturn(reviewPage);

    Pageable pageable = Pageable.unpaged();
    Page<ReviewDto> dtoPage = reviewService.getReviewsForSeller(seller.getId(), pageable);

    assertThat(dtoPage.getTotalElements()).isEqualTo(1);

  }

  @Test
  public void testGetAverageRatingForSeller_WithReviews() {
    // Two reviews with ratings 4 and 6, average should be 5
    Review review1 = new Review();
    review1.setRating(4);
    Review review2 = new Review();
    review2.setRating(6);

    when(reviewRepository.findAllByOrder_Item_Seller(eq(seller)))
        .thenReturn(Arrays.asList(review1, review2));

    double average = reviewService.getAverageRatingForSeller(seller);
    assertThat(average).isEqualTo(5.0);
  }

  @Test
  public void testGetAverageRatingForSeller_NoReviews() {
    when(reviewRepository.findAllByOrder_Item_Seller(eq(seller)))
        .thenReturn(Collections.emptyList());

    double average = reviewService.getAverageRatingForSeller(seller);
    assertThat(average).isEqualTo(0.0);
  }

  @Test
  public void testGetReviewCountForSeller() {
    when(reviewRepository.countByOrder_Item_Seller(eq(seller)))
        .thenReturn(3L);

    long count = reviewService.getReviewCountForSeller(seller);
    assertThat(count).isEqualTo(3L);
  }
}
