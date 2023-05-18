package com.c201.aebook.api.story.persistence.repository;

import com.c201.aebook.api.review.persistence.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.c201.aebook.api.story.persistence.entity.StoryEntity;

@Repository
public interface StoryRepository extends JpaRepository<StoryEntity, Long> {
    @Query(value = "SELECT se FROM StoryEntity se LEFT JOIN FETCH se.user WHERE se.user.id = :userId",
            countQuery = "SELECT COUNT(se) FROM StoryEntity se WHERE se.user.id = :userId")
    Page<StoryEntity> findByUserId(Long userId, Pageable pageable);
}
