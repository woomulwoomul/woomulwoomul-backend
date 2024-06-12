package com.woomulwoomul.woomulwoomulbackend.domain.user

import com.woomulwoomul.woomulwoomulbackend.domain.user.Role.USER
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
class UserRoleRepositoryTest(
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository
) {

    @DisplayName("회원 ID로 모든 회원 권한을 조회하면 정상 작동한다")
    @Test
    fun givenValid_whenFindAllFetchUser_thenReturn() {
        // given
        val userRole = createAndSaveUserRole(USER)

        // when
        val foundUserRoles = userRoleRepository.findAllFetchUser(userRole.user.id!!)

        // then
        assertAll(
            {
                assertThat(foundUserRoles)
                    .extracting("id", "role", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        Tuple.tuple(userRole.id, userRole.role, userRole.status,
                            userRole.createDateTime, userRole.updateDateTime)
                    )
            },
            {
                assertThat(foundUserRoles)
                    .extracting("user")
                    .extracting("id", "username", "imageUrl", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        Tuple.tuple(userRole.user.id, userRole.user.username,
                            userRole.user.imageUrl, userRole.user.status,
                            userRole.user.createDateTime, userRole.user.updateDateTime)
                    )
            }
        )
    }

    private fun createAndSaveUserRole(role: Role): UserRoleEntity {
        val user = userRepository.save(UserEntity(
            username = "tester",
            email = "tester@woomulwoomul.com",
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
        ))
        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }
}