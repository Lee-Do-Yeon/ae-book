package com.c201.aebook.api.story.presentation.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c201.aebook.api.common.BaseResponse;
import com.c201.aebook.api.story.presentation.dto.request.StoryPatchRequestDTO;
import com.c201.aebook.api.story.presentation.dto.request.StoryRequestDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryDeleteResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryDetailResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryListResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StoryPatchResponseDTO;
import com.c201.aebook.api.story.presentation.dto.response.StorySaveResponseDTO;
import com.c201.aebook.api.story.presentation.validator.StoryValidator;
import com.c201.aebook.api.story.service.StoryService;
import com.c201.aebook.api.vo.StoryDeleteSO;
import com.c201.aebook.api.vo.StoryPatchSO;
import com.c201.aebook.api.vo.StorySO;
import com.c201.aebook.auth.CustomUserDetails;
import com.c201.aebook.converter.StoryConverter;
import com.c201.aebook.utils.S3Uploader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "동화 Controller")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/stories")
public class StoryController {

	private final StoryValidator storyValidator;
	private final S3Uploader s3Uploader;
	private final StoryConverter storyConverter;
	private final StoryService storyService;

	@Operation(summary = "동화 등록", description = "새 동화를 등록합니다.")
	@PostMapping(
		path = "",
		consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
	)
	public BaseResponse<?> saveStory(
		@RequestPart(value = "voiceFile") MultipartFile voiceFile,
		@RequestPart(value = "imageFile") MultipartFile imageFile,
		@RequestPart(value = "data") StoryRequestDTO storyRequestDTO,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) throws IOException {
		Long userId = Long.parseLong(customUserDetails.getUsername());
		String dirName = customUserDetails.getUsername() + "/stories";

		// DTO NOT NULL 검증
		storyValidator.validateStoryRequestDTO(storyRequestDTO);

		// aws s3 file upload
		String uploadImageUrl = s3Uploader.upload(imageFile, dirName);
		String uploadVoiceUrl = s3Uploader.upload(voiceFile, dirName);

		// 동화 내용 DB 저장
		StorySO storySO = storyConverter.toStorySO(userId, uploadVoiceUrl, uploadImageUrl, storyRequestDTO);


		StorySaveResponseDTO storySaveResponseDTO = storyService.saveStory(storySO);

		return new BaseResponse<>(storySaveResponseDTO, HttpStatus.OK.value(), "동화 작성 완료");
	}

	@Operation(summary = "로그인 유저의 동화 리스트", description = "마이페이지에서 보여줄 나의 동화 리스트")
	@GetMapping(
		path = "/my"
	)
	public BaseResponse<?> getStoryListByUserId(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		// 로그인 한 유저의 Id를 추출
		Long userId = Long.parseLong(customUserDetails.getUsername());

		// 해당 유저의 동화 리스트 추출
		Page<StoryListResponseDTO> stories = storyService.getStoryListByUserId(userId, pageable);

		return new BaseResponse<>(stories, HttpStatus.OK.value(), "나의 동화책 리스트가 정상적으로 도착했습니다.");
	}

	@Operation(summary = "전체 유저의 동화 리스트", description = "동화 목록에서 보여줄 전체 동화 리스트")
	@GetMapping(
		path = ""
	)
	public BaseResponse<?> getStoryList(
		@PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		// 해당 유저의 동화 리스트 추출
		Page<StoryListResponseDTO> stories = storyService.getStoryList(pageable);

		return new BaseResponse<>(stories, HttpStatus.OK.value(), "전체 리스트가 정상적으로 도착했습니다.");
	}

	@Operation(summary = "특정 동화의 상세 정보 조회", description = "특정 동화의 상세 정보 조회")
	@GetMapping(
		path = "/{storyId}"
	)
	public BaseResponse<?> getStoryDetail(
		@PathVariable(name = "storyId") Long storyId
	) {
		StoryDetailResponseDTO storyDetailResponseDTO = storyService.getStoryDetail(storyId);

		return new BaseResponse<>(storyDetailResponseDTO, HttpStatus.OK.value(), "특정 동화의 정보가 정상적으로 도착했습니다");
	}

	@Operation(summary = "특정 동화의 제목 변경", description = "특정 동화의 제목 변경")
	@PatchMapping(
		path = "/{storyId}",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public BaseResponse<?> updateStoryTitle(
		@PathVariable(name = "storyId") Long storyId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody StoryPatchRequestDTO storyPatchRequestDTO
	) {

		StoryPatchSO storyPatchSO = storyConverter.toStoryPatchSO(customUserDetails.getUsername(), storyId,
			storyPatchRequestDTO);
		storyService.updateStoryTitle(storyPatchSO);

		StoryPatchResponseDTO storyPatchResponseDTO = storyService.updateStoryTitle(storyPatchSO);

		return new BaseResponse<>(storyPatchResponseDTO, HttpStatus.OK.value(), "특정 동화의 제목이 정상적으로 변경되었습니다.");
	}

	@Operation(summary = "특정 동화를 삭제", description = "특정 동화를 삭제")
	@DeleteMapping(
		path = "/{storyId}"
	)
	public BaseResponse<?> deleteStory(
		@PathVariable(name = "storyId") Long storyId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		Long userId = Long.parseLong(customUserDetails.getUsername());

		StoryDeleteSO storyDeleteSO = storyConverter.toStoryDeleteSO(userId, storyId);

		StoryDeleteResponseDTO storyDeleteResponseDTO = storyService.deleteStory(storyDeleteSO);

		return new BaseResponse<>(storyDeleteResponseDTO, HttpStatus.OK.value(), "특정 동화의 삭제를 완료하였습니다.");
	}

}
