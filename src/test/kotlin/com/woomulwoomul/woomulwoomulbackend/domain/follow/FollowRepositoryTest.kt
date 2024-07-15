package com.woomulwoomul.woomulwoomulbackend.domain.follow

import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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

    @DisplayName("팔로워 회원 ID로 전체 팔로우 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAllByFollower_thenReturn() {
        // given
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"),
            createAndSaveUser("tester3", "tester3@woomulwoomul.com"),
            createAndSaveUser("tester4", "tester4@woomulwoomul.com"),
            createAndSaveUser("tester5", "tester5@woomulwoomul.com"))
        val follows = listOf(createAndSaveFollow(users[1], users[0]),
            createAndSaveFollow(users[2], users[0]),
            createAndSaveFollow(users[3], users[0]),
            createAndSaveFollow(users[4], users[0]))

        val pageRequest = PageRequest.of(follows[2].id, 2)

        // when
        val foundFollows = followRepository.findAllByFollower(users[0].id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(foundFollows.total).isEqualTo(follows.size.toLong())
            },
            {
                assertThat(foundFollows.data)
                    .extracting("id", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(follows[2].id, follows[2].status, follows[2].createDateTime, follows[2].updateDateTime),
                        tuple(follows[1].id, follows[1].status, follows[1].createDateTime, follows[1].updateDateTime)
                    )
            },
            {
                assertThat(foundFollows.data)
                    .extracting("user")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(users[3].id, users[3].nickname, users[3].email, users[3].imageUrl, users[3].introduction,
                            users[3].status, users[3].createDateTime, users[3].updateDateTime),
                        tuple(users[2].id, users[2].nickname, users[2].email, users[2].imageUrl, users[2].introduction,
                            users[2].status, users[2].createDateTime, users[2].updateDateTime)
                    )
            },
            {
                assertThat(foundFollows.data)
                    .extracting("followerUser")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(users[0].id, users[0].nickname, users[0].email, users[0].imageUrl, users[0].introduction,
                            users[0].status, users[0].createDateTime, users[0].updateDateTime),
                        tuple(users[0].id, users[0].nickname, users[0].email, users[0].imageUrl, users[0].introduction,
                            users[0].status, users[0].createDateTime, users[0].updateDateTime)
                    )
            }
        )
    }

    @DisplayName("팔로워 회원 ID로 팔로우가 없는데 전체 팔로우 조회를 하면 정상 작동한다")
    @Test
    fun givenEmpty_whenFindAllByFollower_thenReturn() {
        // given
        val user = createAndSaveUser("tester1", "tester1@woomulwoomul.com")
        val pageRequest = PageRequest.of(null, null)

        // when
        val foundFollows = followRepository.findAllByFollower(user.id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(foundFollows.total).isEqualTo(0L)
            },
            {
                assertThat(foundFollows.data).isEqualTo(emptyList<FollowEntity>())
            }
        )
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