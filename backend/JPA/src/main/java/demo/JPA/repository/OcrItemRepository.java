package demo.JPA.repository;

import demo.JPA.entity.OcrItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OcrItemRepository extends JpaRepository<OcrItem, Long> {

    Optional<OcrItem> findbyid(Long id);

}