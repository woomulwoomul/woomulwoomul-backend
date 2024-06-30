package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.api.service.user.request.UserProfileUpdateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetProfileResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserProfileUpdateResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {

    /**
     * 회원 프로필 조회
     * @param userId 회원 ID
     * @throws USER_NOT_FOUND 404
     * @return 회원 프로필 조회 응답
     */
    fun getUserProfile(userId: Long) : UserGetProfileResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        return UserGetProfileResponse(user)
    }

    /**
     * 회원 프로필 업데이트
     * @param userId 회원 ID
     * @throws USER_NOT_FOUND 404
     * @return 회원 프로필 업데이트 응답
     */
    @Transactional
    fun updateUserProfile(userId: Long, @Valid request: UserProfileUpdateServiceRequest): UserProfileUpdateResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        user.updateProfile(request.userNickname, request.userImageUrl, request.userIntroduction)

        return UserProfileUpdateResponse(user)
    }
}