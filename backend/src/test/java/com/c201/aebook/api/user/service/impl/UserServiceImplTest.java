package com.c201.aebook.api.user.service.impl;

import com.c201.aebook.api.notification.service.impl.NotificationServiceImpl;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.user.presentation.dto.response.UserResponseDTO;
import com.c201.aebook.api.vo.UserSO;
import com.c201.aebook.converter.UserConverter;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private UserConverter userConverter;
	@Mock
	private NotificationServiceImpl notificationService;

	@InjectMocks
	private UserServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	@DisplayName("testDuplicatedUserByNickname: Happy Case")
	public void testIsDuplicatedUserByNickname() {
		// given
		String nickname = "test nickname";
		Boolean checkNickname = false;

		BDDMockito.given(userRepository.existsByNickname(nickname)).willReturn(checkNickname);

		// when
		boolean ret = subject.isDuplicatedUserByNickname(nickname);

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertTrue(ret);
		});

	}

	@Test
	@DisplayName("testDuplicatedUserByNickname: sad Case")
	public void testIsDuplicatedUserByNickname1() {
		// given
		String nickname = "test nickname";
		Boolean checkNickname = true;

		BDDMockito.given(userRepository.existsByNickname(nickname)).willReturn(checkNickname);

		// when
		boolean ret = subject.isDuplicatedUserByNickname(nickname);

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertFalse(ret);
		});
	}

	@Test
	@DisplayName("testGetProfileImage: Happy Case")
	public void testGetProfileImage() {
		// given
		Long userId = 1L;
		String profileUrl = "profile url";

		BDDMockito.given(userRepository.findProfileUrlById(userId)).willReturn(profileUrl);

		// when
		String ret = subject.getProfileImage(userId);

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret, "profile url");
		});
	}

	@Test
	@DisplayName("testUpdateUserInfo: Happy Case")
	public void testUpdateUserInfo() {
		// given
		Long userId = 1L;
		String nickname = "test nickname";
		String profileUrl = "test profile url";

		UserSO userSO = UserSO.builder().nickname(nickname).profileUrl(profileUrl).build();

		UserEntity user = UserEntity.builder().nickname("test nickname").profileUrl("test profile url").build();
		BDDMockito.given(userRepository.findById(userId)).willReturn(Optional.of(user));

		user.updateUserEntity(userSO.getNickname(), userSO.getProfileUrl());

		UserResponseDTO userResponseDTO = UserResponseDTO.builder()
				.nickname(userSO.getNickname()).profileUrl(userSO.getProfileUrl()).build();
		BDDMockito.given(userConverter.toUserResponse(user.getNickname(), user.getProfileUrl())).willReturn(userResponseDTO);

		// when
		UserResponseDTO ret = subject.updateUserInfo(userId, userSO);

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getNickname(), "test nickname");
			Assertions.assertEquals(ret.getProfileUrl(), "test profile url");
		});
		BDDMockito.then(userRepository).should(times(1)).findById(userId);

//		throw new RuntimeException("not yet implemented");
	}

	@Test
	@DisplayName("testDeleteUserInfo: Happy Case")
	public void testDeleteUserInfo() {
		// given
		Long userId = 1L;
		Long kakaoId = 123456L;
		String phone = "010-2000-0813";
		String nickname = "test nickname";
		String profileuUrl = "profile url";
		UserEntity user = UserEntity.builder().id(userId).kakaoId(kakaoId).phone(phone).nickname(nickname).status(1).profileUrl(profileuUrl).build();

		BDDMockito.given(userRepository.findById(userId)).willReturn(Optional.of(user));

		user.invalidateUserEntity(null, null, "탈퇴한 사용자", 0, "https://aebookbucket.s3.ap-northeast-2.amazonaws.com/basic_profile.png");

		// when
		subject.deleteUserInfo(userId);

		// then

	}

}
