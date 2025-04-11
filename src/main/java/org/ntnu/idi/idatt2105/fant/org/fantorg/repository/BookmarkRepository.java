package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.Optional;
import java.util.Set;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Bookmark;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Bookmark} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations and additional methods for managing {@link
 * Bookmark} records in the database.
 */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  /**
   * Finds a page of {@link Bookmark} records for a specific {@link User}. This method allows for
   * pagination and returns a subset of bookmarks associated with a user.
   *
   * @param user the {@link User} whose bookmarks are being fetched
   * @param pageable the pagination information to limit the number of results
   * @return a page of {@link Bookmark} records for the specified user
   */
  Page<Bookmark> findByUser(User user, Pageable pageable);

  /**
   * Finds a set of all {@link Bookmark} records for a specific {@link User}. This method returns
   * all bookmarks associated with the given user, without pagination.
   *
   * @param user the {@link User} whose bookmarks are being fetched
   * @return a set of {@link Bookmark} records for the specified user
   */
  Set<Bookmark> findByUser(User user);

  /**
   * Finds a page of {@link Bookmark} records for a specific {@link User} and {@link Item} status.
   * This method filters bookmarks based on both the user and the status of the associated item.
   *
   * @param user the {@link User} whose bookmarks are being fetched
   * @param status the {@link Status} of the items associated with the bookmarks
   * @param pageable the pagination information to limit the number of results
   * @return a page of {@link Bookmark} records for the specified user and item status
   */
  Page<Bookmark> findByUserAndItem_Status(User user, Status status, Pageable pageable);

  /**
   * Finds a {@link Bookmark} record for a specific {@link User} and {@link Item}. This method
   * returns an {@link Optional} of a bookmark, which may be empty if no matching bookmark exists
   * for the given user and item.
   *
   * @param user the {@link User} whose bookmark is being fetched
   * @param item the {@link Item} associated with the bookmark
   * @return an {@link Optional} containing the {@link Bookmark} if found, otherwise empty
   */
  Optional<Bookmark> findByUserAndItem(User user, Item item);

  /**
   * Checks if a {@link Bookmark} exists for a specific {@link User} and {@link Item}. This method
   * returns a boolean indicating whether a bookmark for the given user and item exists.
   *
   * @param user the {@link User} whose bookmark existence is being checked
   * @param item the {@link Item} associated with the bookmark
   * @return {@code true} if a bookmark exists for the given user and item, otherwise {@code false}
   */
  boolean existsByUserAndItem(User user, Item item);

  /**
   * Deletes a {@link Bookmark} for a specific {@link User} and {@link Item}. This method removes
   * the bookmark from the database based on the given user and item.
   *
   * @param user the {@link User} whose bookmark is being deleted
   * @param item the {@link Item} associated with the bookmark
   */
  void deleteByUserAndItem(User user, Item item);
}
