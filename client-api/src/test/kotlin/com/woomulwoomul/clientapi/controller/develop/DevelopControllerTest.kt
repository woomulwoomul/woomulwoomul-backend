package com.woomulwoomul.clientapi.controller.develop

import com.woomulwoomul.clientapi.controller.RestDocsSupport
import com.woomulwoomul.clientapi.service.develop.DevelopService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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
}