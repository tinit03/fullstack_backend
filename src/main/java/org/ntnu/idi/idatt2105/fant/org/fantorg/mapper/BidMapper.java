package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bid;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.BidStatus;

public class BidMapper {
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
