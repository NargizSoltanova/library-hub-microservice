package org.example.bookservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.bookservice.constant.BorrowStatus;
import org.example.bookservice.entity.BorrowEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowEntity,Long> {

    @EntityGraph(attributePaths = "book")
    Page<BorrowEntity> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "book")
    Page<BorrowEntity> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "book")
    Page<BorrowEntity> findAllByStatus(BorrowStatus status, Pageable pageable);

    boolean existsByUserIdAndBookIdAndStatusIn(
            Long userId,
            Long bookId,
            Collection<BorrowStatus> statuses
    );

    @EntityGraph(attributePaths = "book")
    Optional<BorrowEntity> findByIdAndUserIdAndStatusIn(
            Long id,
            Long userId,
            Collection<BorrowStatus> statuses
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update BorrowEntity b
               set b.status = :overdueStatus
             where b.status = :borrowedStatus
               and b.dueDate < :today
            """)
    int markExpiredBorrowsAsOverdue(
            @Param("today") LocalDate today,
            @Param("borrowedStatus") BorrowStatus borrowedStatus,
            @Param("overdueStatus") BorrowStatus overdueStatus
    );
}