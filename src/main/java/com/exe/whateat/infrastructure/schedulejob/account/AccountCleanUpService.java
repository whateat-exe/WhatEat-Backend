package com.exe.whateat.infrastructure.schedulejob.account;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import io.github.x4ala1c.tsid.Tsid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AccountCleanUpService {

    private static final long MAX_TIME_DELETE_ACCOUNT = 60L * 60;

    @Value("${whateat.tsid.epoch}")
    private long tsidEpoch;

    @Autowired
    private AccountRepository accountRepository;

    @Scheduled(cron = "0 * * * * *") // every minute
    public void deleteUnsedCode() {
        final long exceedSixtyMinutes = Instant.now().toEpochMilli() - tsidEpoch + MAX_TIME_DELETE_ACCOUNT;// exceed 60 minutes delete
        final WhatEatId maximumIdForDelete = new WhatEatId(Tsid.fromLong(exceedSixtyMinutes << 22));
        var accountsForDelete = accountRepository.findAllByStatusPendingForDelete(maximumIdForDelete);
        for (var account : accountsForDelete) {
            if(account.getAccountVerify().isEmpty())
                accountRepository.delete(account);
        }
    }
}
