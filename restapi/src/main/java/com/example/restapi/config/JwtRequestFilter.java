package com.example.restapi.config;

import com.example.restapi.service.implementation.CustomUserDetailsService;
import com.example.restapi.service.implementation.TokenBlackListService;
import com.example.restapi.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@RequiredArgsConstructor

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlackListService tokenBlackListService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
             token = requestTokenHeader.substring(7);
             if (token != null && tokenBlackListService.isTokenBlacklisted(token)) {
                 response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
             }
             try {
                 email = jwtTokenUtil.getUsernameFromToken(token);
                 System.out.println("Extracted email: " + email);

             }catch (IllegalArgumentException ex) {
                 throw new RuntimeException("Unable to get jwt token");
             } catch (ExpiredJwtException ex) {
                 throw new RuntimeException("Unable to get jwt token");
             }
        }
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            System.out.println("Extracted email" + email);
            System.out.println("User email" + userDetails.getUsername());
            if (jwtTokenUtil.validateToken(token,userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
