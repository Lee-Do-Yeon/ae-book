package com.c201.aebook.api.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.c201.aebook.api.book.persistence.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class AladinBatchItemWriterTest {
	
	@Mock
	private BookRepository bookRepository;
	
	@Mock
	private ApplicationEventPublisher publisher;
	
	@InjectMocks
	private AladinBatchItemWriter subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	public void testWrite() {
		throw new RuntimeException("not yet implemented");
	}

}
