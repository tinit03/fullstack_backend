package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

}
