package com.woomulwoomul.woomulwoomulbackend.config.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRole;
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRoleRepository;
import com.woomulwoomul.woomulwoomulbackend.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode.TOKEN_UNAUTHENTICATED;
import static com.woomulwoomul.woomulwoomulbackend.common.response.ExceptionCode.TOKEN_UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${api.jwt.secret}")
    private String secret;

    @Value("${api.jwt.domain}")
    private String domain;

    @Value("${api.jwt.time.access}")
    private long accessTokenTime;

    @Value("${api.jwt.time.refresh}")
    private long refreshTokenTime;

    private final UserRoleRepository userRoleRepository;
    private static final String tokenPrefix = "Bearer ";

    /**
     * JWT 토큰 검증
     * @param token 토큰
     * @param jwtType 토큰 타입
     * @throws TOKEN_UNAUTHENTICATED 401
     * @throws TOKEN_UNAUTHORIZED 403
     */
    public void verifyToken(String token, JwtType jwtType) {
        if (token == null || token.isBlank())
            throw new CustomException(TOKEN_UNAUTHENTICATED);

        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token.substring(tokenPrefix.length()));

        long validTime = decodedJWT.getExpiresAt().getTime() - decodedJWT.getIssuedAt().getTime();

        switch (jwtType) {
            case ACCESS:
                if (validTime != accessTokenTime) throw new CustomException(TOKEN_UNAUTHENTICATED);
                break;
            case REFRESH:
                if (validTime != refreshTokenTime) throw new CustomException(TOKEN_UNAUTHENTICATED);
                break;
            default:
                throw new CustomException(TOKEN_UNAUTHENTICATED);
        }

        User user = getUserDetails(Long.valueOf(decodedJWT.getId()));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(),
                user.getPassword(),
                user.getAuthorities()));
    }

    /**
     * 회원 정보 조회
     * @param userId 회원 식별자
     * @throws TOKEN_UNAUTHORIZED 403
     * @return 회원 정보
     */
    private User getUserDetails(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findAllFetchUser(userId);

        com.woomulwoomul.woomulwoomulbackend.domain.user.User user = userRoles.stream()
                .findFirst()
                .orElseThrow(() -> new CustomException(TOKEN_UNAUTHORIZED))
                .getUser();

        List<SimpleGrantedAuthority> grantedAuthorities = userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().name()))
                .toList();

        return new User(user.getId().toString(), "", grantedAuthorities);
    }
}
