package com.example.shoppingmall.global.common.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.shoppingmall.global.common.auth.jwt.JwtFilter;
import com.example.shoppingmall.global.common.auth.jwt.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil; // JWT 유틸 주입

	@Bean
	public JwtFilter jwtAuthenticationFilter() {
		// JwtAuthenticationFilter Bean 수동 등록 (생성자 주입)
		return new JwtFilter(jwtUtil);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			//  CSRF 토큰 비활성화 (JWT 기반 API에서는 보통 사용 안 함)
			.csrf(AbstractHttpConfigurer::disable)
			//  세션 사용하지 않도록 설정 (JWT 방식이기 때문)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			//  요청 권한 설정
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auths/**",
						"/api/users/signup",
						"/api/stores/**",
						"/api/reviews/**",
						"/api/bookmarks/**",
						"/api/boards/**",
						"/api/reports/**").permitAll() // 로그인/회원가입 등은 인증 필요 없음
				.anyRequest().authenticated() // 나머지는 인증 필요
			)
			//  JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
