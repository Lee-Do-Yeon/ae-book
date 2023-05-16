package com.c201.aebook.api.batch;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

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
import org.xml.sax.SAXException;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.helper.AladinBatchItemReaderHelper;

@ExtendWith(MockitoExtension.class)
public class AladinBatchItemReaderTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private AladinBatchItemReader subject;

	@Mock
	private AladinBatchItemReaderHelper helper;

	@BeforeEach
	protected void setUp() throws Exception {
		ReflectionTestUtils.setField(subject, "API_KEY", "ttbleedy9091441001");
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
		AladinBatchItemReaderHelper mockHelper = Mockito.spy(helper);
		Mockito.doReturn(mockBooks).when(mockHelper).getDataFromApi();
		ReflectionTestUtils.setField(mockReader, "aladinBatchItemReaderHelper", mockHelper);

		// when
		BookEntity actualBook = mockReader.read();

		// then
		Assertions.assertNotNull(actualBook);
	}

	@Test
	public void testGetDataFromApi() throws IOException, ParserConfigurationException, SAXException, ParseException {

	}

	@Test
	void testParseBook() {

	}

	@Test
	void testGetItemElementByUrl() {
	}

}
