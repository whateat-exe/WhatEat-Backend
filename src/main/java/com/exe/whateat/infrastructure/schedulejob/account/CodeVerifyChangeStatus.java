package com.exe.whateat.infrastructure.schedulejob.account;

import com.exe.whateat.entity.account.VerificationStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.repository.AccountVerifyRepository;
import io.github.x4ala1c.tsid.Tsid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CodeVerifyChangeStatus {

    private static final long MAX_TIME_EXPIRED = 15L * 60;

    @Value("${whateat.tsid.epoch}")
    private long tsidEpoch;

    @Autowired
    private AccountVerifyRepository accountVerifyRepository;

    @Scheduled(cron = "0 * * * * *") // every minute
    public void changeUnusedCode() {
        final long exceedSixtyMinutes = Instant.now().toEpochMilli() - tsidEpoch + MAX_TIME_EXPIRED;// exceed 15 minutes change to expire
        final WhatEatId maximumIdExpired = new WhatEatId(Tsid.fromLong(exceedSixtyMinutes << 22));
        accountVerifyRepository.updateTheCodeToExpired(maximumIdExpired);
    }
}
