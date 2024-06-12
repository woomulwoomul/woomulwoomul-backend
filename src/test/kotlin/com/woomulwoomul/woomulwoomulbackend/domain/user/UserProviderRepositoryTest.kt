package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType.KAKAO
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
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
class UserProviderRepositoryTest(
    @Autowired private val userProviderRepository: UserProviderRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @DisplayName("SNS ID로 회원 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturn() {
        // given
        val userProvider = createAndSaveUserProvider()

        // when
        val foundUserProvider = userProviderRepository.findFetchUser(userProvider.providerId)

        // then
        assertAll(
            {
                assertThat(foundUserProvider)
                    .extracting("id", "provider", "providerId", "status", "createDateTime", "updateDateTime")
                    .containsExactly(userProvider.id, userProvider.provider, userProvider.providerId,
                        userProvider.status, userProvider.createDateTime, userProvider.updateDateTime)
            },
            {
                assertThat(foundUserProvider!!.user)
                    .extracting("id", "username", "email", "imageUrl", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(userProvider.id, userProvider.user.username, userProvider.user.email,
                        userProvider.user.imageUrl, userProvider.user.status, userProvider.user.createDateTime,
                        userProvider.user.updateDateTime)
            }
        )
    }

    private fun createAndSaveUserProvider(): UserProviderEntity {
        val user = userRepository.save(UserEntity(
            username = "tester",
            email = "tester@woomulwoomul.com",
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        ))

        return userProviderRepository.save(UserProviderEntity(
            user = user,
            provider = KAKAO,
            providerId = "tester"
        ))
    }
}