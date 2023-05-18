package com.c201.aebook.api.painting.presentation.dto.response;

import java.time.LocalDateTime;

import com.c201.aebook.api.painting.persistence.entity.PaintingType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaintingResponseDTO {
	@Schema(description = "그림 ID", defaultValue = "1")
	private Long id;
	@Schema(description = "그림 제목", defaultValue = "내가 그린 공룡 그림")
	private String title;

	@Schema(description = "그림 이미지 URL", defaultValue = "https://aebookbucket.s3.ap-northeast-2.amazonaws.com/6/paintings/Image%20Pasted%20at%202023-4-24%2010-22.png")
	private String fileUrl;
	@Schema(description = "그림 타입", defaultValue = "COLOR")
	private PaintingType type;
	@Schema(description = "그림 저장 시각", defaultValue = "2023-04-14 10:30:15")
	private LocalDateTime createdAt;

	@Schema(description = "그림 제목 수정 시각", defaultValue = "2023-04-17 10:30:15")
	private LocalDateTime updatedAt;
}
