package com.woomulwoomul.woomulwoomulbackend.domain.follow

import com.woomulwoomul.woomulwoomulbackend.domain.user.FollowEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.FollowRepository
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
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
class FollowRepositoryTest(
    @Autowired private val followRepository: FollowRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @ParameterizedTest(name = "[{index}] 회원 ID와 팔로워 회원 ID로 팔로우 존재 여부를 조회하면 {0}을 반환한다")
    @MethodSource("providerExists")
    @DisplayName("회원 ID와 팔로워 회원 ID로 팔로우 존재 여부 조회를 하면 정상 작동한다")
    fun givenValid_whenExists_thenReturn(expected: Boolean) {
        // given
        var userId1 = 1L
        var userId2 = 2L

        if (expected) {
            val user1 = createAndSaveUser("user1", "user1@woomul.com")
            val user2 = createAndSaveUser("user2", "user2@woomul.com")
            createAndSaveFollow(user1, user2)

            userId1 = user1.id!!
            userId2 = user2.id!!
        }

        // when
        val result = followRepository.exists(userId1, userId2)

        // then
        assertThat(result).isEqualTo(expected)
    }

    private fun createAndSaveFollow(user: UserEntity, followerUser: UserEntity): FollowEntity {
        return followRepository.save(
            FollowEntity(user = user, followerUser = followerUser)
        )
    }

    private fun createAndSaveUser(nickname: String, email: String): UserEntity {
        return userRepository.save(
            UserEntity(
            nickname = nickname,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        )
        )
    }

    companion object {
        @JvmStatic
        fun providerExists(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
            )
        }
    }
}