package com.hexagon.presentations.framework.repositories;

import com.hexagon.presentations.framework.entities.Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    @Modifying
    @Query(value = "update Post p set p.score = 0 where p.id = :postId")
    void resetScore(@Param("postId") Long postId);

    @Modifying
    @Query(value = "update Post p set p.score = p.score + 1 where p.id = :postId")
    void upvote(@Param("postId") Long postId);

    @Query(value = "select p.score from Post p where p.id = :postId")
    Integer getScore(@Param("postId") Long postId);
}
