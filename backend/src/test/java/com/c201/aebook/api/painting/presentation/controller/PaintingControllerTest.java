package com.c201.aebook.api.painting.presentation.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.c201.aebook.api.painting.persistence.entity.PaintingType;
import com.c201.aebook.api.painting.presentation.dto.request.PaintingRequestDTO;
import com.c201.aebook.api.painting.presentation.dto.request.PaintingTitleRequestDTO;
import com.c201.aebook.api.painting.presentation.dto.response.PaintingResponseDTO;
import com.c201.aebook.api.painting.presentation.validator.PaintingValidator;
import com.c201.aebook.api.painting.service.impl.PaintingServiceImpl;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.vo.PaintingPatchSO;
import com.c201.aebook.api.vo.PaintingSO;
import com.c201.aebook.auth.CustomUserDetails;
import com.c201.aebook.converter.PaintingConverter;
import com.c201.aebook.utils.S3Downloader;
import com.c201.aebook.utils.S3Uploader;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@Import(PaintingController.class)
public class PaintingControllerTest {
	@Autowired
	private PaintingController paintingController;

	private MockMvc mockMvc;
	@MockBean
	private PaintingServiceImpl paintingService;
	@MockBean
	private PaintingConverter paintingConverter;
	@MockBean
	private PaintingValidator paintingValidator;
	@MockBean
	private S3Uploader s3Uploader;
	@MockBean
	private S3Downloader s3Downloader;

