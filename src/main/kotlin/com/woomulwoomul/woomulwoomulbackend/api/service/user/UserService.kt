package com.woomulwoomul.woomulwoomulbackend.api.service.user

import com.woomulwoomul.woomulwoomulbackend.api.service.s3.S3Service
import com.woomulwoomul.woomulwoomulbackend.api.service.user.request.UserProfileUpdateServiceRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetProfileResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserProfileUpdateResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserUploadImageResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Validated
@Transactional(readOnly = true)
class UserService(
    private val s3Service: S3Service,
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

    /**
     * 회원 이미지 업로드
     * @param userId 회원 식별자
     * @param file 파일
     * @throws FILE_FIELD_REQUIRED 400
     * @throws USER_NOT_FOUND 404
     * @throws IMAGE_TYPE_UNSUPPORTED 415
     * @throws SERVER_ERROR 500
     * @return 회원 이미지 업로드 응답
     */
    @Transactional
    fun uploadImage(userId: Long, file: MultipartFile?): String {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        return s3Service.uploadFile(file, "users/$userId", UUID.randomUUID().toString())
    }
}