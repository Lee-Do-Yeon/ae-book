package com.c201.aebook.api.notification.service.impl;

import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;

import com.c201.aebook.api.notification.persistence.entity.NotificationEntity;
import com.c201.aebook.api.notification.persistence.repository.NotificationRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
	
	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private RestTemplate restTemplate;
	@Mock
	private ObjectMapper objectMapper;
	
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
	@DisplayName("testCreateToken: Happy Case")
	public void testCreateToken() throws ParseException, JsonProcessingException {
		// given
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("apikey", "TalkApiKey"); //api key
		params.add("userid", "TalkUserId"); //사이트 아이디
		HttpEntity<MultiValueMap<String, Object>> TokenRequest = new HttpEntity<>(params);

		ReflectionTestUtils.setField(subject, "TalkApiKey", "TalkApiKey");
		ReflectionTestUtils.setField(subject, "TalkUserId","TalkUserId");

		String response = "{\"code\": 0, \"message\": \"정상적으로 생성하였습니다.\", \"token\": \"token\", \"urlencode\": \"urlencode\"}";

		BDDMockito.given(restTemplate.exchange(
				eq("https://kakaoapi.aligo.in/akv10/token/create/7/d/"),
				eq(HttpMethod.POST),
				eq(TokenRequest),
				eq(String.class)
		)).willReturn(ResponseEntity.ok().body(response));

		// when
		String ret = subject.createToken();

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
		});

	}

	@Test
	@DisplayName("testLowestPriceTalk: Happy Case")
	public void testLowestPriceTalk() {
		// given
		String token = "talk token";
		String bookTitle = "test book title";
		String notificationSubject = "도서 최저가 갱신";

		UserEntity user1 = UserEntity.builder().nickname("test nickname").phone("010-0000-0000").build();
		NotificationEntity notification1 = NotificationEntity.builder().user(user1).build();
		List<NotificationEntity> userList = new ArrayList<>();
		userList.add(notification1);

		String response = "{\"code\": 0, \"message\": \"성공적으로 전송요청 하였습니다.\", \"info\": {\"type\": \"AT\", \"mid\": 571413780, \"current\": \"49889.5\", \"unit\": 6.5, \"total\": 6.5, \"scnt\": 1, \"fcnt\": 0}}";
		ResponseEntity<String> mockedResponse = ResponseEntity.ok(response);

		ReflectionTestUtils.setField(subject, "TalkApiKey", "TestTalkApiKey");
		ReflectionTestUtils.setField(subject, "TalkUserId", "TestTalkUserId");
		ReflectionTestUtils.setField(subject, "TalkSenderKey", "TestSenderKey");
		ReflectionTestUtils.setField(subject, "LowestPriceTplCode", "TestTplCode");
		ReflectionTestUtils.setField(subject, "TalkSender", "TestSender");
		ReflectionTestUtils.setField(subject, "aebookUrl", "aebookUrl");

		BDDMockito.given(restTemplate.exchange(
				eq("https://kakaoapi.aligo.in/akv10/alimtalk/send/"),
				eq(HttpMethod.POST),
				Matchers.<HttpEntity<MultiValueMap<String, Object>>>any(),
				eq(String.class)
		)).willReturn(ResponseEntity.ok().body(response));

		// when
		ResponseEntity<String> ret = subject.LowestPriceTalk(token, userList, bookTitle);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getStatusCode(), HttpStatus.OK);
			Assertions.assertEquals(ret.getBody(), response);
		});

	}

	@Test
	@DisplayName("testCustomizeLowestPriceTalk: Happy Case")
	public void testCustomizeLowestPriceTalk() {
		// given
		String token = "talk token";
		String bookTitle = "test book title";
		String notificationSubject = "도서 최저가 갱신";
		int price = 5000;

		UserEntity user1 = UserEntity.builder().nickname("test nickname").phone("010-0000-0000").build();
		NotificationEntity notification1 = NotificationEntity.builder().user(user1).upperLimit(4000).notificationType("S").build();
		List<NotificationEntity> userList = new ArrayList<>();
		userList.add(notification1);

		String response = "{\"code\": 0, \"message\": \"성공적으로 전송요청 하였습니다.\", \"info\": {\"type\": \"AT\", \"mid\": 571413780, \"current\": \"49889.5\", \"unit\": 6.5, \"total\": 6.5, \"scnt\": 1, \"fcnt\": 0}}";
		ResponseEntity<String> mockedResponse = ResponseEntity.ok(response);

		ReflectionTestUtils.setField(subject, "TalkApiKey", "TestTalkApiKey");
		ReflectionTestUtils.setField(subject, "TalkUserId", "TestTalkUserId");
		ReflectionTestUtils.setField(subject, "TalkSenderKey", "TestSenderKey");
		ReflectionTestUtils.setField(subject, "LowestPriceTplCode", "TestTplCode");
		ReflectionTestUtils.setField(subject, "TalkSender", "TestSender");
		ReflectionTestUtils.setField(subject, "aebookUrl", "aebookUrl");

		BDDMockito.given(restTemplate.exchange(
				eq("https://kakaoapi.aligo.in/akv10/alimtalk/send/"),
				eq(HttpMethod.POST),
				Matchers.<HttpEntity<MultiValueMap<String, Object>>>any(),
				eq(String.class)
		)).willReturn(ResponseEntity.ok().body(response));

		// when
		ResponseEntity<String> ret = subject.CustomizeLowestPriceTalk(token, userList, bookTitle, price);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getStatusCode(), HttpStatus.OK);
			Assertions.assertEquals(ret.getBody(), response);
		});

	}

	@Test
	@DisplayName("testGetNotificationUserInfoByBookId : Happy case")
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
			Assertions.assertNotNull(ret);
		});
		BDDMockito.then(notificationRepository).should(times(1)).findByBookId(bookId);
	}

	@Test
	@DisplayName("testGetNotificationUserInfoByBookIdAndNotificationType: Happy Case")
	public void testGetNotificationUserInfoByBookIdAndNotificationType() {
		// given
		Long bookId = 1234567890L;
		String notificationType = "notificationType";

		List<NotificationEntity> notificationUserList = new ArrayList<>();
		BDDMockito.given(notificationRepository.findByBookIdAndNotificationType(bookId, notificationType)).willReturn(notificationUserList);

		// when
		List<NotificationEntity> ret = subject.getNotificationUserInfoByBookIdAndNotificationType(bookId, notificationType);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
		});
		BDDMockito.then(notificationRepository).should(times(1)).findByBookIdAndNotificationType(bookId, notificationType);

	}

	@Test
	@DisplayName("testContainsKeyStartingWith: Happy Case")
	public void testContainsKeyStartingWith() {
		// given
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("apikey", "apikey");
		map.add("userid", "userId");
		map.add("receiver", "receiver");
		String keyPrefix = "receiver";

		// when
		boolean ret = subject.containsKeyStartingWith(map, keyPrefix);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertTrue(ret);
		});
	}

	@Test
	@DisplayName("testContainsKeyStartingWith: Sad Case")
	public void testContainsKeyStartingWith1() {
		// given
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("apikey", "apikey");
		map.add("userid", "userId");
		String keyPrefix = "receiver";

		// when
		boolean ret = subject.containsKeyStartingWith(map, keyPrefix);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertFalse(ret);
		});
	}

}
