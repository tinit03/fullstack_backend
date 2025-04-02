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

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
  Page<Bookmark> findByUser(User user, Pageable pageable);
  Page<Bookmark> findByUserAndItem_Status(User user, Status status, Pageable pageable);
  Set<Bookmark> findByUser(User user);
  Optional<Bookmark> findByUserAndItem(User user, Item item);

  boolean existsByUserAndItem(User user, Item item);

  void deleteByUserAndItem(User user, Item item);
}
