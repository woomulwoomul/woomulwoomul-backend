package com.woomulwoomul.clientapi.service.user

import com.woomulwoomul.clientapi.controller.user.request.UserValidateNicknameRequest
import com.woomulwoomul.clientapi.service.s3.S3Service
import com.woomulwoomul.clientapi.service.user.request.UserProfileUpdateServiceRequest
import com.woomulwoomul.clientapi.service.user.response.UserGetProfileResponse
import com.woomulwoomul.clientapi.service.user.response.UserProfileUpdateResponse
import com.woomulwoomul.core.common.constant.ExceptionCode.*
import com.woomulwoomul.core.common.constant.ServiceConstants.UNAVAILABLE_NICKNAMES
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.domain.follow.FollowRepository
import com.woomulwoomul.core.domain.user.UserRepository
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
    private val followRepository: FollowRepository,
) {

    /**
     * 회원 프로필 조회
     * @param userId 회원 ID
     * @throws USER_NOT_FOUND 404
     * @return 회원 프로필 조회 응답
     */
    fun getUserProfile(userId: Long) : UserGetProfileResponse {
        val user = userRepository.findByUserId(userId) ?: throw CustomException(USER_NOT_FOUND)

        return UserGetProfileResponse(user)
    }

    /**
     * 회원 프로필 업데이트
     * @param userId 회원 ID
     * @throws USER_NICKNAME_SIZE_INVALID 400
     * @throws USER_IMAGE_URL_SIZE_INVALID 400
     * @throws USER_INTRODUCTION_SIZE_INVALID 400
     * @throws USER_NOT_FOUND 404
     * @return 회원 프로필 업데이트 응답
     */
    @Transactional
    fun updateUserProfile(userId: Long, @Valid request: UserProfileUpdateServiceRequest): UserProfileUpdateResponse {
        val user = userRepository.findByUserId(userId) ?: throw CustomException(USER_NOT_FOUND)

        user.updateProfile(request.userNickname, request.userImageUrl, request.userIntroduction)

        return UserProfileUpdateResponse(user)
    }

    /**
     * 회원 이미지 업로드
     * @param userId 회원 ID
     * @param file 파일
     * @throws FILE_FIELD_REQUIRED 400
     * @throws IMAGE_TYPE_UNSUPPORTED 415
     * @throws SERVER_ERROR 500
     * @return 회원 이미지 업로드 응답
     */
    @Transactional
    fun uploadImage(userId: Long, file: MultipartFile?): String {
        return s3Service.uploadFile(file, "users/$userId", UUID.randomUUID().toString())
    }

    /**
     * 닉네임 검증
     * @param request 닉네임 검증 요청
     * @throws NICKNAME_SIZE_INVALID 400
     * @throws NICKNAME_FORMAT_INVALID 400
     * @throws UNAVAILABLE_NICKNAME 409
     * @throws EXISTING_NICKNAME 409
     */
    fun validateNickname(@Valid request: UserValidateNicknameRequest) {
        if (request.nickname in UNAVAILABLE_NICKNAMES.fields) throw CustomException(UNAVAILABLE_NICKNAME)

        if (userRepository.existsByNickname(request.nickname)) throw CustomException(EXISTING_NICKNAME)
    }
}