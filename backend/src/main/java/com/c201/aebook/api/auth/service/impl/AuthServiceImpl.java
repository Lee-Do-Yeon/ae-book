package com.c201.aebook.api.auth.service.impl;

import com.c201.aebook.api.auth.service.AuthService;
import com.c201.aebook.api.common.LoginUserInfoDTO;
import com.c201.aebook.api.common.TokenDTO;
import com.c201.aebook.api.user.persistence.entity.RefreshRedisTokenEntity;
import com.c201.aebook.api.user.persistence.entity.UserEntity;
import com.c201.aebook.api.user.persistence.repository.RefreshRedisRepository;
import com.c201.aebook.api.user.persistence.repository.UserRepository;
import com.c201.aebook.api.vo.TokenSO;
import com.c201.aebook.auth.dto.KakaoTokenDTO;
import com.c201.aebook.auth.profile.KakaoProfile;
import com.c201.aebook.config.jwt.JwtProperties;
import com.c201.aebook.config.jwt.JwtTokenProvider;
import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${oauth2.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${oauth2.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final UserRepository userRepository;
    private final RefreshRedisRepository refreshRedisRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RestTemplate restTemplate;

    /**
     * 인가코드로 요청하여 카카오 AccessToken을 발급 받는 기능
     * @param code
     * @return KakaoTokenDto
     * */
    @Override
    public KakaoTokenDTO getAccessToken(String code) {
        // 1. Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 2. 카카오 Access token을 발급 받기 위해 카카오 API key 및 Redirect Uri 입력
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId); //REST API KEY
        params.add("redirect_uri", kakaoRedirectUri); //REDIRECT URI
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // 3. 카카오 Access token 발급 받기
        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // 4. 발급 받은 카카오 Access token 정보 중 필요한 정보만 KakaoTokenDto에 저장
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenDTO kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoTokenDto;
    }

    /**
     * 카카오 Access token으로 유저 정보 요청하기
     * @param token
     * @return KakaoProfile
     * */
    @Override
    public KakaoProfile findProfile(String token) {
        // 1. Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        // 2. Http 요청 (POST 방식) 후, response 변수에 유저 프로필 정보를 받기
        ResponseEntity<String> kakaoProfileResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        // 3. 받은 유저 프로필 정보 중 필요한 정보만 KakaoProfile에 저장
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoProfile;
    }

    /**
     * 회원가입 또는 로그인 기능
     * DB에 정보가 없는 회원은 회원가입을 하고, 정보가 있다면 로그인을 진행
     * @param token
     * @return loginUserInfo
     * */
    @Override
    @Transactional
    public LoginUserInfoDTO saveUserOrLogin(String token) {
        KakaoProfile profile = findProfile(token);

        // 1. 카카오 아이디로 유저 정보 찾기
        UserEntity user = userRepository.findByKakaoId(profile.getId());

        // 2. 유저 정보가 없다면
        if(user == null) {
            // 2-1. 유저 닉네임 중복 검사
            Long userCnt = userRepository.countByNicknameStartingWith(profile.getKakao_account().getProfile().getNickname());
            if (userCnt++ > 0) {
                profile.getKakao_account().getProfile().setNickname(profile.getKakao_account().getProfile().getNickname() + userCnt);
            };

            // 2-2. DB에 유저 정보 저장
            user = UserEntity.builder()
                    .kakaoId(profile.getId())
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .phone(profile.getKakao_account().getPhone_number())
                    .profileUrl(profile.getKakao_account().getProfile().getProfile_image_url())
                    .status(1)
                    .build();

            userRepository.save(user);
        }

        // 3. 유저의 token 발급
        TokenDTO tokenDto = jwtTokenProvider.generateTokenDto(String.valueOf(user.getId()));

        // 4. Redis에 refreshToken 저장
        RefreshRedisTokenEntity newRedisToken = RefreshRedisTokenEntity.createToken(String.valueOf(user.getId()), tokenDto.getRefreshToken());
        refreshRedisRepository.save(newRedisToken);

        // 5. 유저 정보와 토큰 정보를 함께 전달
        LoginUserInfoDTO loginUserInfoDto = LoginUserInfoDTO.builder()
                .user(user)
                .tokenDto(tokenDto)
                .build();

        return loginUserInfoDto;
    }

    @Override
    public String resolveToken(HttpServletRequest request, String header) {
        // 1. Header에서 token 정보 가져오기
        String bearerToken = request.getHeader(header);

        // 2. Token이 null이 아니거나 Bearer로 시작하면 token 정보만 리턴
        if (bearerToken != null && bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtProperties.TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    @Transactional
    public TokenDTO reissueAccessToken(TokenSO tokenSO) {
        // 1. refresh token 검증해서 false라면 유효하지 않은 token
        if (!jwtTokenProvider.validateToken(tokenSO.getRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 토큰 인증 및 사용자 정보를 authentication로 담기
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenSO.getAccessToken());

        // 3. 유저를 찾아서 가지고 있는지 확인해서 없다면 에러 발생
        RefreshRedisTokenEntity refreshRedisToken = refreshRedisRepository.findById(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 4. refresh token이 유저에 저장된 refresh token과 다르면 에러 발생
        if (!refreshRedisToken.getToken().equals(tokenSO.getRefreshToken())) {
            throw new CustomException(ErrorCode.MISMATCH_REFRESH_TOKEN);
        }

        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        logoutValueOperations.set(tokenSO.getAccessToken(), tokenSO.getAccessToken());

        // 5. 토큰 새로 생성
        TokenDTO tokenDTO = jwtTokenProvider.generateTokenDto(authentication.getName());

        // 6. Redis에 refreshToken 저장
        RefreshRedisTokenEntity newRedisToken = RefreshRedisTokenEntity.createToken(authentication.getName(), tokenDTO.getRefreshToken());
        refreshRedisRepository.save(newRedisToken);

        return tokenDTO;
    }

}
