package com.c201.aebook.converter;

import org.mapstruct.Mapper;

import com.c201.aebook.api.story.persistence.entity.StoryEntity;
import com.c201.aebook.api.story.presentation.dto.request.StoryPatchRequestDTO;
import com.c201.aebook.api.story.presentation.dto.request.StoryRequestDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryDeleteResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryDetailResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryListResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryPatchResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StorySaveResponseDTO;
import com.c201.aebook.api.vo.StoryDeleteSO;
import com.c201.aebook.api.vo.StoryPatchSO;
import com.c201.aebook.api.vo.StorySO;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StoryConverter {
	StorySO toStorySO(Long userId, String voiceUrl, String imgUrl, StoryRequestDTO storyRequestDTO);

	StorySaveResponseDTO toStorySaveResponseDTO(StorySO storySO);

	StoryDeleteSO toStoryDeleteSO(Long userId, Long storyId);

	@Mappings({
			@Mapping(source = "id", target = "storyId"),
			@Mapping(source = "user.nickname", target = "nickname")
	})
	StoryDetailResponseDTO toStoryDetailResponseDTO(StoryEntity storyEntity);

	StoryPatchSO toStoryPatchSO(String userId, Long storyId, StoryPatchRequestDTO storyPatchRequestDTO);

	StoryPatchResponseDTO toStoryPatchResponseDTO(StoryPatchSO storyPatchSO);

	@Mappings({
			@Mapping(source = "id", target = "storyId")
	})
	StoryDeleteResponseDTO toStoryDeleteResponseDTO(StoryEntity storyEntity);

	@Mappings({
			@Mapping(source = "id", target = "storyId"),
			@Mapping(source = "user.nickname", target = "nickname")
	})
	StoryListResponseDTO toStoryListResponseDTO(StoryEntity storyEntity);
}
