package com.c201.aebook.api.review.presentation.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.c201.aebook.api.review.presentation.dto.request.ReviewRequestDTO;
import com.c201.aebook.api.review.presentation.dto.response.ReviewBookResponseDTO;
import com.c201.aebook.api.review.presentation.dto.response.ReviewMainResponseDTO;
import com.c201.aebook.api.review.presentation.dto.response.ReviewMyResponseDTO;
import com.c201.aebook.api.review.presentation.dto.response.ReviewResponseDTO;
import com.c201.aebook.api.review.presentation.validator.ReviewValidator;
import com.c201.aebook.api.review.service.impl.ReviewServiceImpl;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.vo.ReviewSO;
import com.c201.aebook.auth.CustomUserDetails;
import com.c201.aebook.converter.ReviewConverter;
import com.c201.aebook.utils.RegexValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@Import(ReviewController.class)
public class ReviewControllerTest {

	@Autowired
	private ReviewController reviewController;

	private MockMvc mockMvc;

	@MockBean
	private ReviewValidator reviewValidator;

	@MockBean
	private RegexValidator regexValidator;

	@MockBean
	private ReviewServiceImpl reviewService;

	@MockBean
	private ReviewConverter reviewConverter;

	@SuppressWarnings("deprecation")
	@BeforeEach
	protected void setUp() throws Exception {
		mockMvc = MockMvcBuilders
			.standaloneSetup(reviewController)
			.setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(),
				new PageableHandlerMethodArgumentResolver())
			.addFilters(new SecurityContextPersistenceFilter())
			.build();
	}

	@Test
	@DisplayName("saveReview Test")
	public void testSaveReview() throws Exception {
		// given
		String isbn = "1234567890";

		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("장관이고요 죽겠읍니다", 5);
		ReviewSO reviewSO = ReviewSO.builder()
			.content(reviewRequestDTO.getContent())
			.score(reviewRequestDTO.getScore())
			.build();
		BDDMockito.given(reviewConverter.toReviewSO(Mockito.any(ReviewRequestDTO.class))).willReturn(reviewSO);

		BDDMockito.given(reviewService.saveReview(customUserDetails.getUsername(), isbn, reviewSO))
			.willReturn(ReviewResponseDTO.builder().build());

		// when
		mockMvc.perform(post("/reviews/" + isbn)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(reviewSO))
				.with(user(customUserDetails)))
			.andExpect(status().isOk())
			.andDo(print());

		// then
		BDDMockito.then(reviewService).should(times(1)).saveReview(customUserDetails.getUsername(), isbn, reviewSO);
	}

	@Test
	@DisplayName("getBookReviewList Test")
	public void testGetBookReviewList() throws Exception {
		// given
		String isbn = "1234567890";
		Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "createdAt");

		List<ReviewBookResponseDTO> list = new ArrayList<>();
		list.add(ReviewBookResponseDTO.builder().id(1L).build());
		list.add(ReviewBookResponseDTO.builder().id(2L).build());

		Page<ReviewBookResponseDTO> page = new PageImpl<>(list, pageable, list.size());
		BDDMockito.given(reviewService.getBookReviewList(isbn, pageable)).willReturn(page);

		// when
		mockMvc.perform(get("/reviews/" + isbn))
			.andExpect(status().isOk())
			.andDo(print());

		// then
		BDDMockito.then(reviewService).should(times(1)).getBookReviewList(isbn, pageable);
	}

	@Test
	public void testGetMyReviewList() throws Exception {
		//given
		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());

		List<ReviewMyResponseDTO> list = new ArrayList<>();
		list.add(ReviewMyResponseDTO.builder().id(1L).build());
		list.add(ReviewMyResponseDTO.builder().id(2L).build());

		Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "createdAt");
		Page<ReviewMyResponseDTO> page = new PageImpl<>(list, pageable, list.size());
		BDDMockito.given(reviewService.getMyReviewList(customUserDetails.getUsername(), pageable)).willReturn(page);

		// when
		mockMvc.perform(get("/reviews")
				.with(user(customUserDetails)))
			.andExpect(status().isOk())
			.andDo(print());

		// then
		BDDMockito.then(reviewService).should(times(1)).getMyReviewList(customUserDetails.getUsername(), pageable);
	}

	@Test
	public void testGetMyReview() throws Exception {
		// given
		String isbn = "1234567890";

		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());

		BDDMockito.given(reviewService.getMyReview(customUserDetails.getUsername(), isbn))
			.willReturn(ReviewResponseDTO.builder().build());

		// when
		mockMvc.perform(get("/reviews/" + isbn + "/mine")
				.with(user(customUserDetails)))
			.andExpect(status().isOk())
			.andDo(print());

		// then
	}

	@Test
	public void testModifyReview() throws Exception {
		// given
		Long reviewId = 1L;
		ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO("장관이고요, 절경이네요 ㅇㅅㅇa;;", 4);
		ReviewSO reviewSO = ReviewSO.builder()
			.content(reviewRequestDTO.getContent())
			.score(reviewRequestDTO.getScore())
			.build();
		BDDMockito.given(reviewConverter.toReviewSO(Mockito.any(ReviewRequestDTO.class))).willReturn(reviewSO);

		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());

		BDDMockito.given(reviewService.modifyReview(reviewId, customUserDetails.getUsername(), reviewSO))
			.willReturn(ReviewResponseDTO.builder()
				.build());

		// when
		mockMvc.perform(patch("/reviews/" + reviewId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(reviewSO))
				.with(user(customUserDetails)))
			.andExpect(status().isOk())
			.andDo(print());

		// then
		BDDMockito.then(reviewService)
			.should(times(1))
			.modifyReview(reviewId, customUserDetails.getUsername(), reviewSO);
	}

	@Test
	public void testDeleteReview() throws Exception {
		// given
		Long reviewId = 1L;

		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().build());

		// when
		mockMvc.perform(delete("/reviews/" + reviewId)
				.with(user(customUserDetails)))
			.andExpect(status().isOk())
			.andDo(print());

		// then
		BDDMockito.then(reviewService).should(times(1)).deleteReview(reviewId, customUserDetails.getUsername());
	}

	@Test
	public void testGetLatestReviewList() throws Exception {
		// given
		List<ReviewMainResponseDTO> list = new ArrayList<>();
		list.add(ReviewMainResponseDTO.builder().id(1L).build());
		list.add(ReviewMainResponseDTO.builder().id(2L).build());

		Pageable pageable = PageRequest.of(0, 12, Sort.Direction.DESC, "createdAt");
		List<ReviewMainResponseDTO> result = new PageImpl<>(list, pageable, list.size()).getContent();
		BDDMockito.given(reviewService.getLatestReviewList(pageable)).willReturn(result);

		// when
		mockMvc.perform(get("/reviews/latest"))
			.andExpect(status().isOk())
			.andDo(print());

		// then
		BDDMockito.then(reviewService).should(times(1)).getLatestReviewList(pageable);
	}

}
