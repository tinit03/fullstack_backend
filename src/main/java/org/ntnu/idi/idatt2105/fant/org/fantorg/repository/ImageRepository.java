package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByItem_ItemId(Long itemId);
}
