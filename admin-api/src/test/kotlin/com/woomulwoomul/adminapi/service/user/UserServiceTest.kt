package com.woomulwoomul.adminapi.service.user

import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.domain.user.UserEntity
import com.woomulwoomul.core.domain.user.UserLoginEntity
import com.woomulwoomul.core.domain.user.UserLoginRepository
import com.woomulwoomul.core.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserServiceTest(
    @Autowired private val userService: UserService,
    @Autowired private val userLoginRepository: UserLoginRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @Test
    fun `회원 전체 조회가 정상 작동한다`() {
        // given
        val users = listOf(createAndSaveUser("tester1", "tester1@woomulwoomul.com"),
            createAndSaveUser("tester2", "tester2@woomulwoomul.com"),
            createAndSaveUser("tester3", "tester3@woomulwoomul.com"),
            createAndSaveUser("tester4", "tester4@woomulwoomul.com"),
            createAndSaveUser("tester5", "tester5@woomulwoomul.com"),)

        val userLogins = listOf(
            listOf(
                createAndSaveUserLogin(users[0])
            ),
            listOf(
                createAndSaveUserLogin(users[1]),
                createAndSaveUserLogin(users[1])
            ),
            listOf(
                createAndSaveUserLogin(users[2]),
                createAndSaveUserLogin(users[2]),
                createAndSaveUserLogin(users[2])
            ),
            listOf(
                createAndSaveUserLogin(users[3]),
                createAndSaveUserLogin(users[3])
            ),
            listOf(
                createAndSaveUserLogin(users[4])
            ),
        )

        val pageOffsetRequest = PageOffsetRequest.of(2, 2)

        // when
        val result = userService.getAllUsers(pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(result.total).isEqualTo(users.size.toLong())
            },
            {
                assertThat(result.data)
                    .extracting("id", "nickname", "email", "imageUrl", "lastLoginDateTime", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(
                        tuple(users[2].id, users[2].nickname, users[2].email, users[2].imageUrl,
                            userLogins[2].last().createDateTime, users[2].status, users[2].createDateTime,
                            users[2].updateDateTime),
                        tuple(users[1].id, users[1].nickname, users[1].email, users[1].imageUrl,
                            userLogins[1].last().createDateTime, users[1].status, users[1].createDateTime,
                            users[1].updateDateTime)
                    )
            }
        )
    }

    private fun createAndSaveUserLogin(user: UserEntity): UserLoginEntity {
        return userLoginRepository.save(UserLoginEntity(user = user))
    }

    private fun createAndSaveUser(nickname: String = "tester", email: String = "tester@woomulwoomul.com"): UserEntity {
        return userRepository.save(
            UserEntity(
            nickname = nickname,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        )
        )
    }
}