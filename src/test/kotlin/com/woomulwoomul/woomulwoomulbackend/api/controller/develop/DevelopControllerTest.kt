package com.woomulwoomul.woomulwoomulbackend.api.controller.develop

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.service.develop.DevelopService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class DevelopControllerTest : RestDocsSupport() {

    private val developService = mock(DevelopService::class.java)

    override fun initController(): Any {
        return DevelopController(developService)
    }

    @DisplayName("헬스 체크를 하면 200을 반환한다")
    @Test
    fun givenValid_whenHealthCheck_return200() {
        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/health")
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "develop/health",
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }
}