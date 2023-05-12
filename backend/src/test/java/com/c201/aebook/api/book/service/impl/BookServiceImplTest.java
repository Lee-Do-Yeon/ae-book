package com.c201.aebook.api.book.service.impl;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.api.book.persistence.repository.BookCustomRepository;
import com.c201.aebook.api.book.persistence.repository.BookRepository;
import com.c201.aebook.api.book.presentation.dto.response.BookSearchResponseDTO;
import com.c201.aebook.api.notification.persistence.repository.NotificationRepository;
import com.c201.aebook.converter.BookConverter;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookCustomRepository bookCustomRepository;

	@Mock
	private BookConverter bookConverter;

	@Mock
	private NotificationRepository notificationRepository;

	@InjectMocks
	private BookServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	public void testSearchBookDetail() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAutocompleteTitle() {
		// given
		String keyword = "keyword";
		List<BookEntity> bookList = new ArrayList<>();
		bookList.add(BookEntity.builder().title("title1").build());
		bookList.add(BookEntity.builder().title("title2").build());
		BDDMockito.given(bookRepository.findTop5ByTitleContaining(keyword)).willReturn(bookList);

		// when
		List<String> ret = subject.getAutocompleteTitle(keyword);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.get(0), "title1");
			Assertions.assertEquals(ret.get(1), "title2");
		});
		BDDMockito.then(bookRepository).should(times(1)).findTop5ByTitleContaining(keyword);
	}

	@Test
	public void testSearchBookList() {
		// given
		String keyword = "keyword";
		String[] searchTarget = {"TITLE"};
		Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
		List<BookEntity> bookList = new ArrayList<>();
		bookList.add(BookEntity.builder().title("title1").build());
		bookList.add(BookEntity.builder().title("title2").build());
		Page<BookEntity> bookPage = new PageImpl<>(bookList, pageable, bookList.size());
		BDDMockito.given(bookCustomRepository.searchBookList(keyword.split(" "), searchTarget, pageable))
			.willReturn(bookPage);

		bookPage.stream().forEach(book -> {
			BookSearchResponseDTO responseDTO = BookSearchResponseDTO.builder()
				.title(book.getTitle())
				.build();

			BDDMockito.given(bookConverter.toBookSearchResponseDTO(book))
				.willReturn(responseDTO);
		});

		// when
		Page<BookSearchResponseDTO> ret = subject.searchBookList(keyword, searchTarget, pageable);
		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getContent().get(0).getTitle(), "title1");
			Assertions.assertEquals(ret.getContent().get(1).getTitle(), "title2");
		});
		BDDMockito.then(bookCustomRepository)
			.should(times(1))
			.searchBookList(keyword.split(" "), searchTarget, pageable);
	}

	@Test
	public void testGetNewBookList() {
		throw new RuntimeException("not yet implemented");
	}

}
