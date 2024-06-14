package com.exe.whateat.infrastructure.schedulejob.account;

import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.repository.AccountVerifyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountCleanUpService {

    private static final long MAX_TIME_DELETE_ACCOUNT = 60L * 60 * 1000;

    @Value("${whateat.tsid.epoch}")
    private long tsidEpoch;

    private final AccountRepository accountRepository;
    private final AccountVerifyRepository accountVerifyRepository;

    @Autowired
    public AccountCleanUpService(AccountRepository accountRepository, AccountVerifyRepository accountVerifyRepository) {
        this.accountRepository = accountRepository;
        this.accountVerifyRepository = accountVerifyRepository;
    }

    @Scheduled(cron = "0 0 0 * * *") // every hour
    @Transactional(rollbackOn = Exception.class)
    public void deleteUnsedCode() {
        List<Long> accountIds = new ArrayList<>();
        Long present = Instant.now().toEpochMilli() - tsidEpoch;
        var accounts = accountRepository.getAllAccountPendingExpired(present, MAX_TIME_DELETE_ACCOUNT);
        for (var account : accounts) {
            accountIds.add(account.getId().asTsid().asLong());
            List<Long> idCodeList = new ArrayList<>();
            if (!account.getAccountVerify().isEmpty()) {
                var codeList = account.getAccountVerify();
                codeList.forEach(x -> idCodeList.add(x.getId().asTsid().asLong()));
                accountVerifyRepository.deleteAllCodePendingUnused(idCodeList);
            }
        }
        accountRepository.deleteUnusedAccount(accountIds);
    }
}
