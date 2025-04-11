package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;

/**
 * Data Transfer Object (DTO) used to update the status of an item.
 *
 * <p>This DTO contains the status to which an item should be updated. The status could be values
 * such as "available", "sold", "reserved", etc. The `Status` enumeration defines all possible
 * statuses for an item.
 */
@Getter
@Setter
public class ItemStatusUpdate {

  /**
   * The new status of the item.
   *
   * <p>This field contains the status to which the item should be updated. It is represented by an
   * instance of the {@link Status} enum.
   */
  private Status status;
}
