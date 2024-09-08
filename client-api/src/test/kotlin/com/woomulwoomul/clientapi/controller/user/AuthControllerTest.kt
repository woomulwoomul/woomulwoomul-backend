package com.woomulwoomul.clientapi.controller.user

import com.woomulwoomul.clientapi.controller.RestDocsSupport
import com.woomulwoomul.core.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.woomulwoomul.core.config.auth.JwtProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerTest : RestDocsSupport() {

    private val jwtProvider = mock(JwtProvider::class.java)

    override fun initController(): Any {
        return AuthController(jwtProvider)
    }

    @DisplayName("토큰 재발급을 하면 200을 반환한다")
    @Test
    fun givenValid_whenRefreshToken_thenReturn200() {
        // given
        val headers = HttpHeaders()
        headers.add(AUTHORIZATION, "access-token")
        headers.add(REFRESH_TOKEN, "refresh-token")
        `when`(jwtProvider.createTokenHeaders(anyLong()))
            .thenReturn(headers)

        // when & then
        mockMvc.perform(
            get("/api/auth/token")
                .header(REFRESH_TOKEN, "Bearer refresh-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isOk)
            .andDo(
                document(
                    "auth/refresh-token",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(REFRESH_TOKEN).description("리프레시 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    ),
                    responseHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰"),
                        headerWithName(REFRESH_TOKEN).description("리프레시 토큰")
                    )
                )
            )
    }
}