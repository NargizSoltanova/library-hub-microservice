package org.project.userservice.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SecurityExceptionHandler securityExceptionHandler;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.getUsername(token);

            if (username == null || username.isBlank()) {
                throw new BadCredentialsException("Invalid or expired JWT token");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);

                if (!userDetails.isEnabled()) {
                    SecurityContextHolder.clearContext();
                    securityExceptionHandler.handle(
                            request,
                            response,
                            new AccessDeniedException("User is disabled")
                    );
                    return;
                }

                if (jwtService.isTokenValid(token, userDetails)) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
                }
            }
        } catch (JwtException | AuthenticationException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            securityExceptionHandler.commence(
                    request,
                    response,
                    new BadCredentialsException("Invalid or expired JWT token", exception)
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
