package com.c201.aebook.api.painting.service;

import org.springframework.stereotype.Component;

import com.c201.aebook.api.painting.persistence.entity.PaintingEntity;
import com.c201.aebook.api.painting.persistence.repository.PaintingRepository;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaintingServiceHelper {
	private final UserRepository userRepository;
	private final PaintingRepository paintingRepository;
	
	// 그림 작성자와 로그인한 사용자가 일치하는지 확인하는 함수
	public PaintingEntity getOwnPainting(Long userId, Long paintingId) {
		// 1. 사용자 유효성 검사
		UserEntity userEntity = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 2. 그림 유효성 검사
		PaintingEntity paintingEntity = paintingRepository.findById(paintingId)
			.orElseThrow(() -> new CustomException(ErrorCode.PAINTING_NOT_FOUND));

		// 3. 작성자와 로그인한 사용자가 일치하는지 검사
		if (paintingEntity.getUser().getId() != userEntity.getId()) {
			throw new CustomException(ErrorCode.FORBIDDEN_USER);
		}
		return paintingEntity;
	}
}