	@SuppressWarnings("deprecation")
	@BeforeEach
	protected void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(paintingController)
			.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(),
				new PageableHandlerMethodArgumentResolver())
			.addFilters(new SecurityContextPersistenceFilter())
			.build();
	}

	@DisplayName("testSavePainting: Happy Case")
	@Test
	public void testSavePainting() throws Exception {
		// given
		Map<String, String> input = new HashMap<>();
		input.put("title", "test-title");
		input.put("type", "COLOR");
		String contents = new ObjectMapper().writeValueAsString(input);

		Long userId = 1L;
		UserEntity user = UserEntity.builder().id(userId).build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		MockMultipartFile paintingFile = new MockMultipartFile(
			"paintingFile",
			"test.png",
			MediaType.IMAGE_PNG_VALUE,
			"<<png data>>".getBytes(StandardCharsets.UTF_8)
		);

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"",
			"application/json",
			contents.getBytes(StandardCharsets.UTF_8)
		);

		PaintingRequestDTO paintingRequestDTO = new PaintingRequestDTO(input.get("title"),
			PaintingType.valueOf(input.get("type")));
		String uploadImageUrl = "uploadImageUrl";
		BDDMockito.given(s3Uploader.upload(any(MultipartFile.class), any(String.class))).willReturn(uploadImageUrl);

		PaintingSO paintingSO = new PaintingSO(paintingRequestDTO.getTitle(), uploadImageUrl, PaintingType.COLOR,
			userId);
		BDDMockito.given(
				paintingConverter.toPaintingSO(any(Long.class), any(String.class), any(PaintingRequestDTO.class)))
			.willReturn(paintingSO);
		PaintingResponseDTO paintingResponseDTO = PaintingResponseDTO.builder().build();
		BDDMockito.given(paintingService.savePainting(paintingSO))
			.willReturn(paintingResponseDTO);
		// when
		mockMvc.perform(multipart("/paintings")
				.file(paintingFile)
				.file(data)
				.contentType("multipart/form-data")
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.with(user(customUserDetails)))
			.andExpect(status().isOk());

		// then
		BDDMockito.then(paintingService).should(times(1)).savePainting(any(PaintingSO.class));
	}

	@DisplayName("testDownloadPainting: Happy Case")
	@Test
	public void testDownloadPainting() throws Exception {
		// given
		Long paintingId = 1L;
		String filePath = "filePath";
		Long userId = 1L;
		UserEntity user = UserEntity.builder().id(userId).build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		BDDMockito.given(paintingService.getFilePath(paintingId, userId)).willReturn(filePath);
		// when
		mockMvc.perform(get("/paintings/download/" + paintingId)
				.with(user(customUserDetails)))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(paintingService).should(times(1)).getFilePath(paintingId, userId);
		BDDMockito.then(s3Downloader).should(times(1)).download(filePath);

	}

	@DisplayName("testDeletePainting: Happy Case")
	@Test
	public void testDeletePainting() throws Exception {
		// given
		Long paintingId = 1L;
		Long userId = 1L;
		UserEntity user = UserEntity.builder().id(userId).build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		String filePath = "filePath";
		BDDMockito.given(paintingService.getFilePath(paintingId, userId)).willReturn(filePath);
		// when
		mockMvc.perform(delete("/paintings/" + paintingId)
				.with(user(customUserDetails)))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(paintingService).should(times(1)).getFilePath(paintingId, userId);
	}

	@DisplayName("testUpdatePaintingTitle: Happy Case")
	@Test
	public void testUpdatePaintingTitle() throws Exception {
		// given
		Long paintingId = 1L;
		Long userId = 1L;
		UserEntity user = UserEntity.builder().id(userId).build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		PaintingTitleRequestDTO paintingTitleRequestDTO = new PaintingTitleRequestDTO("title");
		PaintingPatchSO paintingPatchSO = PaintingPatchSO.builder().build();
		BDDMockito.given(
				paintingConverter.toPaintingPatchSO(any(Long.class), any(Long.class), any(PaintingTitleRequestDTO.class)))
			.willReturn(paintingPatchSO);
		PaintingResponseDTO paintingResponseDTO = PaintingResponseDTO.builder().build();
		BDDMockito.given(paintingService.updatePaintingTitle(paintingPatchSO))
			.willReturn(paintingResponseDTO);
		// when
		mockMvc.perform(patch("/paintings/" + paintingId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(paintingTitleRequestDTO))
				.with(user(customUserDetails)))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(paintingService).should(times(1)).updatePaintingTitle(any(PaintingPatchSO.class));
	}

	@DisplayName("testGetPaintingDetails: Happy Case")
	@Test
	public void testGetPaintingDetails() throws Exception {
		// given
		Long paintingId = 1L;
		Long userId = 1L;
		UserEntity user = UserEntity.builder().id(userId).build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		PaintingResponseDTO paintingResponseDTO = PaintingResponseDTO.builder().build();
		BDDMockito.given(paintingService.getPaintingDetails(userId, paintingId)).willReturn(paintingResponseDTO);

		// when
		mockMvc.perform(get("/paintings/" + paintingId)
				.with(user(customUserDetails)))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(paintingService).should(times(1)).getPaintingDetails(userId, paintingId);

	}

	@DisplayName("testGetPaintingList: Happy Case")
	@Test
	public void testGetPaintingList() throws Exception {
		// given
		Long paintingId = 1L;
		Long userId = 1L;
		PaintingType type = PaintingType.COLOR;
		UserEntity user = UserEntity.builder().id(userId).build();
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		List<PaintingResponseDTO> bookList = new ArrayList<>();
		bookList.add(PaintingResponseDTO.builder().build());

		Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<PaintingResponseDTO> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

		BDDMockito.given(paintingService.getPaintingList(userId, type, pageable)).willReturn(bookPage);

		// when
		mockMvc.perform(get("/paintings")
				.param("type", "COLOR")
				.with(user(customUserDetails)))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(paintingService).should(times(1)).getPaintingList(userId, type, pageable);

	}

	@DisplayName("testGetNewPaintingList: Happy Case")
	@Test
	public void testGetNewPaintingList() throws Exception {
		// given
		List<PaintingResponseDTO> bookList = new ArrayList<>();
		bookList.add(PaintingResponseDTO.builder().build());

		BDDMockito.given(paintingService.getNewPaintingList()).willReturn(bookList);

		// when
		mockMvc.perform(get("/paintings/new"))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(paintingService).should(times(1)).getNewPaintingList();
	}

}
