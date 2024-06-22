package com.exe.whateat.application.postcomment.response;

import com.exe.whateat.application.user.response.UserResponse;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PostCommentResponse {
    private Tsid id;
    private Tsid accountId;
    private String fullName;
    private String imageUrl;
    private String createdAt;
    private boolean isModified;
    private String content;
}
