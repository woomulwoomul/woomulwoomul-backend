package com.woomulwoomul.woomulwoomulbackend.domain.user

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

    @DisplayName("회원 식별자로 모든 회원 권한을 조회하면 정상 작동한다")
    @Test
    fun givenValid_whenFindAllFetchUser_thenReturn() {
        // given
//        val user = createAndSaveUser(contact)
//        val role = Role.USER
//        val userRole = createAndSaveUserRole(user, role)
//
//        // when
//        val foundUserRoles = userRoleRepository.findAllFetchUser(user.id!!)
//
//        // then
//        assertAll(
//            {
//                assertThat(foundUserRoles)
//                    .extracting("id", "role", "createDateTime", "updateDateTime", "user")
//                    .containsExactly(
//                        Tuple.tuple(userRole.id, userRole.role, userRole.createDateTime, userRole.updateDateTime,
//                            userRole.user)
//                    )
//            },
//            {
//                assertThat(foundUserRoles)
//                    .extracting("user")
//                    .extracting("id", "username", "password", "imageUrl", "temporaryPasswordYn", "createDateTime",
//                        "updateDateTime", "contact")
//                    .containsExactly(
//                        Tuple.tuple(user.id, user.username, user.password, user.imageUrl, user.temporaryPasswordYn,
//                            user.createDateTime, user.updateDateTime, user.contact)
//                    )
//            }
//        )
    }

    private fun createAndSaveUserRole(user: User, role: Role): UserRole {
        return userRoleRepository.save(UserRole(user = user, role = role))
    }
}