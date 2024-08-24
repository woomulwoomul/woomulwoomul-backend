package com.woomulwoomul.clientapi.service.notification

import com.woomulwoomul.core.common.constant.ExceptionCode.NOTIFICATION_NOT_FOUND
import com.woomulwoomul.core.common.constant.NotificationConstants
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.common.response.CustomException
import com.woomulwoomul.core.common.utils.DateTimeUtils
import com.woomulwoomul.core.domain.base.NotificationServiceStatus.READ
import com.woomulwoomul.core.domain.base.NotificationServiceStatus.USER_DEL
import com.woomulwoomul.core.domain.notification.NotificationEntity
import com.woomulwoomul.core.domain.notification.NotificationRepository
import com.woomulwoomul.core.domain.notification.NotificationType
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NotificationServiceTest(
    @Autowired private val notificationService: NotificationService,
    @Autowired private val notificationRepository: NotificationRepository,
    @Autowired private val userRepository: UserRepository
) {

    @DisplayName("알림 전체 조회가 정상 동작한다")
    @Test
    fun givenValid_whenGetAllNotification_thenReturn() {
        // given
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"))
        val notifications = listOf(createAndSaveNotification(users[0], users[1], null, NotificationType.FOLLOW,
            NotificationConstants.FOLLOW.toMessage(users[1].nickname), "",
            NotificationConstants.FOLLOW.toLink(listOf(users[1].id!!))),
            createAndSaveNotification(users[0], users[1], null, NotificationType.ANSWER,
                NotificationConstants.ANSWER.toMessage(users[1].nickname), "",
                NotificationConstants.ANSWER.toLink(listOf(users[1].id!!, 1L))),
            createAndSaveNotification(users[0], users[1], null, NotificationType.ANSWER,
                NotificationConstants.ANSWER.toMessage(users[1].nickname), "",
                NotificationConstants.ANSWER.toLink(listOf(users[1].id!!, 2L))))

        val pageRequest = PageRequest.of(null, 3)
        val now = LocalDateTime.now()

        // when
        val response = notificationService.getAllNotification(users[0].id!!, pageRequest, now)

        // then
        assertAll(
            {
                assertThat(response.total).isEqualTo(notifications.size.toLong())
            },
            {
                assertThat(response.data)
                    .extracting("notificationId", "notificationType", "notificationTitle", "notificationContext",
                        "notificationLink", "notificationDateTime")
                    .containsExactly(
                        tuple(notifications[2].id, notifications[2].type, notifications[2].title, notifications[2].context,
                            notifications[2].link,
                            DateTimeUtils.getDurationDifference(notifications[2].createDateTime!!, now)),
                        tuple(notifications[1].id, notifications[1].type, notifications[1].title, notifications[1].context,
                            notifications[1].link,
                            DateTimeUtils.getDurationDifference(notifications[1].createDateTime!!, now)),
                        tuple(notifications[0].id, notifications[0].type, notifications[0].title, notifications[0].context,
                            notifications[0].link,
                            DateTimeUtils.getDurationDifference(notifications[0].createDateTime!!, now))
                    )
            },
            {
                assertThat(response.data)
                    .extracting("sender")
                    .extracting("userId", "userNickname", "userImageUrl")
                    .containsExactly(
                        tuple(users[1].id, users[1].nickname, users[1].imageUrl),
                        tuple(users[1].id, users[1].nickname, users[1].imageUrl),
                        tuple(users[1].id, users[1].nickname, users[1].imageUrl)
                    )
            }
        )
    }

    @DisplayName("알림 읽기가 정상 작동한다")
    @Test
    fun givenValid_whenReadNotification_thenReturn() {
        // given
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"))
        val notification = createAndSaveNotification(users[0], users[1], null, NotificationType.FOLLOW,
            NotificationConstants.FOLLOW.toMessage(users[1].nickname), "",
            NotificationConstants.FOLLOW.toLink(listOf(users[1].id!!)))

        // when
        notificationService.readNotification(users[0].id!!, notification.id!!)

        // then
        assertThat(notification.status).isEqualTo(READ)
    }

    @DisplayName("존재하지 않은 알림 읽기를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingNotification_whenReadNotification_thenThrow() {
        // given
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"))
        val notification = createAndSaveNotification(users[0], users[1], null, NotificationType.FOLLOW,
            NotificationConstants.FOLLOW.toMessage(users[1].nickname), "",
            NotificationConstants.FOLLOW.toLink(listOf(users[1].id!!)))
            .also { it.status = USER_DEL }
        notificationRepository.save(notification)

        // when & then
        assertThatThrownBy { notificationService.readNotification(users[0].id!!, notification.id!!) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(NOTIFICATION_NOT_FOUND)
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
        return notificationRepository.save(
            NotificationEntity(
            receiver = receiver,
            senderUser = senderUser,
            senderAdmin = senderAdmin,
            type = type,
            title = title,
            context = context,
            link = link
        )
        )
    }

    private fun createAndSaveUser(
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserEntity {
        return userRepository.save(
            UserEntity(
            nickname = nickname,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        )
        )
    }
}