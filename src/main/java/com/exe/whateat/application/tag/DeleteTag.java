package com.exe.whateat.application.tag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.TagRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteTag {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "tags",
            description = "APIs for tags"
    )
    public static final class DeleteTagController extends AbstractController {

        private DeleteTagService deleteTagService;

        @DeleteMapping("/tags/{id}")
        @Operation(
                summary = "Delete tag API. Only for ADMIN and MANAGER."
        )
        @ApiResponse(
                description = "Successful deactivation.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed deleting",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> deleteTag(@PathVariable Tsid id) {
            deleteTagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class DeleteTagService {

        private TagRepository tagRepository;

        public void deleteTag(Tsid id) {
            var tag = tagRepository.findById(new WhatEatId(id))
                    .orElseThrow(() -> WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0010)
                            .reason("id", "Nhãn món ăn không tồn tại.")
                            .build());
            tagRepository.delete(tag);
        }
    }
}
