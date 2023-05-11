package com.c201.aebook.api.auth.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import com.c201.aebook.api.auth.service.impl.AuthServiceImpl;
import com.c201.aebook.converter.TokenConverter;

@ExtendWith(SpringExtension.class)
@Import(AuthController.class)
public class AuthControllerTest {
	
	@Autowired
	private AuthController authController;
	
	private MockMvc mockMvc;
	
	@MockBean
    private AuthServiceImpl authService;
	
	@MockBean
    private RedisTemplate redisTemplate;
	
	@MockBean
    private TokenConverter tokenConverter;

	@BeforeEach
	protected void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
	}

	@Test
	public void testLogin() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testReissueAccessToken() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testLogout() {
		throw new RuntimeException("not yet implemented");
	}

}
