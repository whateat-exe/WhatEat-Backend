package com.exe.whateat.infrastructure.schedulejob.dish;

import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.repository.DishRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChangeDishStatusExpired {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional(rollbackOn = Exception.class)
    public void change() {
        dishRepository.changeAllExpiredDish();
    }

}
