package com.woomulwoomul.woomulwoomulbackend.domain.question

import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CategoryRepositoryTest(
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
) {

    @DisplayName("전체 카테고리 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAll_thenReturn() {
        // given
        val adminRole = createAndSaveUserRole(Role.ADMIN)
        val categories = listOf(
            createAndSaveCategory(adminRole.user, "카테고리1"),
            createAndSaveCategory(adminRole.user, "카테고리2"),
            createAndSaveCategory(adminRole.user, "카테고리3"),
            createAndSaveCategory(adminRole.user, "카테고리4"),
            createAndSaveCategory(adminRole.user, "카테고리5")
        )
        val pageFrom = 1L
        val pageSize = 2L

        // when
        val foundCategories = categoryRepository.findAll(pageFrom, pageSize)

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
                            categories[1].id, categories[1].name, categories[1].status, categories[1].createDateTime,
                            categories[1].updateDateTime
                        ),
                        tuple(
                            categories[2].id, categories[2].name, categories[2].status, categories[2].createDateTime,
                            categories[2].updateDateTime
                        )
                    )
            }
        )
    }

    @DisplayName("카테고리가 없는데 전체 카테고리 조회가 정상 작동한다")
    @Test
    fun givenEmpty_whenFindAll_thenReturn() {
        // given
        val pageFrom = 0L
        val pageSize = 1L

        // when
        val categories = categoryRepository.findAll(pageFrom, pageSize)

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
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"
            ))

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }
}