package com.exe.whateat.application.randomhistory;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.randomhistory.response.RandomResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.random.QRandomCooldown;
import com.exe.whateat.entity.random.QRandomHistory;
import com.exe.whateat.entity.random.RandomCooldown;
import com.exe.whateat.entity.random.RandomHistory;
import com.exe.whateat.infrastructure.repository.RandomCooldownRepository;
import com.exe.whateat.infrastructure.repository.RandomHistoryRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.github.x4ala1c.tsid.TsidGenerator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RandomServiceImpl implements RandomService {

    @Value("${whateat.random.maxcount}")
    private int maxCount;

    @Value("${whateat.random.maxtime}")
    private long maxTime;

    @Value("${whateat.tsid.epoch}")
    private long epoch;

    private final RandomHistoryRepository randomHistoryRepository;
    private final RandomCooldownRepository randomCooldownRepository;
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    @Autowired
    public RandomServiceImpl(RandomHistoryRepository randomHistoryRepository, RandomCooldownRepository randomCooldownRepository,
                             EntityManager entityManager, CriteriaBuilderFactory criteriaBuilderFactory) {
        this.randomHistoryRepository = randomHistoryRepository;
        this.randomCooldownRepository = randomCooldownRepository;
        this.entityManager = entityManager;
        this.criteriaBuilderFactory = criteriaBuilderFactory;
    }

    @Override
    public void saveRandomHistory(Account account, Food randomizedFood, boolean shouldBeReset) {
        checkAccount(account);
        if (randomizedFood == null) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("food", "Món ăn không hợp lệ để lưu lịch sử random món ăn.")
                    .build();
        }
        if (shouldBeReset) {
            final RandomCooldown randomCooldown = RandomCooldown.builder()
                    .id(WhatEatId.generate())
                    .account(account)
                    .build();
            randomCooldownRepository.save(randomCooldown);
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
        final Tsid currentTimestamp = TsidGenerator.globalGenerate();
        long timestampInMillisMaxTimeAgo = currentTimestamp.asLong() >>> 22;
        timestampInMillisMaxTimeAgo -= maxTime * 1000;
        timestampInMillisMaxTimeAgo <<= 22;
        final Tsid currentTimestampMaxTimeAgo = Tsid.fromLong(timestampInMillisMaxTimeAgo);
        final QRandomCooldown qRandomCooldown = QRandomCooldown.randomCooldown;
        final RandomCooldown recentStartOfCooldown = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(qRandomCooldown)
                .from(qRandomCooldown)
                .where(qRandomCooldown.account.eq(account).and(qRandomCooldown.id.id.gt(currentTimestampMaxTimeAgo)))
                .orderBy(qRandomCooldown.id.id.desc())
                .fetchFirst();
        if (recentStartOfCooldown == null) {
            return RandomResponse.builder()
                    .timeLeft(0)
                    .maxCount(maxCount)
                    .countLeft(maxCount)
                    .reset(true)
                    .build();
        }
        final QRandomHistory qRandomHistory = QRandomHistory.randomHistory;
        final long recentRandomHistoryCount = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(qRandomHistory)
                .from(qRandomHistory)
                .where(qRandomHistory.account.eq(account).and(qRandomHistory.id.id.gt(recentStartOfCooldown.getId().asTsid())))
                .orderBy(qRandomHistory.id.id.desc())
                .limit(maxCount)
                .fetchCount();
        final int count = (int) (maxCount - recentRandomHistoryCount);
        final long timeLeft = count > 0
                ? 0
                : calculateTimeLeft(currentTimestamp, recentStartOfCooldown.getId().asTsid());
        return RandomResponse.builder()
                .timeLeft(timeLeft)
                .maxCount(maxCount)
                .countLeft(count)
                .reset(false)
                .build();
    }

    private long calculateTimeLeft(Tsid currentTimestamp, Tsid randomTime) {
        final long currentTimestampAsMillis = (currentTimestamp.asLong() >>> 22) + epoch;
        final long lastRandomTimeAsMillis = (randomTime.asLong() >>> 22) + epoch;
        return (maxTime - ((currentTimestampAsMillis - lastRandomTimeAsMillis) / 1000L));
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
