package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.BidMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.OrderMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bid;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.*;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BidRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BidService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatMessageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.springframework.stereotype.Service;

/** Service implementation for managing bids on auctioned items. */
@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
  private final BidRepository bidRepository;
  private final ItemRepository itemRepository;
  private final OrderRepository orderRepository;
  private final NotificationService notificationService;
  private final ChatMessageService chatMessageService;

  /**
   * Places a new bid on an item.
   *
   * @param dto The bid creation data.
   * @param bidder The user placing the bid.
   * @return The created bid as a DTO.
   * @throws EntityNotFoundException If the item is not found.
   * @throws IllegalArgumentException If the item is not biddable or the bidder is the seller.
   */
  @Override
  public BidDto placeBid(BidCreateDto dto, User bidder) {
    Item item =
        itemRepository
            .findById(dto.getItemId())
            .orElseThrow(() -> new EntityNotFoundException("Item is not found"));
    if (item.getListingType() != ListingType.BID) {
      throw new IllegalArgumentException("Item listed is not biddable");
    }
    if (bidder == item.getSeller()) {
      throw new IllegalArgumentException("You cannot place bidder who is seller");
    }

    Map<String, String> args =
        Map.of("user", bidder.getFirstName() + " " + bidder.getLastName(), "item", item.getTitle());
    String link = "/messages?itemId=" + item.getItemId() + "&recipientId=" + bidder.getEmail();
    notificationService.send(item.getSeller(), args, NotificationType.NEW_BID, link);
    Bid bid = BidMapper.toBid(dto, item, bidder);
    Bid savedBid = bidRepository.save(bid);
    chatMessageService.send(
        ChatMessageCreateDto.builder()
            .senderId(bidder.getEmail())
            .recipientId(item.getSeller().getEmail())
            .itemId(item.getItemId())
            .type(MessageType.BID)
            .content(savedBid.getId().toString())
            .build());
    return BidMapper.toDto(savedBid);
  }

  /**
   * Retrieves all bids for a specific item.
   *
   * @param itemId The ID of the item.
   * @return List of bids in DTO format.
   */
  @Override
  public List<BidDto> getBidsForItem(Long itemId) {
    List<Bid> bids = bidRepository.findByItem_ItemId(itemId);
    return bids.stream().map(BidMapper::toDto).collect(Collectors.toList());
  }

  /**
   * Accepts a bid and creates an order.
   *
   * @param bidId The ID of the bid to accept.
   * @param seller The user accepting the bid.
   * @return The created order as a DTO.
   * @throws EntityNotFoundException If the bid is not found.
   * @throws IllegalArgumentException If the user is not authorized to accept the bid.
   */
  @Override
  public OrderDto acceptBid(Long bidId, User seller) {
    Bid bid =
        bidRepository
            .findById(bidId)
            .orElseThrow(() -> new EntityNotFoundException("Bid is not found"));
    if (!bid.getItem().getSeller().getId().equals(seller.getId())) {
      throw new IllegalArgumentException("User is not authorized to accept this bid");
    }
    Item item = bid.getItem();
    item.setStatus(Status.SOLD);
    itemRepository.save(item);
    bid.setStatus(BidStatus.ACCEPTED);
    Bid savedBid = bidRepository.save(bid);

    Order order = buildOrder(bid.getItem(), bid.getBidder(), savedBid);
    Order savedOrder = orderRepository.save(order);

    Map<String, String> args =
        Map.of(
            "user",
            seller.getFirstName() + " " + seller.getLastName(),
            "item",
            bid.getItem().getTitle());
    String link =
        "/messages?itemId="
            + savedOrder.getItem().getItemId()
            + "&recipientId="
            + seller.getEmail();
    notificationService.send(bid.getBidder(), args, NotificationType.BID_ACCEPTED, link);
    chatMessageService.send(
        ChatMessageCreateDto.builder()
            .senderId(seller.getEmail())
            .recipientId(savedBid.getBidder().getEmail())
            .itemId(savedBid.getItem().getItemId())
            .type(MessageType.STATUS_CHANGED)
            .content(savedBid.getId().toString())
            .build());
    return OrderMapper.toDto(savedOrder);
  }

  /**
   * Rejects a bid.
   *
   * @param bidId The ID of the bid to reject.
   * @param seller The user rejecting the bid.
   * @throws EntityNotFoundException If the bid is not found.
   * @throws IllegalArgumentException If the user is not authorized to reject the bid.
   */
  @Override
  public void rejectBid(Long bidId, User seller) {
    Bid bid =
        bidRepository
            .findById(bidId)
            .orElseThrow(() -> new EntityNotFoundException("Bid is not found"));
    if (!bid.getItem().getSeller().getId().equals(seller.getId())) {
      throw new IllegalArgumentException("User is not authorized to accept this bid");
    }
    bid.setStatus(BidStatus.REJECTED);
    Bid savedBid = bidRepository.save(bid);
    Map<String, String> args =
        Map.of(
            "user",
            seller.getFirstName() + " " + seller.getLastName(),
            "item",
            bid.getItem().getTitle());
    String link =
        "/messages?itemId=" + savedBid.getItem().getItemId() + "&recipientId=" + seller.getEmail();
    notificationService.send(savedBid.getBidder(), args, NotificationType.BID_ACCEPTED, link);
    chatMessageService.send(
        ChatMessageCreateDto.builder()
            .senderId(seller.getEmail())
            .recipientId(savedBid.getBidder().getEmail())
            .itemId(savedBid.getItem().getItemId())
            .type(MessageType.STATUS_CHANGED)
            .content(savedBid.getId().toString())
            .build());
  }

  /**
   * Retrieves a specific bid if the user is either the bidder or the item's seller.
   *
   * @param id The ID of the bid.
   * @param user The requesting user.
   * @return The bid as a DTO.
   * @throws EntityNotFoundException If the bid does not exist.
   * @throws SecurityException If the user is not authorized to view the bid.
   */
  @Override
  public BidDto getBidFromId(Long id, User user) {
    Bid bid =
        bidRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bid not found"));
    if (bid.getBidder().getId() == user.getId()
        || bid.getItem().getSeller().getId() == user.getId()) {
      return BidMapper.toDto(bid);
    } else {
      throw new SecurityException("This user is unauthorized");
    }
  }

  /**
   * Helper method for building a new order when a bid is accepted.
   *
   * @param item The item being purchased.
   * @param buyer The buyer who won the bid.
   * @param bid The bid
   * @return The new order object.
   */
  private Order buildOrder(Item item, User buyer, Bid bid) {
    Order order = new Order();
    order.setItem(item);
    order.setBuyer(buyer);
    order.setOrderDate(LocalDateTime.now());
    order.setPrice(bid.getAmount());
    return order;
  }
}
