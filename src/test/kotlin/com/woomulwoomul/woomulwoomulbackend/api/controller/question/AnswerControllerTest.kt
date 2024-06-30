package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.service.question.AnswerService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllCategoryResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.AnswerFindAllResponse
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AnswerControllerTest : RestDocsSupport() {

    private val answerService = mock(AnswerService::class.java)

    override fun initController(): Any {
        return AnswerController(answerService)
    }

    @DisplayName("전체 답변 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllAnswers_thenReturn200() {
        // given
        `when`(answerService.getAllAnswers(anyLong(), any()))
            .thenReturn(PageData(
                listOf(
                    AnswerFindAllResponse(1L, 1L, "0F0F0F", listOf(
                        AnswerFindAllCategoryResponse(1L, "카테고리1"),
                        AnswerFindAllCategoryResponse(2L, "카테고리2"),
                        AnswerFindAllCategoryResponse(3L, "카테고리3"))),
                    AnswerFindAllResponse(2L, 2L, "0F0F0F", listOf(
                        AnswerFindAllCategoryResponse(1L, "카테고리1"),
                        AnswerFindAllCategoryResponse(2L, "카테고리2"),
                        AnswerFindAllCategoryResponse(3L, "카테고리3"))),
                    AnswerFindAllResponse(3L, 3L, "0F0F0F", listOf(
                        AnswerFindAllCategoryResponse(1L, "카테고리1"),
                        AnswerFindAllCategoryResponse(2L, "카테고리2"),
                        AnswerFindAllCategoryResponse(3L, "카테고리3")))
                ),
                10L
            ))

        // when & then
        mockMvc.perform(
            get("/api/users/{user-id}/answers", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .queryParam("page-from", "0")
                .queryParam("page-size", "3")
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/get-all-answers",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("total").type(JsonFieldType.NUMBER)
                            .description("총 갯수"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("데이터들"),
                        fieldWithPath("data[].answerId").type(JsonFieldType.NUMBER)
                            .description("답변 ID"),
                        fieldWithPath("data[].questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data[].backgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data[].categories").type(JsonFieldType.ARRAY)
                            .description("카테고리"),
                        fieldWithPath("data[].categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data[].categories[].name").type(JsonFieldType.STRING)
                            .description("카테고리명"),
                    ),
                    queryParameters(
                        parameterWithName("page-from").description("페이지 시작점").optional(),
                        parameterWithName("page-size").description("페이지 크기").optional()
                    ),
                )
            )
    }
}