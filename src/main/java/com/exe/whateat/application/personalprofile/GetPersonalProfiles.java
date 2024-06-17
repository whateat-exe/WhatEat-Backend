package com.exe.whateat.application.personalprofile;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.personalprofile.response.PersonalProfileResponse;
import com.exe.whateat.application.personalprofile.response.PersonalProfilesResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.profile.PersonalProfile;
import com.exe.whateat.entity.profile.ProfileType;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PersonalProfileRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetPersonalProfiles {

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "profile",
            description = "APIs for profile filters."
    )
    public static final class GetPersonalProfilesController extends AbstractController {

        private final GetPersonalProfilesService service;

        @GetMapping("/personal-profiles")
        @Operation(
                summary = "Get profile filters API. Returns the information of profile filters. USER only."
        )
        @ApiResponse(
                description = "Successful retrieval. Returns information of the profile filters.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PersonalProfilesResponse.class))
        )
        @ApiResponse(
                description = "Failed.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getProfiles() {
            final PersonalProfilesResponse response = service.get();
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional
    @RequiredArgsConstructor
    @SuppressWarnings("Duplicates")
    public static class GetPersonalProfilesService {

        private final PersonalProfileRepository personalProfileRepository;
        private final WhatEatSecurityHelper securityHelper;
        private final WhatEatMapper<PersonalProfile, PersonalProfileResponse> mapper;

        public PersonalProfilesResponse get() {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            final List<PersonalProfile> personalProfiles = personalProfileRepository.findByAccountId(account.getId());
            return PersonalProfilesResponse.builder()
                    .allergy(personalProfiles.stream()
                            .filter(e -> e.getType() == ProfileType.ALLERGY)
                            .map(mapper::convertToDto)
                            .toList())
                    .like(personalProfiles.stream()
                            .filter(e -> e.getType() == ProfileType.LIKE)
                            .map(mapper::convertToDto)
                            .toList())
                    .dislike(personalProfiles.stream()
                            .filter(e -> e.getType() == ProfileType.DISLIKE)
                            .map(mapper::convertToDto)
                            .toList())
                    .build();
        }
    }
}
