package com.moyeo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import com.moyeo.config.security.common.CustomAuthenticationEntryPoint;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //  CORS 해결을 위해 추가
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
		httpSecurity.cors().configurationSource(request -> {
			CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
			configuration.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowedOrigins(List.of("*"));

            return configuration;
		}); //  CORS 해결을 위해 추가

        // UI를 사용하는 것을 기본값으로 가진 시큐리티 설정을 비활성화합니다.
        httpSecurity.httpBasic().disable()
            .csrf().disable() // REST API에서는 CSRF 보안이 필요 없기 때문에 비활성화하는 로직
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 토큰으로 인증을 처리하며, 세션은 사용하지 않기 떄문에 STATELESS 설정
            .and()
            .authorizeRequests() //request에 대한 사용 권한을 체크
            .antMatchers("/api/auth/**").authenticated()
            .antMatchers("/api/login/**").permitAll()
            .antMatchers("/exception/**").permitAll()
                .antMatchers("/test/**").permitAll()

            .anyRequest().hasRole("USER")
            .and()
            .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
            .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
            // UsernamePasswordAuthenticationFilter 앞에 JwtAuthenticationFilter를 추가하겠다는 의미

        // httpSecurity.httpBasic().disable()
        //     .csrf().disable()
        //     .headers().frameOptions().disable();
    }

    // webSecurity는 HttpSecurity 앞단에 적용, 스프링 시큐리티의 영향권 밖에 있음. 즉, 인증과 인가가 적용되지 않는 리소스 접근에 대해서만 사용.
    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/v3/api-docs/**", "/swagger-resources/**",
                "/swagger-ui/**", "/webjars/**", "/swagger/**", "/sign-api/exception");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
