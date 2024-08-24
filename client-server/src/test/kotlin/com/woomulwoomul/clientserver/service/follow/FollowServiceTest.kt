package com.woomulwoomul.clientserver.service.follow

import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.domain.base.ServiceStatus.USER_DEL
import com.woomulwoomul.core.domain.follow.FollowEntity
import com.woomulwoomul.core.domain.follow.FollowRepository
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class FollowServiceTest(
    @Autowired private val followService: FollowService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val followRepository: FollowRepository,
) {

    @DisplayName("팔로잉 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAllFollowing_thenReturn() {
        // given
        val users = listOf(
            createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"),
            createAndSaveUser("tester3", "tester3@woomulwoomul.com"),
            createAndSaveUser("tester4", "tester4@woomulwoomul.com"),
            createAndSaveUser("tester5", "tester5@woomulwoomul.com")
        )
        val follows = listOf(
            createAndSaveFollow(users[1], users[0]),
            createAndSaveFollow(users[2], users[0]),
            createAndSaveFollow(users[3], users[0]),
            createAndSaveFollow(users[4], users[0]),
        )

        val pageRequest = PageRequest.of(null, null)

        // when
        val foundFollows = followService.getAllFollowing(users[0].id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(foundFollows.total).isEqualTo(follows.size.toLong())
            },
            {
                assertThat(foundFollows.data)
                    .extracting("followId", "userId", "userNickname", "userImageUrl")
                    .containsExactly(
                        tuple(follows[3].id, users[4].id, users[4].nickname, users[4].imageUrl),
                        tuple(follows[2].id, users[3].id, users[3].nickname, users[3].imageUrl),
                        tuple(follows[1].id, users[2].id, users[2].nickname, users[2].imageUrl),
                        tuple(follows[0].id, users[1].id, users[1].nickname, users[1].imageUrl)
                    )
            }
        )
    }

    @DisplayName("팔로우 삭제가 정상 작동한다")
    @Test
    fun givenValid_whenDeleteFollow_thenReturn() {
        // given
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"))
        val follows = listOf(createAndSaveFollow(users[0], users[1]), createAndSaveFollow(users[1], users[0]))

        // when
        followService.deleteFollow(users[0].id!!, users[1].id!!)

        // then
        assertThat(follows)
            .extracting("status")
            .containsExactly(USER_DEL, USER_DEL)
    }

    private fun createAndSaveFollow(user: UserEntity, followerUser: UserEntity): FollowEntity {
        return followRepository.save(
            FollowEntity(user = user, followerUser = followerUser)
        )
    }

    private fun createAndSaveUser(
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserEntity {
        return userRepository.save(
            UserEntity(
                nickname = nickname,
                email = email,
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                introduction = "우물우물",
            )
        )
    }
}