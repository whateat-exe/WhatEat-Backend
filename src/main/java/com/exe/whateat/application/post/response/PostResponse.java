package com.exe.whateat.application.post.response;

import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class PostResponse {

    private Tsid id;
    private String content;
    private String accountName;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PostImageResponse> postImages;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer numberOfUp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer numberOfDown;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalComments;
    private String createdAt;
    private boolean isModified;
    private boolean isVoted;
    private PostVotingResponse postVoting;
    private Long totalVote;
    private Long totalComment;
}
