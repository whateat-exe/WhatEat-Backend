package com.exe.whateat.infrastructure.schedulejob.account;

import com.exe.whateat.infrastructure.repository.AccountVerifyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class CodeVerifyCleanUp {

    @Autowired
    private AccountVerifyRepository accountVerifyRepository;

    @Scheduled(cron = "0 0/30 * * * *") // runs every 30 minutes
    @Transactional(rollbackOn = Exception.class)
    public void deleteUnsedCode() {
        accountVerifyRepository.deleteAllCodeExpired();
    }

}
