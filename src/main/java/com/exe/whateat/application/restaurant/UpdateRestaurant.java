package com.exe.whateat.application.restaurant;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateRestaurant {

    @Data
    public static final class UpdateRestaurantRequest {

        @Size(max = 255)
        private String name;

        @Size(max = 5000)
        private String description;

        private String address;

        @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 digits.")
        private String phoneNumber;

        private String image;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant",
            description = "APIs for restaurant accounts."
    )
    public static final class UpdateRestaurantController extends AbstractController {

        private final UpdateRestaurantService service;

        @PatchMapping("/restaurants/{id}")
        @Operation(
                summary = "Update restaurant API. Returns the new information of restaurant.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the restaurant to update.",
                        content = @Content(schema = @Schema(implementation = UpdateRestaurantRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful update. Returns new information of the restaurant.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        )
        @ApiResponse(
                description = "Failed updating of the restaurant.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> updateRestaurant(@PathVariable(name = "id") Tsid id,
                                                       @RequestBody UpdateRestaurantRequest request) {
            final RestaurantResponse response = service.update(id, request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class UpdateRestaurantService {

        private final RestaurantRepository restaurantRepository;
        private final FirebaseImageService firebaseImageService;
        private final WhatEatMapper<Restaurant, RestaurantResponse> mapper;

        @SuppressWarnings("java:S3776")
        public RestaurantResponse update(Tsid id, UpdateRestaurantRequest request) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final Restaurant restaurant = restaurantRepository.findById(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0002)
                            .reason("restaurant_id", "Unknown restaurant ID.")
                            .build());
            if (restaurant.getAccount().getStatus() != ActiveStatus.ACTIVE) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0003)
                        .reason("restaurant_status", "Restaurant must be active to be updated.")
                        .build();
            }
            if (StringUtils.isNotEmpty(request.getName())) {
                if (!Objects.equals(restaurant.getName(), request.getName())
                        && restaurantRepository.existsByNameIgnoreCase(request.getName())) {
                    throw WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0001)
                            .reason("restaurantName", "Tên nhà hàng đã tồn tại.")
                            .build();
                }
                restaurant.setName(request.getName());
            }
            if (StringUtils.isNotEmpty(request.getDescription())) {
                restaurant.setDescription(request.getDescription());
            }
            if (StringUtils.isNotEmpty(request.getAddress())) {
                restaurant.setAddress(request.getAddress());
            }
            if (StringUtils.isNotEmpty(request.getPhoneNumber())) {
                final String phoneNumber = request.getPhoneNumber();
                if (phoneNumber.length() != 11 && phoneNumber.length() != 10) {
                    throw WhatEatException.builder()
                            .code(WhatEatErrorCode.WEV_0007)
                            .reason("phone_number", "Phone number must be between 10 and 11 digits.")
                            .build();
                }
                restaurant.getAccount().setPhoneNumber(phoneNumber);
            }
            FirebaseImageResponse firebaseImageResponse = null;
            try {
                if (StringUtils.isNotEmpty(request.getImage())) {
                    firebaseImageResponse = firebaseImageService.uploadBase64Image(request.getImage());
                    restaurant.setImage(firebaseImageResponse.url());
                }
                final Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
                return mapper.convertToDto(updatedRestaurant);
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
                        .reason("restaurant", "Error while updating restaurant. Please contact admin.")
                        .build();
            }
        }
    }
}
