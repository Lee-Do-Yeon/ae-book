package com.c201.aebook.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; //JWT 토큰 생성 관리
    private final RedisTemplate redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        try {
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e){
            log.error("ExpiredJwtException: {}", e.getMessage());
            throw new JwtException("토큰 기한이 만료");
        }catch (IllegalArgumentException e){
            log.error("IllegalArgumentException: {}", e.getMessage());
            throw new JwtException("유효하지 않은 토큰");
        }catch (SignatureException e){
            log.error("SignatureException: {}", e.getMessage());
            throw new JwtException("사용자 인증 실패");
        }

        filterChain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Header에서 key 이름이 Authorization인 것 가져와서 저장
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length()); // prefix를 제외한 token 정보를 return
        }
        return null;
    }

}
