package com.c201.aebook.api.painting.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c201.aebook.api.painting.persistence.entity.PaintingEntity;
import com.c201.aebook.api.painting.persistence.repository.PaintingRepository;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class PaintingServiceHelperTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private PaintingRepository paintingRepository;
	@InjectMocks
	private PaintingServiceHelper subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@DisplayName("getOwnPainting: Happy Case")
	@Test
	void testGetOwnPainting() {
		// given
		Long userId = 1L;
		Long paintingId = 1L;
		UserEntity userEntity = UserEntity.builder().id(userId).build();
		BDDMockito.given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
		PaintingEntity paintingEntity = PaintingEntity.builder().id(paintingId).user(userEntity).build();
		BDDMockito.given(paintingRepository.findById(paintingId)).willReturn(Optional.of(paintingEntity));
		// when
		PaintingEntity ret = subject.getOwnPainting(userId, paintingId);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getId(), paintingId);
			Assertions.assertEquals(ret.getUser(), userEntity);
		});
		BDDMockito.then(userRepository).should(times(1)).findById(userId);
		BDDMockito.then(paintingRepository).should(times(1)).findById(paintingId);
	}

	@DisplayName("getOwnPainting: author and login user do not match")
	@Test
	void testGetOwnPainting2() {
		// given
		Long authorUserId = 2L;
		Long loginUserId = 1L;
		Long paintingId = 1L;
		UserEntity authorUserEntity = UserEntity.builder().id(authorUserId).build();
		UserEntity loginUserEntity = UserEntity.builder().id(loginUserId).build();
		BDDMockito.given(userRepository.findById(loginUserId)).willReturn(Optional.of(loginUserEntity));
		PaintingEntity paintingEntity = PaintingEntity.builder().id(paintingId).user(authorUserEntity).build();
		BDDMockito.given(paintingRepository.findById(paintingId)).willReturn(Optional.of(paintingEntity));
		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			subject.getOwnPainting(loginUserId, paintingId);
		});
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertEquals(((CustomException)throwable).getErrorCode(), ErrorCode.FORBIDDEN_USER);
		});
		BDDMockito.then(userRepository).should(times(1)).findById(loginUserId);
		BDDMockito.then(paintingRepository).should(times(1)).findById(paintingId);
	}
}