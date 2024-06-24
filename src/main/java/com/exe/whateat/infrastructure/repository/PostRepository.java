package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface PostRepository extends JpaRepository<Post, WhatEatId> {

    @Query(value = """
               SELECT p FROM Post p
               JOIN FETCH p.postImages
               WHERE p.id = ?1
            """)
    Set<Post> getPostById(WhatEatId postId);
}