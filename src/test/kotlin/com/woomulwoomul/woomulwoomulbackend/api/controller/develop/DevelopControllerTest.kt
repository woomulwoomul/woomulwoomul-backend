package com.woomulwoomul.woomulwoomulbackend.api.controller.develop

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.service.develop.DevelopService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
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
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "develop/health",
                    preprocessResponse(Preprocessors.prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
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
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "develop/reset",
                    preprocessResponse(Preprocessors.prettyPrint()),
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