package org.project.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.project.userservice.constant.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "book_name", nullable = false)
    private String bookName;

    @Column(name = "borrowed_at", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;
}
