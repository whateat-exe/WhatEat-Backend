package com.exe.whateat.application.subscription;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import com.exe.whateat.infrastructure.payos.request.PayOSPaymentResponse;

public interface SubscriptionService {

    PayOSPaymentResponse subscribeRestaurant(Restaurant restaurant, RestaurantSubscriptionType subscriptionType);

    PayOSPaymentResponse subscribeUser(Account account);
}
