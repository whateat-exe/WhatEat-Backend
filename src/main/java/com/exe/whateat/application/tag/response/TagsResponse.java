package com.exe.whateat.application.tag.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class TagsResponse extends PaginationResponse<TagResponse> {
    public TagsResponse(List<TagResponse> data, Long total) {
        super(data, total);
    }
}
