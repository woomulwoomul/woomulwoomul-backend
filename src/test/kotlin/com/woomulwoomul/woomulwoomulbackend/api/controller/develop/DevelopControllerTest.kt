package com.woomulwoomul.woomulwoomulbackend.api.controller.develop

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.service.develop.DevelopService
import com.woomulwoomul.woomulwoomulbackend.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DevelopControllerTest : RestDocsSupport() {

    private val developService = mock(DevelopService::class.java)

    override fun initController(): Any {
        return DevelopController(developService)
    }

    @DisplayName("헬스 체크를 하면 200을 반환한다")
    @Test
    fun givenValid_whenHealthCheck_thenReturn200() {
        // when & then
        mockMvc.perform(
            get("/api/health")
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "develop/health",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }

    @DisplayName("테스터 토큰을 조회하면 200을 반환한다")
    @Test
    fun givenValid_whenGetTesterToken_thenReturn200() {
        // given
        val testerId = 1L
        val headers = HttpHeaders().apply {
            add(AUTHORIZATION, "access-token")
            add(REFRESH_TOKEN, "refresh-token")
        }

        `when`(developService.getTesterToken(anyLong()))
            .thenReturn(headers)

        // when & then
        mockMvc.perform(
            get("/api/tester/{testerId}", testerId)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "develop/get-tester-token",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    ),
                    responseHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰"),
                        headerWithName(REFRESH_TOKEN).description("리프레시 토큰"),
                    )
                )
            )
    }

    @DisplayName("데이터 리셋을 하면 200을 반환한다")
    @Test
    fun givenValid_whenReset_thenReturn200() {
        // when & then
        mockMvc.perform(
            post("/api/reset")
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "develop/reset",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }
}