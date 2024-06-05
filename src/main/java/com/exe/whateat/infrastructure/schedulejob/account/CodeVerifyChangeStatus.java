package com.exe.whateat.infrastructure.schedulejob.account;

import com.exe.whateat.infrastructure.repository.AccountVerifyRepository;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.BatchSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CodeVerifyChangeStatus {

    private static final Long MAX_TIME_EXPIRED = 15L * 60 * 1000;

    @Value("${whateat.tsid.epoch}")
    private long tsidEpoch;

    @Autowired
    private AccountVerifyRepository accountVerifyRepository;

    @Scheduled(cron = "0 0/15 * * * *") // every 15 minutes
    @Transactional(rollbackOn = Exception.class)
    @BatchSize(size = 10)
    public void changeUnusedCode() {
        Long present = Instant.now().toEpochMilli() - tsidEpoch;
        accountVerifyRepository.updateTheCodeToExpired(present, MAX_TIME_EXPIRED);
    }
}
