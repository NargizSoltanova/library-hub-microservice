package org.project.userservice.service

import org.project.userservice.dto.UserResponse
import org.project.userservice.dto.UserUpdateDto
import org.project.userservice.entity.UserEntity
import org.project.userservice.exception.UserNotFoundException
import org.project.userservice.mapper.UserMapper
import org.project.userservice.repository.UserRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

abstract class UserServiceSpecSupport extends Specification{
    def repository = Mock(UserRepository)
    def mapper = Mock(UserMapper)
    def service = new UserService(repository, mapper)
}

class UserServiceMeSpec extends UserServiceSpecSupport {

    def cleanup() { SecurityContextHolder.clearContext() }

    def "me returns the authenticated user"() {
        given:
        def user = UserEntity.builder().id(1L).username("reader").isActive(true).build()
        def response = UserResponse.builder().id(1L).username("reader").build()
        SecurityContextHolder.context.authentication =
                new UsernamePasswordAuthenticationToken(user, null, [])
        mapper.toResponse(user) >> response

        when:
        def result = service.me()

        then:
        result.is(response)
        0 * repository._
    }
}

class UserServiceUpdateSpec extends UserServiceSpecSupport {

    def cleanup() { SecurityContextHolder.clearContext() }

    def "update changes and saves the authenticated user"() {
        given:
        def user = UserEntity.builder().id(1L).email("old@mail.test").fullName("Old").isActive(true).build()
        def request = UserUpdateDto.builder().email("new@mail.test").fullName("New Name").build()
        def response = UserResponse.builder().id(1L).email(request.email).fullName(request.fullName).build()
        SecurityContextHolder.context.authentication =
                new UsernamePasswordAuthenticationToken(user, null, [])
        repository.save(user) >> user
        mapper.toResponse(user) >> response

        when:
        def result = service.update(request)

        then:
        result.is(response)
        user.email == "new@mail.test"
        user.fullName == "New Name"
    }
}

class UserServiceDeleteSpec extends UserServiceSpecSupport {

    def "delete performs a soft delete"() {
        given:
        def user = UserEntity.builder().id(7L).isActive(true).build()
        repository.findById(7L) >> Optional.of(user)

        when:
        service.deleteUser(7L)

        then:
        !user.isActive
        1 * repository.save(user)
    }
}

class UserServiceGetByIdSpec extends UserServiceSpecSupport {

    def "getUserById throws when the user does not exist"() {
        given:
        repository.findById(99L) >> Optional.empty()

        when:
        service.getUserById(99L)

        then:
        def error = thrown(UserNotFoundException)
        error.message == "User not found"
        0 * repository.save(_)
    }
}

class UserServiceGetAllSpec extends UserServiceSpecSupport {

    def "getAllUsers maps the repository page"() {
        given:
        def pageable = PageRequest.of(0, 10)
        def user = UserEntity.builder().id(3L).username("alice").build()
        def response = UserResponse.builder().id(3L).username("alice").build()
        repository.findAll(pageable) >> new PageImpl([user], pageable, 1)
        mapper.toResponse(user) >> response

        when:
        def result = service.getAllUsers(pageable)

        then:
        result.content == [response]
        result.totalElements == 1
    }
}
