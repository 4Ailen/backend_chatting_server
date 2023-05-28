package com.aliens.friendship.backend_chatting_server.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secret;
    long tokenValidityInSeconds =  	172800;


    public String generateToken(Long memberId,List<Long> roomIds ) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInSeconds);

        Claims claims = Jwts.claims();
        claims.put("memberId",memberId);
        // 매칭된 인원에 수에 대해서 가변적이기 때문에 다음과 같이 숫자를 기반으로 추가
        for(int i = 0 ; i < roomIds.size(); i ++ ){
            claims.put(String.valueOf(i),roomIds.get(i));
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(validity)
                .signWith(getSigningKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }


    public Long getCurrentMemberIdFromToken(String token) {
        return extractAllClaims(token).get("memberId", Long.class);
    }

    public List<Long> getRoomIdsFromToken(String token) {
        Claims claim = extractAllClaims(token);

        return IntStream.range(1, claim.size())
                .mapToObj(i -> claim.get(String.valueOf(i), Long.class))
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