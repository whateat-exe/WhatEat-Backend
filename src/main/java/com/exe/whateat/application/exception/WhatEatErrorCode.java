package com.exe.whateat.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Guidelines:
 * <ul>
 * <li> WEB: Business errors.
 * <li> WEV: Validation errors.
 * <li> WES: Server errors.
 * <li> WEA: Authentication/Authorization errors.
 * </ul>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WhatEatErrorCode {

    WEB_0000("Dummy business error code", HttpStatus.BAD_REQUEST),
    WEB_0001("Restaurant name or email already exists", HttpStatus.BAD_REQUEST),
    WEB_0002("Restaurant not found", HttpStatus.BAD_REQUEST),
    WEB_0003("Invalid restaurant status", HttpStatus.BAD_REQUEST),
    WEB_0004("Tên của món ăn đã bị trùng", HttpStatus.BAD_REQUEST),
    WEB_0005("Món ăn không tìm thấy", HttpStatus.BAD_REQUEST),
    WEB_0006("Món ăn được chỉ tới không hợp lệ", HttpStatus.BAD_REQUEST),
    WEB_0007("Trạng thái món ăn không hợp lệ", HttpStatus.BAD_REQUEST),
    WEB_0008("Type không phù hợp", HttpStatus.BAD_REQUEST),
    WEB_0009("Food Tag đã tồn tại", HttpStatus.BAD_REQUEST),
    WEB_0010("ID không tồn tại", HttpStatus.BAD_REQUEST),
    WEB_0011("Tài khoản không hợp lệ để gửi mã xác thực", HttpStatus.BAD_REQUEST),
    WEB_0012("Trạng thái hoạt động không hợp lệ", HttpStatus.BAD_REQUEST),
    WEB_0013("Món ăn không lấy ngẫu nhiên được.", HttpStatus.BAD_REQUEST),
    WEB_0014("Món ăn đã bị trùng.", HttpStatus.BAD_REQUEST),
    WEB_0015("Món ăn không tồn tại.", HttpStatus.BAD_REQUEST),
    WEB_0016("Đánh giá không tồn tại.", HttpStatus.BAD_REQUEST),
    WEB_0017("Bạn đã tạo đánh giá trước đó cho món này.", HttpStatus.BAD_REQUEST),
    WEV_0000("Invalid request", HttpStatus.BAD_REQUEST),
    WEV_0001("Invalid email address", HttpStatus.BAD_REQUEST),
    WEV_0002("Invalid password", HttpStatus.BAD_REQUEST),
    WEV_0003("Invalid amount", HttpStatus.BAD_REQUEST),
    WEV_0004("Invalid timestamp format", HttpStatus.BAD_REQUEST),
    WEV_0005("Invalid ID format", HttpStatus.BAD_REQUEST),
    WEV_0006("Invalid image format", HttpStatus.BAD_REQUEST),
    WEV_0007("Invalid phone number", HttpStatus.BAD_REQUEST),
    WEV_0008("Invalid full name", HttpStatus.BAD_REQUEST),
    WEV_0009("Oversize image", HttpStatus.BAD_REQUEST),
    WES_0000("Dummy server error", HttpStatus.INTERNAL_SERVER_ERROR),
    WES_0001("Lỗi server", HttpStatus.INTERNAL_SERVER_ERROR),
    WES_0002("Unknown user account", HttpStatus.INTERNAL_SERVER_ERROR),
    WES_0003("3rd party service error", HttpStatus.INTERNAL_SERVER_ERROR),
    WES_0004("Unknown image URL", HttpStatus.INTERNAL_SERVER_ERROR),
    WEA_0000("Dummy authorization/authentication error code", HttpStatus.FORBIDDEN),
    WEA_0001("Not authorized", HttpStatus.UNAUTHORIZED),
    WEA_0002("Forbidden", HttpStatus.FORBIDDEN),
    WEA_0003("Invalid authentication token", HttpStatus.UNAUTHORIZED),
    WEA_0004("Inactive account", HttpStatus.UNAUTHORIZED),
    WEA_0005("Invalid login credentials", HttpStatus.UNAUTHORIZED),
    WEA_0006("Invalid refresh token", HttpStatus.BAD_REQUEST),
    WEA_0007("Internal Server", HttpStatus.INTERNAL_SERVER_ERROR),
    WEA_0008("Unverified user account", HttpStatus.UNAUTHORIZED),
    WEA_0009("Unverified restaurant account", HttpStatus.UNAUTHORIZED),
    WEA_0010("Unverified account", HttpStatus.UNAUTHORIZED),
    WEA_0011("Mã xác thực không hợp lệ.", HttpStatus.BAD_REQUEST),
    WEA_0012("Lỗi mã xác thực tài khoản", HttpStatus.BAD_REQUEST),
    WEA_0013("Lỗi xác thực tài khoản", HttpStatus.BAD_REQUEST);

    private final String title;
    private final HttpStatus status;

    @Override
    public String toString() {
        return name().replace("_", "-");
    }
}
