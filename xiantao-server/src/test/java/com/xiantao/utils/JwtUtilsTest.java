package com.xiantao.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JwtUtils 安全关键路径单元测试：签发/解析、过期、密钥不匹配（伪造/轮换）三类失败路径。
 * 与 F5（JWT 密钥外置轮换）呼应——验证旧/异密钥签发的令牌不被接受。
 */
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private static final String SECRET = "unit-test-secret-key-at-least-32-bytes-long-000000";
    private static final long EXPIRATION = 86_400_000L;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiration", EXPIRATION);
    }

    @Test
    @DisplayName("签发的令牌可解析出 userId 与 username 且校验通过")
    void generateAndParse_roundTrip() {
        String token = jwtUtils.generateToken(100L, "test001");
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(100L, jwtUtils.getUserId(token));
        assertEquals("test001", jwtUtils.getUsername(token));
    }

    @Test
    @DisplayName("已过期令牌 validateToken 返回 false")
    void expiredToken_isInvalid() {
        // 用相同密钥手工签发一个过去时间即过期的令牌
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date past = new Date(System.currentTimeMillis() - 10_000);
        String expired = Jwts.builder()
                .subject("test001")
                .issuedAt(new Date(past.getTime() - 1000))
                .expiration(past)
                .signWith(key)
                .compact();
        assertFalse(jwtUtils.validateToken(expired));
    }

    @Test
    @DisplayName("异密钥（伪造/旧密钥）签发的令牌不被接受")
    void tamperedOrForeignKeyToken_isRejected() {
        SecretKey foreignKey = Keys.hmacShaKeyFor(
                "a-completely-different-secret-key-32bytes-min-000".getBytes(StandardCharsets.UTF_8));
        String forged = Jwts.builder()
                .subject("attacker")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(foreignKey)
                .compact();
        assertFalse(jwtUtils.validateToken(forged));
    }

    @Test
    @DisplayName("结构非法的令牌 validateToken 返回 false 而非抛异常")
    void malformedToken_isInvalid() {
        assertFalse(jwtUtils.validateToken("not-a-jwt"));
        assertFalse(jwtUtils.validateToken(""));
    }
}
