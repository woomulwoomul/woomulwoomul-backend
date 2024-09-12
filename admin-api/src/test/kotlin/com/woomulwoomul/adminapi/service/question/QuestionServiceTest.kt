package com.woomulwoomul.adminapi.service.question

import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.domain.question.CategoryEntity
import com.woomulwoomul.core.domain.question.CategoryRepository
import com.woomulwoomul.core.domain.question.QuestionCategoryRepository
import com.woomulwoomul.core.domain.question.QuestionRepository
import com.woomulwoomul.core.domain.user.*
import org.assertj.core.api.Assertions
import org.assertj.core.groups.Tuple
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
class QuestionServiceTest(
    @Autowired private val questionService: QuestionService,
    @Autowired private val questionRepository: QuestionRepository,
    @Autowired private val questionCategoryRepository: QuestionCategoryRepository,
    @Autowired private val categoryRepository: CategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @DisplayName("전체 카테고리 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindAllCategories_thenReturn() {
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
        val result = questionService.getAllCategories(pageOffsetRequest)

        // then
        assertAll(
            {
                Assertions.assertThat(result.total).isEqualTo(categories.size.toLong())
            },
            {
                Assertions.assertThat(result.data)
                    .extracting("id", "name")
                    .containsExactly(
                        Tuple.tuple(categories[2].id, categories[2].name),
                        Tuple.tuple(categories[1].id, categories[1].name),
                    )
            }
        )
    }

    private fun createAndSaveCategory(
        user: UserEntity,
        name: String = "카테고리"
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
            )
        )

        return userRoleRepository.save(UserRoleEntity(user = user, role = role))
    }
}