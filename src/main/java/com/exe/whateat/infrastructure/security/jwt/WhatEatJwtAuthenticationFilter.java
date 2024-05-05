package com.exe.whateat.infrastructure.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class WhatEatJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_REGEX = "^Bearer\\s+(\\S+)$";

    private final WhatEatJwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(authorizationHeader)) {
            final DecodedJWT jwt = extractToken(authorizationHeader);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtHelper.extractEmail(jwt));
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private DecodedJWT extractToken(String authorizationHeader) {
        final Pattern pattern = Pattern.compile(BEARER_REGEX);
        final Matcher matcher = pattern.matcher(authorizationHeader);
        if (!matcher.matches()) {
            throw new JWTVerificationException("No Bearer token found");
        }
        final String token = matcher.group(1);
        return jwtHelper.verifyToken(token);
    }
}
