package org.project.userservice.security;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.project.userservice.config.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class JwtService {
    private JwtProperties jwtProperties;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
