package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.api.service.user.request.UserProfileUpdateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.*
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.USER
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest(
    @Autowired private val userService: UserService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
) {

    @DisplayName("회원 프로필 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetUserProfile_thenReturn() {
        // given
        val userRole = createAndSaveUserRole(USER)

        // when
        val response = userService.getUserProfile(userRole.user.id!!)

        // then
        assertThat(response)
            .extracting("userId", "nickname", "imageUrl", "introduction")
            .containsExactly(userRole.user.id!!, userRole.user.nickname, userRole.user.imageUrl,
                userRole.user.introduction)
    }

    @DisplayName("존재하지 않은 회원 ID로 회원 프로필 조회를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUserId_whenGetUserProfile_thenThrow() {
        // given
        val userId = Long.MAX_VALUE

        // when & then
        assertThatThrownBy { userService.getUserProfile(userId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("회원 프로필 업데이트가 정상 작동한다")
    @Test
    fun givenValid_whenUpdateUserProfile_thenReturn() {
        // given
        val userRole = createAndSaveUserRole(USER)

        val request = createValidUserProfileUpdateServiceRequest()

        // when
        val response = userService.updateUserProfile(userRole.user.id!!, request)

        // then
        assertThat(response)
            .extracting("userId", "nickname", "imageUrl", "introduction")
            .containsExactly(userRole.user.id!!, userRole.user.nickname, request.userImageUrl, request.userIntroduction)
    }

    @DisplayName("존재하지 않은 회원 ID로 회원 프로필 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenUpdateUserProfile_thenThrow() {
        // given
        val userId = 1L
        val request = createValidUserProfileUpdateServiceRequest()

        // when & then
        assertThatThrownBy { userService.updateUserProfile(userId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    private fun createValidUserProfileUpdateServiceRequest(): UserProfileUpdateServiceRequest {
        return UserProfileUpdateServiceRequest("woomul", "https://www.google.com", "")
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