package org.project.userservice.repository;

import org.project.userservice.entity.BorrowHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistoryEntity,Long > {

    Optional<BorrowHistoryEntity> findByBorrowId(Long borrowId);

    boolean existsByBorrowId(Long borrowId);
}
