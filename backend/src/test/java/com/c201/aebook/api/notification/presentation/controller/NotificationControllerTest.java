package com.c201.aebook.api.notification.presentation.controller;

import com.c201.aebook.api.notification.presentation.dto.request.NotificationRequestDTO;
import com.c201.aebook.api.notification.presentation.dto.request.NotificationUpdateRequestDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationBookDetailResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationBookListResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationResponseDTO;
import com.c201.aebook.api.notification.presentation.dto.response.NotificationUpdateResponseDTO;
import com.c201.aebook.api.notification.presentation.vaildator.NotificationValidator;
import com.c201.aebook.api.notification.service.impl.NotificationServiceImpl;
import com.c201.aebook.api.review.presentation.dto.request.ReviewRequestDTO;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.vo.NotificationPatchSO;
import com.c201.aebook.api.vo.NotificationSO;
import com.c201.aebook.auth.CustomUserDetails;
import com.c201.aebook.converter.NotificationConverter;
import com.c201.aebook.utils.RegexValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Import(NotificationController.class)
public class NotificationControllerTest {

	@Autowired
	NotificationController notificationController;

	private MockMvc mockMvc;

	@MockBean
	private NotificationServiceImpl notificationService;

	@MockBean
	private RegexValidator regexValidator;

	@MockBean
	private NotificationValidator notificationValidator;
	@MockBean
	private NotificationConverter notificationConverter;

	@BeforeEach
	protected void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
				.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(),
						new PageableHandlerMethodArgumentResolver())
				.addFilters(new SecurityContextPersistenceFilter())
				.build();
	}

	@Test
	@DisplayName("testSaveNotification: Happy Case")
	public void testSaveNotification() throws Exception {
		// given
		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());
		NotificationRequestDTO notificationRequestDTO = NotificationRequestDTO.builder().isbn("123456789").upperLimit(5000).notificationType("S").build();

		NotificationSO notificationSO = NotificationSO.builder().isbn("123456789").upperLimit(5000).notificationType("S").build();
		BDDMockito.given(notificationConverter.toNotificationSO(any(NotificationRequestDTO.class))).willReturn(notificationSO);

		NotificationResponseDTO notificationResponseDTO = NotificationResponseDTO.builder().upperLimit(5000).notificationType("S").build();
		BDDMockito.given(notificationService.saveNotification(customUserDetails.getUsername(), notificationSO)).willReturn(notificationResponseDTO);

		// when
		mockMvc.perform(post("/notifications")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(notificationRequestDTO))
						.with(user(customUserDetails)))
				.andExpect(status().isOk())
				.andDo(print());

		// then
		BDDMockito.then(notificationService).should(times(1)).saveNotification(customUserDetails.getUsername(), notificationSO);
	}

	@Test
	@DisplayName("testGetNotificationBookList: Happy Case")
	public void testGetNotificationBookList() throws Exception {
		// given
		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());
		Pageable pageable = PageRequest.of(0, 6, Sort.Direction.DESC, "createdAt");

		List<NotificationBookListResponseDTO> list = new ArrayList<>();
		list.add(NotificationBookListResponseDTO.builder().isbn("0123456789").title("test title").price(5000).build());
		list.add(NotificationBookListResponseDTO.builder().isbn("1234567890").title("test title2").price(6000).build());

		Page<NotificationBookListResponseDTO> page = new PageImpl<>(list, pageable, list.size());
		BDDMockito.given(notificationService.getMyNotificationBookList(customUserDetails.getUsername(), pageable)).willReturn(page);

		// when
		mockMvc.perform(get("/notifications")
						.with(user(customUserDetails)))
				.andExpect(status().isOk())
				.andDo(print());

		// then
		BDDMockito.then(notificationService).should(times(1)).getMyNotificationBookList(customUserDetails.getUsername(), pageable);

	}

	@Test
	@DisplayName("testGetNotificationBookDetail: Happy Case")
	public void testGetNotificationBookDetail() throws Exception {
		// given
		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());
		Long notificationId = 1L;

		BDDMockito.given(notificationService.getMyNotificationBookDetail(notificationId)).willReturn(NotificationBookDetailResponseDTO.builder().build());

		// when
		mockMvc.perform(get("/notifications/" + notificationId)
						.with(user(customUserDetails)))
				.andExpect(status().isOk())
				.andDo(print());

		// then
		BDDMockito.then(notificationService).should(times(1)).getMyNotificationBookDetail(notificationId);
	}

	@Test
	@DisplayName("testUpdateNotification: Happy Case")
	public void testUpdateNotification() throws Exception {
		// given
		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());
		Long notificationId = 1L;

		NotificationUpdateRequestDTO notificationUpdateRequestDTO = NotificationUpdateRequestDTO.builder().notificationType("D").upperLimit(0).build();
		NotificationPatchSO notificationPatchSO = NotificationPatchSO.builder().notificationType(notificationUpdateRequestDTO.getNotificationType()).upperLimit(notificationUpdateRequestDTO.getUpperLimit()).build();
		BDDMockito.given(notificationConverter.toNotificationPatchSO(Mockito.any(NotificationUpdateRequestDTO.class))).willReturn(notificationPatchSO);

		BDDMockito.given(notificationService.updateNotification(customUserDetails.getUsername(), notificationId,notificationPatchSO))
				.willReturn(NotificationUpdateResponseDTO.builder().build());

		// when
		mockMvc.perform(patch("/notifications/" + notificationId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(notificationPatchSO))
						.with(user(customUserDetails)))
				.andExpect(status().isOk())
				.andDo(print());

		// then
		BDDMockito.then(notificationService).should(times(1)).updateNotification(customUserDetails.getUsername(), notificationId, notificationPatchSO);

	}

	@Test
	@DisplayName("testDeleteNotification: Happy Case")
	public void testDeleteNotification() throws Exception {
		// given
		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());
		Long notificationId = 1L;

		// when
		mockMvc.perform(delete("/notifications/" + notificationId)
						.with(user(customUserDetails)))
				.andExpect(status().isOk())
				.andDo(print());

		// then
		BDDMockito.then(notificationService).should(times(1)).deleteNotification(customUserDetails.getUsername(), notificationId);
	}

}
