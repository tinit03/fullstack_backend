package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.BidMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.OrderMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bid;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BidRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BidService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
  private final BidRepository bidRepository;
  private final ItemRepository itemRepository;
  private final OrderRepository orderRepository;
  private final NotificationService notificationService;
  @Override
  public BidDto placeBid(BidCreateDto dto, User bidder) {
    Item item = itemRepository.findById(dto.getItemId())
        .orElseThrow(() -> new EntityNotFoundException("Item is not found"));
    if(item.getListingType()!= ListingType.BID) {
      throw new IllegalArgumentException("Item listed is not biddable");
    }

    Map<String, String> args = Map.of("user", bidder.getFirstName() +" "+bidder.getLastName()
        , "item", item.getTitle());
    String link = "/items/" + item.getItemId();
    notificationService.send(item.getSeller(),args, NotificationType.NEW_BID,link);
    Bid bid = BidMapper.toBid(dto, item, bidder);
    Bid savedBid = bidRepository.save(bid);
    return BidMapper.toDto(savedBid);
  }

  @Override
  public List<BidDto> getBidsForItem(Long itemId) {
    List<Bid> bids = bidRepository.findByItem_ItemId(itemId);
    return bids.stream()
        .map(BidMapper::toDto)
        .collect(Collectors.toList());  }

  @Override
  public OrderDto acceptBid(Long bidId, User seller) {
    Bid bid = bidRepository.findById(bidId)
        .orElseThrow(() -> new EntityNotFoundException("Bid is not found"));
    if(!bid.getItem().getSeller().getId().equals(seller.getId())) {
      throw new IllegalArgumentException("User is not authorized to accept this bid");
    }
    bid.setStatus(BidStatus.ACCEPTED);
    bidRepository.save(bid);
    Order order = buildOrder(bid.getItem(), bid.getBidder());
    Order savedOrder = orderRepository.save(order);
    Map<String, String> args = Map.of(
        "user", seller.getFirstName() + " " + seller.getLastName(),
        "item", bid.getItem().getTitle()
    );
    String link = "/items/" + savedOrder.getItem().getItemId();
    notificationService.send(bid.getBidder(), args, NotificationType.BID_ACCEPTED, link);
    return OrderMapper.toDto(savedOrder);
  }

  private Order buildOrder(Item item, User buyer) {
    Order order = new Order();
    order.setItem(item);
    order.setBuyer(buyer);
    order.setOrderDate(LocalDateTime.now());
    return order;
  }
}
