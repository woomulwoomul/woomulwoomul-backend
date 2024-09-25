package com.woomulwoomul.clientapi.controller.question

import com.woomulwoomul.clientapi.controller.RestDocsSupport
import com.woomulwoomul.clientapi.controller.question.request.AnswerCreateRequest
import com.woomulwoomul.clientapi.controller.question.request.AnswerUpdateRequest
import com.woomulwoomul.clientapi.service.question.AnswerService
import com.woomulwoomul.clientapi.service.question.response.*
import com.woomulwoomul.core.common.constant.BackgroundColor
import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.response.PageData
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class AnswerControllerTest : RestDocsSupport() {

    private val answerService = mock(AnswerService::class.java)

    override fun initController(): Any {
        return AnswerController(answerService)
    }

    @DisplayName("답변 전체 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllAnswers_thenReturn200() {
        // given
        val pageCursorRequest = PageCursorRequest.of(null, 3)

        `when`(answerService.getAllAnswers(anyLong(), anyLong(), any()))
            .thenReturn(PageData(
                listOf(
                    AnswerFindAllResponse(1L, "답변", "", LocalDateTime.now(),
                        10L,
                        listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg"),
                        1L, "질문", BackgroundColor.entries[0].value,
                        listOf(AnswerFindAllCategoryResponse(1L, "카테고리1"),
                            AnswerFindAllCategoryResponse(2L, "카테고리2"),
                            AnswerFindAllCategoryResponse(3L, "카테고리3"))),
                    AnswerFindAllResponse(2L, "답변", "", LocalDateTime.now(),
                        10L,
                        listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg"),
                        2L, "질문", BackgroundColor.entries[1].value,
                        listOf(AnswerFindAllCategoryResponse(1L, "카테고리1"),
                            AnswerFindAllCategoryResponse(2L, "카테고리2"),
                            AnswerFindAllCategoryResponse(3L, "카테고리3"))),
                    AnswerFindAllResponse(3L, "답변", "", LocalDateTime.now(),
                        10L,
                        listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg"),
                        3L, "질문", BackgroundColor.entries[2].value,
                        listOf(AnswerFindAllCategoryResponse(1L, "카테고리1"),
                            AnswerFindAllCategoryResponse(2L, "카테고리2"),
                            AnswerFindAllCategoryResponse(3L, "카테고리3")))
                ),
                pageCursorRequest.size
            ))

        // when & then
        mockMvc.perform(
            get("/api/users/{userId}/answers", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .queryParam("page-from", pageCursorRequest.from.toString())
                .queryParam("page-size", pageCursorRequest.size.toString())
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
                        fieldWithPath("data[].answerText").type(JsonFieldType.STRING).optional()
                            .description("답변 내용"),
                        fieldWithPath("data[].answerImageUrl").type(JsonFieldType.STRING).optional()
                            .description("답변 이미지"),
                        fieldWithPath("data[].answerUpdateDateTime").type(JsonFieldType.STRING)
                            .description("답변 수정일"),
                        fieldWithPath("data[].answeredUserCnt").type(JsonFieldType.NUMBER)
                            .description("답변한 회원수"),
                        fieldWithPath("data[].answeredUserImageUrls").type(JsonFieldType.ARRAY)
                            .description("답변한 회원 프로필 이미지"),
                        fieldWithPath("data[].questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data[].questionText").type(JsonFieldType.STRING)
                            .description("질문 내용"),
                        fieldWithPath("data[].questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data[].categories").type(JsonFieldType.ARRAY)
                            .description("카테고리들"),
                        fieldWithPath("data[].categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data[].categories[].name").type(JsonFieldType.STRING)
                            .description("카테고리명")
                    ),
                    queryParameters(
                        parameterWithName("page-from").description("페이지 시작점").optional(),
                        parameterWithName("page-size").description("페이지 크기").optional()
                    ),
                )
            )
    }

    @DisplayName("회원 ID와 답변 ID로 답변 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAnswerByUserIdAndAnswerId_thenReturn200() {
        // given
        `when`(answerService.getAnswerByUserIdAndAnswerId(anyLong(), anyLong()))
            .thenReturn(
                AnswerFindResponse(1L,
                    "답변",
                    "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                    3L,
                    listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg"),
                    1L,
                    "질문",
                    BackgroundColor.WHITE.value,
                    listOf(AnswerFindCategoryResponse(1L, "카테고리1"),
                        AnswerFindCategoryResponse(2L, "카테고리2"),
                        AnswerFindCategoryResponse(3L, "카테고리3")))
            )

        // when & then
        mockMvc.perform(
            get("/api/users/{userId}/answers/{answerId}", 1, 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/get-answer-by-answer-id",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.answerId").type(JsonFieldType.NUMBER)
                            .description("답변 ID"),
                        fieldWithPath("data.answerText").type(JsonFieldType.STRING).optional()
                            .description("답변 내용"),
                        fieldWithPath("data.answerImageUrl").type(JsonFieldType.STRING).optional()
                            .description("답변 이미지"),
                        fieldWithPath("data.answerUpdateDateTime").type(JsonFieldType.STRING)
                            .description("답변 수정일"),
                        fieldWithPath("data.answeredUserCnt").type(JsonFieldType.NUMBER)
                            .description("답변한 회원수"),
                        fieldWithPath("data.answeredUserImageUrls").type(JsonFieldType.ARRAY)
                            .description("답변한 회원 프로필 이미지"),
                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data.questionText").type(JsonFieldType.STRING)
                            .description("질문 내용"),
                        fieldWithPath("data.questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data.categories").type(JsonFieldType.ARRAY)
                            .description("카테고리들"),
                        fieldWithPath("data.categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data.categories[].name").type(JsonFieldType.STRING)
                            .description("카테고리명"),
                    )
                )
            )
    }

    @DisplayName("회원 ID와 질문 ID로 답변 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAnswerByUserIdAndQuestionId_thenReturn200() {
        // given
        `when`(answerService.getAnswerByUserIdAndQuestionId(anyLong(), anyLong()))
            .thenReturn(
                AnswerFindResponse(1L,
                    "답변",
                    "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                    3L,
                    listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg"),
                    1L,
                    "질문",
                    BackgroundColor.WHITE.value,
                    listOf(AnswerFindCategoryResponse(1L, "카테고리1"),
                        AnswerFindCategoryResponse(2L, "카테고리2"),
                        AnswerFindCategoryResponse(3L, "카테고리3")))
            )

        // when & then
        mockMvc.perform(
            get("/api/users/{userId}/questions/{questionId}/answers", 1, 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/get-answer-by-question-id",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.answerId").type(JsonFieldType.NUMBER)
                            .description("답변 ID"),
                        fieldWithPath("data.answerText").type(JsonFieldType.STRING).optional()
                            .description("답변 내용"),
                        fieldWithPath("data.answerImageUrl").type(JsonFieldType.STRING).optional()
                            .description("답변 이미지"),
                        fieldWithPath("data.answerUpdateDateTime").type(JsonFieldType.STRING)
                            .description("답변 수정일"),
                        fieldWithPath("data.answeredUserCnt").type(JsonFieldType.NUMBER)
                            .description("답변한 회원수"),
                        fieldWithPath("data.answeredUserImageUrls").type(JsonFieldType.ARRAY)
                            .description("답변한 회원 프로필 이미지"),
                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data.questionText").type(JsonFieldType.STRING)
                            .description("질문 내용"),
                        fieldWithPath("data.questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data.categories").type(JsonFieldType.ARRAY)
                            .description("카테고리들"),
                        fieldWithPath("data.categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data.categories[].name").type(JsonFieldType.STRING)
                            .description("카테고리명"),
                    )
                )
            )
    }

    @DisplayName("답변 작성를 하면 201을 반환한다")
    @Test
    fun givenValid_whenCreateAnswer_thenReturn201() {
        // given
        `when`(answerService.createAnswer(anyLong(), anyLong(), anyLong(), any()))
            .thenReturn(
                AnswerCreateResponse(
                    1L,
                    "tester",
                    1L,
                    "질문",
                    BackgroundColor.WHITE.value,
                    listOf(
                        AnswerCreateCategoryResponse(1L, "카테고리1"),
                        AnswerCreateCategoryResponse(2L, "카테고리2"),
                        AnswerCreateCategoryResponse(3L, "카테고리3")
                    )
            ))

        val request = createValidAnswerCreateRequest()

        // when & then
        mockMvc.perform(
            post("/api/users/{userId}/questions/{questionId}", 1, 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated)
            .andDo(
                document(
                    "answer/create-answer",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                    requestFields(
                        fieldWithPath("answerText").type(JsonFieldType.STRING).optional()
                            .description("답변 내용"),
                        fieldWithPath("answerImageUrl").type(JsonFieldType.STRING).optional()
                            .description("답변 이미지"),
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
                        fieldWithPath("data.userNickname").type(JsonFieldType.STRING)
                            .description("회원 닉네임"),
                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data.questionText").type(JsonFieldType.STRING)
                            .description("질문 내용"),
                        fieldWithPath("data.questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data.categories").type(JsonFieldType.ARRAY)
                            .description("카테고리들"),
                        fieldWithPath("data.categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data.categories[].name").type(JsonFieldType.STRING)
                            .description("카테고리명")
                    )
                )
            )
    }

    @DisplayName("답변 업데이트를 하면 200을 반환한다")
    @Test
    fun givenValid_whenUpdateAnswer_thenReturn200() {
        // given
        `when`(answerService.updateAnswer(anyLong(), anyLong(), any()))
            .thenReturn(
                com.woomulwoomul.clientapi.service.question.response.AnswerUpdateResponse(
                    1L,
                    "답변",
                    "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                    3L,
                    listOf(
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
                    ),
                    1L,
                    "질문",
                    BackgroundColor.WHITE.value,
                    listOf(
                        AnswerUpdateCategoryResponse(
                            1L,
                            "카테고리1"
                        ),
                        AnswerUpdateCategoryResponse(
                            2L,
                            "카테고리2"
                        ),
                        AnswerUpdateCategoryResponse(
                            3L,
                            "카테고리3"
                        )
                    )
                )
            )

        val answerId = 1L
        val request = createValidAnswerUpdateRequest()

        // when & then
        mockMvc.perform(
            patch("/api/answers/{answerId}", answerId)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/update-answer",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                    requestFields(
                        fieldWithPath("answerText").type(JsonFieldType.STRING).optional()
                            .description("답변 내용"),
                        fieldWithPath("answerImageUrl").type(JsonFieldType.STRING).optional()
                            .description("답변 이미지"),
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.answerId").type(JsonFieldType.NUMBER)
                            .description("답변 ID"),
                        fieldWithPath("data.answerText").type(JsonFieldType.STRING).optional()
                            .description("답변 내용"),
                        fieldWithPath("data.answerImageUrl").type(JsonFieldType.STRING).optional()
                            .description("답변 이미지"),
                        fieldWithPath("data.answerUpdateDateTime").type(JsonFieldType.STRING)
                            .description("답변 수정일"),
                        fieldWithPath("data.answeredUserCnt").type(JsonFieldType.NUMBER)
                            .description("답변한 회원수"),
                        fieldWithPath("data.answeredUserImageUrls").type(JsonFieldType.ARRAY)
                            .description("답변한 회원 프로필 이미지"),
                        fieldWithPath("data.questionId").type(JsonFieldType.NUMBER)
                            .description("질문 ID"),
                        fieldWithPath("data.questionText").type(JsonFieldType.STRING)
                            .description("질문 내용"),
                        fieldWithPath("data.questionBackgroundColor").type(JsonFieldType.STRING)
                            .description("질문 배경 색상"),
                        fieldWithPath("data.categories").type(JsonFieldType.ARRAY)
                            .description("카테고리들"),
                        fieldWithPath("data.categories[].categoryId").type(JsonFieldType.NUMBER)
                            .description("카테고리 ID"),
                        fieldWithPath("data.categories[].name").type(JsonFieldType.STRING)
                            .description("카테고리명"),
                    )
                )
            )
    }

    @DisplayName("답변 삭제를 하면 200을 반환한다")
    @Test
    fun givenValid_whenDeleteAnswer_thenReturn200() {
        // given
        val answerId = 1L

        // when & then
        mockMvc.perform(
            delete("/api/answers/{answerId}", answerId)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/delete-answer",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }

    @DisplayName("답변 이미지 업로드를 하면 200을 반환한다")
    @Test
    fun givenValid_whenUploadImage_thenReturn200() {
        // given
        `when`(answerService.uploadImage(anyLong(), anyLong(), any()))
            .thenReturn("https://t1.kakaocdn.net/account_images/default_profile.jpeg")

        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))
        val questionId = 1L

        // when & then
        mockMvc.perform(
            post("/api/questions/{questionId}/answers/image", questionId)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("file", file.toString())
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/upload-image",
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

    @DisplayName("답변이 존재할시 답변 존재 여부 조회를 하면 200을 반환한다")
    @Test
    fun givenExistingAnswer_whenIsExistingAnswer_thenReturn200() {
        // given
        `when`(answerService.isExistingAnswer(anyLong(), anyLong()))
            .thenReturn(true)

        val questionId = 1L

        // when & then
        mockMvc.perform(
            get("/api/questions/{questionId}/answers", questionId)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/is-existing-answer/true-response",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                    )
                )
            )
    }

    @DisplayName("답변이 미존재할시 답변 존재 여부 조회를 하면 200을 반환한다")
    @Test
    fun givenNonExistingAnswer_whenIsExistingAnswer_thenReturn200() {
        // given
        `when`(answerService.isExistingAnswer(anyLong(), anyLong()))
            .thenReturn(false)

        val questionId = 1L

        // when & then
        mockMvc.perform(
            get("/api/questions/{questionId}/answers", questionId)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/is-existing-answer/false-response",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                    )
                )
            )
    }

    private fun createValidAnswerUpdateRequest(): AnswerUpdateRequest {
        return AnswerUpdateRequest("수정 답변", "")
    }

    private fun createValidAnswerCreateRequest(): AnswerCreateRequest {
        return AnswerCreateRequest("답변", "")
    }
}