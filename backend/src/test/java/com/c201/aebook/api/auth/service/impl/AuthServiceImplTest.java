package com.c201.aebook.api.auth.service.impl;

import com.c201.aebook.api.common.LoginUserInfoDTO;
import com.c201.aebook.api.common.TokenDTO;
import com.c201.aebook.api.user.persistence.entity.RefreshRedisTokenEntity;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.RefreshRedisRepository;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.TokenSO;
import com.c201.aebook.auth.dto.KakaoTokenDTO;
import com.c201.aebook.auth.profile.KakaoProfile;
import com.c201.aebook.config.jwt.JwtTokenProvider;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.parser.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


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
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private ObjectMapper objectMapper;

	@Spy
	@InjectMocks
	private AuthServiceImpl subject;

	@BeforeEach
	protected void setUp() throws Exception {
		ReflectionTestUtils.setField(subject, "kakaoClientId", "kakaoClientId");
		ReflectionTestUtils.setField(subject, "kakaoRedirectUri", "kakaoRedirectUri");
	}
	
	@Test
	@DisplayName("testGetAccessToken: Happy Case")
	public void testGetAccessToken() throws JsonProcessingException {
		// given
		String code = "test kakao code";
		String accessTokenJson = "{\"access_token\":\"access token\",\"token_type\":\"bearer\",\"refresh_token\":\"refresh token\",\"expires_in\":21599,\"scope\":\"profile_image profile_nickname phone_number\",\"refresh_token_expires_in\":5183999}";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "kakaoClientId");
		params.add("redirect_uri", "kakaoRedirectUri");
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		ReflectionTestUtils.setField(subject, "kakaoClientId", "kakaoClientId");
		ReflectionTestUtils.setField(subject, "kakaoRedirectUri","kakaoRedirectUri" );
		BDDMockito.given(restTemplate.exchange(
				eq("https://kauth.kakao.com/oauth/token"),
				eq(HttpMethod.POST),
				eq(kakaoTokenRequest),
				eq(String.class)
		)).willReturn(ResponseEntity.ok().body(accessTokenJson));

		// when
		KakaoTokenDTO ret = subject.getAccessToken(code);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getAccess_token(), "access token");
			Assertions.assertEquals(ret.getToken_type(), "bearer");
			Assertions.assertEquals(ret.getRefresh_token(), "refresh token");
			Assertions.assertEquals(ret.getExpires_in(), 21599);
			Assertions.assertEquals(ret.getScope(), "profile_image profile_nickname phone_number");
			Assertions.assertEquals(ret.getRefresh_token_expires_in(), 5183999);
		});

	}

	@Test
	@DisplayName("testFindProfile: Happy Case")
	public void testFindProfile() {
		// given
		String token = "test kakao access token";
		String kakaoProfileJson = "{\"id\":123456789,\"connected_at\":\"2023-04-19T02:22:56Z\",\"properties\":{\"nickname\":\"test nickname\",\"profile_image\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg\",\"thumbnail_image\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg\"},\"kakao_account\":{\"profile_nickname_needs_agreement\":false,\"profile_image_needs_agreement\":false,\"profile\":{\"nickname\":\"test nickname\",\"thumbnail_image_url\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg\",\"profile_image_url\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg\",\"is_default_image\":true},\"has_phone_number\":true,\"phone_number_needs_agreement\":false,\"phone_number\":\"+82 10-0000-0000\"}}";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

		ReflectionTestUtils.setField(subject, "kakaoClientId", "c8552298d81db44a10187c0ca23800c8");
		ReflectionTestUtils.setField(subject, "kakaoRedirectUri","http://localhost:3000/user/oauth" );
		BDDMockito.given(restTemplate.exchange(
				eq("https://kapi.kakao.com/v2/user/me"),
				eq(HttpMethod.POST),
				eq(kakaoProfileRequest),
				eq(String.class)
		)).willReturn(ResponseEntity.ok().body(kakaoProfileJson));

		// when
		KakaoProfile ret = subject.findProfile(token);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.id, 123456789L);
			Assertions.assertEquals(ret.connected_at, "2023-04-19T02:22:56Z");
			Assertions.assertEquals(ret.properties.nickname, "test nickname");
			Assertions.assertEquals(ret.properties.profile_image, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg");
			Assertions.assertEquals(ret.properties.thumbnail_image, "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg");
			Assertions.assertEquals(ret.kakao_account.phone_number, "+82 10-0000-0000");
		});

	}

	@Test
	@DisplayName("testSaveUserOrLogin: Happy Case")
	public void testSaveUserOrLogin() {
		// given
		String token = "test kakao access token";
		String kakaoProfileJson = "{\"id\":123456789,\"connected_at\":\"2023-04-19T02:22:56Z\",\"properties\":{\"nickname\":\"test nickname\",\"profile_image\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg\",\"thumbnail_image\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg\"},\"kakao_account\":{\"profile_nickname_needs_agreement\":false,\"profile_image_needs_agreement\":false,\"profile\":{\"nickname\":\"test nickname\",\"thumbnail_image_url\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg\",\"profile_image_url\":\"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg\",\"is_default_image\":true},\"has_phone_number\":true,\"phone_number_needs_agreement\":false,\"phone_number\":\"+82 10-0000-0000\"}}";
		Long kakaoId = 123456789L;
		KakaoProfile.KakaoAccount.Profile profile = KakaoProfile.KakaoAccount.Profile.builder()
				.nickname("test nickname")
				.thumbnail_image_url("thumbnail img")
				.profile_image_url("profile img")
				.is_default_image(true).build();
		KakaoProfile.KakaoAccount kakaoAccount = KakaoProfile.KakaoAccount.builder()
				.profile_nickname_needs_agreement(false)
				.profile_image_needs_agreement(false)
				.profile(profile)
				.has_phone_number(true)
				.phone_number_needs_agreement(false)
				.phone_number("+82 10-0000-0000").build();
		KakaoProfile.Properties properties = KakaoProfile.Properties.builder()
				.nickname("test nickname")
				.profile_image("profile img")
				.thumbnail_image("thumbnail img").build();
		KakaoProfile kakaoProfile = KakaoProfile.builder().id(kakaoId).connected_at("2023-04-19T02:22:56Z").kakao_account(kakaoAccount).properties(properties).build();

		BDDMockito.given(restTemplate.exchange(
				eq("https://kapi.kakao.com/v2/user/me"),
				eq(HttpMethod.POST),
				any(HttpEntity.class),
				eq(String.class)
		)).willReturn(ResponseEntity.ok().body(kakaoProfileJson));

		BDDMockito.given(userRepository.findByKakaoId(kakaoId)).willReturn(null);
		BDDMockito.given(userRepository.countByNicknameStartingWith(any())).willReturn(1L);

		TokenDTO tokenDTO = TokenDTO.builder().accessToken("access token").refreshToken("refresh token").grantType("type").AuthorizationHeader("header").accessTokenExpiresIn(1234L).build();
		BDDMockito.given(jwtTokenProvider.generateTokenDto(anyString())).willReturn(tokenDTO);

		RefreshRedisTokenEntity refreshRedisToken = RefreshRedisTokenEntity.createToken(anyString(), tokenDTO.getRefreshToken());

		// when
		LoginUserInfoDTO ret = subject.saveUserOrLogin(token);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getUser().getNickname(), "test nickname2");
			Assertions.assertEquals(ret.getUser().getKakaoId(), 123456789L);
			Assertions.assertEquals(ret.getUser().getPhone(), "+82 10-0000-0000");
			Assertions.assertEquals(ret.getUser().getProfileUrl(), "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg");
			Assertions.assertEquals(ret.getUser().getStatus(), 1);
			Assertions.assertEquals(ret.getTokenDto().getAccessToken(), "access token");
			Assertions.assertEquals(ret.getTokenDto().getRefreshToken(), "refresh token");
		});

	}

	@Test
	@DisplayName("testResolveToken: Happy Case")
	public void testResolveToken() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		String header = "Authorization";
		String token = "Bearer testToken";
		request.addHeader(header, token);

		// when
		String ret = subject.resolveToken(request, header);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret, "testToken");
		});

	}

	@Test
	@DisplayName("testResolveToken: Sad Case")
	public void testResolveToken1() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		String header = "Authorization";
		String token = "testToken";
		request.addHeader(header, token);

		// when
		String ret = subject.resolveToken(request, header);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNull(ret);
		});

	}

	@Test
	@DisplayName("testReissueAccessToken: Happy Case")
	public void testReissueAccessToken() {
		// given
		TokenSO tokenSO = TokenSO.builder().accessToken("access token").refreshToken("refresh token").build();

		BDDMockito.given(jwtTokenProvider.validateToken(tokenSO.getRefreshToken())).willReturn(true);

		Authentication authentication = mock(Authentication.class);
		BDDMockito.given(jwtTokenProvider.getAuthentication(tokenSO.getAccessToken())).willReturn(authentication);

		String userId = "1";
		RefreshRedisTokenEntity refreshRedisToken = RefreshRedisTokenEntity.createToken(userId, "refresh token");
		BDDMockito.given(refreshRedisRepository.findById(authentication.getName())).willReturn(Optional.of(refreshRedisToken));

		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		TokenDTO tokenDTO = TokenDTO.builder().accessToken("newAccessToken").refreshToken("newRefreshToken").build();
		BDDMockito.given(jwtTokenProvider.generateTokenDto(authentication.getName())).willReturn(tokenDTO);

		// when
		TokenDTO ret = subject.reissueAccessToken(tokenSO);

		// then
		Assertions.assertAll("결괏값 검증", () -> {
			Assertions.assertNotNull(ret);
			Assertions.assertEquals(ret.getAccessToken(), "newAccessToken");
			Assertions.assertEquals(ret.getRefreshToken(), "newRefreshToken");
		});
	}

	@Test
	@DisplayName("testReissueAccessToken: Sad Case Invalid Refresh Token")
	public void testReissueAccessToken1() {
		// given
		TokenSO tokenSO = TokenSO.builder().accessToken("access token").refreshToken("refresh token").build();

		BDDMockito.given(jwtTokenProvider.validateToken(tokenSO.getRefreshToken())).willReturn(false);

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			TokenDTO ret = subject.reissueAccessToken(tokenSO);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testReissueAccessToken: Sad Case Refresh Token Not Found")
	public void testReissueAccessToken2() {
		// given
		TokenSO tokenSO = TokenSO.builder().accessToken("access token").refreshToken("refresh token").build();

		BDDMockito.given(jwtTokenProvider.validateToken(tokenSO.getRefreshToken())).willReturn(true);

		Authentication authentication = mock(Authentication.class);
		BDDMockito.given(jwtTokenProvider.getAuthentication(tokenSO.getAccessToken())).willReturn(authentication);

		String userId = "1";
		BDDMockito.given(refreshRedisRepository.findById(authentication.getName())).willReturn(Optional.empty());

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			TokenDTO ret = subject.reissueAccessToken(tokenSO);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.REFRESH_TOKEN_NOT_FOUND, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testReissueAccessToken: Sad Case Mismatch Refresh Token")
	public void testReissueAccessToken3() {
		// given
		TokenSO tokenSO = TokenSO.builder().accessToken("access token").refreshToken("refresh token").build();

		BDDMockito.given(jwtTokenProvider.validateToken(tokenSO.getRefreshToken())).willReturn(true);

		Authentication authentication = mock(Authentication.class);
		BDDMockito.given(jwtTokenProvider.getAuthentication(tokenSO.getAccessToken())).willReturn(authentication);

		String userId = "1";
		RefreshRedisTokenEntity refreshRedisToken = RefreshRedisTokenEntity.createToken(userId, "mismatch refresh token");
		BDDMockito.given(refreshRedisRepository.findById(authentication.getName())).willReturn(Optional.of(refreshRedisToken));

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			TokenDTO ret = subject.reissueAccessToken(tokenSO);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.MISMATCH_REFRESH_TOKEN, ((CustomException)throwable).getErrorCode());
		});
	}

}
