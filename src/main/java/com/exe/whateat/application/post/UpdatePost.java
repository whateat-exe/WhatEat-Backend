package com.exe.whateat.application.post;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.application.post.mapper.PostMapper;
import com.exe.whateat.application.post.response.PostResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.post.PostImage;
import com.exe.whateat.entity.post.QPost;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostImageRepository;
import com.exe.whateat.infrastructure.repository.PostRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdatePost {

    @Getter
    @Setter
    public static class UpdatePostImageRequest {

        private Tsid imageId;
        private String image;
        private String caption;
    }

    @Getter
    @Setter
    public static class UpdatePostRequest {

        private String content;
        private List<UpdatePostImageRequest> images;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post",
            description = "APIs for posts."
    )
    public static final class CreateFoodTagController extends AbstractController {

        private UpdatePostService service;

        @PatchMapping("/posts/{id}")
        @Operation(
                summary = "Create post API. Returns the new information of post",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the post.",
                        content = @Content(schema = @Schema(implementation = UpdatePostRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns new information of the post.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the post.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> updatePost(@RequestBody @Valid UpdatePostRequest updatePostRequest, @PathVariable Tsid id) {
            var response = service.updatePost(updatePostRequest, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class UpdatePostService {

        private final PostRepository postRepository;
        private final PostImageRepository postImageRepository;
        private final FirebaseImageService firebaseImageService;
        private final PostMapper postMapper;
        private final WhatEatSecurityHelper securityHelper;
        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        public PostResponse updatePost(UpdatePostRequest request, Tsid postId) {
            var user = securityHelper.getCurrentLoggedInAccount();
            final QPost qPost = QPost.post;
            BooleanExpression predicates = qPost.isNotNull();
            predicates = predicates.and(qPost.id.eq(WhatEatId.builder().id(postId).build()));
            BlazeJPAQuery<Post> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory);
            final List<Post> posts = query
                    .select(qPost)
                    .from(qPost)
                    .leftJoin(qPost.postImages).fetchJoin()
                    .where(predicates)
                    .fetch();
            var post = posts.get(0);
            if (!(user.get().getId().asTsid().equals(post.getAccount().getId().asTsid())))
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("user", "Không đúng user để có thể sửa bài post")
                        .build();
            //set image
            List<PostImage> postImages = new ArrayList<>();
            if (request.images != null) {
                if (request.images.size() > 3)
                    throw WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("post_image", "Số lượng ảnh không được vượt quá 3")
                            .build();
                for (var postImageBase64 : request.images) {
                    if (postImageBase64 != null && postImageBase64.imageId == null)
                        throw WhatEatException.builder()
                                .code(WhatEatErrorCode.WES_0001)
                                .reason("post_image", "không có image id")
                                .build();
                    if (postImageBase64.image != null) {
                        var postImage = postImageRepository.findById(WhatEatId.builder().id(postImageBase64.imageId).build())
                                .orElseThrow(() -> WhatEatException.builder()
                                        .code(WhatEatErrorCode.WES_0001)
                                        .reason("post image", "post image sai Id")
                                        .build());
                        if (postImageBase64.caption != null) {
                            postImage.setCaption(postImageBase64.caption);
                        }
                        if (postImageBase64.image != null) {
                            FirebaseImageResponse firebaseImageResponse = null;
                            try {
                                firebaseImageResponse = firebaseImageService.uploadBase64Image(postImageBase64.image);
                                postImage.setImage(firebaseImageResponse.url());
                                postImages.add(postImage);
                            } catch (Exception e) {
                                // Image is created. Time to delete!
                                if (firebaseImageResponse != null) {
                                    firebaseImageService.deleteImage(firebaseImageResponse.id(), FirebaseImageService.DeleteType.ID);
                                }
                                if (e instanceof WhatEatException whatEatException) {
                                    throw whatEatException;
                                }
                                throw WhatEatException.builder()
                                        .code(WhatEatErrorCode.WES_0001)
                                        .reason("post", "Lỗi trong việc tạo post image.")
                                        .build();
                            }
                        }
                    }
                }
                postImageRepository.saveAll(postImages);
            }
            // content
            if (request.content != null)
                post.setContent(request.content);
            post.setLastModified(Instant.now());
            return postMapper.convertToDto(postRepository.saveAndFlush(post));
        }
    }
}