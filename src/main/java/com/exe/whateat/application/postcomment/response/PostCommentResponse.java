package com.exe.whateat.application.postcomment.response;

import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PostCommentResponse {
    private Tsid id;
    private String content;
}
