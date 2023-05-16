package com.c201.aebook.api.auth.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import com.c201.aebook.api.user.persistence.repository.RefreshRedisRepository;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.config.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

	@Mock
    private UserRepository userRepository;
	
	@Mock
    private RefreshRedisRepository refreshRedisRepository;
	
	@Mock
    private JwtTokenProvider jwtTokenProvider;
	
	@Mock
    private RedisTemplate redisTemplate;
	
	@Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
	
	@InjectMocks
	private AuthServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
		ReflectionTestUtils.setField(subject, "kakaoClientId", "kakaoClientId");
		ReflectionTestUtils.setField(subject, "kakaoRedirectUri", "kakaoRedirectUri");
	}

	@Test
	public void testGetAccessToken() {
//		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testFindProfile() {
//		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testSaveUserOrLogin() {
//		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testResolveToken() {
//		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testReissueAccessToken() {
//		throw new RuntimeException("not yet implemented");
	}

}
