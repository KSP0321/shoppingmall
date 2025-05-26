package com.example.shoppingmall.global.common.auth.controller;

import com.example.shoppingmall.global.common.auth.dto.request.SignInRequestDto;
import com.example.shoppingmall.global.common.auth.dto.response.TokenResponse;
import com.example.shoppingmall.global.common.auth.service.AuthService;
import com.example.shoppingmall.global.common.enums.SuccessCode;
import com.example.shoppingmall.global.common.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin") //로그인
    public ResponseEntity<ApiResponseDto<TokenResponse>> signIn(@RequestBody SignInRequestDto requestDto) {

        TokenResponse signinDto = authService.signIn(requestDto);

        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.LOGIN_SUCCESS, signinDto));
    }

    @PostMapping("/logout") // 로그아웃
    public ResponseEntity<ApiResponseDto<Void>> logout(@RequestBody String refreshToken ) {

        authService.logout(refreshToken);

        return ResponseEntity.ok(ApiResponseDto.success(SuccessCode.LOGOUT_SUCCESS, null));
    }
}
