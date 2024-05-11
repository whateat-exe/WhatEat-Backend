package com.exe.whateat.application.restaurant;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RequestType;
import com.exe.whateat.entity.request.RestaurantRequest;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.entity.restaurant.RestaurantStatus;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateRestaurant {

    private static final String REQUEST_CONTENT = """
            Restaurant ID: %s
            Name: %s
            Address: %s
            """;

    @Data
    public static final class CreateRestaurantRequest {

        @NotBlank(message = "Email is required.")
        @Email(message = "Email must have valid format.")
        private String email;

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters.")
        private String password;

        @NotBlank(message = "Name is required.")
        private String name;

        @NotBlank(message = "Description is required.")
        private String description;

        @NotBlank(message = "Phone number is required.")
        @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 digits.")
        private String phoneNumber;

        @NotBlank(message = "Address is required.")
        private String address;

        @NotBlank(message = "Image is required.")
        private String image;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant",
            description = "APIs for restaurant accounts."
    )
    public static final class CreateRestaurantController extends AbstractController {

        private final CreateRestaurantService service;

        @PostMapping("/restaurants")
        @Operation(
                summary = "Create restaurant API. Returns the new information of restaurant.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the restaurant.",
                        content = @Content(schema = @Schema(implementation = CreateRestaurantRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns new information of the restaurant.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the restaurant.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createRestaurant(@RequestBody @Valid CreateRestaurantRequest request) {
            final RestaurantResponse response = service.create(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreateRestaurantService {

        private final RestaurantRepository restaurantRepository;
        private final FirebaseImageService firebaseImageService;
        private final RestaurantRequestRepository restaurantRequestRepository;
        private final PasswordEncoder passwordEncoder;
        private final WhatEatMapper<Restaurant, RestaurantResponse> mapper;

        public RestaurantResponse create(CreateRestaurantRequest request) {
            if (restaurantRepository.existsByNameAndAccountEmail(request.getName(), request.getEmail())) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0001)
                        .reason("name_email", "Restaurant's name and email must be unique.")
                        .build();
            }
            final Account account = Account.builder()
                    .id(WhatEatId.generate())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getName())
                    .phoneNumber(request.getPhoneNumber())
                    .role(AccountRole.RESTAURANT)
                    .status(ActiveStatus.ACTIVE)
                    .build();
            Restaurant restaurant = Restaurant.builder()
                    .id(WhatEatId.generate())
                    .account(account)
                    .name(request.getName())
                    .description(request.getDescription())
                    .address(request.getAddress())
                    .status(RestaurantStatus.PENDING)
                    .image(StringUtils.EMPTY)
                    .build();
            FirebaseImageResponse firebaseImageResponse = null;
            try {
                restaurantRepository.save(restaurant);
                firebaseImageResponse = firebaseImageService.uploadBase64Image(request.getImage());
                restaurant.setImage(firebaseImageResponse.url());
                restaurant = restaurantRepository.save(restaurant);
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
                        .reason("restaurant", "Error while creating restaurant. Please contact admin.")
                        .build();
            }
            // Create request to validate the restaurant.
            final RestaurantRequest restaurantRequest = RestaurantRequest.builder()
                    .id(WhatEatId.generate())
                    .restaurant(restaurant)
                    .title("Request for creation of Restaurant \"%s\"")
                    .content(String.format(REQUEST_CONTENT, restaurant.getId().asTsid().toString(),
                            restaurant.getName(), restaurant.getAddress()))
                    .type(RequestType.NEW_RESTAURANT)
                    .createdAt(Instant.now())
                    .build();
            restaurantRequestRepository.save(restaurantRequest);
            return mapper.convertToDto(restaurant);
        }
    }
}
