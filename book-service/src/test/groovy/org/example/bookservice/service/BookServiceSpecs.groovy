package org.example.bookservice.service

import org.example.bookservice.dto.BookRequest
import org.example.bookservice.dto.BookResponse
import org.example.bookservice.entity.BookEntity
import org.example.bookservice.entity.CategoryEntity
import org.example.bookservice.exception.BookNotFoundException
import org.example.bookservice.exception.ConflictException
import org.example.bookservice.mapper.BookMapper
import org.example.bookservice.repository.BookRepository
import org.example.bookservice.repository.CategoryRepository
import spock.lang.Specification

abstract class BookServiceSpecSupport extends Specification {
    BookRepository bookRepository = Mock()
    CategoryRepository categoryRepository = Mock()
    BookMapper mapper = Mock()
    BookService service = new BookService(bookRepository, categoryRepository, mapper)

    static BookRequest request(String isbn = " isbn-1 ") {
        BookRequest.builder()
                .title(" Clean Code ")
                .author(" Robert Martin ")
                .isbn(isbn)
                .categoryId(2L)
                .totalCopies(4)
                .availableCopies(3)
                .publishedYear(2008)
                .build()
    }
}

class BookServiceGetSpec extends BookServiceSpecSupport {
    def "getBookById returns an active book"() {
        given:
        def book = BookEntity.builder().id(1L).isActive(true).build()
        def response = BookResponse.builder().id(1L).build()
        bookRepository.findByIdAndIsActiveTrue(1L) >> Optional.of(book)
        mapper.toResponse(book) >> response

        when:
        def result = service.getBookById(1L)

        then:
        result.is(response)
    }
}

class BookServiceCreateSpec extends BookServiceSpecSupport {
    def "create normalizes fields and saves the book"() {
        given:
        def input = request()
        def category = CategoryEntity.builder().id(2L).build()
        def response = BookResponse.builder().id(10L).isbn("ISBN-1").build()
        BookEntity savedBook
        bookRepository.existsByIsbnIgnoreCase("ISBN-1") >> false
        categoryRepository.findById(2L) >> Optional.of(category)
        bookRepository.save(_ as BookEntity) >> {
            BookEntity book -> savedBook = book;
            book.id = 10L; book
        }
        mapper.toResponse(_ as BookEntity) >> response

        when:
        def result = service.create(input)

        then:
        result.is(response)
        savedBook.title == "Clean Code"
        savedBook.author == "Robert Martin"
        savedBook.isbn == "ISBN-1"
        savedBook.category.is(category)
        savedBook.isActive
    }
}

class BookServiceDuplicateIsbnSpec extends BookServiceSpecSupport {
    def "create rejects a duplicate ISBN"() {
        given:
        def input = request(" duplicate ")
        bookRepository.existsByIsbnIgnoreCase("DUPLICATE") >> true

        when:
        service.create(input)

        then:
        def error = thrown(ConflictException)
        error.message == "Duplicate isbn"
        0 * categoryRepository.findById(_)
        0 * bookRepository.save(_)
    }
}

class BookServiceUpdateSpec extends BookServiceSpecSupport {
    def "update changes an existing active book"() {
        given:
        def input = request("new-isbn")
        def book = BookEntity.builder().id(5L).title("Old").isActive(true).build()
        def category = CategoryEntity.builder().id(2L).build()
        def response = BookResponse.builder().id(5L).title("Clean Code").build()
        bookRepository.existsByIsbnIgnoreCaseAndIdNot("NEW-ISBN", 5L) >> false
        bookRepository.findByIdAndIsActiveTrue(5L) >> Optional.of(book)
        categoryRepository.findById(2L) >> Optional.of(category)
        bookRepository.save(book) >> book
        mapper.toResponse(book) >> response

        when:
        def result = service.update(5L, input)

        then:
        result.is(response)
        book.title == "Clean Code"
        book.isbn == "NEW-ISBN"
    }
}

class BookServiceDeleteSpec extends BookServiceSpecSupport {
    def "delete throws when an active book cannot be found"() {
        given:
        bookRepository.findByIdAndIsActiveTrue(404L) >> Optional.empty()

        when:
        service.delete(404L)

        then:
        def error = thrown(BookNotFoundException)
        error.message.contains("404")
        0 * bookRepository.save(_)
    }
}