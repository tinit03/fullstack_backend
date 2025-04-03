package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import org.springframework.data.domain.Sort;

public class SortUtil {

  /**
   * Builds a Sort object from the given field and direction.
   * Defaults to descending order if no valid direction is provided.
   *
   * @param sortField the field to sort by
   * @param sortDir the direction ("asc" or "desc")
   * @return a Sort object
   */
  public static Sort buildSort(String sortField, String sortDir) {
    if (sortField == null || sortField.trim().isEmpty()) {
      sortField = "publishedAt"; // default field if none is provided
    }

    return "asc".equalsIgnoreCase(sortDir)
        ? Sort.by(sortField).ascending()
        : Sort.by(sortField).descending();
  }
}