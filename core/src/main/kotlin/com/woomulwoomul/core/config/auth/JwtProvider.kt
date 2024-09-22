package com.woomulwoomul.core.config.auth

import arrow.core.Either
import arrow.core.getOrElse
import com.woomulwoomul.core.common.constant.CustomCookies
import com.woomulwoomul.core.common.constant.CustomHttpHeaders
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.domain.user.*
import io.github.nefilim.kjwt.*
import jakarta.servlet.http.Cookie
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtProvider(
    @Value("\${api.jwt.secret}")
    private var secret: String,

    @Value("\${api.jwt.domain}")
    private var domain: String,

    @Value("\${api.jwt.time.access}")
    private var accessTokenTime: Long,

    @Value("\${api.jwt.time.refresh}")
    private var refreshTokenTime: Long,

    private val userRoleRepository: UserRoleRepository,
    private val userLoginRepository: UserLoginRepository,
) {

    companion object {
        const val TOKEN_PREFIX: String = "Bearer "
    }

    /**
     * JWT 토큰 헤더 생성
     * @param userId 회원 ID
     * @throws USER_NOT_FOUND 404
     * @throws SERVER_ERROR 500
     * @return HTTP 헤더
     */
    fun createTokenHeaders(userId: Long): HttpHeaders {
        val userAndUserRoles = getUserAndUserRoles(userId)
        val userDetails = getUserDetails(userAndUserRoles.first, userAndUserRoles.second)
        val time = System.currentTimeMillis()
        val authorities = userDetails.authorities.map { it.authority }

        logUserLogin(userAndUserRoles.first)

        val headers = HttpHeaders()

        generateToken(userDetails, time, accessTokenTime, authorities)
            .sign(secret)
            .fold(
                { throw CustomException(SERVER_ERROR) },
                { signedJWT ->
                    headers.add(HttpHeaders.AUTHORIZATION, signedJWT.rendered) }
            )

        if (authorities.contains(Role.MASTER.name))
            return headers

        generateToken(userDetails, time, refreshTokenTime, authorities)
            .sign(secret)
            .fold(
                { throw CustomException(SERVER_ERROR) },
                { signedJWT ->
                    headers.add(CustomHttpHeaders.REFRESH_TOKEN, signedJWT.rendered) }
            )

        return headers
    }

    /**
     * JWT 토큰 쿠키 생성
     * @param userId 회원 ID
     * @throws USER_NOT_FOUND 404
     * @throws SERVER_ERROR 500
     * @return HTTP 헤더
     */
    fun createTokenCookies(userId: Long): Array<Cookie> {
        val userAndUserRoles = getUserAndUserRoles(userId)
        val userDetails = getUserDetails(userAndUserRoles.first, userAndUserRoles.second)
        val time = System.currentTimeMillis()
        val authorities = userDetails.authorities.map { it.authority }

        logUserLogin(userAndUserRoles.first)

        val accessTokenCookie = generateToken(userDetails, time, accessTokenTime, authorities)
            .sign(secret)
            .fold(
                { throw CustomException(SERVER_ERROR) }, 
                { signedJWT -> Cookie(CustomCookies.ACCESS_TOKEN, signedJWT.rendered) }
            ).apply {
                path = "/"
                maxAge = (accessTokenTime / 1000).toInt()
            }

        if (authorities.contains(Role.MASTER.name))
            return arrayOf(accessTokenCookie)

        val refreshTokenCookie = generateToken(userDetails, time, refreshTokenTime, authorities)
            .sign(secret)
            .fold(
                { throw CustomException(SERVER_ERROR) }, 
                { signedJWT -> Cookie(CustomCookies.REFRESH_TOKEN, signedJWT.rendered) }
            ).apply {
                path = "/"
                maxAge = (refreshTokenTime / 1000).toInt()
        }

        return arrayOf(accessTokenCookie, refreshTokenCookie)
    }

    /**
     * JWT 토큰 검증
     * @param token 토큰
     * @throws TOKEN_UNAUTHENTICATED 401
     * @throws TOKEN_UNAUTHORIZED 403
     */
    fun verifyToken(token: String) {
        val jwt = verifySignature<JWSHMAC256Algorithm>(token.removePrefix(TOKEN_PREFIX), secret)

        return jwt.fold(
            { throw CustomException(TOKEN_UNAUTHENTICATED) },
            {
                val userAndUserRoles = getUserAndUserRoles(it.subject()
                    .getOrElse { throw CustomException(TOKEN_UNAUTHORIZED) }
                    .toLong())

                val user = getUserDetails(userAndUserRoles.first, userAndUserRoles.second)

                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    user.username,
                    user.password,
                    user.authorities
                )
            }
        )
    }

    /**
     * JWT 토큰 검증
     * @param token 토큰
     * @throws TOKEN_UNAUTHORIZED 403
     * @return 토큰 검증 결과
     */
    fun isValidToken(token: String): Boolean {
        return when (val jwt = verifySignature<JWSHMAC256Algorithm>(token.removePrefix(TOKEN_PREFIX), secret)) {
            is Either.Left -> false
            is Either.Right -> {
                val userAndUserRoles = getUserAndUserRoles(jwt.value.subject()
                    .getOrElse { throw CustomException(TOKEN_UNAUTHORIZED) }
                    .toLong())

                val user = getUserDetails(userAndUserRoles.first, userAndUserRoles.second)

                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    user.username,
                    user.password,
                    user.authorities
                )

                true
            }
        }
    }

    /**
     * 토큰 해독
     * @param token 토큰
     * @throws TOKEN_UNAUTHENTICATED 401
     * @return 해독 JWT 토큰
     */
    fun decodeToken(token: String): DecodedJWT<JWSHMAC256Algorithm> {
        require(token.isEmpty()) {
            throw CustomException(TOKEN_UNAUTHENTICATED)
        }

        return when (val decodedJwt = JWT.decodeT(token.removePrefix(TOKEN_PREFIX), JWSHMAC256Algorithm)) {
            is Either.Left -> throw CustomException(TOKEN_UNAUTHENTICATED)
            is Either.Right -> decodedJwt.value
        }
    }

    /**
     * 토큰 제작
     * @param userDetails 회원 정보
     * @param currentTime 현재 시간
     * @param tokenTime 토큰 시간
     * @param authorities 회원 권한들
     * @return JWT 토큰
     */
    private fun generateToken(userDetails: User, currentTime: Long, tokenTime: Long, authorities: List<String>):
            JWT<JWSHMAC256Algorithm> {
        return JWT.hs256 {
            subject(userDetails.username)
            issuedAt(Instant.ofEpochMilli(currentTime))
            expiresAt(Instant.ofEpochMilli(currentTime + tokenTime))
            issuer(domain)
            claim("roles", authorities)
        }
    }

    /**
     * 회원 정보 조회
     * @param user 회원
     * @param userRoles 회원 권한
     * @return 회원 정보
     */
    private fun getUserDetails (user: UserEntity, userRoles: List<UserRoleEntity>): User {
        val grantedAuthorities = userRoles.stream()
            .map { SimpleGrantedAuthority(it.role.name) }
            .toList()

        return User(user.id.toString(), "", grantedAuthorities)
    }

    /**
     * 회원 르고인 로깅
     * @param user 회원
     */
    private fun logUserLogin(user: UserEntity) {
        userLoginRepository.save(UserLoginEntity(user = user))
    }

    /**
     * 회원 및 회원 권한 조회
     * @param userId 회원 ID
     * @throws USER_NOT_FOUND 404
     * @return 회원 및 회원 권한
     */
    private fun getUserAndUserRoles(userId: Long): Pair<UserEntity, List<UserRoleEntity>> {
        val userRoles = userRoleRepository.findAllFetchUser(userId)

        val user = userRoles.stream()
            .findFirst()
            .orElseThrow{ CustomException(USER_NOT_FOUND) }
            .user

        return Pair(user, userRoles)
    }
}
