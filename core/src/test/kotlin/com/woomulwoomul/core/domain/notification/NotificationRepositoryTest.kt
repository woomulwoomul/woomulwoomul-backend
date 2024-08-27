package com.woomulwoomul.core.domain.notification

import com.woomulwoomul.core.common.constant.NotificationConstants
import com.woomulwoomul.core.common.request.PageRequest
import com.woomulwoomul.core.domain.notification.NotificationType.*
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserRepository
import com.woomulwoomul.core.domain.user.UserRoleRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NotificationRepositoryTest(
    @Autowired private val notificationRepository: NotificationRepository,
    @Autowired private val userRepository: UserRepository
) {

    @DisplayName("알림 전체 조회가 정상 동작한다")
    @Test
    fun givenValid_whenFindAll_thenReturn() {
        // given
        val admin = createAndSaveUser("admin", "admin@woomulwoomul.com")
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"))
        val notifications = listOf(createAndSaveNotification(users[0], users[1], null, FOLLOW,
            NotificationConstants.FOLLOW.toMessage(users[1].nickname), "",
            NotificationConstants.FOLLOW.toLink(listOf(users[1].id!!))),
            createAndSaveNotification(users[0], users[1], null, ANSWER,
                NotificationConstants.ANSWER.toMessage(users[1].nickname), "",
                NotificationConstants.ANSWER.toLink(listOf(users[1].id!!, 1L))),
            createAndSaveNotification(users[0], users[1], null, ANSWER,
                NotificationConstants.ANSWER.toMessage(users[1].nickname), "",
                NotificationConstants.ANSWER.toLink(listOf(users[1].id!!, 2L))),
            createAndSaveNotification(users[0], null, admin, ADMIN_UNANSWERED,
                NotificationConstants.ADMIN_UNANSWERED.toMessage(), "", NotificationConstants.ANSWER.toLink()))

        val pageRequest = PageRequest.of(null, 3)

        // when
        val foundNotifications = notificationRepository.findAll(users[0].id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(foundNotifications.total).isEqualTo(notifications.size.toLong())
            },
            {
                assertThat(foundNotifications.data)
                    .extracting("id", "type", "title", "context", "link", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(notifications[3].id, notifications[3].type, notifications[3].title, notifications[3].context,
                            notifications[3].link, notifications[3].status, notifications[3].createDateTime,
                            notifications[3].updateDateTime),
                        tuple(notifications[2].id, notifications[2].type, notifications[2].title, notifications[2].context,
                            notifications[2].link, notifications[2].status, notifications[2].createDateTime,
                            notifications[2].updateDateTime),
                        tuple(notifications[1].id, notifications[1].type, notifications[1].title, notifications[1].context,
                            notifications[1].link, notifications[1].status, notifications[1].createDateTime,
                            notifications[1].updateDateTime)
                    )
            },
            {
                assertThat(foundNotifications.data)
                    .extracting("receiver")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(users[0].id, users[0].nickname, users[0].email, users[0].imageUrl, users[0].introduction,
                            users[0].status, users[0].createDateTime, users[0].updateDateTime),
                        tuple(users[0].id, users[0].nickname, users[0].email, users[0].imageUrl, users[0].introduction,
                            users[0].status, users[0].createDateTime, users[0].updateDateTime),
                        tuple(users[0].id, users[0].nickname, users[0].email, users[0].imageUrl, users[0].introduction,
                            users[0].status, users[0].createDateTime, users[0].updateDateTime)
                    )
            }
        )
    }

    @DisplayName("알림이 없는데 알림 전체 조회를 하면 정상 동작한다")
    @Test
    fun givenEmpty_whenFindAll_thenReturn() {
        // given
        val user = createAndSaveUser("tester1", "tester1@woomulwoomul.com")
        val pageRequest = PageRequest.of(null, null)

        // when
        val foundNotifications = notificationRepository.findAll(user.id!!, pageRequest)

        // then
        assertAll(
            {
                assertThat(foundNotifications.total).isEqualTo(0L)
            },
            {
                assertThat(foundNotifications.data).isEmpty()
            }
        )
    }

    @DisplayName("회원 ID와 알림 ID로 알림 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindByNotificationIdAndUserId_thenReturn() {
        // given
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"))
        val notification = createAndSaveNotification(users[0], users[1], null, FOLLOW,
            NotificationConstants.FOLLOW.toMessage(users[1].nickname), "",
            NotificationConstants.FOLLOW.toLink(listOf(users[1].id!!)))

        // when
        val foundNotification = notificationRepository.findByNotificationIdAndUserId(notification.id!!, users[0].id!!)

        // then
        assertAll(
            {
                assertThat(foundNotification)
                    .extracting("id", "type", "title", "context", "link", "status", "createDateTime", "updateDateTime")
                    .containsExactly(notification.id, notification.type, notification.title, notification.context,
                        notification.link, notification.status, notification.createDateTime, notification.updateDateTime)
            },
            {
                assertThat(foundNotification)
                    .extracting("receiver")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(users[0].id, users[0].nickname, users[0].email, users[0].imageUrl,
                        users[0].introduction, users[0].status, users[0].createDateTime, users[0].updateDateTime)
            },
            {
                assertThat(foundNotification)
                    .extracting("senderUser")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(users[1].id, users[1].nickname, users[1].email, users[1].imageUrl,
                        users[1].introduction, users[1].status, users[1].createDateTime, users[1].updateDateTime)
            },
            {
                assertThat(foundNotification?.senderAdmin).isNull()
            }
        )
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
        return notificationRepository.save(NotificationEntity(
            receiver = receiver,
            senderUser = senderUser,
            senderAdmin = senderAdmin,
            type = type,
            title = title,
            context = context,
            link = link
        ))
    }

    private fun createAndSaveUser(
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserEntity {
        return userRepository.save(UserEntity(
            nickname = nickname,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        ))
    }
}