package com.c201.aebook.api.book.service.impl;

import static org.mockito.Mockito.times;

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

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.api.book.persistence.repository.BookCustomRepository;
import com.c201.aebook.api.book.persistence.repository.BookRepository;
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
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetNewBookList() {
		throw new RuntimeException("not yet implemented");
	}

}
