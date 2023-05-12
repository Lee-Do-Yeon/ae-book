package com.c201.aebook.api.book.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.c201.aebook.api.book.persistence.entity.BookEntity;

public interface BookCustomRepository {
	public Page<BookEntity> searchBookList(String[] keyword, String[] searchTarget, Pageable pageable);
}
