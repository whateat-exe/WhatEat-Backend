package com.exe.whateat.application.post.response;

import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostImageResponse {

    private Tsid id;
    private String caption;
    private String imageUrl;
}
