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

    @Column(nullable = false)
    private Long book_id;

    @Column(nullable = false)
    private String book_name;

    @Column(nullable = false)
    private LocalDateTime borrowed_at;

    private LocalDateTime returned_at;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;
}
