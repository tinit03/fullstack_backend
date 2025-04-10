package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

/**
 * REST controller for managing bid operations. Provides endpoints for placing bids,
 * retrieving bids for a specific item or by bid id, and accepting or rejecting bids.
 */
@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {
  private final BidService bidService;

  /**
   * Places a new bid on an item.
   *
   * @param dto the data transfer object containing bid creation details
   * @param bidder the authenticated user placing the bid
   * @return a ResponseEntity containing the bid details if the bid was placed successfully
   */
  @Operation(summary = "Place Bid",
      description = "Places a new bid on an item by the authenticated bidder.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bid placed successfully")
  })
  @PostMapping
  public ResponseEntity<BidDto> placeBid(
      @RequestBody BidCreateDto dto,
      @AuthenticationPrincipal User bidder) {
    BidDto bidDto = bidService.placeBid(dto, bidder);
    return ResponseEntity.ok(bidDto);
  }

  /**
   * Retrieves all bids for a given item.
   *
   * @param itemId the ID of the item for which bids are to be retrieved
   * @return a ResponseEntity containing a list of bid details for the specified item
   */
  @Operation(summary = "Get Bids for Item",
      description = "Retrieves all bids placed on a specified item.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bids retrieved successfully")
  })
  @GetMapping("/item/{itemId}")
  public ResponseEntity<List<BidDto>> getBidsForItem(@PathVariable Long itemId) {
    List<BidDto> bids = bidService.getBidsForItem(itemId);
    return ResponseEntity.ok(bids);
  }

  /**
   * Retrieves the details of a specific bid by its ID.
   *
   * @param bidId the ID of the bid to retrieve
   * @param user the authenticated user requesting the bid details
   * @return a ResponseEntity containing the bid details
   */
  @Operation(summary = "Get Bid by ID",
      description = "Retrieves a specific bid based on its ID for the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bid details retrieved successfully")
  })
  @GetMapping("/{bidId}")
  public ResponseEntity<BidDto> getBidById(@PathVariable Long bidId, @AuthenticationPrincipal User user) {
    BidDto bid = bidService.getBidFromId(bidId, user);
    return ResponseEntity.ok(bid);
  }

  /**
   * Accepts a bid, thereby creating an order based on the accepted bid.
   *
   * @param bidId the ID of the bid to be accepted
   * @param seller the authenticated seller accepting the bid
   * @return a ResponseEntity containing the order details resulting from the accepted bid
   */
  @Operation(summary = "Accept Bid",
      description = "Accepts a specific bid and creates an order for the corresponding item.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bid accepted and order created successfully")
  })
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
