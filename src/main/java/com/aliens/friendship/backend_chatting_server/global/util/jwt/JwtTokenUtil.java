package com.aliens.friendship.backend_chatting_server.global.util.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${spring.jwt.chatting-access-token-secret-key}")
    private String secret;

    public Long getCurrentMemberIdFromToken(String token) {
        return extractAllClaims(token).get("memberId", Long.class);
    }

    public List<Long> getRoomIdsFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return IntStream.range(0, claims.size() - 3)
                .mapToObj(i -> claims.get(String.valueOf(i), Long.class))
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("SecurityException | MalformedJwtException e");
        } catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException e");
        } catch (UnsupportedJwtException e) {
            log.info("UnsupportedJwtException e");
        } catch (IllegalArgumentException e) {
            log.info("IllegalArgumentException e");
        }
        return false;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
        }
}