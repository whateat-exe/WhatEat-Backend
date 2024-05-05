package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.PostVoting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostVotingRepository extends JpaRepository<PostVoting, WhatEatId> {
}