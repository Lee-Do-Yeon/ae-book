package com.c201.aebook.api.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.helper.ItemReaderDelegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * 알리딘 api에서 데이터 읽어옴
 * */

@Slf4j
@Component
@RequiredArgsConstructor
public class AladinBatchItemReader implements ItemReader<BookEntity> {

	private int nextIndex = 0;
	private List<BookEntity> bookList = new ArrayList<>();


	private final ItemReaderDelegate itemReaderDelegate;

	/*
	 * 알라딘 api에서 책 정보를 읽어옴
	 * */
	@Override
	public BookEntity read() throws Exception{

		if (bookList.size() == 0) {
			try {
				bookList = getDataFromApi();
			} catch (Exception e) {
				log.error("aladin read error", e);
				throw new Exception();
			}
		}

		BookEntity nextBookEntity = null;
		if (nextIndex < bookList.size()) {
			nextBookEntity = bookList.get(nextIndex);
			nextIndex++;
		}

		return nextBookEntity;
	}

	private List<BookEntity> getDataFromApi() throws
		Exception {

		return itemReaderDelegate.getBookListFromAPI();
	}


}
