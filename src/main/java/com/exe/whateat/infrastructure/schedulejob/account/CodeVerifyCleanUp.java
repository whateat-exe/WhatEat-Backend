package com.exe.whateat.infrastructure.schedulejob.account;

import com.exe.whateat.infrastructure.repository.AccountVerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Instant;

@Service
public class CodeVerifyCleanUp {

    @Autowired
    private AccountVerifyRepository accountVerifyRepository;

    @Scheduled(cron = "0 0 0 * * *") // 12h moi dem
    public void deleteUnsedCode() {
        Instant sixtyMinutes = Instant.now().minusSeconds(60 * 60); // dang de 60 phut
        var codeVerifyings = accountVerifyRepository.findAllByCreatedAtBefore(sixtyMinutes);
        codeVerifyings.forEach(x -> accountVerifyRepository.delete(x));
    }

}
