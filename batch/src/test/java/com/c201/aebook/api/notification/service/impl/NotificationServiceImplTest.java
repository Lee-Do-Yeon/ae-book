package com.c201.aebook.api.notification.service.impl;

import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.c201.aebook.api.notification.persistence.entity.NotificationEntity;
import com.c201.aebook.api.notification.persistence.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
	
	@Mock
	private NotificationRepository notificationRepository;
	
	@InjectMocks
	private NotificationServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
		ReflectionTestUtils.setField(subject, "TalkApiKey", "TalkApiKey");
		ReflectionTestUtils.setField(subject, "TalkUserId", "TalkUserId");
		ReflectionTestUtils.setField(subject, "TalkSenderKey", "TalkSenderKey");
		ReflectionTestUtils.setField(subject, "LowestPriceTplCode", "LowestPriceTplCode");
		ReflectionTestUtils.setField(subject, "CustomizeLowestPriceTplCode", "CustomizeLowestPriceTplCode");
		ReflectionTestUtils.setField(subject, "TalkSender", "TalkSender");
		ReflectionTestUtils.setField(subject, "aebookUrl", "aebookUrl");
	}

	@Test
	public void testCreateToken() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testLowestPriceTalk() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testCustomizeLowestPriceTalk() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	@DisplayName("testGetNotificationUserInfoByBookId : happy case")
	public void testGetNotificationUserInfoByBookId() {
		// given
		Long bookId = 1234567890l;
		List<NotificationEntity> notificationUserList = new ArrayList<>();
		BDDMockito.given(notificationRepository.findByBookId(bookId)).willReturn(notificationUserList);
		
		// when
		String notificationType = "notificationType"; 
		List<NotificationEntity> ret = subject.getNotificationUserInfoByBookId(bookId, notificationType);
		
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertEquals(ret, ret);
		});
		BDDMockito.then(notificationRepository).should(times(1)).findByBookId(bookId);
	}

	@Test
	public void testGetNotificationUserInfoByBookIdAndNotificationType() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testContainsKeyStartingWith() {
		throw new RuntimeException("not yet implemented");
	}

}
