package com.c201.aebook.api.user.service;

import com.c201.aebook.api.user.presentation.dto.response.UserResponseDTO;
import com.c201.aebook.api.vo.UserSO;

public interface UserService {

    public boolean isDuplicatedUserByNickname(String nickname);

    public String getProfileImage(Long userId);

    public UserResponseDTO updateUserInfo(Long userId, UserSO userSO);

    public void deleteUserInfo(Long userId);
}
