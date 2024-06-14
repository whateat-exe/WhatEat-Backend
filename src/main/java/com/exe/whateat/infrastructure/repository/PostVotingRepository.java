package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.PostVoting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostVotingRepository extends JpaRepository<PostVoting, WhatEatId> {

    @Query(
            value = """
                        SELECT pv FROM PostVoting pv
                        WHERE pv.account.id = ?1 AND pv.post.id = ?2
                    """
    )
    PostVoting postVotingAlreadyExists(WhatEatId accountId, WhatEatId postId);
}