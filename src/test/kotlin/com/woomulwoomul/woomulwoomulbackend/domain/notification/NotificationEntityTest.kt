package com.woomulwoomul.woomulwoomulbackend.domain.notification

import com.woomulwoomul.woomulwoomulbackend.common.constant.NotificationConstants
import com.woomulwoomul.woomulwoomulbackend.domain.base.NotificationServiceStatus.READ
import com.woomulwoomul.woomulwoomulbackend.domain.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NotificationEntityTest {

    @DisplayName("알림 읽기가 정상 작동한다")
    @Test
    fun givenValid_whenRead_thenReturn() {
        // given
        val users = listOf(createAndSaveUser(1L,"tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser(2L, "tester2", "tester2@woomulwoomul.com"))
        val notification = createAndSaveNotification(users[0], users[1], null, NotificationType.FOLLOW,
            NotificationConstants.FOLLOW.toMessage(users[1].nickname), "",
            NotificationConstants.FOLLOW.toLink(listOf(users[1].id!!)))

        // when
        notification.read()

        // then
        assertThat(notification.status).isEqualTo(READ)
    }

    private fun createAndSaveNotification(
        receiver: UserEntity,
        senderUser: UserEntity?,
        senderAdmin: UserEntity?,
        type: NotificationType,
        title: String,
        context: String,
        link: String,
    ): NotificationEntity {
        return NotificationEntity(
            receiver = receiver,
            senderUser = senderUser,
            senderAdmin = senderAdmin,
            type = type,
            title = title,
            context = context,
            link = link
        )
    }

    private fun createAndSaveUser(
        id: Long,
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserEntity {
        return UserEntity(
            id = id,
            nickname = nickname,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        )
    }
}