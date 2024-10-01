package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.request.PageCursorRequest
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.domain.base.ServiceStatus
import com.woomulwoomul.core.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CategoryRepositoryTest(
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
) {

    @DisplayName("전체 카테고리 커서 페이징 조회가 정상 작동한다")
    @Test
    fun givenValidCursorRequest_whenFindAll_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )
        val pageCursorRequest = PageCursorRequest.of(categories[2].id, 2)

        // when
        val foundCategories = categoryRepository.findAll(pageCursorRequest)

        // then
        assertAll(
            {
                assertThat(foundCategories.total).isEqualTo(categories.size.toLong())
            },
            {
                assertThat(foundCategories.data)
                    .extracting("id", "name", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(
                            categories[2].id, categories[2].name, categories[2].status, categories[2].createDateTime,
                            categories[2].updateDateTime
                        ),
                        tuple(
                            categories[1].id, categories[1].name, categories[1].status, categories[1].createDateTime,
                            categories[1].updateDateTime
                        )
                    )
            }
        )
    }

    @DisplayName("카테고리가 없는데 전체 카테고리 커서 페이징 조회를 하면 정상 작동한다")
    @Test
    fun givenEmptyCursorRequest_whenFindAll_thenReturn() {
        // given
        val pageCursorRequest = PageCursorRequest.of(null, null)

        // when
        val categories = categoryRepository.findAll(pageCursorRequest)

        // then
        assertAll(
            {
                assertThat(categories.total).isEqualTo(0L)
            },
            {
                assertThat(categories.data).isEqualTo(emptyList<CategoryEntity>())
            }
        )
    }

    @DisplayName("전체 카테고리 오프셋 페이징 조회가 정상 작동한다")
    @Test
    fun givenValidOffsetRequest_whenFindAll_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )
        val pageOffsetRequest = PageOffsetRequest.of(2, 2)

        // when
        val foundCategories = categoryRepository.findAll(pageOffsetRequest)

        // then
        assertAll(
            {
                assertThat(foundCategories.total).isEqualTo(categories.size.toLong())
            },
            {
                assertThat(foundCategories.data)
                    .extracting("id", "name", "status", "createDateTime", "updateDateTime")
                    .containsExactly(
                        tuple(
                            categories[2].id, categories[2].name, categories[2].status, categories[2].createDateTime,
                            categories[2].updateDateTime
                        ),
                        tuple(
                            categories[1].id, categories[1].name, categories[1].status, categories[1].createDateTime,
                            categories[1].updateDateTime
                        ),
                    )
            }
        )
    }

    @DisplayName("카테고리가 없는데 전체 카테고리 오프셋 페이징 조회를 하면 정상 작동한다")
    @Test
    fun givenEmptyOffsetRequest_whenFindAll_thenReturn() {
        // given
        val pageCursorRequest = PageCursorRequest.of(null, null)

        // when
        val categories = categoryRepository.findAll(pageCursorRequest)

        // then
        assertAll(
            {
                assertThat(categories.total).isEqualTo(0L)
            },
            {
                assertThat(categories.data).isEqualTo(emptyList<CategoryEntity>())
            }
        )
    }

    @ParameterizedTest(name = "[{index}] 카테고리 ID로 {0} 상태인 카테고리를 {1} 상태 조회를 하면 정상 작동한다")
    @MethodSource("providerFind")
    @DisplayName("카테고리 ID로 특정 상태인 카테고리를 조회하면 정상 작동한다")
    fun givenProvider_whenFind_thenReturn(status: ServiceStatus, statusesQuery: List<ServiceStatus>) {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")
        category.updateStatus(status)

        // when
        val foundCategory = categoryRepository.find(category.id!!, statusesQuery)

        // then
        assertAll(
            {
                assertThat(foundCategory)
                    .extracting("id", "name", "status", "createDateTime", "updateDateTime")
                    .containsExactly(category.id, category.name, category.status, category.createDateTime,
                        category.updateDateTime)
            },
            {
                assertThat(foundCategory)
                    .extracting("admin")
                    .extracting("id", "nickname", "email", "imageUrl", "introduction", "status", "createDateTime",
                        "updateDateTime")
                    .containsExactly(adminRole.user.id, adminRole.user.nickname, adminRole.user.email,
                        adminRole.user.imageUrl, adminRole.user.introduction, adminRole.user.status,
                        adminRole.user.createDateTime, adminRole.user.updateDateTime)
            }
        )
    }

    @DisplayName("존재하지 않는 카테고리 ID로 카테고리 조회를 하면 정상 작동한다")
    @Test
    fun givenNonExisting_whenFind_thenReturn() {
        // given
        val categoryId = Long.MAX_VALUE

        // when
        val category = categoryRepository.find(categoryId)

        // then
        assertThat(category).isNull()
    }

    @DisplayName("카테고리 ID들로 카테고리 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindByIds_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )

        // when
        val foundCategories = categoryRepository.findByIds(categories.stream()
            .map { it.id!! }
            .toList())

        // then
        assertThat(foundCategories)
            .extracting("id", "name", "status", "createDateTime", "updateDateTime")
            .containsExactly(
                tuple(categories[0].id, categories[0].name, categories[0].status, categories[0].createDateTime,
                    categories[0].updateDateTime),
                tuple(categories[1].id, categories[1].name, categories[1].status, categories[1].createDateTime,
                    categories[1].updateDateTime),
                tuple(categories[2].id, categories[2].name, categories[2].status, categories[2].createDateTime,
                    categories[2].updateDateTime),
                tuple(categories[3].id, categories[3].name, categories[3].status, categories[3].createDateTime,
                    categories[3].updateDateTime),
                tuple(categories[4].id, categories[4].name, categories[4].status, categories[4].createDateTime,
                    categories[4].updateDateTime)
            )
    }

    @DisplayName("카테고리명으로 카테고리 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val category = createAndSaveCategory(adminRole.user, "카테고리")

        // when
        val foundCategory = categoryRepository.find(category.name)

        // then
        assertThat(foundCategory)
            .extracting("id", "name", "status", "createDateTime", "updateDateTime")
            .containsExactly(category.id, category.name, category.status, category.createDateTime,
                category.updateDateTime)
    }

    @ParameterizedTest(name = "[{index}] 카테고리명으로 카테고리 존재 여부 조회를 하면 {0}를 반환한다")
    @MethodSource("providerExists")
    @DisplayName("카테고리명으로 카테고리 존재 여부 조회가 정상 작동한다")
    fun givenProviderCategoryName_whenExists_thenReturn(expected: Boolean) {
        // given
        val categoryName = if (expected) {
            val adminRole = createAndSaveUserRole(Role.ADMIN)
            createAndSaveCategory(adminRole.user, "카테고리").name
        } else {
            "없음"
        }

        // when
        val result = categoryRepository.exists(categoryName)

        // then
        assertThat(result).isEqualTo(expected)
    }

    private fun createAndSaveCategory(
        user: UserEntity,
        name: String = "카테고리명"
    ): CategoryEntity {
        return categoryRepository.save(CategoryEntity(name = name, admin = user))
    }

    private fun createAndSaveUserRole(
        role: Role,
        nickname: String = "tester",
        email: String = "tester@woomulwoomul.com",
    ): UserRoleEntity {
        val user = userRepository.save(
            UserEntity(
                nickname = nickname,
                email = email,
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg"
            ))

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }

    companion object {
        @JvmStatic
        fun providerExists(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
            )
        }

        @JvmStatic
        fun providerFind(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE)),
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE, ServiceStatus.USER_DEL)),
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.ACTIVE, listOf(ServiceStatus.ACTIVE, ServiceStatus.USER_DEL, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL, ServiceStatus.ACTIVE)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.USER_DEL, listOf(ServiceStatus.USER_DEL, ServiceStatus.ACTIVE, ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL, ServiceStatus.ACTIVE)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL, ServiceStatus.USER_DEL)),
                Arguments.of(ServiceStatus.ADMIN_DEL, listOf(ServiceStatus.ADMIN_DEL, ServiceStatus.ACTIVE, ServiceStatus.USER_DEL)),
            )
        }
    }
}