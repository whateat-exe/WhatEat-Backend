package com.exe.whateat.application.common.response;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"page", "limit", "totalPages", "count", "data"})
public abstract class PaginationResponse<T> {

    @Getter
    private final int count;

    @Getter
    private int page;

    @Getter
    private int limit;

    @Getter
    private final List<T> data;

    @JsonIgnore
    private final Long total;

    protected PaginationResponse(List<T> data, Long total) {
        if (total == null || total < 0) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("total", "Lỗi khi trả về dữ liệu danh sách cho người dùng.")
                    .build();
        }
        this.data = Objects.requireNonNullElse(data, Collections.emptyList());
        this.count = this.data.size();
        this.total = total;
    }

    @JsonGetter
    public int getTotalPages() {
        if (limit < 0) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("totalPages", "Lỗi khi tính tổng trang.")
                    .build();
        }
        return (int) (((total % limit) == 0)
                ? (total / limit)
                : ((total / limit) + 1));
    }

    public void setPage(Integer page) {
        if (page == null || page < 0) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("page", "Lỗi khi gắn biến 'page'.")
                    .build();
        }
        this.page = page;
    }

    public void setLimit(Integer limit) {
        if (limit == null || limit < 1) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("limit", "Lỗi khi gắn biến 'limit'.")
                    .build();
        }
        this.limit = limit;
    }
}
