package com.testProject.TestProject.filter;

import com.testProject.TestProject.Entity.User;
import com.testProject.TestProject.Repository.UserRepository;
import com.testProject.TestProject.Service.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Step 1: Extract JWT token from Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUsername(jwt); // Extract username from token
                System.out.println("Extracted username from token: " + username); // Debugging log
            } catch (Exception e) {
                System.out.println("Error extracting username from token: " + e.getMessage());
            }
        }

        // Step 2: Validate the token and set the authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> userOptional = null;

            if (username.contains("@")) {
                userOptional = userRepository.findByEmail(username);
            } else if (username.matches("\\d+")) {
                userOptional = userRepository.findByMobileNumber(username);
            }
            else {
                userOptional = userRepository.findByUsername(username);
            }

            // Proceed only if the user is found and the token is valid
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (jwtTokenUtil.validateToken(jwt, user)) {
                    // Create an authentication token without authorities
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, List.of() // Empty authorities for now
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication successful for user: " + username); // Debugging log
                } else {
                    System.out.println("Invalid or expired JWT token for user: " + username);
                }
            } else {
                System.out.println("No user found with the username: " + username);
            }
        }

        // Step 3: Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}

