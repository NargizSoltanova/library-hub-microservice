package org.example.bookservice.repository;

import jakarta.persistence.LockModeType;
import org.example.bookservice.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity,Long>, JpaSpecificationExecutor<BookEntity> {
    boolean existsByIsbnIgnoreCase(String isbn);

    boolean existsByIsbnIgnoreCaseAndIdNot(String isbn, Long id);

    boolean existsByCategoryId(Long categoryId);

    Optional<BookEntity> findByIdAndIsActiveTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from BookEntity b
                where b.id = :id and b.isActive = true
            """)
    Optional<BookEntity> findActiveByIdForUpdate(@Param("id") Long id);
}
