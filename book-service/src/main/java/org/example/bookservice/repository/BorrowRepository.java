package org.example.bookservice.repository;

import org.example.bookservice.entity.BorrowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowEntity,Long> {
}
