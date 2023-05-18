package com.c201.aebook.api.batch;

import java.util.List;
import java.util.Optional;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.c201.aebook.api.book.persistence.repository.BookRepository;
import com.c201.aebook.api.event.NotificationEvent;

import lombok.RequiredArgsConstructor;

/*
 * 알라딘 api에서 가져온 데이터 저장
 * */

@Component
@RequiredArgsConstructor
public class AladinBatchItemWriter implements ItemWriter<BookEntity> {

	private final BookRepository bookRepository;
	private final ApplicationEventPublisher publisher;

	@Override
	public void write(List<? extends BookEntity> items) throws Exception {
		for (BookEntity item : items) {
			Optional<BookEntity> book = bookRepository.findByIsbn(item.getIsbn());
			if (book.isPresent()) {//기존 책이 있는 경우 갱신
				BookEntity updateBook = book.get();
				updateBook.setPrice(item.getPrice());
				updateBook.setAladinUrl(item.getAladinUrl());
				updateBook.setId(item.getId());

				bookRepository.save(updateBook);

				//기존 가격보다 최저가인 경우 알림 이벤트 발생
				if(book.get().getPrice() > item.getPrice()){
					publisher.publishEvent(new NotificationEvent(this, updateBook, item));
				}
			} else {
				bookRepository.save(item);
			}
		}
	}

}
