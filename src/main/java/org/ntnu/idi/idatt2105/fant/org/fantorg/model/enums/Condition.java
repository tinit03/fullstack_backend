package org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums;

/**
 * Enum representing the condition of an item being listed for sale.
 * <p>
 * The Condition enum provides various stages of item condition, from new to functional parts:
 * <ul>
 *   <li><b>NEW</b>: The item is brand new and has never been used.</li>
 *   <li><b>LIKE_NEW</b>: The item has been used but is still in perfect condition.</li>
 *   <li><b>VERY_GOOD</b>: The item shows minor signs of use but is in great condition.</li>
 *   <li><b>GOOD</b>: The item has noticeable wear but is still fully functional.</li>
 *   <li><b>ACCEPTABLE</b>: The item is heavily used but still works.</li>
 *   <li><b>FOR_PARTS</b>: The item is not functional and is only suitable for parts or repair.</li>
 * </ul>
 * </p>
 */
public enum Condition {

  /**
   * The item is brand new and has never been used.
   */
  NEW,

  /**
   * The item has been used but is in perfect condition.
   */
  LIKE_NEW,

  /**
   * The item has minor signs of use but is in great condition.
   */
  VERY_GOOD,

  /**
   * The item shows noticeable wear but remains fully functional.
   */
  GOOD,

  /**
   * The item is heavily used but still works.
   */
  ACCEPTABLE,

  /**
   * The item is not functional and is suitable only for parts or repair.
   */
  FOR_PARTS
}
