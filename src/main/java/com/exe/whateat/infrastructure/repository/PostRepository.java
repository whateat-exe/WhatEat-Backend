package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, WhatEatId> {

    @Query(value = """
                       SELECT p FROM Post p
                       JOIN FETCH p.postImages
                       JOIN FETCH p.postVoting 
                       WHERE p.id = ?1
                    """)
    Optional<Post> getPostById(WhatEatId postId);
}