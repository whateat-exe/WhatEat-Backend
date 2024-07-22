package com.exe.whateat.infrastructure.schedulejob.subscribtion;

import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChangeStatusExpiredByValidityEnd {

    private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;
    private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional(rollbackOn = Exception.class)
    public void change() {
        userSubscriptionTrackerRepository.changeAllExpiredSubscription();
        restaurantSubscriptionTrackerRepository.changeAllExpiredSubscription();
    }

}
