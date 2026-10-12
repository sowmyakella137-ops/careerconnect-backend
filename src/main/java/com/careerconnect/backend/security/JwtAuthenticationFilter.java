
package com.careerconnect.backend.security;

import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Claims claims = jwtService.validateToken(token);
                String email = claims.getSubject();

                User user = userRepository.findByEmail(email)
                        .orElse(null);

                if (user == null || !user.isEnabled()) {
                    SecurityContextHolder.clearContext();
                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"error\":\"Account is disabled or unavailable\"}");
                    return;
                }

                String role = user.getRole().name();

                var authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                List.of(new SimpleGrantedAuthority(
                                        "ROLE_" + role)));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\":\"Invalid or expired token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
