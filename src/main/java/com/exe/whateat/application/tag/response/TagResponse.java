package com.exe.whateat.application.tag.response;

import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class TagResponse {

    private Tsid id;
    private String name;
    private String type;
}
