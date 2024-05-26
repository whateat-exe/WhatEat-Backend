package com.exe.whateat.application.common.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class PaginationRequest {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_LIMIT = 10;

    @Min(value = 0, message = "'page' phải bắt đầu từ 0 trở đi.")
    private int page = DEFAULT_PAGE;

    @Min(value = 1, message = "'limit' phải bắt đầu từ 1 trở đi.")
    private int limit = DEFAULT_LIMIT;

    public final int getPage() {
        return page;
    }

    public final int getLimit() {
        return limit;
    }

    @JsonIgnore
    public final long getOffset() {
        return ((long) getPage() * getLimit());
    }
}
