package com.c201.aebook.api.painting.presentation.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c201.aebook.api.common.BaseResponse;
import com.c201.aebook.api.common.constants.ApplicationConstants;
import com.c201.aebook.api.painting.persistence.entity.PaintingType;
import com.c201.aebook.api.painting.presentation.dto.request.PaintingRequestDTO;
import com.c201.aebook.api.painting.presentation.dto.request.PaintingTitleRequestDTO;
import com.c201.aebook.api.painting.presentation.dto.response.PaintingResponseDTO;
import com.c201.aebook.api.painting.presentation.validator.PaintingValidator;
import com.c201.aebook.api.painting.service.impl.PaintingServiceImpl;
import com.c201.aebook.api.vo.PaintingPatchSO;
import com.c201.aebook.api.vo.PaintingSO;
import com.c201.aebook.auth.CustomUserDetails;
import com.c201.aebook.converter.PaintingConverter;
import com.c201.aebook.utils.S3Downloader;
import com.c201.aebook.utils.S3Uploader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "그림 Controller")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/paintings")
public class PaintingController {
	private final PaintingServiceImpl paintingService;
	private final PaintingConverter paintingConverter;
	private final PaintingValidator paintingValidator;
	private final S3Uploader s3Uploader;
	private final S3Downloader s3Downloader;

	@Operation(summary = "그림 저장", description = "그림을 저장합니다.")
	@PostMapping(
		path = "",
		consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
	)
	public BaseResponse<?> savePainting(
		@RequestPart(value = "paintingFile") MultipartFile multipartFile,
		@RequestPart(value = "data") PaintingRequestDTO paintingRequestDTO,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) throws IOException {
		Long userId = Long.parseLong(customUserDetails.getUsername());
		String dirName = String.valueOf(userId) + "/paintings";

		// DTO NOT NULL 검증
		paintingValidator.validatePaintingRequestDTO(paintingRequestDTO);

		// aws s3 file upload
		String uploadImageUrl = s3Uploader.upload(multipartFile, dirName);

		// 그림 DB 저장
		PaintingSO paintingSO = paintingConverter.toPaintingSO(userId, uploadImageUrl, paintingRequestDTO);
		PaintingResponseDTO paintingResponseDTO = paintingService.savePainting(paintingSO);

		return new BaseResponse<>(paintingResponseDTO, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
	}

	@Operation(summary = "그림 다운로드", description = "그림을 다운로드합니다.")
	@GetMapping("/download/{paintingId}")
	public ResponseEntity<?> downloadPainting(
		@PathVariable(name = "paintingId") Long paintingId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) throws IOException {
		Long userId = Long.parseLong(customUserDetails.getUsername());
		String filePath = paintingService.getFilePath(paintingId, userId);
		return s3Downloader.download(filePath);
	}

	@Operation(summary = "그림 삭제", description = "그림을 삭제합니다.")
	@DeleteMapping("/{paintingId}")
	public BaseResponse<?> deletePainting(
		@PathVariable(name = "paintingId") Long paintingId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		Long userId = Long.parseLong(customUserDetails.getUsername());
		String filePath = paintingService.getFilePath(paintingId, userId);
		s3Downloader.delete(filePath);
		paintingService.deletePainting(paintingId, userId);
		return new BaseResponse<>(paintingId, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
	}

	@Operation(summary = "그림 제목 수정", description = "특정 그림의 제목을 수정합니다.")
	@PatchMapping(
		path = "/{paintingId}",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public BaseResponse<?> updatePaintingTitle(
		@PathVariable(name = "paintingId") Long paintingId,
		@RequestBody PaintingTitleRequestDTO paintingTitleRequestDTO,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		Long userId = Long.parseLong(customUserDetails.getUsername());
		// DTO NOT NULL 검증
		paintingValidator.validatePaintingTitleRequestDTO(paintingTitleRequestDTO);
		PaintingPatchSO paintingPatchSO = paintingConverter.toPaintingPatchSO(paintingId, userId,
			paintingTitleRequestDTO);
		// 그림 제목 수정
		PaintingResponseDTO paintingResponseDTO = paintingService.updatePaintingTitle(paintingPatchSO);

		return new BaseResponse<>(paintingResponseDTO, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
	}

	@Operation(summary = "특정 그림 조회", description = "특정 그림 상세 정보를 반환합니다.")
	@GetMapping("/{paintingId}")
	public BaseResponse<?> getPaintingDetails(
		@PathVariable(name = "paintingId") Long paintingId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		Long userId = Long.parseLong(customUserDetails.getUsername());
		PaintingResponseDTO painting = paintingService.getPaintingDetails(userId, paintingId);

		return new BaseResponse<>(painting, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
	}

	@Operation(summary = "로그인한 유저의 그림 리스트", description = "로그인한 유저의 그림 리스트를 반환합니다.")
	@GetMapping()
	public BaseResponse<?> getPaintingList(
		@RequestParam(name = "type") PaintingType type,
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Long userId = Long.parseLong(customUserDetails.getUsername());
		Page<PaintingResponseDTO> paintingList = paintingService.getPaintingList(userId, type, pageable);

		return new BaseResponse<>(paintingList, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
	}

	@Operation(summary = "메인화면에서 보이는 작품 리스트", description = "메인화면에서 그림 리스트를 반환합니다.")
	@GetMapping("/new")
	public BaseResponse<?> getNewPaintingList() {
		List<PaintingResponseDTO> paintingList = paintingService.getNewPaintingList();
		return new BaseResponse<>(paintingList, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
	}

}
