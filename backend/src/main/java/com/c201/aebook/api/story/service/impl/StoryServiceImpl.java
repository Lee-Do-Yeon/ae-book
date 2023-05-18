package com.c201.aebook.api.story.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.c201.aebook.api.story.presentation.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c201.aebook.api.story.persistence.entity.StoryEntity;
import com.c201.aebook.api.story.persistence.repository.StoryRepository;
import com.c201.aebook.api.story.service.StoryService;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.StoryDeleteSO;
import com.c201.aebook.api.vo.StoryPatchSO;
import com.c201.aebook.api.vo.StorySO;
import com.c201.aebook.converter.StoryConverter;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {
	private final StoryRepository storyRepository;
	private final UserRepository userRepository;
	private final StoryConverter storyConverter;

	@Override
	public StorySaveResponseDTO saveStory(StorySO storySO) {
		// 유효한 userId인지 검증
		UserEntity user = userRepository.findById(storySO.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		storyRepository.save(StoryEntity.builder()
			.title(storySO.getTitle())
			.content(storySO.getContent())
			.voiceUrl(storySO.getVoiceUrl())
			.imgUrl(storySO.getImgUrl())
			.user(user)
			.build());

		return storyConverter.toStorySaveResponseDTO(storySO);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<StoryListResponseDTO> getStoryListByUserId(Long userId, Pageable pageable) {
		// 1. User 유효성 검증
		UserEntity userEntity = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Page<StoryEntity> stories = storyRepository.findByUserId(userId, pageable);
		return stories.map(storyEntity -> storyConverter.toStoryListResponseDTO(storyEntity));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<StoryListResponseDTO> getStoryList(Pageable pageable) {
		Page<StoryEntity> stories = storyRepository.findAll(pageable);
		return stories.map(storyEntity -> storyConverter.toStoryListResponseDTO(storyEntity));
	}

	@Override
	@Transactional(readOnly = true)
	public StoryDetailResponseDTO getStoryDetail(Long storyId) {
		// 1. Story 유효성 검증
		StoryEntity storyEntity = storyRepository.findById(storyId)
			.orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

		return storyConverter.toStoryDetailResponseDTO(storyEntity);
	}

	@Override
	public StoryPatchResponseDTO updateStoryTitle(StoryPatchSO storyPatchSO) {
		// 1. Story 유효성 검증
		StoryEntity storyEntity = storyRepository.findById(storyPatchSO.getStoryId())
			.orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

		// 2. User 유효성 검증
		UserEntity userEntity = userRepository.findById(Long.parseLong(storyPatchSO.getUserId()))
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// Entity에 Setter가 없기 때문에 builder 패턴 사용
		storyRepository.save(StoryEntity.builder()
			.id(storyEntity.getId())
			.title(storyPatchSO.getTitle())
			.content(storyEntity.getContent())
			.imgUrl(storyEntity.getImgUrl())
			.voiceUrl(storyEntity.getVoiceUrl())
			.user(storyEntity.getUser())
			.build());

		return storyConverter.toStoryPatchResponseDTO(storyPatchSO);
	}

	@Override
	public StoryDeleteResponseDTO deleteStory(StoryDeleteSO storyDeleteSO) {
		// 1. Story 유효성 검증
		StoryEntity storyEntity = storyRepository.findById(storyDeleteSO.getStoryId())
			.orElseThrow(() -> new CustomException(ErrorCode.STORY_NOT_FOUND));

		// 2. User 유효성 검증
		UserEntity userEntity = userRepository.findById(storyDeleteSO.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		storyRepository.deleteById(storyDeleteSO.getStoryId());

		return storyConverter.toStoryDeleteResponseDTO(storyEntity);
	}
}
