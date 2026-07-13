package org.example.bookservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.bookservice.constant.BorrowStatus;
import org.example.bookservice.entity.BorrowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowEntity,Long> {

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
