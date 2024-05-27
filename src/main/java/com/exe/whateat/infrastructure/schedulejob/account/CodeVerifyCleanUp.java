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
public class CodeVerifyCleanUp {

    @Autowired
    private AccountVerifyRepository accountVerifyRepository;

    @Scheduled(cron = "0 0/15 * * * *") // runs every 15 minutes
    public void deleteUnsedCode() {
        var expireCodes = accountVerifyRepository.findAllByStatus(VerificationStatus.EXPIRED);
        expireCodes.forEach(x -> accountVerifyRepository.delete(x));
    }

}
