package org.ntnu.idi.idatt2105.fant.org.fantorg.specification;

import org.springframework.data.domain.Sort;


/**
 * Utility class for building {@link Sort} objects used in sorting query results.
 * This class provides a static method to create a {@link Sort} object based on a specified field and direction.
 * The sorting is applied in the specified order (ascending or descending), and a default sorting field
 * is used if none is provided.
 *
 * @version 1.0
 */
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