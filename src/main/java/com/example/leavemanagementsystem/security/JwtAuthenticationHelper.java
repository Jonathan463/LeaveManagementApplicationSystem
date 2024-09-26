package com.example.leavemanagementsystem.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationHelper {
    @Value("${jwt.key}")
    private  String jwtKey;
    private static final long JWT_TOKEN_VALIDITY = 3600000; // 1hr

    public String getUsernameFromToken(String token)
    {
        Claims claims =  getClaimsFromToken(token);
        return claims.getSubject();
    }

    public Claims getClaimsFromToken(String token)
    {
        return Jwts.parserBuilder().setSigningKey(jwtKey.getBytes())
                .build().parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token)
    {
        Claims claims =  getClaimsFromToken(token);
        Date expDate = claims.getExpiration();
        return expDate.before(new Date());
    }

    public String generateToken(String username) {

        // populate the token with any data of choice here
        Map<String,Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+JWT_TOKEN_VALIDITY))
                .signWith(new SecretKeySpec(jwtKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()),SignatureAlgorithm.HS512)
                .compact();
    }

    public String setTokenExpirationToPast(String token) {
        // Parse the token to get claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtKey.getBytes())
                .build()
                .parseClaimsJws(token).getBody();

        // Set token expiration to a past date (e.g., 1 minute ago)
        claims.setExpiration(new Date(System.currentTimeMillis() - 3600000)); // 1 hour ago

        return Jwts.builder()
                .setClaims(claims)
                .signWith(new SecretKeySpec(jwtKey.getBytes(),SignatureAlgorithm.HS512.getJcaName()),SignatureAlgorithm.HS512)
                .compact();
    }

}
