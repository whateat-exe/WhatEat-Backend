package com.exe.whateat.application.post.response;

import com.exe.whateat.application.user.response.UserResponse;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PostResponse {

    private Tsid id;
    private String content;
    private Tsid accountId;
    private List<PostImageResponse> postImages;
    private int numberOfUp;
    private int numberOfDown;
}
