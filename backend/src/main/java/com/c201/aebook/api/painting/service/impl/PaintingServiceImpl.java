package com.c201.aebook.api.painting.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c201.aebook.api.painting.persistence.entity.PaintingEntity;
import com.c201.aebook.api.painting.persistence.entity.PaintingType;
import com.c201.aebook.api.painting.persistence.repository.PaintingRepository;
import com.c201.aebook.api.painting.presentation.dto.response.PaintingResponseDTO;
import com.c201.aebook.api.painting.service.PaintingService;
import com.c201.aebook.api.painting.service.PaintingServiceHelper;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.PaintingPatchSO;
import com.c201.aebook.api.vo.PaintingSO;
import com.c201.aebook.converter.PaintingConverter;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaintingServiceImpl implements PaintingService {
	private final PaintingRepository paintingRepository;
	private final UserRepository userRepository;
	private final PaintingConverter paintingConverter;
	private final PaintingServiceHelper paintingServiceHelper;

	@Override
	public PaintingResponseDTO savePainting(PaintingSO paintingSO) {
		UserEntity user = userRepository.findById(paintingSO.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		PaintingEntity paintingEntity = paintingRepository.save(PaintingEntity.builder()
			.title(paintingSO.getTitle())
			.fileUrl(paintingSO.getFileUrl())
			.type(paintingSO.getType())
			.user(user)
			.build());
		PaintingResponseDTO paintingResponseDTO = paintingConverter.toPaintingResponseDTO(paintingEntity);
		return paintingResponseDTO;
	}

	@Override
	public Page<PaintingResponseDTO> getPaintingList(Long userId, PaintingType type, Pageable pageable) {
		UserEntity userEntity = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Page<PaintingEntity> paintingEntities = paintingRepository.findByUserIdAndType(userId, type, pageable);
		Page<PaintingResponseDTO> result = paintingEntities.map(
			painting -> paintingConverter.toPaintingResponseDTO(painting));
		return result;
	}

	@Override
	public void deletePainting(Long paintingId, Long userId) {
		PaintingEntity paintingEntity = paintingServiceHelper.getOwnPainting(userId, paintingId);
		paintingRepository.delete(paintingEntity);
	}

	@Override
	public String getFilePath(Long paintingId, Long userId) {
		PaintingEntity paintingEntity = paintingServiceHelper.getOwnPainting(userId, paintingId);
		return paintingEntity.getFileUrl().substring(53);
	}

	@Override
	public PaintingResponseDTO updatePaintingTitle(PaintingPatchSO paintingPatchSO) {
		PaintingEntity paintingEntity = paintingServiceHelper.getOwnPainting(paintingPatchSO.getUserId(),
			paintingPatchSO.getPaintingId());
		paintingEntity.updatePainting(paintingPatchSO.getTitle());
		paintingRepository.save(paintingEntity);
		PaintingResponseDTO paintingResponseDTO = paintingConverter.toPaintingResponseDTO(paintingEntity);
		return paintingResponseDTO;
	}

	@Override
	public PaintingResponseDTO getPaintingDetails(Long userId, Long paintingId) {
		PaintingEntity paintingEntity = paintingServiceHelper.getOwnPainting(userId, paintingId);
		PaintingResponseDTO paintingResponseDTO = paintingConverter.toPaintingResponseDTO(paintingEntity);
		return paintingResponseDTO;
	}

	@Override
	public List<PaintingResponseDTO> getNewPaintingList() {
		List<PaintingEntity> paintingEntities = paintingRepository.findTop12ByTypeOrderByCreatedAtDesc(
			PaintingType.COLOR);
		List<PaintingResponseDTO> result = paintingEntities.stream().map(
				painting -> paintingConverter.toPaintingResponseDTO(painting))
			.collect(Collectors.toList());
		return result;
	}
}
