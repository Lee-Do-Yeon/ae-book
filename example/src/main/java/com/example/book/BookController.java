package com.example.book;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class BookController {
	
	
	@GetMapping("/book")
	public ResponseEntity<?> getAllBooks(){
		List<BookDTO> list = new ArrayList<>();
		list.add(BookDTO.builder().bookName("bookName1").author("author1").price(500).build());
		list.add(BookDTO.builder().bookName("bookName2").author("author2").price(1000).build());
		list.add(BookDTO.builder().bookName("bookName3").author("author3").price(1500).build());
		list.add(BookDTO.builder().bookName("bookName4").author("author4").price(2000).build());
		log.info(list.toString());
		return ResponseEntity.ok(list);			
	}

}
