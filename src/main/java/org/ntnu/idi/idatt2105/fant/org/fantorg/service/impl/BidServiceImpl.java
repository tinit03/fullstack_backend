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
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.MessageType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.NotificationType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.BidRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ItemRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BidService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ChatMessageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
  private final BidRepository bidRepository;
  private final ItemRepository itemRepository;
  private final OrderRepository orderRepository;
  private final NotificationService notificationService;
  private final ChatMessageService chatMessageService;

  @Override
  public BidDto placeBid(BidCreateDto dto, User bidder) {
    Item item = itemRepository.findById(dto.getItemId())
        .orElseThrow(() -> new EntityNotFoundException("Item is not found"));
    if(item.getListingType()!= ListingType.BID) {
      throw new IllegalArgumentException("Item listed is not biddable");
    }
    if (bidder == item.getSeller()) {
      throw new IllegalArgumentException("You cannot place bidder who is seller");
    }

    Map<String, String> args = Map.of("user", bidder.getFirstName() +" "+bidder.getLastName()
        , "item", item.getTitle());
    String link = "/messages?itemId=" + item.getItemId() + "&recipientId=" + bidder.getEmail();
    notificationService.send(item.getSeller(),args, NotificationType.NEW_BID,link);
    Bid bid = BidMapper.toBid(dto, item, bidder);
    Bid savedBid = bidRepository.save(bid);
    chatMessageService.save(ChatMessageCreateDto.builder()
        .senderId(bidder.getEmail())
        .recipientId(item.getSeller().getEmail())
        .itemId(item.getItemId())
        .type(MessageType.BID)
        .content(savedBid.getId().toString())
        .build());
    return BidMapper.toDto(savedBid);
  }

  @Override
  public List<BidDto> getBidsForItem(Long itemId) {
    List<Bid> bids = bidRepository.findByItem_ItemId(itemId);
    return bids.stream()
        .map(BidMapper::toDto)
        .collect(Collectors.toList());  }

  @Override
  public OrderDto acceptBid(Long bidId, User seller, boolean accept) {
    Bid bid = bidRepository.findById(bidId)
        .orElseThrow(() -> new EntityNotFoundException("Bid is not found"));
    if(!bid.getItem().getSeller().getId().equals(seller.getId())) {
      throw new IllegalArgumentException("User is not authorized to accept this bid");
    }
    bid.setStatus(accept ? BidStatus.ACCEPTED : BidStatus.REJECTED);
    Bid savedBid = bidRepository.save(bid);
    Order order = buildOrder(bid.getItem(), bid.getBidder());
    Order savedOrder = orderRepository.save(order);
    Map<String, String> args = Map.of(
        "user", seller.getFirstName() + " " + seller.getLastName(),
        "item", bid.getItem().getTitle()
    );
    String link = "/messages?itemId=" + savedOrder.getItem().getItemId() + "&recipientId=" + seller.getEmail();
    notificationService.send(bid.getBidder(), args, NotificationType.BID_ACCEPTED, link);
    chatMessageService.save(ChatMessageCreateDto.builder()
        .senderId(seller.getEmail())
        .recipientId(savedBid.getBidder().getEmail())
        .itemId(savedBid.getItem().getItemId())
        .type(MessageType.STATUS_CHANGED)
        .content(savedBid.getId().toString())
        .build());
    return OrderMapper.toDto(savedOrder);
  }

  @Override
  public BidDto getBidFromId(Long id, User user) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bid not found"));
    if(bid.getBidder().getId() == user.getId() || bid.getItem().getSeller().getId() == user.getId()){
      return BidMapper.toDto(bid);
    } else{
      throw new SecurityException("This user is unauthorized");
    }
  }

  private Order buildOrder(Item item, User buyer) {
    Order order = new Order();
    order.setItem(item);
    order.setBuyer(buyer);
    order.setOrderDate(LocalDateTime.now());
    return order;
  }
}
