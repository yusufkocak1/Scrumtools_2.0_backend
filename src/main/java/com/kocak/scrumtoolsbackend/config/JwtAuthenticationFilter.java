package com.kocak.scrumtoolsbackend.config;

import com.kocak.scrumtoolsbackend.service.CustomUserDetailsService;
import com.kocak.scrumtoolsbackend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        logger.debug("Processing request: {} {}", request.getMethod(), requestURI);
        logger.debug("Authorization header: {}", requestTokenHeader != null ? "Bearer ***" : "null");

        String username = null;
        String jwtToken = null;

        if (StringUtils.hasText(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
                logger.debug("Extracted username from token: {}", username);
            } catch (Exception e) {
                logger.error("JWT Token parsing error for URI: {} - Error: {}", requestURI, e.getMessage());
                logger.debug("Token that failed: {}", jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            }
        } else {
            logger.debug("No valid Authorization header found for URI: {}", requestURI);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.debug("Loaded user details for: {}", username);

                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.debug("Authentication set for user: {}", username);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                }
            } catch (Exception e) {
                logger.error("Error during authentication process for user: {} - Error: {}", username, e.getMessage());
            }
        } else if (username == null && StringUtils.hasText(requestTokenHeader)) {
            logger.warn("Could not extract username from token for URI: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }
}
