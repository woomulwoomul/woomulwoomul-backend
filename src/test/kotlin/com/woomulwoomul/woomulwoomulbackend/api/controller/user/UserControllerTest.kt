package com.woomulwoomul.woomulwoomulbackend.api.controller.user

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.controller.user.request.UserProfileUpdateRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.user.UserService
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetProfileResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserProfileUpdateResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.USER_NICKNAME_SIZE_INVALID
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : RestDocsSupport() {

    private val userService = mock(UserService::class.java)

    override fun initController(): Any {
        return UserController(userService)
    }

    @DisplayName("회원 프로필 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetUserProfile_thenReturn200() {
        // given
        `when`(userService.getUserProfile(anyLong()))
            .thenReturn(UserGetProfileResponse(
                1L,
                "tester",
                "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                "우물우물"
            ))

        // when & then
        mockMvc.perform(
            get("/api/users/{user-id}", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "user/get-user-profile",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                            .description("회원 ID"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("회원 닉네임"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                            .description("회원 프로필 이미지"),
                        fieldWithPath("data.introduction").type(JsonFieldType.STRING)
                            .description("회원 소개").optional(),
                    ),
                )
            )
    }

    @DisplayName("회원 프로필 업데이트 하면 200을 반환한다")
    @Test
    fun givenValid_whenUpdateUserProfile_thenReturn200() {
        // given
        val request = createValidUserUpdateProfileRequest()

        `when`(userService.updateUserProfile(anyLong(), any()))
            .thenReturn(
                UserProfileUpdateResponse(
                1L,
                "tester",
                "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                "우물우물"
            )
            )

        // when & then
        mockMvc.perform(
            patch("/api/users")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "user/update-user-profile",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("userNickname").type(JsonFieldType.STRING)
                            .description("회원 닉네임"),
                        fieldWithPath("userImageUrl").type(JsonFieldType.STRING)
                            .description("회원 이미지 URL"),
                        fieldWithPath("userIntroduction").type(JsonFieldType.STRING)
                            .description("회원 소개"),
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                            .description("회원 ID"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("회원 닉네임"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                            .description("회원 프로필 이미지"),
                        fieldWithPath("data.introduction").type(JsonFieldType.STRING)
                            .description("회원 소개").optional(),
                    ),
                )
            )
    }

    @DisplayName("회원 닉네임 미입력시 회원 프로필 업데이트 하면 400을 반환한다")
    @Test
    fun givenBlankUserNickname_whenUpdateUserProfile_thenReturn400() {
        // given
        val request = createValidUserUpdateProfileRequest()
        request.userNickname = ""

        // when & then
        mockMvc.perform(
            patch("/api/users")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "user/update-user-profile/user-nickname-blank",
                    preprocessRequest(prettyPrint()),
                    requestFields(
                        fieldWithPath("userNickname").type(JsonFieldType.STRING)
                            .description("회원 닉네임"),
                        fieldWithPath("userImageUrl").type(JsonFieldType.STRING)
                            .description("회원 이미지 URL"),
                        fieldWithPath("userIntroduction").type(JsonFieldType.STRING)
                            .description("회원 소개"),
                    ),
                )
            )
    }

//    @DisplayName("회원 닉네임이 2자 미만일시 회원 프로필 업데이트 하면 400을 반환한다")
//    @Test
//    fun givenLesserThan2SizeUserNickname_whenUpdateUserProfile_thenReturn400() {
//        // given
//        val request = createValidUserUpdateProfileRequest()
//        request.userNickname = "a"
//
//        `when`(userService.updateUserProfile(anyLong(), any()))
//            .thenThrow(CustomException(USER_NICKNAME_SIZE_INVALID))
//
//        // when & then
//        mockMvc.perform(
//            patch("/api/users")
//                .header(AUTHORIZATION, "Bearer access-token")
//                .principal(mockPrincipal)
//                .contentType(APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(request))
//        ).andDo(print())
//            .andExpect(status().isBadRequest)
//            .andDo(
//                MockMvcRestDocumentation.document(
//                    "user/update-user-profile/user-nickname-size-lesser",
//                    preprocessRequest(prettyPrint()),
//                    requestFields(
//                        fieldWithPath("userNickname").type(JsonFieldType.STRING)
//                            .description("회원 닉네임"),
//                        fieldWithPath("userImageUrl").type(JsonFieldType.STRING)
//                            .description("회원 이미지 URL"),
//                        fieldWithPath("userIntroduction").type(JsonFieldType.STRING)
//                            .description("회원 소개"),
//                    ),
//                )
//            )
//    }

//    @DisplayName("회원 닉네임이 30자 초과일시 회원 프로필 업데이트 하면 400을 반환한다")
//    @Test
//    fun givenGreaterThan30SizeUserNickname_whenUpdateUserProfile_thenReturn400() {
//        // given
//        val request = createValidUserUpdateProfileRequest()
//        request.userNickname = "a".repeat(31)
//
//        // when & then
//        mockMvc.perform(
//            patch("/api/users")
//                .header(AUTHORIZATION, "Bearer access-token")
//                .principal(mockPrincipal)
//                .contentType(APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(request))
//        ).andDo(print())
//            .andExpect(status().isBadRequest)
//            .andDo(
//                MockMvcRestDocumentation.document(
//                    "user/update-user-profile/user-nickname-size-greater",
//                    preprocessRequest(prettyPrint()),
//                    requestFields(
//                        fieldWithPath("userNickname").type(JsonFieldType.STRING)
//                            .description("회원 닉네임"),
//                        fieldWithPath("userImageUrl").type(JsonFieldType.STRING)
//                            .description("회원 이미지 URL"),
//                        fieldWithPath("userIntroduction").type(JsonFieldType.STRING)
//                            .description("회원 소개"),
//                    ),
//                )
//            )
//    }

    @DisplayName("회원 이미지 URL 미입력시 회원 프로필 업데이트 하면 400을 반환한다")
    @Test
    fun givenBlankUserImageUrl_whenUpdateUserProfile_thenReturn400() {
        // given
        val request = createValidUserUpdateProfileRequest()
        request.userImageUrl = ""

        // when & then
        mockMvc.perform(
            patch("/api/users")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "user/update-user-profile/user-image-url-blank",
                    preprocessRequest(prettyPrint()),
                    requestFields(
                        fieldWithPath("userNickname").type(JsonFieldType.STRING)
                            .description("회원 닉네임"),
                        fieldWithPath("userImageUrl").type(JsonFieldType.STRING)
                            .description("회원 이미지 URL"),
                        fieldWithPath("userIntroduction").type(JsonFieldType.STRING)
                            .description("회원 소개"),
                    ),
                )
            )
    }

