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
        val userRoleEntity = createAndSaveUserRole(USER)

        // when
        val foundUserRoles = userRoleRepository.findAllFetchUser(userRoleEntity.userEntity.id!!)

        // then
        assertAll(
            {
                assertThat(foundUserRoles)
                    .extracting("id", "role", "serviceStatus", "createDateTime", "updateDateTime")
                    .containsExactly(
                        Tuple.tuple(userRoleEntity.id, userRoleEntity.role, userRoleEntity.serviceStatus,
                            userRoleEntity.createDateTime, userRoleEntity.updateDateTime)
                    )
            },
            {
                assertThat(foundUserRoles)
                    .extracting("userEntity")
                    .extracting("id", "username", "imageUrl", "serviceStatus", "createDateTime", "updateDateTime")
                    .containsExactly(
                        Tuple.tuple(userRoleEntity.userEntity.id, userRoleEntity.userEntity.username,
                            userRoleEntity.userEntity.imageUrl, userRoleEntity.userEntity.serviceStatus,
                            userRoleEntity.userEntity.createDateTime, userRoleEntity.userEntity.updateDateTime)
                    )
            }
        )
    }

    private fun createAndSaveUserRole(role: Role): UserRoleEntity {
        val userEntity = userRepository.save(UserEntity(
            username = "tester",
            email = "tester@woomulwoomul.com",
            imageUrl = "https://www.google.com"
        ))
        return userRoleRepository.save(UserRoleEntity(userEntity = userEntity, role = role))
    }
}