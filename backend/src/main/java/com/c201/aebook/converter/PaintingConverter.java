package com.c201.aebook.converter;

import org.mapstruct.Mapper;

import com.c201.aebook.api.painting.persistence.entity.PaintingEntity;
import com.c201.aebook.api.painting.presentation.dto.request.PaintingRequestDTO;
import com.c201.aebook.api.painting.presentation.dto.request.PaintingTitleRequestDTO;
import com.c201.aebook.api.painting.presentation.dto.response.PaintingResponseDTO;
import com.c201.aebook.api.vo.PaintingPatchSO;
import com.c201.aebook.api.vo.PaintingSO;

@Mapper(componentModel = "spring")
public interface PaintingConverter {
	PaintingSO toPaintingSO(Long userId, String fileUrl, PaintingRequestDTO paintingRequestDTO);

	PaintingResponseDTO toPaintingResponseDTO(PaintingEntity paintingEntity);

	PaintingPatchSO toPaintingPatchSO(Long paintingId, Long userId, PaintingTitleRequestDTO paintingTitleRequestDTO);
}
