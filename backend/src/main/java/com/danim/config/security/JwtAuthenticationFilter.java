package com.danim.config.security;

import com.danim.exception.BaseException;
import com.danim.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        log.info("[doFilterInternal] token 값 추출 완료");
        log.info("accessToken : {}", accessToken);
        log.info("refreshToken : {}", refreshToken);
        // 유효한 토큰인지 확인합니다.
        if (accessToken != null) {
            Authentication authentication;
            // 엑세스 토큰이 유효한 상황
            if (jwtTokenProvider.validateToken(accessToken)) {
//                log.info("accessToken 만료 안됨");
                try {
                    this.setAuthentication(accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.info("accessToken 만료 및 refreshToken 없음");
//                throw new BaseException(ErrorMessage.UNAUTHORIZED_ACCESSTOKEN);
            }
        } else if (refreshToken != null) {
            // 엑세스 토큰이 만료된 상황 | 리프레시 토큰 존재하는 상황
            log.info("accessToken 만료된 상황");
            // 재발급 후, 컨텍스트에 다시 넣기
            /// 리프레시 토큰 검증
            boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);
            /// 리프레시 토큰 저장소 존재유무 확인
            boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);

            log.info("validateRefreshToken, {}", validateRefreshToken);
            log.info("isRefreshToken, {}", isRefreshToken);
            if (validateRefreshToken && isRefreshToken) {
                /// 리프레시 토큰으로 정보 가져오기
                String clientid = jwtTokenProvider.getUsername(refreshToken);
                log.info("clientId : {}", clientid);
                /// 권한정보 받아오기
                String role = jwtTokenProvider.getRole(clientid);
                log.info("role : {}", role);
                /// 토큰 발급
                String newAccessToken = jwtTokenProvider.recreateToken(clientid, role);
                log.info("newAccessToken : {}", newAccessToken);
                /// 헤더에 억세스 토큰 추가
                jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
                /// 컨텍스트에 넣기
                try {
                    this.setAuthentication(newAccessToken);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
    // SecurityContext 에 Authentication 객체 저장
    public void setAuthentication(String token) throws Exception {
        // 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthenthication(token);
        // SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
