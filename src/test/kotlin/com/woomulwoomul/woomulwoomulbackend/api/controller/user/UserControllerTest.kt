package com.woomulwoomul.woomulwoomulbackend.api.controller.user

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.controller.user.request.UserProfileUpdateRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.user.UserService
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserGetProfileResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.user.response.UserProfileUpdateResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : RestDocsSupport() {

    private val userService = mock(UserService::class.java)

    override fun initController(): Any {
        return UserController(userService)
    }

    @DisplayName("회원 닉네임 검증을 하면 200을 반환한다")
    @Test
    fun givenValid_whenValidateNickname_thenReturn200() {
        // given
        val nickname = "tester"

        // when & then
        mockMvc.perform(
            get("/api/users/nickname")
                .queryParam("nickname", nickname)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "user/validate-nickname",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    ),
                    queryParameters(
                        parameterWithName("nickname")
                            .description("닉네임")
                    )
                )
            )
    }

    @DisplayName("닉네임 미입력시 회원 닉네임 검증을 하면 400을 반환한다")
    @Test
    fun givenBlankNickname_whenValidateNickname_thenReturn400() {
        // given
        val nickname = null

        // when & then
        mockMvc.perform(
            get("/api/users/nickname")
                .queryParam("nickname", nickname)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "user/validate-nickname/nickname-field-required",
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("nickname")
                            .description("닉네임")
                    )
                )
            )
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
            get("/api/users/{userId}", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
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
                document(
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
                document(
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
                document(
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

    @DisplayName("회원 이미지 업로드를 하면 200을 반환한다")
    @Test
    fun givenValid_whenUploadImage_thenReturn200() {
        // given
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        `when`(userService.uploadImage(anyLong(), Mockito.any()))
            .thenReturn("https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640")

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users/image")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .param("file", file.toString())
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "user/upload-image",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.STRING)
                            .description("데이터")
                    )
                )
            )
    }

    private fun createValidUserUpdateProfileRequest(): UserProfileUpdateRequest {
        return UserProfileUpdateRequest(
            "tester",
            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
            "우물우물"
        )
    }
}