package com.exe.whateat.application.personalprofile;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.personalprofile.response.PersonalProfileResponse;
import com.exe.whateat.application.personalprofile.response.PersonalProfilesResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.profile.PersonalProfile;
import com.exe.whateat.entity.profile.ProfileType;
import com.exe.whateat.entity.profile.QPersonalProfile;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PersonalProfileRepository;
import com.exe.whateat.infrastructure.repository.TagRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddPersonalProfile {

    @Data
    public static final class AddPersonalProfileRequest {

        private Set<Tsid> like;
        private Set<Tsid> dislike;
        private Set<Tsid> allergy;
    }

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "profile",
            description = "APIs for profile filters."
    )
    public static final class AddPersonalProfileController extends AbstractController {

        private final AddPersonalProfileService service;

        @PostMapping("/personal-profiles")
        @Operation(
                summary = "Add profile filters API. Returns the new information of profile filters. USER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food.",
                        content = @Content(schema = @Schema(implementation = AddPersonalProfileRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful addition. Returns new information of the profile filters.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PersonalProfilesResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> addProfileFilter(@RequestBody AddPersonalProfileRequest request) {
            final PersonalProfilesResponse response = service.add(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    @RequiredArgsConstructor
    @SuppressWarnings("Duplicates")
    public static class AddPersonalProfileService {

        private static final String PERSONAL_PROFILE = "personalProfile";

        private final PersonalProfileRepository personalProfileRepository;
        private final TagRepository tagRepository;
        private final WhatEatSecurityHelper securityHelper;
        private final WhatEatMapper<PersonalProfile, PersonalProfileResponse> mapper;
        private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;

        @PersistenceContext
        private EntityManager entityManager;

        private final CriteriaBuilderFactory criteriaBuilderFactory;

        @Value("${whateat.profiles.maxcount}")
        private int profilesMaxCount;

        public PersonalProfilesResponse add(AddPersonalProfileRequest request) {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            if (!userSubscriptionTrackerRepository.userIsUnderActiveSubscription(account.getId())) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0021)
                        .reason("subscription", "Bạn cần nạp VIP để xài nha.")
                        .build();
            }
            final Set<Tsid> like = Objects.requireNonNullElse(request.getLike(), Set.of());
            final Set<Tsid> dislike = Objects.requireNonNullElse(request.getDislike(), Set.of());
            final Set<Tsid> allergy = Objects.requireNonNullElse(request.getAllergy(), Set.of());
            final int count = like.size() + dislike.size() + allergy.size();
            final QPersonalProfile qPersonalProfile = QPersonalProfile.personalProfile;
            final long currentTotalCount = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                    .select(qPersonalProfile)
                    .from(qPersonalProfile)
                    .where(qPersonalProfile.account.eq(account))
                    .fetchCount();
            if ((count + currentTotalCount) > profilesMaxCount) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0018)
                        .reason(PERSONAL_PROFILE, "Số lượng bộ lọc vượt quá cho phép.")
                        .build();
            }
            if (count == 0) {
                return PersonalProfilesResponse.builder()
                        .allergy(Collections.emptyList())
                        .dislike(Collections.emptyList())
                        .like(Collections.emptyList())
                        .build();
            }
            if (someProfilesAreDuplicated(like, dislike, allergy)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0019)
                        .reason(PERSONAL_PROFILE, "Một vài tag bị trùng.")
                        .build();
            }
            final List<PersonalProfile> likePersonalProfiles = new LinkedList<>();
            for (Tsid id : like) {
                likePersonalProfiles.add(PersonalProfile.builder()
                        .id(WhatEatId.generate())
                        .type(ProfileType.LIKE)
                        .tag(tagRepository.getReferenceById(new WhatEatId(id)))
                        .account(account)
                        .build());
            }
            for (Tsid id : dislike) {
                likePersonalProfiles.add(PersonalProfile.builder()
                        .id(WhatEatId.generate())
                        .type(ProfileType.DISLIKE)
                        .tag(tagRepository.getReferenceById(new WhatEatId(id)))
                        .account(account)
                        .build());
            }
            for (Tsid id : allergy) {
                likePersonalProfiles.add(PersonalProfile.builder()
                        .id(WhatEatId.generate())
                        .type(ProfileType.ALLERGY)
                        .tag(tagRepository.getReferenceById(new WhatEatId(id)))
                        .account(account)
                        .build());
            }
            try {
                personalProfileRepository.saveAllAndFlush(likePersonalProfiles);
            } catch (ConstraintViolationException | DataIntegrityViolationException e) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0019)
                        .reason(PERSONAL_PROFILE, "Bộ lọc đã bị trùng hoặc là ID của tag không hợp lệ.")
                        .build();
            }
            final List<PersonalProfile> newPersonalProfiles = personalProfileRepository.findByAccountId(account.getId());
            return PersonalProfilesResponse.builder()
                    .allergy(newPersonalProfiles.stream()
                            .filter(e -> e.getType() == ProfileType.ALLERGY)
                            .map(mapper::convertToDto)
                            .toList())
                    .like(newPersonalProfiles.stream()
                            .filter(e -> e.getType() == ProfileType.LIKE)
                            .map(mapper::convertToDto)
                            .toList())
                    .dislike(newPersonalProfiles.stream()
                            .filter(e -> e.getType() == ProfileType.DISLIKE)
                            .map(mapper::convertToDto)
                            .toList())
                    .build();
        }

        private boolean someProfilesAreDuplicated(Set<Tsid> like, Set<Tsid> dislike, Set<Tsid> allergy) {
            final Set<Tsid> likeAfterIntersectionWithDislike = new HashSet<>(like);
            if (likeAfterIntersectionWithDislike.removeAll(dislike)) {
                return true;
            }
            final Set<Tsid> dislikeAfterIntersectionWithAllergy = new HashSet<>(dislike);
            if (dislikeAfterIntersectionWithAllergy.removeAll(allergy)) {
                return true;
            }
            final Set<Tsid> allergyAfterIntersectionWithLike = new HashSet<>(allergy);
            return allergyAfterIntersectionWithLike.removeAll(like);
        }
    }
}
