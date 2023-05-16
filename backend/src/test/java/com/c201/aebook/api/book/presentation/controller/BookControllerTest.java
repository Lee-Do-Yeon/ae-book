package com.c201.aebook.api.book.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.c201.aebook.api.book.presentation.dto.response.BookResponseDTO;
import com.c201.aebook.api.book.presentation.dto.response.BookSearchResponseDTO;
import com.c201.aebook.api.book.presentation.dto.response.BookSimpleResponseDTO;
import com.c201.aebook.api.book.service.impl.BookServiceImpl;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.auth.CustomUserDetails;
import com.c201.aebook.utils.RegexValidator;

@ExtendWith(SpringExtension.class)
@Import(BookController.class)
public class BookControllerTest {
	@Autowired
	private BookController bookController;
	private MockMvc mockMvc;
	@MockBean
	private BookServiceImpl bookService;
	@MockBean
	private RegexValidator regexValidator;

	@BeforeEach
	protected void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(bookController)
			.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
			.build();
	}

	@DisplayName("testSearchBookDetail: Happy Case")
	@Test
	public void testSearchBookDetail() throws Exception {
		// given
		String isbn = "9791185121147";
		CustomUserDetails customUserDetails = new CustomUserDetails(UserEntity.builder().id(1L).build());
		BookResponseDTO responseDTO = BookResponseDTO.builder().isbn(isbn).build();
		BDDMockito.given(bookService.searchBookDetail(isbn, customUserDetails)).willReturn(responseDTO);
		// when
		mockMvc.perform(get("/books/" + isbn)).andExpect(status().isOk());
		// then
		BDDMockito.then(bookService).should(times(1)).searchBookDetail(eq(isbn), any(CustomUserDetails.class));
	}

	@DisplayName("testGetAutocompleteTitle: Happy Case")
	@Test
	public void testGetAutocompleteTitle() throws Exception {
		// given
		String keyword = "keyword";
		List<String> titleList = new ArrayList<>();
		titleList.add("title1");
		titleList.add("title2");
		BDDMockito.given(bookService.getAutocompleteTitle(keyword)).willReturn(titleList);
		// when
		mockMvc.perform(get("/books/autocomplete")
				.param("keyword", keyword))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(bookService).should(times(1)).getAutocompleteTitle(keyword);
	}

	@DisplayName("testSearchBookList: Happy Case")
	@Test
	public void testSearchBookList() throws Exception {
		// given
		String keyword = "keyword";
		String[] searchTargets = {"TITLE"};
		Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
		List<BookSearchResponseDTO> bookList = new ArrayList<>();
		bookList.add(BookSearchResponseDTO.builder().title("title1").build());
		bookList.add(BookSearchResponseDTO.builder().title("title2").build());
		Page<BookSearchResponseDTO> bookPage = new PageImpl<>(bookList, pageable, bookList.size());
		BDDMockito.given(bookService.searchBookList(keyword, searchTargets, pageable)).willReturn(bookPage);

		// when
		mockMvc.perform(get("/books")
				.param("keyword", keyword)
				.param("searchTargets", searchTargets))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(bookService).should(times(1)).searchBookList(keyword, searchTargets, pageable);
	}

	@DisplayName("testGetNewBookList: Happy Case")
	@Test
	public void testGetNewBookList() throws Exception {
		// given
		List<BookSimpleResponseDTO> bookList = new ArrayList<>();
		bookList.add(BookSimpleResponseDTO.builder().build());
		BDDMockito.given(bookService.getNewBookList()).willReturn(bookList);
		// when
		mockMvc.perform(get("/books/new"))
			.andExpect(status().isOk());
		// then
		BDDMockito.then(bookService).should(times(1)).getNewBookList();
	}

}
