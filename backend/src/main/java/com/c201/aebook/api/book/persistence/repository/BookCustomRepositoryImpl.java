package com.c201.aebook.api.book.persistence.repository;

import static com.c201.aebook.api.book.persistence.entity.QBookEntity.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.c201.aebook.api.book.persistence.entity.BookEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BookCustomRepositoryImpl implements BookCustomRepository {
	private final JPAQueryFactory queryFactory;
	private final String TITLE_STRING = "TITLE";
	private final String AUTHOR_STRING = "AUTHOR";
	private final String PUBLISHER_STRING = "PUBLISHER";

	@Override
	public Page<BookEntity> searchBookList(String[] keyword, String[] searchTarget, Pageable pageable) {
		QueryResults<BookEntity> bookList = queryFactory
			.selectFrom(bookEntity)
			.where(checkSearchOption(searchTarget, keyword))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(bookList.getResults(), pageable, bookList.getTotal());
	}

	// 체크된 검색 옵션으로 동적 쿼리 만들기
	private BooleanBuilder checkSearchOption(String[] searchTarget, String[] keywords) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		Arrays.stream(searchTarget)
			.forEach(target -> {
				if (TITLE_STRING.equals(target)) {
					BooleanBuilder titleBooleanBuilder = new BooleanBuilder();
					Stream.of(keywords)
						.forEach(keyword -> titleBooleanBuilder.and(bookEntity.title.containsIgnoreCase(keyword)));
					booleanBuilder.or(titleBooleanBuilder);
				} else if (AUTHOR_STRING.equals(target)) {
					BooleanBuilder authorBooleanBuilder = new BooleanBuilder();
					Stream.of(keywords)
						.forEach(keyword -> authorBooleanBuilder.and(bookEntity.author.containsIgnoreCase(keyword)));
					booleanBuilder.or(authorBooleanBuilder);
				} else if (PUBLISHER_STRING.equals(target)) {
					BooleanBuilder publisherBooleanBuilder = new BooleanBuilder();
					Stream.of(keywords)
						.forEach(
							keyword -> publisherBooleanBuilder.and(bookEntity.publisher.containsIgnoreCase(keyword)));
					booleanBuilder.or(publisherBooleanBuilder);
				}
			});

		return booleanBuilder;
	}
}
