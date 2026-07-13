package org.example.bookservice.specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import org.example.bookservice.entity.BookEntity;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecification {

    public static Specification<BookEntity> filter(Long categoryId, String author) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));

            if (categoryId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category").get("id"), categoryId)
                );
            }

            if (author != null && !author.isBlank()) {
                String normalized = "%" + author.trim().toLowerCase() + "%";

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("author")), normalized)
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<BookEntity> search(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String normalizedKeyword = "%" + keyword.trim().toLowerCase() + "%";

            var activePredicate = criteriaBuilder.isTrue(root.get("isActive"));

            var titlePredicate =
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), normalizedKeyword);

            var authorPredicate =
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), normalizedKeyword
                    );

            return criteriaBuilder.and(
                    activePredicate,
                    criteriaBuilder.or(titlePredicate, authorPredicate)
            );
        };
    }
}
