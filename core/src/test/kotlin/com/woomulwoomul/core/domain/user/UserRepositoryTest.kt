package com.woomulwoomul.core.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRepositoryTest(
    @Autowired private val userRepository: UserRepository,
) {

    @ParameterizedTest(name = "[{index}] 회원 닉네임 {0}로 회원 존재 여부를 조회하면 {1}를 반환한다")
    @MethodSource("providerExists")
    @DisplayName("회원 이름으로 회원 존재 여부 조회를 하면 정상 작동한다")
    fun givenProvider_whenExistsByNickname_thenReturn(nickname: String, expected: Boolean) {
        // given
        if (expected) createAndSaveUser()

        // when
        val result = userRepository.existsByNickname(nickname)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @DisplayName("회원 ID로 회원 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindByUserId_thenReturn() {
        // given
        val user = createAndSaveUser()

        // when
        val result = userRepository.findByUserId(user.id!!)

        // then
        assertThat(result)
            .extracting("nickname", "email", "imageUrl", "status", "createDateTime", "updateDateTime")
            .containsExactly(user.nickname, user.email, user.imageUrl, user.status, user.createDateTime,
                user.updateDateTime)
    }

    @DisplayName("회원 닉네임으로 회원 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindByNickname_thenReturn() {
        // given
        val user = createAndSaveUser()

        // when
        val result = userRepository.findByNickname(user.nickname)

        // then
        assertThat(result)
            .extracting("nickname", "email", "imageUrl", "status", "createDateTime", "updateDateTime")
            .containsExactly(user.nickname, user.email, user.imageUrl, user.status, user.createDateTime,
                user.updateDateTime)
    }

    private fun createAndSaveUser(): UserEntity {
        return userRepository.save(UserEntity(
            nickname = "tester",
            email = "tester@woomulwoomul.com",
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        ))
    }

    companion object {
        @JvmStatic
        fun providerExists(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("tester", true),
                Arguments.of("notfound", false)
            )
        }
    }
}