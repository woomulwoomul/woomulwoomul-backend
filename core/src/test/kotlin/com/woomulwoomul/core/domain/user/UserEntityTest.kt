package com.woomulwoomul.core.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserEntityTest {

    @DisplayName("회원 프로필 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdateProfile_thenReturn() {
        // given
        val user = createUserEntity()
        val nickname = "woomul"
        val imageUrl = "https://www.google.com"
        val introduction: String? = null

        // when
        user.updateProfile(nickname, imageUrl, introduction)

        // then
        assertThat(user)
            .extracting("nickname", "imageUrl", "introduction")
            .containsExactly(nickname, imageUrl, introduction)
    }

    private fun createUserEntity(): UserEntity {
        return UserEntity(
            1L,
            "tester",
            "tester@woomulwoomul.com",
            "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
            "우물우물",
        )
    }
}