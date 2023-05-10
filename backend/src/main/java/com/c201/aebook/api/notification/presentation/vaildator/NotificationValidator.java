package com.c201.aebook.api.notification.presentation.vaildator;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.c201.aebook.api.common.CommonValidator;
import com.c201.aebook.api.notification.presentation.dto.request.NotificationRequestDTO;
import com.c201.aebook.api.notification.presentation.dto.request.NotificationUpdateRequestDTO;

@Component
public class NotificationValidator extends CommonValidator {
	public void validateNotificationRequestDTO(NotificationRequestDTO notificationRequestDTO) {
		checkStringType(notificationRequestDTO.getIsbn(), "isbn");
		checkNotificationIntType(notificationRequestDTO.getUpperLimit(), "금액 상한선");
		checkNotificationType(notificationRequestDTO.getNotificationType(), "알림 타입");
	}

	public void checkNotificationType(String notificationType, String name) {
		if (!Arrays.asList("S", "D").contains(notificationType)) {
			throw new IllegalArgumentException(name + "은/는 필수 입력값이며 S 또는 D만 가능합니다.");
		}
	}

	public void validateNotificationUpdateRequestDTO(NotificationUpdateRequestDTO notificationUpdateRequestDTO) {
		checkNotificationIntType(notificationUpdateRequestDTO.getUpperLimit(), "금액 상한선");
		checkNotificationType(notificationUpdateRequestDTO.getNotificationType(), "알림 타입");
	}

	public void checkNotificationIntType(int value, String name) {
		if (value < 0) {
			throw new IllegalArgumentException(name + "은/는 필수 입력값이며 양수 값만 가능합니다.");
		}
	}
}
