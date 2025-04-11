package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
      @ApiResponse(responseCode = "200", description = "Bid placed successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BidDto.class))
          }),
  })
  @PostMapping
  public ResponseEntity<BidDto> placeBid(
      @Parameter(description = "Bid creation info") @RequestBody BidCreateDto dto,
      @Parameter(description = "The authenticated user") @AuthenticationPrincipal User bidder) {
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
      @ApiResponse(responseCode = "200", description = "Bids retrieved successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BidDto.class))
          }),
  })
  @GetMapping("/item/{itemId}")
  public ResponseEntity<List<BidDto>> getBidsForItem(@Parameter(description = "Identification of item") @PathVariable Long itemId) {
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
      @ApiResponse(responseCode = "200", description = "Bid details retrieved successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BidDto.class))
          }),
  })
  @GetMapping("/bid/{bidId}")
  public ResponseEntity<BidDto> getBidFromId(
      @Parameter(description = "Bid identificator") @PathVariable Long bidId,
      @Parameter(description = "The authenticated user") @AuthenticationPrincipal User user){
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
      @ApiResponse(responseCode = "200", description = "Bid accepted and order created successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = OrderDto.class))
          }),
  })
  @PostMapping("/{bidId}/accept")
  public ResponseEntity<OrderDto> acceptBid(
      @Parameter(description = "Identificator of bid") @PathVariable Long bidId,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User seller) {
    OrderDto orderDto = bidService.acceptBid(bidId, seller);
    return ResponseEntity.ok(orderDto);
  }

  /**
   * Rejects a bid
   *
   * @param bidId the ID of the bid to be accepted
   * @param seller the authenticated seller rejecting the bid
   * @return a ResponseEntity containing the order details resulting from the accepted bid
   */
  @Operation(summary = "Reject Bid",
      description = "Rejects bid")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bid rejected", content=@Content)
  })
  @PostMapping("/{bidId}/reject")
  public ResponseEntity<Void> rejectBid(@Parameter(description = "Identificator of bid") @PathVariable Long bidId,
     @AuthenticationPrincipal User seller) {
    bidService.rejectBid(bidId, seller);
    return ResponseEntity.ok().build();
  }
}
