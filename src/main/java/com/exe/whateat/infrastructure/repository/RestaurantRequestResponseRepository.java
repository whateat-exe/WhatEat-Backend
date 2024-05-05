package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RestaurantRequestResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRequestResponseRepository extends JpaRepository<RestaurantRequestResponse, WhatEatId> {
}