package com.c201.aebook.api.story.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.c201.aebook.api.story.persistence.entity.StoryEntity;

@Repository
public interface StoryRepository extends JpaRepository<StoryEntity, Long> {

	// @Query(value = "select s from StoryEntity s left join fetch s.user su")
	Page<StoryEntity> findAllByUserId(Long userId, Pageable pageable);
}
