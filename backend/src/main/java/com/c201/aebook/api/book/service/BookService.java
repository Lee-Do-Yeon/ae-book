package com.c201.aebook.api.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.c201.aebook.api.book.presentation.dto.response.BookResponseDTO;
import com.c201.aebook.api.book.presentation.dto.response.BookSearchResponseDTO;

public interface BookService {

	// 1. ISBN으로 도서 상세 정보 조회
	public BookResponseDTO searchBookDetail(String isbn);

	// 2. 검색시 책 제목 자동 완성
	public List<String> getAutocompleteTitle(String keyword);

	// 3. 도서 검색 결과 반환
	public Page<BookSearchResponseDTO> searchBookList(String keyword, boolean[] searchType, Pageable pageable);
}
