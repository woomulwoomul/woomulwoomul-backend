package com.woomulwoomul.woomulwoomulbackend.api.service.develop

import com.woomulwoomul.woomulwoomulbackend.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.TESTER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.USER
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DevelopServiceTest(
    @Autowired private val developService: DevelopService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
) {

    @DisplayName("서버명 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetServerName_thenReturn() {
        // given
        val serverName = "Woomulwoomul Test"

        // when
        val gotServerName = developService.getServerName()

        // then
        assertThat(gotServerName).isEqualTo(serverName)
    }

    @DisplayName("테스터 토큰 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetTesterToken_thenReturn() {
        // given
        val testerId = 1L
        val userRole = createAndSaveUserRole(role = USER, nickname = "tester".plus(testerId))

        // when
        val headers = developService.getTesterToken(testerId)

        // then
        assertAll(
            {
                assertThat(headers[AUTHORIZATION]).isNotEmpty()
            },
            {
                assertThat(headers[REFRESH_TOKEN].toString()).isNotEmpty()
            }
        )
    }

    @DisplayName("존재하지 않은 테스터 ID로 테스터 토큰 조회를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingTesterId_whenGetTesterToken_thenThrow() {
        // given
        val testerId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { developService.getTesterToken(testerId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(TESTER_NOT_FOUND)
    }

    private fun createAndSaveUserRole(
        role: Role,
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserRoleEntity {
        val user = userRepository.save(
            UserEntity(
                nickname = nickname,
                email = email,
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                introduction = "우물우물",
            )
        )

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }
}