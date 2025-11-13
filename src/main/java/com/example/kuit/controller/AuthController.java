package com.example.kuit.controller;
import com.example.kuit.dto.request.LoginRequest;
import com.example.kuit.dto.request.ReissueRequest;
import com.example.kuit.dto.response.LoginResponse;
import com.example.kuit.dto.response.ReissueResponse;
import com.example.kuit.jwt.JwtUtil;
import com.example.kuit.model.Role;
import com.example.kuit.model.TokenType;
import com.example.kuit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // POST /api/auth/login  - 로그인 API
    /**
     * 요청 형식
     1. 일반 유저 로그인
         {
            "username" : "member1",
            "password" : "pass1234"
         }

     2. 관리자 로그인
         {
         "username" : "admin1",
         "password" : "pass1234"
         }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // TODO: 로그인 성공시 RefreshToken 까지 발급해보기
        LoginResponse response = authService.login(request.username(), request.password());

        return ResponseEntity.ok(response);
    }

    // POST /api/auth/reissue  - 토큰 재발급 API
    /**
     * 요청 형식
        {
            "refreshToken": "<JWT_REFRESH_TOKEN>"
        }

        Bearer 접두사 붙일 필요 X
     */
    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(@RequestBody ReissueRequest request) {

        String token = request.refreshToken();
        // TODO: 토큰 유효성 검사 - jwtUtil.validate 메서드 활용
        if(!jwtUtil.validate(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다.");
        }
        // TODO: 토큰 타입 검사 - jwtUtil.getTokenType 메서드 활용
        if(jwtUtil.getTokenType(token) != TokenType.REFRESH) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 필요합니다.");
        }
        // TODO: reissue API 완성 - Role.ROLE_USER 는 임시값이므로 토큰으로부터 추출해서 넘겨주어야합니다.
        ReissueResponse response = authService.reissue(jwtUtil.getUsername(token), jwtUtil.getRole(token), request.refreshToken());

        return ResponseEntity.ok(response);
    }

}
