package com.c201.aebook.api.story.service;

import com.c201.aebook.api.story.presentation.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.c201.aebook.api.vo.StoryDeleteSO;
import com.c201.aebook.api.vo.StoryPatchSO;
import com.c201.aebook.api.vo.StorySO;

public interface StoryService {
	public StorySaveResponseDTO saveStory(StorySO storySO);

	Page<StoryListResponseDTO> getStoryListByUserId(Long userId, Pageable pageable);

	public StoryDeleteResponseDTO deleteStory(StoryDeleteSO storyId);

	Page<StoryListResponseDTO> getStoryList(Pageable pageable);

	StoryDetailResponseDTO getStoryDetail(Long storyId);

	StoryPatchResponseDTO updateStoryTitle(StoryPatchSO storyPatchSO);

}
