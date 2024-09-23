package com.woomulwoomul.core.domain.user

import com.woomulwoomul.core.common.request.PageOffsetRequest
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
class UserLoginRepositoryTest(
    @Autowired private val userLoginRepository: UserLoginRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @Test
    fun `전체 회원 로그인 오프셋 페이징 조회가 정상 작동한다`() {
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
        val foundUserLogins = userLoginRepository.findAll(pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(foundUserLogins.total).isEqualTo(users.size.toLong())
            },
            {
                assertThat(foundUserLogins.data)
                    .extracting("id", "createDateTime")
                    .containsExactly(
                        tuple(userLogins[2].last().id, userLogins[2].last().createDateTime),
                        tuple(userLogins[1].last().id, userLogins[1].last().createDateTime)
                    )
            }
        )
    }

    @Test
    fun `회원 로그인이 존재하지 않을 경우 전체 회원 로그인 오프셋 페이징 조회를 하면 정상 작동한다`() {
        // given
        val pageOffsetRequest = PageOffsetRequest.of(1, 10)

        // when
        val foundUserLogins = userLoginRepository.findAll(pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(foundUserLogins.total).isEqualTo(0L)
            },
            {
                assertThat(foundUserLogins.data).isEmpty()
            }
        )
    }

    private fun createAndSaveUserLogin(user: UserEntity): UserLoginEntity {
        return userLoginRepository.save(UserLoginEntity(user = user))
    }

    private fun createAndSaveUser(nickname: String = "tester", email: String = "tester@woomulwoomul.com"): UserEntity {
        return userRepository.save(UserEntity(
            nickname = nickname,
            email = email,
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
        ))
    }
}