package com.exe.whateat.application.tag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.application.tag.response.TagsResponse;
import com.exe.whateat.entity.food.QTag;
import com.exe.whateat.entity.food.Tag;
import com.exe.whateat.infrastructure.repository.TagRepository;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetTags {

    public static final class GetTagsRequest extends PaginationRequest {

    }

    @RestController
    @AllArgsConstructor
    @io.swagger.v3.oas.annotations.tags.Tag(
            name = "tag",
            description = "APIs for get tags"
    )
    public static class GetTagController extends AbstractController {

        private final GetTagService getTagService;

        @GetMapping("tags")
        public ResponseEntity<Object> getTagController(@Valid GetTagsRequest getTagsRequest) {
            var response = getTagService.getTagService(getTagsRequest);
            return ResponseEntity.ok(response);
        }
    }

    @AllArgsConstructor
    @Service
    public static class GetTagService {

        private final TagRepository tagRepository;
        private final TagMapper tagMapper;
        private final EntityManager entityManager;

        public TagsResponse getTagService(GetTagsRequest getTagsRequest) {

            final QTag qTag = QTag.tag;
            final JPAQuery<Tag> tagJPAQuery = new JPAQuery<>(entityManager)
                    .select(qTag)
                    .from(qTag)
                    .limit(getTagsRequest.getLimit())
                    .offset(getTagsRequest.getOffset());
            final List<Tag> tags = tagJPAQuery.fetch();
            final long total = tagRepository.count();
            final TagsResponse response = new TagsResponse(tags.stream().map(tagMapper::convertToDto).toList(), total);
            response.setPage(getTagsRequest.getPage());
            response.setLimit(getTagsRequest.getLimit());
            return response;
        }
    }
}
