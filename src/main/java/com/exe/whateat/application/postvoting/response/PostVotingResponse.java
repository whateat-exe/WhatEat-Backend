package com.exe.whateat.application.postvoting.response;

import com.exe.whateat.entity.common.PostVotingType;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class PostVotingResponse {

    private Tsid id;
    private Tsid accountId;
    private Tsid postId;
    private PostVotingType type;
}
