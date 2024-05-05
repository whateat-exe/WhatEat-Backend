package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RestaurantRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRequestRepository extends JpaRepository<RestaurantRequest, WhatEatId> {
}