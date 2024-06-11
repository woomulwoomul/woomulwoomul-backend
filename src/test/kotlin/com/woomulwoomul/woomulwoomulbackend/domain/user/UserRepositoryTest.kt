package com.woomulwoomul.woomulwoomulbackend.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
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

    @ParameterizedTest(name = "[{index}] 회원 이름 {0}로 회원 존재 여부를 조회하면 {1}를 반환한다")
    @MethodSource("providerExists")
    @DisplayName("회원 이름으로 회원 존재 여부 조회를 하면 정상 작동한다")
    fun givenProvider_whenExists_thenReturn(username: String, expected: Boolean) {
        // given
        if (expected) createAndSaveUser()

        // when
        val result = userRepository.exists(username)

        // then
        assertThat(result).isEqualTo(expected)

    }

    private fun createAndSaveUser(): UserEntity {
        return userRepository.save(UserEntity(
            username = "tester",
            email = "tester@woomulwoomul.com",
            imageUrl = "https://www.google.com"
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