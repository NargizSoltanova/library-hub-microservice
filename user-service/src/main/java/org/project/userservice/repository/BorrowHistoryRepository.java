package org.project.userservice.repository;

import org.project.userservice.entity.BorrowHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistoryEntity,Integer> {
}
