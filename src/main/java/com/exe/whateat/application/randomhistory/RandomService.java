package com.exe.whateat.application.randomhistory;

import com.exe.whateat.application.randomhistory.response.RandomResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.food.Food;

public interface RandomService {

    void saveRandomHistory(Account account, Food randomizedFood);

    RandomResponse checkIfAllowedToRandomize(Account account);
}
