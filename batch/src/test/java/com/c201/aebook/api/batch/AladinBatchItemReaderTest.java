package com.c201.aebook.api.batch;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.helper.ItemReaderDelegate;

@ExtendWith(MockitoExtension.class)
public class AladinBatchItemReaderTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private AladinBatchItemReader subject;

	@Mock
	private ItemReaderDelegate helper;

	@BeforeEach
	protected void setUp() throws Exception {
		ReflectionTestUtils.setField(helper, "API_KEY", "ttbleedy9091441001");
	}

	@Test
	public void readTest() throws Exception {
		// given
		AladinBatchItemReader mockReader = Mockito.spy(subject);

		// bookList에 담길 가짜 데이터를 생성
		List<BookEntity> mockBooks = new ArrayList<>();
		mockBooks.add(BookEntity.builder().author("작가더미1").title("제목더미1").build());
		mockBooks.add(BookEntity.builder().author("작가더미2").title("제목더미2").build());

		// getDataFromApi() 호출 결과를 고정
		ItemReaderDelegate mockHelper = Mockito.spy(helper);
		Mockito.doReturn(mockBooks).when(mockHelper).getBookListFromAPI();
		ReflectionTestUtils.setField(mockReader, "itemReaderDelegate", mockHelper);

		// when
		BookEntity actualBook = mockReader.read();

		// then
		Assertions.assertNotNull(actualBook);
	}

	@Test
	public void testGetDataFromApi() throws Exception {
		//given
		ItemReaderDelegate mockHelper = Mockito.spy(helper);

		// Mock 도서 정보
		List<BookEntity> mockBooks = new ArrayList<>();
		mockBooks.add(BookEntity.builder().author("작가1").title("제목1").build());
		mockBooks.add(BookEntity.builder().author("작가2").title("제목2").build());

		// mockHelper.getDataFromApi()가 호출될 때 mockBooks를 반환하도록 설정
		Mockito.when(mockHelper.getBookListFromAPI()).thenReturn(mockBooks);

		// reader에 mockHelper를 주입하여 private 필드인 itemReaderDelegate를 설정
		ReflectionTestUtils.setField(subject, "itemReaderDelegate", mockHelper);

		//when
		List<BookEntity> books = mockHelper.getBookListFromAPI();

		//then
		Assertions.assertEquals(mockBooks.size(), books.size());

		for (int i = 0; i < mockBooks.size(); i++) {
			BookEntity expected = mockBooks.get(i);
			BookEntity actual = books.get(i);
			Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
			Assertions.assertEquals(expected.getTitle(), actual.getTitle());
		}

	}

}
