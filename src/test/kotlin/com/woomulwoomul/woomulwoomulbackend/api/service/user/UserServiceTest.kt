package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.api.controller.user.request.UserValidateNicknameRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.user.request.UserProfileUpdateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.*
import com.woomulwoomul.woomulwoomulbackend.common.constant.ServiceConstants.UNAVAILABLE_NICKNAMES
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.USER
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.stream.Stream

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

    @DisplayName("회원 닉네임이 2자 미만일 경우 회원 프로필 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenUserNicknameLesserThan2Size_whenUpdateUserProfile_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)

        val request = createValidUserProfileUpdateServiceRequest()
        request.userNickname = "a"

        // when & then
        assertThatThrownBy { userService.updateUserProfile(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(USER_NICKNAME_SIZE_INVALID.message)
    }

    @DisplayName("회원 닉네임이 30자 초과일 경우 회원 프로필 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenUserNicknameGreaterThan30Size_whenUpdateUserProfile_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)

        val request = createValidUserProfileUpdateServiceRequest()
        request.userNickname = "a".repeat(31)

        // when & then
        assertThatThrownBy { userService.updateUserProfile(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(USER_NICKNAME_SIZE_INVALID.message)
    }

    @DisplayName("회원 이미지 URL이 1자 미만일 경우 회원 프로필 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenUserImageUrlLesserThan1Size_whenUpdateUserProfile_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)

        val request = createValidUserProfileUpdateServiceRequest()
        request.userImageUrl = ""

        // when & then
        assertThatThrownBy { userService.updateUserProfile(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(USER_IMAGE_URL_SIZE_INVALID.message)
    }

    @DisplayName("회원 이미지 URL이 500자 초과일 경우 회원 프로필 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenUserImageUrlGreaterThan500Size_whenUpdateUserProfile_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)

        val request = createValidUserProfileUpdateServiceRequest()
        request.userImageUrl = "a".repeat(501)

        // when & then
        assertThatThrownBy { userService.updateUserProfile(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(USER_IMAGE_URL_SIZE_INVALID.message)
    }

    @DisplayName("회원 소개글이 60자 초과일 경우 회원 프로필 업데이트를 하면 예외가 발생한다")
    @Test
    fun givenUserIntroductionGreaterThan60Size_whenUpdateUserProfile_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)

        val request = createValidUserProfileUpdateServiceRequest()
        request.userIntroduction = "a".repeat(61)

        // when & then
        assertThatThrownBy { userService.updateUserProfile(userRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(USER_INTRODUCTION_SIZE_INVALID.message)
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

    @DisplayName("회원 이미지 업로드가 정상 작동한다")
    @Test
    fun givenValid_whenUploadImage_thenReturn() {
        // given
        val userRole = createAndSaveUserRole(USER)
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        // when
        val response = userService.uploadImage(userRole.user.id!!, file)

        // then
        assertThat(response).isNotNull()
    }

    @DisplayName("파일 없이 회원 이미지 업로드를 하면 예외가 발생한다")
    @Test
    fun givenNullFile_whenUploadImage_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)
        val file: MultipartFile? = null

        // when & then
        assertThatThrownBy { userService.uploadImage(userRole.user.id!!, file) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FILE_FIELD_REQUIRED)
    }

    @DisplayName("지원하지 않는 파일 타입으로 회원 이미지 업로드를 하면 예외가 발생한다")
    @Test
    fun givenUnsupportedImageType_whenUploadImage_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)
        val file = MockMultipartFile("file", "file.doc", "file/doc", ByteArray(1))

        // when & then
        assertThatThrownBy { userService.uploadImage(userRole.user.id!!, file) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(IMAGE_TYPE_UNSUPPORTED)
    }

    @DisplayName("닉네임 검증이 정상 작동한다")
    @Test
    fun givenValid_whenValidateNickname_thenReturn() {
        // given
        val request = UserValidateNicknameRequest("tester")

        // when
        userService.validateNickname(request)

        // then
        assertThat(userRepository.exists(request.nickname)).isFalse()
    }

    @DisplayName("2자 미만인 닉네임으로 닉네임 검증을 하면 예외가 발생한다")
    @Test
    fun givenLesserThan2Nickname_whenValidateNickname_thenThrow() {
        // given
        val request = UserValidateNicknameRequest("a")

        // when & then
        assertThatThrownBy { userService.validateNickname(request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(NICKNAME_SIZE_INVALID.message)
    }

    @DisplayName("10자 초과인 닉네임으로 닉네임 검증을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan10Nickname_whenValidateNickname_thenThrow() {
        // given
        val request = UserValidateNicknameRequest("a".repeat(11))

        // when & then
        assertThatThrownBy { userService.validateNickname(request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(NICKNAME_SIZE_INVALID.message)
    }

    @DisplayName("잘못된 포맷인 닉네임으로 닉네임 검증을 하면 예외가 발생한다")
    @Test
    fun givenPatternNickname_whenValidateNickname_thenThrow() {
        // given
        val request = UserValidateNicknameRequest("AAA")

        // when & then
        assertThatThrownBy { userService.validateNickname(request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .message()
            .asString()
            .contains(NICKNAME_PATTERN_INVALID.message)
    }


    @ParameterizedTest(name = "[{index}] {0}으로 닉네임 검증을 하면 예외가 발생한다")
    @MethodSource("providerValidateNickname")
    @DisplayName("사용 불가능한 닉네임으로 닉네임 검증을 하면 예외가 발생한다")
    fun givenPatternNickname_whenValidateNickname_thenThrow(nickname: String) {
        // given
        val request = UserValidateNicknameRequest(nickname)

        // when & then
        assertThatThrownBy { userService.validateNickname(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(UNAVAILABLE_NICKNAME)
    }

    @DisplayName("존재하는 닉네임으로 닉네임 검증을 하면 예외가 발생한다")
    @Test
    fun givenExistingNickname_whenValidateNickname_thenThrow() {
        // given
        val userRole = createAndSaveUserRole(USER)
        val request = UserValidateNicknameRequest(userRole.user.nickname)

        // when & then
        assertThatThrownBy { userService.validateNickname(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_NICKNAME)
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

    companion object {
        @JvmStatic
        fun providerValidateNickname(): Stream<Arguments> {
            return UNAVAILABLE_NICKNAMES.fields
                .map { Arguments.of(it) }
                .stream()
        }
    }
}