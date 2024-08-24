package com.woomulwoomul.clientserver.controller.follow

import com.woomulwoomul.clientserver.controller.RestDocsSupport
import com.woomulwoomul.clientserver.service.follow.FollowService
import com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.PageData
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FollowControllerTest : RestDocsSupport() {

    private val followService = mock(FollowService::class.java)

    override fun initController(): Any {
        return FollowController(followService)
    }

    @DisplayName("팔로잉 회원 전체 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllFollowing_thenReturn200() {
        // given
        val pageRequest = PageRequest.of(null, 5)

        `when`(followService.getAllFollowing(anyLong(), any()))
            .thenReturn(
                PageData(
                listOf(
                    com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse(
                        1L, 1L, "tester1",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
                    ),
                    com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse(
                        2L, 2L, "tester2",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
                    ),
                    com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse(
                        3L, 3L, "tester3",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
                    ),
                    com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse(
                        4L, 4L, "tester4",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
                    ),
                    com.woomulwoomul.clientserver.service.user.response.UserGetAllFollowingResponse(
                        5L, 5L, "tester5",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
                    )
                ),
                pageRequest.size
            )
            )

        // when & then
        mockMvc.perform(
            get("/api/following")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .queryParam("page-from", pageRequest.from.toString())
                .queryParam("page-size", pageRequest.size.toString())
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "follow/get-all-following",
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
                        fieldWithPath("total").type(JsonFieldType.NUMBER)
                            .description("총 갯수"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("데이터들"),
                        fieldWithPath("data[].followId").type(JsonFieldType.NUMBER)
                            .description("팔로우 ID"),
                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER)
                            .description("회원 ID"),
                        fieldWithPath("data[].userNickname").type(JsonFieldType.STRING)
                            .description("회원 닉네임"),
                        fieldWithPath("data[].userImageUrl").type(JsonFieldType.STRING)
                            .description("회원 이미지 URL"),
                    ),
                    queryParameters(
                        parameterWithName("page-from").description("페이지 시작점").optional(),
                        parameterWithName("page-size").description("페이지 크기").optional()
                    ),
                )
            )
    }

    @DisplayName("팔로우 삭제를 하면 200을 반환한다")
    @Test
    fun givenValid_whenDeleteFollow_thenReturn200() {
        // given
        val userId = 1L

        // when & then
        mockMvc.perform(
            delete("/api/follow")
                .header(AUTHORIZATION, "Bearer access-token")
                .queryParam("user-id", userId.toString())
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "follow/delete-follow",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    ),
                    queryParameters(parameterWithName("user-id").description("회원 ID"))
                )
            )
    }

    @DisplayName("회원 ID 미입력시 팔로우 삭제를 하면 400을 반환한다")
    @Test
    fun givenBlankUserId_whenDeleteFollow_thenReturn400() {
        // given
        val userId = null

        // when & then
        mockMvc.perform(
            delete("/api/follow")
                .header(AUTHORIZATION, "Bearer access-token")
                .queryParam("user-id", userId)
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "follow/delete-follow/user-id-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                )
            )
        queryParameters(parameterWithName("user-id").description("회원 ID"))
    }
}