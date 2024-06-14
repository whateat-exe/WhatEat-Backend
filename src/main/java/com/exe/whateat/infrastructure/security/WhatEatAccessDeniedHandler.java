package com.exe.whateat.infrastructure.security;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@AllArgsConstructor
public final class WhatEatAccessDeniedHandler implements AccessDeniedHandler {

    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(CONTENT_TYPE);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(WhatEatErrorResponse.builder()
                .code(WhatEatErrorCode.WEA_0002)
                .reason("token", "Không có token hoặc token hiện tại không đủ thẩm quyền.")
                .build()));
        writer.flush();
    }
}
