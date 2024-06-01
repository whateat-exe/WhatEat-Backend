package com.exe.whateat.application.randomhistory;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.randomhistory.response.RandomResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.random.QRandomHistory;
import com.exe.whateat.entity.random.RandomHistory;
import com.exe.whateat.infrastructure.repository.RandomHistoryRepository;
import io.github.x4ala1c.tsid.Tsid;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class RandomServiceImpl implements RandomService {

    @Value("${whateat.random.maxcount}")
    private int maxCount;

    @Value("${whateat.random.maxtime}")
    private long maxTime;

    @Value("${whateat.tsid.epoch}")
    private long epoch;

    private final RandomHistoryRepository randomHistoryRepository;
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Autowired
    public RandomServiceImpl(RandomHistoryRepository randomHistoryRepository, EntityManager entityManager,
                             CriteriaBuilderFactory criteriaBuilderFactory) {
        this.randomHistoryRepository = randomHistoryRepository;
        this.entityManager = entityManager;
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    @Override
    @Transactional
    public void saveRandomHistory(Account account, Food randomizedFood) {
        checkAccount(account);
        if (randomizedFood == null) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("food", "Món ăn không hợp lệ để lưu lịch sử random món ăn.")
                    .build();
        }
        randomHistoryRepository.save(RandomHistory.builder()
                .id(WhatEatId.generate())
                .account(account)
                .food(randomizedFood)
                .build());
    }

    @Override
    public RandomResponse checkIfAllowedToRandomize(Account account) {
        checkAccount(account);
        final QRandomHistory qRandomHistory = QRandomHistory.randomHistory;
        final List<RandomHistory> recentRandomHistory = new BlazeJPAQuery<RandomHistory>(entityManager, criteriaBuilderFactory)
                .select(qRandomHistory)
                .from(qRandomHistory)
                .orderBy(qRandomHistory.id.id.desc())
                .limit(maxCount)
                .fetch();
        if (recentRandomHistory.isEmpty()) {
            return RandomResponse.builder()
                    .countLeft(maxCount)
                    .maxCount(maxCount)
                    .timeLeft(0)
                    .build();
        }
        final long currentTimestamp = Instant.now().toEpochMilli() - epoch;
        final List<RandomHistory> recentBelowMaxTimeHistory = recentRandomHistory.stream()
                .filter(rh -> {
                    final long result = extractTimestampDifference(rh.getId().asTsid(), currentTimestamp) / 1000;
                    return result < maxTime;
                })
                .toList();
        if (recentBelowMaxTimeHistory.isEmpty()) {
            return RandomResponse.builder()
                    .countLeft(maxCount)
                    .maxCount(maxCount)
                    .timeLeft(0)
                    .build();
        }
        final RandomHistory minRandomHistoryRecently = recentBelowMaxTimeHistory.stream()
                .min(Comparator.comparing(AbstractEntity::getId))
                .orElseThrow(() -> WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("randomStatus", "Không truy xuất được thời gian còn lại cho lần random tiếp theo.")
                        .build());
        final long timeLeft = recentBelowMaxTimeHistory.size() < maxCount
                ? 0
                : (maxTime - (extractTimestampDifference(minRandomHistoryRecently.getId().asTsid(), currentTimestamp) / 1000));
        return RandomResponse.builder()
                .countLeft(maxCount - recentBelowMaxTimeHistory.size())
                .maxCount(maxCount)
                .timeLeft(timeLeft)
                .build();
    }

    private static long extractTimestampDifference(Tsid id, long currentTimestamp) {
        return currentTimestamp - (id.asLong() >>> 22);
    }

    private static void checkAccount(Account account) {
        if (account == null || account.getStatus() != ActiveStatus.ACTIVE || account.getRole() != AccountRole.USER) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("account", "Tài khoản không hợp lệ để lưu lịch sử random món ăn.")
                    .build();
        }
    }
}
