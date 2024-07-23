package com.woomulwoomul.woomulwoomulbackend.api.controller.notification

import com.woomulwoomul.woomulwoomulbackend.api.controller.RestDocsSupport
import com.woomulwoomul.woomulwoomulbackend.api.service.notification.NotificationService
import com.woomulwoomul.woomulwoomulbackend.api.service.notification.response.NotificationGetAllResponse
import com.woomulwoomul.woomulwoomulbackend.api.service.notification.response.NotificationGetAllSenderResponse
import com.woomulwoomul.woomulwoomulbackend.common.constant.NotificationConstants
import com.woomulwoomul.woomulwoomulbackend.common.request.PageRequest
import com.woomulwoomul.woomulwoomulbackend.common.response.PageData
import com.woomulwoomul.woomulwoomulbackend.common.utils.DateTimeUtils
import com.woomulwoomul.woomulwoomulbackend.domain.notification.NotificationType
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
import java.time.LocalDateTime

class NotificationControllerTest : RestDocsSupport() {

    private val notificationService = mock(NotificationService::class.java)

    override fun initController(): Any {
        return NotificationController(notificationService)
    }

    @DisplayName("알림 전체 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllNotifications_thenReturn200() {
        // given
        val pageRequest = PageRequest.of(null, 3)
        val now = LocalDateTime.now()

        `when`(notificationService.getAllNotification(anyLong(), any(), any()))
            .thenReturn(
                PageData(
                listOf(
                    NotificationGetAllResponse(
                        2L,
                        NotificationType.ANSWER,
                        NotificationConstants.ANSWER.toMessage("tester1"),
                        "",
                        NotificationConstants.ANSWER.toLink(listOf(2L)),
                        DateTimeUtils.getDurationDifference(now.minusDays(1), now),
                        NotificationGetAllSenderResponse(
                            2L,
                            "tester1",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
                        )
                    ),
                    NotificationGetAllResponse(
                        1L,
                        NotificationType.FOLLOW,
                        NotificationConstants.FOLLOW.toMessage("tester1"),
                        "",
                        NotificationConstants.FOLLOW.toLink(listOf(1L)),
                        DateTimeUtils.getDurationDifference(now.minusHours(1), now),
                        NotificationGetAllSenderResponse(
                            1L,
                            "tester1",
                            "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
                        )
                    )
                ),
                    pageRequest.size
                )
            )

        // when & then
        mockMvc.perform(
            get("/api/notification")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .queryParam("page-from", pageRequest.from.toString())
                .queryParam("page-size", pageRequest.size.toString())
                .contentType(APPLICATION_JSON_VALUE)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "notification/get-all-notifications",
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
                        fieldWithPath("data[].notificationId").type(JsonFieldType.NUMBER)
                            .description("알림 ID"),
                        fieldWithPath("data[].notificationType").type(JsonFieldType.STRING)
                            .description("알림 타입"),
                        fieldWithPath("data[].notificationTitle").type(JsonFieldType.STRING)
                            .description("알림 제목"),
                        fieldWithPath("data[].notificationContext").type(JsonFieldType.STRING)
                            .description("알림 내용"),
                        fieldWithPath("data[].notificationLink").type(JsonFieldType.STRING)
                            .description("알림 링크"),
                        fieldWithPath("data[].notificationDateTime").type(JsonFieldType.STRING)
                            .description("알림 시간"),
                        fieldWithPath("data[].sender").type(JsonFieldType.OBJECT)
                            .description("알림 전송자"),
                        fieldWithPath("data[].sender.userId").type(JsonFieldType.NUMBER)
                            .description("알림 전송자 회원 ID"),
                        fieldWithPath("data[].sender.userNickname").type(JsonFieldType.STRING)
                            .description("알림 전송자 회원 닉네임"),
                        fieldWithPath("data[].sender.userImageUrl").type(JsonFieldType.STRING)
                            .description("알림 전송자 회원 이미지 URL")
                    ),
                    queryParameters(
                        parameterWithName("page-from").description("페이지 시작점").optional(),
                        parameterWithName("page-size").description("페이지 크기").optional()
                    ),
                )
            )
    }
}