package com.woomulwoomul.woomulwoomulbackend.domain.follow

import com.woomulwoomul.woomulwoomulbackend.domain.base.ServiceStatus.USER_DEL
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FollowEntityTest {

    @DisplayName("언팔로우가 정상 작동한다")
    @Test
    fun givenValid_whenUnfollow_thenReturn() {
        // given
        val users = listOf(createUser(1L, "tester1", "tester1@woomulwoomul.com"),
            createUser(2L, "tester2", "tester2@woomulwoomul.com"))
        val follow = createFollow(1L, users[0], users[1])

        // when
        follow.unfollow()

        // then
        assertThat(follow.status).isEqualTo(USER_DEL)
    }

    private fun createFollow(id: Long, user: UserEntity, followerUser: UserEntity): FollowEntity {
        return FollowEntity(id, user, followerUser)
    }

    private fun createUser(id: Long, nickname: String, email: String): UserEntity {
        return UserEntity(id, nickname, email, "", null)
    }
}