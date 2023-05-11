package com.c201.aebook.api.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class AladinBatchItemReaderTest {

	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private AladinBatchItemReader subject;

	@BeforeEach
	protected void setUp() throws Exception {
	}

	@Test
	public void testRead() {
		throw new RuntimeException("not yet implemented");
	}

}
