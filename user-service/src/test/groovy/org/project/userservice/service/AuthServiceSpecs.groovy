package org.project.userservice.service

import org.project.userservice.config.JwtProperties
import org.project.userservice.constant.Role
import org.project.userservice.dto.auth.LoginRequest
import org.project.userservice.dto.auth.RegisterRequest
import org.project.userservice.entity.UserEntity
import org.project.userservice.exception.ConflictException
import org.project.userservice.repository.UserRepository
import org.project.userservice.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import java.time.LocalDateTime

abstract class AuthServiceSpecSupport extends Specification {
    def jwtProperties = Mock(JwtProperties)
    def userRepository = Mock(UserRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def authManager = Mock(AuthenticationManager)
    def jwtService = Mock(JwtService)
    def service = new AuthService(
            jwtProperties,
            userRepository,
            passwordEncoder,
            authManager,
            jwtService
    )
}

class AuthServiceRegisterSpec extends AuthServiceSpecSupport {

    def "register creates an active USER with an encoded password"() {
        given:
        def request = new RegisterRequest(
                username: "reader",
                email: "reader@mail.test",
                password: "password123",
                fullName: "Library Reader"
        )
        def createdAt = LocalDateTime.of(2026, 7, 18, 12, 0)

        when:
        def result = service.register(request)

        then:
        1 * userRepository.existsByUsername("reader") >> false
        1 * userRepository.existsByEmail("reader@mail.test") >> false
        1 * passwordEncoder.encode("password123") >> "encoded-password"
        1 * userRepository.save({ UserEntity user ->
            user.username == "reader" &&
                    user.email == "reader@mail.test" &&
                    user.password == "encoded-password" &&
                    user.fullName == "Library Reader" &&
                    user.role == Role.USER &&
                    user.isActive
        }) >> { UserEntity user ->
            user.id = 42L
            user.createdAt = createdAt
            user
        }
        0 * _

        and:
        result.id == 42L
        result.username == "reader"
        result.email == "reader@mail.test"
        result.role == Role.USER
        result.createdAt == createdAt
    }

    def "register rejects a duplicate username before checking email"() {
        given:
        def request = new RegisterRequest(
                username: "reader",
                email: "new@mail.test",
                password: "password123"
        )

        when:
        service.register(request)

        then:
        1 * userRepository.existsByUsername("reader") >> true
        0 * userRepository.existsByEmail(_)
        0 * passwordEncoder._
        0 * userRepository.save(_)

        and:
        def error = thrown(ConflictException)
        error.message == "Username already exists."
    }

    def "register rejects a duplicate email"() {
        given:
        def request = new RegisterRequest(
                username: "new-reader",
                email: "reader@mail.test",
                password: "password123"
        )

        when:
        service.register(request)

        then:
        1 * userRepository.existsByUsername("new-reader") >> false
        1 * userRepository.existsByEmail("reader@mail.test") >> true
        0 * passwordEncoder._
        0 * userRepository.save(_)

        and:
        def error = thrown(ConflictException)
        error.message == "Email already exists."
    }
}

class AuthServiceLoginSpec extends AuthServiceSpecSupport {

    def "login authenticates, updates last login and returns a JWT"() {
        given:
        def request = new LoginRequest(username: "reader", password: "password123")
        def user = UserEntity.builder()
                .id(7L)
                .username("reader")
                .role(Role.USER)
                .isActive(true)
                .build()
        def authentication = Mock(Authentication)
        def beforeLogin = LocalDateTime.now()

        when:
        def result = service.login(request)
        def afterLogin = LocalDateTime.now()

        then:
        1 * authManager.authenticate({ token ->
            token instanceof UsernamePasswordAuthenticationToken &&
                    token.principal == "reader" &&
                    token.credentials == "password123"
        }) >> authentication
        1 * authentication.getPrincipal() >> user
        1 * userRepository.save(user) >> user
        1 * jwtService.generateToken(user) >> "jwt-token"
        1 * jwtProperties.getExpiration() >> 86_400_000L
        0 * _

        and:
        user.lastLoginAt != null
        !user.lastLoginAt.isBefore(beforeLogin)
        !user.lastLoginAt.isAfter(afterLogin)
        result.accessToken == "jwt-token"
        result.tokenType == "Bearer"
        result.expiresIn == 86_400_000L
    }

    def "authentication failure without changing user state"() {
        given:
        def request = new LoginRequest(username: "reader", password: "wrong-password")

        when:
        service.login(request)

        then:
        1 * authManager.authenticate(_ as UsernamePasswordAuthenticationToken) >> {
            throw new BadCredentialsException("Bad credentials")
        }
        0 * userRepository._
        0 * jwtService._
        0 * jwtProperties._

        and:
        def error = thrown(BadCredentialsException)
        error.message == "Bad credentials"
    }
}
