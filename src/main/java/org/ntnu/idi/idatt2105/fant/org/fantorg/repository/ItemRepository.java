package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import java.util.Optional;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
  Page<Item> findItemByTitle(String itemName, Pageable pageable);

  Page<Item> findItemBySeller(User seller,Pageable pageable);

  Page<Item> findByTitleContainingIgnoreCaseAndCategory(String keyword, Category category, Pageable pageable);

  Page<Item> findByTitleContainingIgnoreCase(String keyword, Pageable pagable);

  Page<Item> findByCategory(Category category, Pageable pageable);

}
