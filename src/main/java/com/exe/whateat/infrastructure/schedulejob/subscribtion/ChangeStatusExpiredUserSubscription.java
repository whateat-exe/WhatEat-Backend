package com.exe.whateat.infrastructure.schedulejob.subscribtion;

import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChangeStatusExpiredUserSubscription {

    private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;

    @Scheduled(cron = "0 */5 * * * *") // every minute
    @Transactional(rollbackOn = Exception.class)
    public void changeExpiredRestaurantSubscription() {
        userSubscriptionTrackerRepository.changeAllExpiredPayment();
    }
}
