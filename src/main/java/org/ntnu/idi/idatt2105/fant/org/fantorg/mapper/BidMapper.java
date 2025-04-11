package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bid;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;

/**
 * A utility class that provides methods for mapping between Bid objects and BidDTOs.
 * <p>
 * The BidMapper class helps convert between the Bid entity and its corresponding
 * Data Transfer Object (DTO), and also facilitates creating a Bid object from a
 * BidCreateDto.
 * </p>
 */
public class BidMapper {

  /**
   * Converts a Bid entity to its corresponding BidDTO.
   * <p>
   * This method maps the relevant fields of the Bid entity to a BidDTO.
   * The BidDTO is used to transfer data about the bid in a non-persistent format.
   * </p>
   *
   * @param bid The Bid entity to be converted.
   * @return The BidDTO containing bid details.
   */
  public static BidDto toDto(Bid bid){
    BidDto dto = new BidDto();
    dto.setId(bid.getId());
    dto.setAmount(bid.getAmount());
    dto.setBidTime(bid.getBidTime());
    dto.setStatus(bid.getStatus().toString());

    if (bid.getItem() != null) {
      dto.setItemId(bid.getItem().getItemId());
    }

    if (bid.getBidder() != null) {
      dto.setBidderId(bid.getBidder().getId());
      dto.setBidderName(bid.getBidder().getFirstName() + " " + bid.getBidder().getLastName());
    }

    return dto;
  }

  /**
   * Converts a BidCreateDto to a Bid entity.
   * <p>
   * This method creates a new Bid object based on the provided BidCreateDto,
   * setting the relevant fields and assigning default values where necessary.
   * </p>
   *
   * @param dto The BidCreateDto containing data for the new Bid.
   * @param item The Item associated with the bid.
   * @param bidder The User placing the bid.
   * @return The Bid entity created from the provided data.
   */
  public static Bid toBid(BidCreateDto dto, Item item, User bidder){
    Bid bid = new Bid();
    bid.setAmount(dto.getAmount());
    bid.setBidTime(java.time.LocalDateTime.now());
    bid.setStatus(BidStatus.PENDING);  // default status
    bid.setItem(item);
    bid.setBidder(bidder);

    return bid;
  }
}
