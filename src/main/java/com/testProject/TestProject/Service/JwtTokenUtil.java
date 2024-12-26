package com.testProject.TestProject.Service;
import com.testProject.TestProject.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Generate token with given username
    public String generateToken(String userName) {

        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName, 1000 * 60 * 60 * 20); //  only
    }

    public String generateRefreshToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName, 1000 * 60 * 60 * 2); // Refresh token valid for 2 hr
    }

    // Create a JWT token with specified claims and subject (username)
    private String createToken(Map<String, Object> claims, String userName, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Token valid for 3 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Get the signing key for JWT token
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired for username: " + extractUsername(token)); // Added logging for expired tokens
            throw new ExpiredJwtException(null, null, "Token has expired.");
        }
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    // Validate the token against user details and expiration
    public Boolean validateToken(String token, Object userObject) {
        String usernameFromToken = extractUsername(token);

        if (userObject instanceof User) {
            return usernameFromToken.equals(((User) userObject).getUsername()) && !isTokenExpired(token);
        } else if (userObject instanceof UserDetails) {
            return usernameFromToken.equals(((UserDetails) userObject).getUsername()) && !isTokenExpired(token);
        }

        return false;
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            // Validate refresh token and extract user information
            String username = extractUsername(refreshToken);
            System.out.println(refreshToken);
            return generateToken(username);
        } catch (ExpiredJwtException e) {
            System.out.println("Refresh token expired, please login again.");
            throw e; // Handle expired refresh token
        }
    }
}
