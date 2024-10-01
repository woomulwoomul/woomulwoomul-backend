package com.woomulwoomul.adminapi.service.user

import com.woomulwoomul.adminapi.service.user.response.UserFindAllUserResponse
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.response.PageData
import com.woomulwoomul.core.domain.user.UserLoginRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userLoginRepository: UserLoginRepository,
) {

    /**
     * 회원 전체 조회
     * @param pageOffsetRequest 페이징 오프셋 요청
     * @return 회원 전체 조회 응답
     */
    fun getAllUsers(pageOffsetRequest: PageOffsetRequest): PageData<UserFindAllUserResponse> {
        val userLogins = userLoginRepository.findAll(pageOffsetRequest)
        return PageData(userLogins.data.map { UserFindAllUserResponse(it.user, it) }, userLogins.total)
    }
}
