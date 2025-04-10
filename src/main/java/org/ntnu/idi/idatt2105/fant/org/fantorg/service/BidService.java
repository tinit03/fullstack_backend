package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.bid.BidDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

public interface BidService {
  BidDto placeBid(BidCreateDto dto, User bidder);
  List<BidDto> getBidsForItem(Long itemId);
  OrderDto acceptBid(Long bidId, User seller, boolean accept);
}
