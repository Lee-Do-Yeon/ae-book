package com.c201.aebook.api.user.service.impl;

import com.c201.aebook.api.notification.service.impl.NotificationServiceImpl;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.presentation.dto.response.UserResponseDTO;
import com.c201.aebook.api.user.service.UserService;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.UserSO;
import com.c201.aebook.converter.UserConverter;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final NotificationServiceImpl notificationService;

    @Override
    public boolean isDuplicatedUserByNickname(String nickname) {
        // 닉네임 존재 여부를 true, false로 반환
        boolean userNickname = userRepository.existsByNickname(nickname);

        // 닉네임이 존재한다면 중복이므로 false 반환
        if(userNickname) {
            return false;
        }

        return true;
    }

    @Override
    public String getProfileImage(Long userId) {
        // 사용자 아이디로 프로필 이미지 찾기
        return userRepository.findProfileUrlById(userId);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserInfo(Long userId, UserSO userSO) {
        // 1. 사용자 아이디로 user 찾기
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 사용자 정보(nickname, profileUrl) 업데이트
        user.updateUserEntity(userSO.getNickname(), userSO.getProfileUrl());

        // 3. userResponseDTO에 저장
        UserResponseDTO userResponseDTO = userConverter.toUserResponse(user.getNickname(), user.getProfileUrl());

        return userResponseDTO;
    }

    @Override
    @Transactional
    public void deleteUserInfo(Long userId) {
        // 1. 사용자 아이디로 user 찾기
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 사용자가 신청한 알림 모두 삭제하기
        notificationService.deleteAllNoticiation(userId);

        // 3. 사용자 탈퇴처리(카카오 아이디와 전화번호는 null로 변경, 닉네임은 '탈퇴한 사용자'로 변경, status는 0으로 변경, 프로필 이미지는 기본 이미지로 변경)
        user.invalidateUserEntity(null, null, "탈퇴한 사용자", 0, "https://aebookbucket.s3.ap-northeast-2.amazonaws.com/basic_profile.png");
    }
}
