package com.c201.aebook.api.story.presentation.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoryListResponseDTO {

	@Schema(description = "동화 ID", defaultValue = "1")
	private Long storyId;

	@Schema(description = "동화 작성자 nickname", defaultValue = "동글이")
	private String nickname;

	@Schema(description = "동화 제목", defaultValue = "아기 공룡의 하루")
	private String title;

	@Schema(description = "동화 이미지 url", defaultValue = "https://aebookbucket.s3.ap-northeast-2.amazonaws.com/%EC%95%84%EC%9D%B4%EB%B6%81.png")
	private String imgUrl;

	@Schema(description = "동화 작성 시간", defaultValue = "2023-04-14 10:30:15")
	private LocalDateTime createdAt;

	@Schema(description = "동화 수정 시간", defaultValue = "2023-04-17 10:30:15")
	private LocalDateTime updatedAt;

	@Builder
	public StoryListResponseDTO(Long storyId, String nickname, String title, String imgUrl,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.storyId = storyId;
		this.nickname = nickname;
		this.title = title;
		this.imgUrl = imgUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
