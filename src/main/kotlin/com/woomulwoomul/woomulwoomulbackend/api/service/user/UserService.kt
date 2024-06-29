package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetProfileResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.question.QuestionAnswerRepository
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserVisitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {

    /**
     * 회원 프로필 조회
     * @param userId 회원 ID
     * @throws USER_NOT_FOUND 404
     * @return 회원 프로필 응답
     */
    fun getUserProfile(userId: Long) : UserGetProfileResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        return UserGetProfileResponse(user)
    }
}