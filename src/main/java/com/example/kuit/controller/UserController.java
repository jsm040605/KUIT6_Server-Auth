package com.example.kuit.controller;

import com.example.kuit.dto.response.AdminResponse;
import com.example.kuit.dto.response.ProfileResponse;
import com.example.kuit.jwt.JwtUtil;
import com.example.kuit.model.Role;
import com.example.kuit.model.TokenType;
import com.example.kuit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // GET /api/users/me  - 나의 프로필 조회 API (인가 불필요)
    /**
     * 요청 형식
        Authorization 헤더: Bearer <Access Token>
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me(HttpServletRequest request) {

        String username = (String)request.getAttribute("username");

        ProfileResponse profile = userService.getProfile(username);

        return ResponseEntity.ok(profile);
    }

    // GET /api/users/admin - 관리자 확인 API (인가 필요)
    /**
     * 요청 형식
        Authorization 헤더: Bearer <Access Token>
     */
    @GetMapping("/admin")
    public ResponseEntity<AdminResponse> admin() {
        // TODO: 토큰 추출 - extractBearer 메서드 활용
        // TODO: 토큰 유효성 검사 - jwtUtil.validate 메서드 활용
        // TODO: 토큰 타입 검사 - jwtUtil.getTokenType 메서드 활용
        // TODO: 토큰으로부터 유저 Role 추출 - jwtUtil.getRole 메서드 활용
        // TODO: 관리자 권한 검사 - 토큰으로부터 추출한 Role 이 Role.ROLE_ADMIN 과 동일한지 검증 -> 6번 구현

        return ResponseEntity.ok(AdminResponse.ok());
    }

    //extractBearer : 요구사항 5번 구현으로 AuthInterceptor로 이동
}
