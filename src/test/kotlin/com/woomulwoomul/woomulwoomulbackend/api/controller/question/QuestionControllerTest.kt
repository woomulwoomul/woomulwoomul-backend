package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.service.question.QuestionService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindAllCategoryResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindCategoryResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.QuestionFindResponse
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

class QuestionControllerTest : RestDocsSupport() {

    private val questionService = mock(QuestionService::class.java)

    override fun initController(): Any {
        return QuestionController(questionService)
    }

    @DisplayName("기본 질문을 조회하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAdminQuestions_thenReturn200() {
        // given
        `when`(questionService.getDefaultQuestions(anyList()))
            .thenReturn(listOf(QuestionFindResponse(
                1L,
                "질문1",
                "0F0F0F",
                listOf(QuestionFindCategoryResponse(1L, "카테고리1"),
                    QuestionFindCategoryResponse(2L, "카테고리2"))
            ), QuestionFindResponse(
                2L,
                "질문2",
                "0F0F0F",
                listOf(QuestionFindCategoryResponse(1L, "카테고리1"),
                    QuestionFindCategoryResponse(2L, "카테고리2"))
            )))

        // when & then
        mockMvc.perform(
            get("/api/questions")
                .contentType(APPLICATION_JSON_VALUE)
                .queryParam("question-ids", "")
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "question/get-default-questions",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("데이터들"),
                        fieldWithPath("data[].questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data[].questionText").type(JsonFieldType.STRING)
                            .description("질문"),
                        fieldWithPath("data[].backgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data[].categories").type(JsonFieldType.ARRAY)
                            .description("카테고리"),
                        fieldWithPath("data[].categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data[].categories[].categoryName").type(JsonFieldType.STRING)
                            .description("카테고리명"),
                    ),
                    queryParameters(
                        parameterWithName("question-ids").description("질문 ID들")
                    ),
                )
            )
    }

    @DisplayName("전체 카테고리 조회하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllCategories_thenReturn200() {
        // given
        val pageFrom = 0L
        val pageSize = 20L
        val total = 100L

        `when`(questionService.getAllCategories(pageFrom, pageSize))
            .thenReturn(PageData(listOf(
                QuestionFindAllCategoryResponse(1L, "카테고리1"),
                QuestionFindAllCategoryResponse(2L, "카테고리2")), total))

        // when & then
        mockMvc.perform(
            get("/api/categories")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .queryParam("page-from", pageFrom.toString())
                .queryParam("page-size", pageSize.toString())
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "question/get-all-categories",
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
                        fieldWithPath("data[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data[].categoryName").type(JsonFieldType.STRING)
                            .description("카테고리"),
                    ),
                    queryParameters(
                        parameterWithName("page-from").description("페이지 시작점").optional(),
                        parameterWithName("page-size").description("페이지 크기").optional()
                    ),
                )
            )
    }
}