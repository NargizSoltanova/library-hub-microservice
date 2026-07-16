package org.example.bookservice.service

import org.example.bookservice.constant.BorrowStatus
import org.example.bookservice.dto.BorrowResponse
import org.example.bookservice.entity.BookEntity
import org.example.bookservice.entity.BorrowEntity
import org.example.bookservice.event.BorrowEvent
import org.example.bookservice.exception.ConflictException
import org.example.bookservice.mapper.BorrowMapper
import org.example.bookservice.repository.BookRepository
import org.example.bookservice.repository.BorrowRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

abstract class BorrowServiceSpecSupport extends Specification {
    BorrowRepository borrowRepository = Mock()
    BookRepository bookRepository = Mock()
    BorrowMapper mapper = Mock()
    ApplicationEventPublisher publisher = Mock()
    BorrowService service = new BorrowService(borrowRepository, bookRepository, mapper, publisher)
}

class BorrowServiceBorrowSpec extends BorrowServiceSpecSupport {
    def "borrow decreases availability, saves and publishes an event"() {
        given:
        def book = BookEntity.builder().id(4L).title("Dune").availableCopies(2).isActive(true).build()
        def response = BorrowResponse.builder().id(20L).userId(8L).status(BorrowStatus.BORROWED).build()
        borrowRepository.existsByUserIdAndBookIdAndStatusIn(8L, 4L, _) >> false
        bookRepository.findActiveByIdForUpdate(4L) >> Optional.of(book)
        borrowRepository.save(_ as BorrowEntity) >> { BorrowEntity borrow ->
            borrow.id = 20L; borrow.borrowedAt = java.time.LocalDateTime.now(); borrow.status = BorrowStatus.BORROWED; borrow
        }
        mapper.toResponse(_ as BorrowEntity) >> response

        when:
        def result = service.borrow(4L, 8L)

        then:
        result.is(response)
        book.availableCopies == 1
        1 * publisher.publishEvent({ BorrowEvent event -> event.borrowId() == 20L && event.status() == BorrowStatus.BORROWED })
    }
}

class BorrowServiceDuplicateSpec extends BorrowServiceSpecSupport {
    def "borrow rejects a book already borrowed by the user"() {
        given:
        borrowRepository.existsByUserIdAndBookIdAndStatusIn(8L, 4L, _) >> true

        when:
        service.borrow(4L, 8L)

        then:
        def error = thrown(ConflictException)
        error.message == "Book has already been borrowed."
        0 * bookRepository.findActiveByIdForUpdate(_)
        0 * borrowRepository.save(_)
    }
}

class BorrowServiceReturnSpec extends BorrowServiceSpecSupport {
    def "returnBook marks the borrow returned and restores availability"() {
        given:
        def book = BookEntity.builder().id(4L).title("Dune").availableCopies(1).isActive(true).build()
        def borrow = BorrowEntity.builder().id(20L).userId(8L).book(book).status(BorrowStatus.BORROWED)
                .borrowedAt(java.time.LocalDateTime.now().minusDays(2)).build()
        def response = BorrowResponse.builder().id(20L).status(BorrowStatus.RETURNED).build()
        borrowRepository.findByIdAndUserIdAndStatusIn(20L, 8L, _) >> Optional.of(borrow)
        bookRepository.findActiveByIdForUpdate(4L) >> Optional.of(book)
        borrowRepository.save(borrow) >> borrow
        mapper.toResponse(borrow) >> response

        when:
        def result = service.returnBook(20L, 8L)

        then:
        result.is(response)
        borrow.status == BorrowStatus.RETURNED
        borrow.returnedAt != null
        book.availableCopies == 2
        1 * publisher.publishEvent({ BorrowEvent event -> event.status() == BorrowStatus.RETURNED })
    }
}

class BorrowServiceMyBorrowsSpec extends BorrowServiceSpecSupport {
    def "getMyBorrows maps only the requested user's page"() {
        given:
        def pageable = PageRequest.of(0, 5)
        def borrow = BorrowEntity.builder().id(1L).userId(8L).build()
        def response = BorrowResponse.builder().id(1L).userId(8L).build()
        borrowRepository.findAllByUserId(8L, pageable) >> new PageImpl([borrow], pageable, 1)
        mapper.toResponse(borrow) >> response

        when:
        def result = service.getMyBorrows(8L, pageable)

        then:
        result.content == [response]
    }
}

class BorrowServiceOverdueSpec extends BorrowServiceSpecSupport {
    def "getOverdueBorrows requests and maps overdue records"() {
        given:
        def pageable = PageRequest.of(1, 5)
        def borrow = BorrowEntity.builder().id(2L).status(BorrowStatus.OVERDUE).build()
        def response = BorrowResponse.builder().id(2L).status(BorrowStatus.OVERDUE).build()
        borrowRepository.findAllByStatus(BorrowStatus.OVERDUE, pageable) >> new PageImpl([borrow], pageable, 6)
        mapper.toResponse(borrow) >> response

        when:
        def result = service.getOverdueBorrows(pageable)

        then:
        result.content == [response]
    }
}