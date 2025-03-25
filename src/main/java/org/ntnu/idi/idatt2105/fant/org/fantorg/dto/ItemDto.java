package org.ntnu.idi.idatt2105.fant.org.fantorg.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;


@Setter
@Getter
public class ItemDto {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private List<String> tags;
  private String city;
  private String postalCode;
  private LocalDateTime publishedAt;
  private Long sellerId;         // om vi ønsker å henvise bruker til selgerens brukerside
  private String sellerUsername; // om vi ønsker å vise selger sin brukernavn

}
