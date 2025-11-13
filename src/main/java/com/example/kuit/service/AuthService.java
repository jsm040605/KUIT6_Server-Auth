package com.example.kuit.service;

import com.example.kuit.dto.response.LoginResponse;
import com.example.kuit.dto.response.ReissueResponse;
import com.example.kuit.jwt.JwtUtil;
import com.example.kuit.model.RefreshToken;
import com.example.kuit.model.Role;
import com.example.kuit.model.User;
import com.example.kuit.repository.RefreshTokenRepository;
import com.example.kuit.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        if (!user.password().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(username, user.role().name());
        String refreshToken = jwtUtil.generateRefreshToken(username, user.role().name());

        Instant refreshExp = jwtUtil.getExpiration(refreshToken);

        refreshTokenRepository.deleteByUsername(username);

        refreshTokenRepository.save(new RefreshToken(username, refreshToken, refreshExp));

        return LoginResponse.of(accessToken, refreshToken);
    }

    public ReissueResponse reissue(String username, Role role, String refreshToken) {
        // TODO: DB에 RefreshToken 존재 여부 확인 - refreshTokenRepository.findByUsername 메서드 활용
        RefreshToken dbRefreshToken = refreshTokenRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다."));

        // TODO: DB에 저장되어있는 토큰의 만료 여부 검사 - refresh
        if(dbRefreshToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다.");
        }
        // TODO: DB에 저장되어있는 토큰과 요청으로 받은 토큰의 동일 여부 검사
        RefreshToken userRefreshToken = new RefreshToken(username, refreshToken, jwtUtil.getExpiration(refreshToken));
        if(!userRefreshToken.equals(dbRefreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 동일하지 않습니다.");
        }
        // TODO: AccessToken 재발급 & Refresh Token 재발급
        refreshTokenRepository.deleteByUsername(username);

        String newRefreshToken = jwtUtil.generateRefreshToken(username, role.getValue());
        refreshTokenRepository.save(new RefreshToken(jwtUtil.getUsername(newRefreshToken), newRefreshToken, jwtUtil.getExpiration(refreshToken)));

        return ReissueResponse.of(jwtUtil.generateAccessToken(username, role.getValue()), newRefreshToken);
    }
}
