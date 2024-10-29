package com.hackathon.bankingapp.service;

import com.hackathon.bankingapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.header}")
    private String jwtHeader;

    @Value("${jwt.prefix}")
    private String jwtPrefix;

    private final List<String> loggedOutTokens = new CopyOnWriteArrayList<>();

    public String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(jwtHeader);

        if (bearer != null && bearer.startsWith(jwtPrefix)) {
            // Remove the "Bearer " prefix
            return bearer.substring(7);
        }

        return null;
    }

    public String getToken(User user) {
        return getToken(new HashMap<>(), user);
    }

    public String getToken(Map<String, Object> extraClaims, User user) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDateFromToken = getExpirationDateFromToken(token);
        return expirationDateFromToken.before(new Date());
    }

    public void logoutToken(String token) {
        loggedOutTokens.add(token);
    }

    public boolean isTokenLoggedOut(String token) {
        return loggedOutTokens.contains(token);
    }
}
