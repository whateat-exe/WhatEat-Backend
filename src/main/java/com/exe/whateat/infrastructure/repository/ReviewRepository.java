package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.random.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Rating, WhatEatId> {

    boolean existsByDishIdAndAccountId(WhatEatId id, WhatEatId id1);

}
