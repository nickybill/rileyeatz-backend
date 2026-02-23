package com.rileyeatz.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 🔐 Use a strong secret key (minimum 256 bits for HS256)
    private static final String SECRET =
            "your_super_secret_key_that_is_at_least_32_characters_long";

    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ================= EXTRACT USERNAME =================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ================= EXTRACT EXPIRATION =================
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ================= EXTRACT CLAIM =================
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ================= EXTRACT ALL CLAIMS =================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ================= CHECK IF TOKEN EXPIRED =================
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ================= GENERATE TOKEN =================
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= VALIDATE TOKEN =================
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }
}