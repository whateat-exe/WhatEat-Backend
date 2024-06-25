package com.exe.whateat.application.postcomment.response;

import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Builder
@Getter
@Setter
public class PostCommentResponse {
    private Tsid id;
    private Tsid accountId;
    private String fullName;
    private String imageUrl;
    private Instant createdAt;
    private boolean isModified;
    private String content;
}
