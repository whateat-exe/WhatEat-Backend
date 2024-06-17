package com.exe.whateat.infrastructure.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class WhatEatJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final class AccountNotActiveException extends RuntimeException {

        public AccountNotActiveException(String message) {
            super(message);
        }
    }

    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";
    private static final String BEARER_REGEX = "^Bearer\\s+(\\S+)$";

    private final WhatEatJwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Value("${whateat.api.path}")
    private String api;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authorizationHeader = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(authorizationHeader)) {
                final DecodedJWT jwt = extractToken(authorizationHeader);
                final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtHelper.extractEmail(jwt));
                if (userDetails instanceof Account account && account.getStatus() != ActiveStatus.ACTIVE) {
                    throw new AccountNotActiveException("Tài khoản không trong trạng thái ACTIVE.");
                }
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            final String message = e.getMessage();
            if (StringUtils.containsIgnoreCase(message, "expire")
                    && requestPathIsInWhitelist(request.getRequestURI())) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(CONTENT_TYPE);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(objectMapper.writeValueAsString(WhatEatErrorResponse.builder()
                        .code(WhatEatErrorCode.WEA_0003)
                        .reason("token", e.getMessage())
                        .build()));
                writer.flush();
            }
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(WhatEatErrorResponse.builder()
                    .code(WhatEatErrorCode.WEA_0003)
                    .reason("token", "Token không hợp lệ.")
                    .build()));
            writer.flush();
        } catch (AccountNotActiveException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(CONTENT_TYPE);
            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(WhatEatErrorResponse.builder()
                    .code(WhatEatErrorCode.WEA_0004)
                    .reason("account", e.getMessage())
                    .build()));
            writer.flush();
        }
    }

    private boolean requestPathIsInWhitelist(String requestPath) {
        return StringUtils.containsIgnoreCase(requestPath, api + "/auth");
    }

    private DecodedJWT extractToken(String authorizationHeader) {
        final Pattern pattern = Pattern.compile(BEARER_REGEX);
        final Matcher matcher = pattern.matcher(authorizationHeader);
        if (!matcher.matches()) {
            throw new JWTVerificationException("Cấu trúc của Authorization Header hoặc token không hợp lệ.");
        }
        final String token = matcher.group(1);
        return jwtHelper.verifyToken(token);
    }
}
