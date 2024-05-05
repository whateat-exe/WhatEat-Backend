package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.restaurant.RestaurantAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantAddressRepository extends JpaRepository<RestaurantAddress, WhatEatId> {
}