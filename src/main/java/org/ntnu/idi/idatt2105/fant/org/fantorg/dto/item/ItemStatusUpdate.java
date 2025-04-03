package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;

@Getter
@Setter
public class ItemStatusUpdate {
  private Status status;
}
