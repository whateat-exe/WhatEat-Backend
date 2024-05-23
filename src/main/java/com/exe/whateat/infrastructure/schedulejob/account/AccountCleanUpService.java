//package com.exe.whateat.infrastructure.schedulejob.account;
//
//import com.exe.whateat.infrastructure.repository.AccountRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//
//@Service
//public class AccountCleanUpService {
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Scheduled(cron = "0 0 1 * * *") // 1h AM moi sang
//    public void deleteUnsedCode() {
//        Instant oneDay = Instant.now().minusSeconds(60 * 60 * 24); // dang de 1 ngay
//        var codeVerifyings = accountRepository.findAllByCreatedAtBefore(oneDay);
//        codeVerifyings.forEach(x -> accountVerifyRepository.delete(x));
//    }
//}
