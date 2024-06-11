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
        val userProviderEntity = createAndSaveUserProvider()

        // when
        val foundUserProviderEntity = userProviderRepository.findFetchUser(userProviderEntity.providerId)

        // then
        assertAll(
            {
                assertThat(foundUserProviderEntity)
                    .extracting("id", "provider", "providerId", "serviceStatus", "createDateTime", "updateDateTime")
                    .containsExactly(userProviderEntity.id, userProviderEntity.provider,
                            userProviderEntity.providerId, userProviderEntity.serviceStatus,
                            userProviderEntity.createDateTime, userProviderEntity.updateDateTime)
            },
            {
                assertThat(foundUserProviderEntity!!.userEntity)
                    .extracting("id", "username", "email", "imageUrl", "serviceStatus", "createDateTime",
                        "updateDateTime")
                    .containsExactly(userProviderEntity.id, userProviderEntity.userEntity.username,
                            userProviderEntity.userEntity.email, userProviderEntity.userEntity.imageUrl,
                            userProviderEntity.userEntity.serviceStatus, userProviderEntity.userEntity.createDateTime,
                            userProviderEntity.userEntity.updateDateTime)
            }
        )
    }

    private fun createAndSaveUserProvider(): UserProviderEntity {
        val userEntity = userRepository.save(UserEntity(
            username = "tester",
            email = "tester@woomulwoomul.com",
            imageUrl = "https://www.google.com"
        ))

        return userProviderRepository.save(UserProviderEntity(
            userEntity = userEntity,
            provider = KAKAO,
            providerId = "tester"
        ))
    }
}