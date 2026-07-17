package org.project.userservice.repository;

import org.project.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    @EntityGraph(attributePaths = "borrowHistories")
    Optional<UserEntity> findByUsername (String username);
    boolean existsByUsername(String username);
    boolean existsByEmail (String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
            select u
                from UserEntity u
                where u.isActive = true and coalesce(u.lastLoginAt, u.createdAt) < :date
        """)
    List<UserEntity> findInactiveUsers( @Param("date") LocalDateTime date );
}
