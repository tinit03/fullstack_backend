package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageProfileDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;

@Setter
@Getter
public class UserDto {
  private Long id;
  private String email;
  private String fullName;
  private List<ItemDto> ownItems;
  private Set<ItemDto> bookmarkedItems;
  private ImageProfileDto profilePicture;
}
