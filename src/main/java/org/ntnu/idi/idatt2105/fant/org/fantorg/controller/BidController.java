package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
  private final BidService bidService;

  @PostMapping
  public ResponseEntity<BidDto> placeBid(
      @RequestBody BidCreateDto dto,
      @AuthenticationPrincipal User bidder){
      BidDto bidDto = bidService.placeBid(dto, bidder);
      return ResponseEntity.ok(bidDto);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<List<BidDto>> getBidsFormItem(@PathVariable Long itemId){
    List<BidDto> bids = bidService.getBidsForItem(itemId);
    return ResponseEntity.ok(bids);
  }

  @GetMapping("/{bitId}")
  public ResponseEntity<BidDto> getBidFromId(@PathVariable Long bidId, @AuthenticationPrincipal User user){
    BidDto bid = bidService.getBidFromId(bidId, user);
    return ResponseEntity.ok(bid);
  }

  @PostMapping("/{bidId}/accept")
  public ResponseEntity<OrderDto> acceptBid(@PathVariable Long bidId,
      @AuthenticationPrincipal User seller) {
    OrderDto orderDto = bidService.acceptBid(bidId, seller, true);
    return ResponseEntity.ok(orderDto);
  }

  @PostMapping("/{bidId}/reject")
  public ResponseEntity<OrderDto> rejectBid(@PathVariable Long bidId,
     @AuthenticationPrincipal User seller) {
    OrderDto orderDto = bidService.acceptBid(bidId, seller, false);
    return ResponseEntity.ok(orderDto);
  }
}
