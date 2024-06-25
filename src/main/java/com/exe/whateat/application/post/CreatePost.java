package com.exe.whateat.application.post;

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
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostImageRepository;
import com.exe.whateat.infrastructure.repository.PostRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePost {

    @Getter
    @Setter
    public static class CreatePostImageRequest {

        @NotNull(message = "Image is required ")
        private String image;

        @NotNull(message = "Caption is required ")
        private String caption;
    }

    @Getter
    @Setter
    public static class CreatePostRequest {

        @NotNull(message = "The content must not be null")
        private String content;

        @NotNull(message = "Image is required ")
        @Size(min = 1, max = 3, message = "The number of image is between 1 and 3")
        private List<CreatePostImageRequest> images;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post",
            description = "APIs for posts."
    )
    public static final class CreateFoodTagController extends AbstractController {

        private CreatePostService service;

        @PostMapping("/posts")
        @Operation(
                summary = "Create post API. Returns the new information of post",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the post.",
                        content = @Content(schema = @Schema(implementation = CreatePostRequest.class))
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
        public ResponseEntity<Object> createPost(@RequestBody @Valid CreatePostRequest createFoodTagRequest) {
            var response = service.createPost(createFoodTagRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class CreatePostService {

        private final PostRepository postRepository;
        private final PostImageRepository postImageRepository;
        private final FirebaseImageService firebaseImageService;
        private final PostMapper postMapper;
        private WhatEatSecurityHelper securityHelper;

        public PostResponse createPost(CreatePostRequest request) {
            var user = securityHelper.getCurrentLoggedInAccount();
            Post post = Post
                    .builder()
                    .id(WhatEatId.generate())
                    .content(request.content)
                    .account(user.get())
                    .createdAt(Instant.now())
                    .lastModified(Instant.now())
                    .build();
            var postCreated = postRepository.save(post);
            List<PostImage> postImages = new ArrayList<>();
            for (var postImageBase64 : request.images) {
                PostImage postImage = PostImage
                        .builder()
                        .id(WhatEatId.generate())
                        .caption(postImageBase64.caption)
                        .post(postCreated)
                        .build();
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
            var postImageCreated = postImageRepository.saveAll(postImages);
            postCreated.setPostImages(postImageCreated);
            var response = postMapper.convertToDto(postRepository.save(postCreated));
            return response;
        }
    }
}