//    @DisplayName("회원 이미지 URL가 500자 초과일시 회원 프로필 업데이트 하면 400을 반환한다")
//    @Test
//    fun givenGreaterThan500UserImageUrl_whenUpdateUserProfile_thenReturn400() {
//        // given
//        val request = createValidUserUpdateProfileRequest()
//        request.userImageUrl = "a".repeat(501)
//
//        // when & then
//        mockMvc.perform(
//            patch("/api/users")
//                .header(AUTHORIZATION, "Bearer access-token")
//                .principal(mockPrincipal)
//                .contentType(APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(request))
//        ).andDo(print())
//            .andExpect(status().isBadRequest)
//            .andDo(
//                MockMvcRestDocumentation.document(
//                    "user/update-user-profile/user-image-url-size-greater",
//                    preprocessRequest(prettyPrint()),
//                    requestFields(
//                        fieldWithPath("userNickname").type(JsonFieldType.STRING)
//                            .description("회원 닉네임"),
//                        fieldWithPath("userImageUrl").type(JsonFieldType.STRING)
//                            .description("회원 이미지 URL"),
//                        fieldWithPath("userIntroduction").type(JsonFieldType.STRING)
//                            .description("회원 소개"),
//                    ),
//                )
//            )
//    }

//    @DisplayName("회원 소개글 30자 초과일시 회원 프로필 업데이트 하면 400을 반환한다")
//    @Test
//    fun givenSizeGreaterThan30UserImageUrl_whenUpdateUserProfile_thenReturn400() {
//        // given
//        val request = createValidUserUpdateProfileRequest()
//        request.userImageUrl = "a".repeat(31)
//
//        // when & then
//        mockMvc.perform(
//            patch("/api/users")
//                .header(AUTHORIZATION, "Bearer access-token")
//                .principal(mockPrincipal)
//                .contentType(APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(request))
//        ).andDo(print())
//            .andExpect(status().isBadRequest)
//            .andDo(
//                MockMvcRestDocumentation.document(
//                    "user/update-user-profile/user-introduction-size-greater",
//                    preprocessRequest(prettyPrint()),
//                    requestFields(
//                        fieldWithPath("userNickname").type(JsonFieldType.STRING)
//                            .description("회원 닉네임"),
//                        fieldWithPath("userImageUrl").type(JsonFieldType.STRING)
//                            .description("회원 이미지 URL"),
//                        fieldWithPath("userIntroduction").type(JsonFieldType.STRING)
//                            .description("회원 소개"),
//                    ),
//                )
//            )
//    }

    private fun createValidUserUpdateProfileRequest(): UserProfileUpdateRequest {
        return UserProfileUpdateRequest(
            "tester",
            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
            "우물우물"
        )
    }
}