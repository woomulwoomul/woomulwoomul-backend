package com.woomulwoomul.woomulwoomulbackend.api.controller.question

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.controller.question.request.AnswerCreateRequest
import com.woomulwoomul.woomulwoomulbackend.api.controller.question.request.AnswerUpdateRequest
import com.woomulwoomul.woomulwoomulbackend.api.service.question.AnswerService
import com.woomulwoomul.woomulwoomulbackend.api.service.question.response.*
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
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

    @DisplayName("전체 답변 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllAnswers_thenReturn200() {
        // given
        val pageRequest = PageRequest.of(null, 3)

        `when`(answerService.getAllAnswers(anyLong(), anyLong(), any()))
            .thenReturn(PageData(
                listOf(
                    AnswerFindAllResponse(1L, "답변", "", LocalDateTime.now(),
                        10L,
                        listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"),
                        1L, "질문", "0F0F0F",
                        listOf(AnswerFindAllCategoryResponse(1L, "카테고리1"),
                            AnswerFindAllCategoryResponse(2L, "카테고리2"),
                            AnswerFindAllCategoryResponse(3L, "카테고리3"))),
                    AnswerFindAllResponse(2L, "답변", "", LocalDateTime.now(),
                        10L,
                        listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"),
                        2L, "질문", "0F0F0F",
                        listOf(AnswerFindAllCategoryResponse(1L, "카테고리1"),
                            AnswerFindAllCategoryResponse(2L, "카테고리2"),
                            AnswerFindAllCategoryResponse(3L, "카테고리3"))),
                    AnswerFindAllResponse(3L, "답변", "", LocalDateTime.now(),
                        10L,
                        listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"),
                        3L, "질문", "0F0F0F",
                        listOf(AnswerFindAllCategoryResponse(1L, "카테고리1"),
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
                .queryParam("page-from", pageRequest.from.toString())
                .queryParam("page-size", pageRequest.size.toString())
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

    @DisplayName("답변 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAnswer_thenReturn200() {
        // given
        `when`(answerService.getAnswer(anyLong(), anyLong()))
            .thenReturn(
                AnswerFindResponse(1L,
                    "답변",
                    "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                    3L,
                    listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"),
                    1L,
                    "질문",
                    "0F0F0F",
                    listOf(AnswerFindCategoryResponse(1L, "카테고리1"),
                        AnswerFindCategoryResponse(2L, "카테고리2"),
                        AnswerFindCategoryResponse(3L, "카테고리3")))
            )

        // when & then
        mockMvc.perform(
            get("/api/users/{user-id}/answers/{answer-id}", 1, 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "answer/get-answer",
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
                    "0F0F0F",
                    listOf(
                        AnswerCreateCategoryResponse(1L, "카테고리1"),
                        AnswerCreateCategoryResponse(2L, "카테고리2"),
                        AnswerCreateCategoryResponse(3L, "카테고리3")
                    )
            ))

        val request = createValidAnswerCreateRequest()

        // when & then
        mockMvc.perform(
            post("/api/users/{user-id}/questions/{question-id}", 1, 1)
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
                AnswerUpdateResponse(1L,
                    "답변",
                    "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                    3L,
                    listOf("https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                        "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"),
                    1L,
                    "질문",
                    "0F0F0F",
                    listOf(AnswerUpdateCategoryResponse(1L, "카테고리1"),
                        AnswerUpdateCategoryResponse(2L, "카테고리2"),
                        AnswerUpdateCategoryResponse(3L, "카테고리3")))
            )

        val answerId = 1L
        val request = createValidAnswerUpdateRequest()

        // when & then
        mockMvc.perform(
            patch("/api/answers/{answer-id}", answerId)
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
            delete("/api/answers/{answer-id}", answerId)
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
            .thenReturn("https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640")

        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))
        val questionId = 1L

        // when & then
        mockMvc.perform(
            post("/api/questions/{question-id}/answers/image", questionId)
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

    private fun createValidAnswerUpdateRequest(): AnswerUpdateRequest {
        return AnswerUpdateRequest("수정 답변", "")
    }

    private fun createValidAnswerCreateRequest(): AnswerCreateRequest {
        return AnswerCreateRequest("답변", "")
    }
}