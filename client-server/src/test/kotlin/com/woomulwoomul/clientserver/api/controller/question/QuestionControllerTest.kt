package com.woomulwoomul.clientserver.api.controller.question

import com.woomulwoomul.clientserver.api.controller.RestDocsSupport
import com.woomulwoomul.clientserver.api.controller.question.request.QuestionUserCreateRequest
import com.woomulwoomul.clientserver.api.service.question.QuestionService
import com.woomulwoomul.clientserver.api.service.question.response.*
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
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class QuestionControllerTest : RestDocsSupport() {

    private val questionService = mock(QuestionService::class.java)

    override fun initController(): Any {
        return QuestionController(questionService)
    }

    @DisplayName("기본 질문을 조회하면 200을 반환한다")
    @Test
    fun givenValid_whenGetDefaultQuestion_thenReturn200() {
        // given
        `when`(questionService.getDefaultQuestion(any()))
            .thenReturn(QuestionFindResponse(
                1L,
                "질문1",
                "0F0F0F",
                listOf(QuestionFindCategoryResponse(1L, "카테고리1"),
                    QuestionFindCategoryResponse(2L, "카테고리2"))
            ))

        // when & then
        mockMvc.perform(
            get("/api/questions")
                .contentType(APPLICATION_JSON_VALUE)
                .queryParam("question-id", "")
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "question/get-default-question",
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data.questionText").type(JsonFieldType.STRING)
                            .description("질문"),
                        fieldWithPath("data.backgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data.categories").type(JsonFieldType.ARRAY)
                            .description("카테고리"),
                        fieldWithPath("data.categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data.categories[].categoryName").type(JsonFieldType.STRING)
                            .description("카테고리명"),
                    ),
                    queryParameters(
                        parameterWithName("question-id").description("질문 ID")
                    ),
                )
            )
    }

    @DisplayName("전체 카테고리 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllCategories_thenReturn200() {
        // given
        val pageRequest = PageRequest.of(null, 2)

        `when`(questionService.getAllCategories(any()))
            .thenReturn(PageData(listOf(
                QuestionFindAllCategoryResponse(1L, "카테고리1"),
                QuestionFindAllCategoryResponse(2L, "카테고리2")
            ), pageRequest.size))

        // when & then
        mockMvc.perform(
            get("/api/categories")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .queryParam("page-from", pageRequest.from.toString())
                .queryParam("page-size", pageRequest.size.toString())
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "question/get-all-categories",
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
                        fieldWithPath("data[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING)
                            .description("카테고리명"),
                    ),
                    queryParameters(
                        parameterWithName("page-from").description("페이지 시작점").optional(),
                        parameterWithName("page-size").description("페이지 크기").optional()
                    ),
                )
            )
    }

    @DisplayName("회원 질문 생성을 하면 201을 반환한다")
    @Test
    fun givenValid_whenCreateUserQuestion_thenReturn201() {
        // given
        val request = createValidQuestionUserCreateRequest()

        `when`(questionService.createUserQuestion(anyLong(), any()))
            .thenReturn(QuestionUserCreateResponse(
                1L,
                request.questionText!!,
                request.questionBackgroundColor!!,
                request.categoryIds!!.map { QuestionUserCreateCategoryResponse(it, "카테고리".plus(it)) }.toList()
            ))

        // when & then
        mockMvc.perform(
            post("/api/questions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated)
            .andDo(
                document(
                    "question/create-user-question",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("questionText").type(JsonFieldType.STRING)
                            .description("질문"),
                        fieldWithPath("questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("categoryIds").type(JsonFieldType.ARRAY)
                            .description("카테고리 ID들")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data.questionText").type(JsonFieldType.STRING)
                            .description("질문"),
                        fieldWithPath("data.questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data.categories").type(JsonFieldType.ARRAY)
                            .description("카테고리들"),
                        fieldWithPath("data.categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data.categories[].categoryName").type(JsonFieldType.STRING)
                            .description("카테고리명")
                    )
                )
            )
    }

    @DisplayName("질문 미입력시 회원 질문 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankQuestionText_whenCreateUserQuestion_thenReturn400() {
        // given
        val request = createValidQuestionUserCreateRequest()
        request.questionText = ""

        // when & then
        mockMvc.perform(
            post("/api/questions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "question/create-user-question/question-text-blank",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("questionText").type(JsonFieldType.STRING)
                            .description("질문"),
                        fieldWithPath("questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("categoryIds").type(JsonFieldType.ARRAY)
                            .description("카테고리 ID들")
                    )
                )
            )
    }

    @DisplayName("질문 배경 색상 미입력시 회원 질문 생성을 하면 400을 반환한다")
    @Test
    fun givenBlankQuestionBackgroundColor_whenCreateUserQuestion_thenReturn400() {
        // given
        val request = createValidQuestionUserCreateRequest()
        request.questionBackgroundColor = ""

        // when & then
        mockMvc.perform(
            post("/api/questions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "question/create-user-question/question-background-color-blank",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("questionText").type(JsonFieldType.STRING)
                            .description("질문"),
                        fieldWithPath("questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("categoryIds").type(JsonFieldType.ARRAY)
                            .description("카테고리 ID들")
                    )
                )
            )
    }

    @DisplayName("카테고리 ID들 미입력시 회원 질문 생성을 하면 400을 반환한다")
    @Test
    fun givenNullCategoryIds_whenCreateUserQuestion_thenReturn400() {
        // given
        val request = createValidQuestionUserCreateRequest()
        request.categoryIds = null

        // when & then
        mockMvc.perform(
            post("/api/questions")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "question/create-user-question/category-ids-blank",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("questionText").type(JsonFieldType.STRING)
                            .description("질문"),
                        fieldWithPath("questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).optional()
                            .description("카테고리 ID들")
                    )
                )
            )
    }

    private fun createValidQuestionUserCreateRequest(questionText: String = "질문 내용",
                                                     questionBackgroundColor: String = "0F0F0F",
                                                     categoryIds: List<Long> = listOf(1L, 2L))
    : QuestionUserCreateRequest {
        return QuestionUserCreateRequest(questionText, questionBackgroundColor, categoryIds)
    }
}