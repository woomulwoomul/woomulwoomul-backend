package com.woomulwoomul.core.domain.notification

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class NotificationTypeTest {

    @DisplayName("알림 타입 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetEntries_thenReturn() {
        // when
        val entries = NotificationType.entries

        // then
        assertThat(entries).contains(NotificationType.FOLLOW, NotificationType.ANSWER, NotificationType.ADMIN_UNANSWERED)
    }

    @DisplayName("알림 타입 값 조회가 정상 작동한다")
    @Test
    fun givenValid_whenValues_thenReturn() {
        // when
        val values = NotificationType.values()

        // then
        assertThat(values).containsExactly(NotificationType.FOLLOW, NotificationType.ANSWER, NotificationType.ADMIN_UNANSWERED)
    }

    @ParameterizedTest(name = "[{index}] {0}을 알림 타입으로 변환한다")
    @EnumSource(NotificationType::class)
    @DisplayName("알림 타입 변환이 정상 작동한다")
    fun givenValid_wheValueOf_thenReturn(notificationType: NotificationType) {
        // when
        val result = NotificationType.valueOf(notificationType.name)

        // then
        assertThat(result).isEqualTo(notificationType)
    }
}