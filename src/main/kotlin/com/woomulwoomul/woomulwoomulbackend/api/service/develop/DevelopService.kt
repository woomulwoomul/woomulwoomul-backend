package com.woomulwoomul.woomulwoomulbackend.api.service.develop

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.TESTER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.config.auth.JwtProvider
import com.woomulwoomul.woomulwoomulbackend.domain.follow.FollowRepository
import com.woomulwoomul.woomulwoomulbackend.domain.question.*
import com.woomulwoomul.woomulwoomulbackend.domain.user.*
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DevelopService(
    @Value("\${api.name}") private val serverName: String,
    @PersistenceContext private val entityManager: EntityManager,
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val categoryRepository: CategoryRepository,
    private val questionRepository: QuestionRepository,
    private val questionCategoryRepository: QuestionCategoryRepository,
    private val answerRepository: AnswerRepository,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val followRepository: FollowRepository,
    private val jwtProvider: JwtProvider,
) {

    private val TESTER_CONST = "tester"

    /**
     * 서버명 조회
     * @return 서버명
     */
    fun getServerName(): String {
        return serverName
    }

    /**
     * 테스터 토큰 조회
     * @param testerId 테스터 ID
     * @throws TESTER_NOT_FOUND 404
     * @return 헤더
     */
    fun getTesterToken(testerId: Long): HttpHeaders {
        val tester = userRepository.findByNickname(TESTER_CONST.plus(testerId))
            ?: throw CustomException(TESTER_NOT_FOUND)

        return jwtProvider.createToken(tester.id!!)
    }

    /**
     * 데이터 초기화 및 테스트 데이터 주입
     */
    @Transactional
    fun resetAndInject() {
        resetDatabase()

        val admin = injectAdmin()
        val categories = injectCategories(admin)
        val questions = injectQuestions(admin)
        injectQuestionCategories(categories, questions)

        val users = injectUsers()
        val answers = injectAnswers()
        injectQuestionAnswers(users.subList(0, 9), questions, answers)
    }

    /**
     * 관리자 주입
     */
    private fun injectAdmin(): UserEntity {
        val admin = userRepository.save(UserEntity(nickname = "관리자", email = "admin@woomulwoomul.com",
            imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640"))

        userRoleRepository.saveAll(listOf(
            UserRoleEntity(user = admin, role = Role.USER),
            UserRoleEntity(user = admin, role = Role.ADMIN)
        ))

        return admin
    }

    /**
     * 카테고리 주입
     */
    private fun injectCategories(admin: UserEntity): List<CategoryEntity> {
        return (1..10).map {
            CategoryEntity(admin = admin, name = "카테고리${it}")
        }.let { it ->
            categoryRepository.saveAll(it)
        }
    }

    /**
     * 질문 주입
     */
    private fun injectQuestions(admin: UserEntity): List<QuestionEntity> {
        val colorCodes = listOf("FF0000", "00FFFF", "0000FF", "00008B", "ADD8E6",
            "800080", "FFFF00", "00FF00", "FF00FF", "FFC0CB")
        return (1 .. 10).map {
            QuestionEntity(user = admin, text = "질문${it}번 입니다.", backgroundColor = colorCodes[it - 1])
        }.let {
            questionRepository.saveAll(it)
        }
    }

    /**
     * 질문 카테고리 주입
     */
    private fun injectQuestionCategories(
        categories: List<CategoryEntity>,
        questions: List<QuestionEntity>,
    ) {
        questions.forEach { question ->
            categories.shuffled().take((1..categories.size).random()).forEach { category ->
                questionCategoryRepository.save(QuestionCategoryEntity(question = question, category = category))
            }
        }
    }

    /**
     * 회원 주입
     */
    private fun injectUsers(): List<UserEntity> {
        val users = (1..100).map {
            UserEntity(
                nickname = "${TESTER_CONST}${it}",
                email = "${TESTER_CONST}${it}@woomulwoomul.com",
                imageUrl = "https://t1.kakaocdn.net/account_images/default_profile.jpeg.twg.thumb.R640x640",
                introduction = "소개${it}"
            )
        }.also { userRepository.saveAll(it) }

        userRoleRepository.saveAll(users.map { UserRoleEntity(user = it, role = Role.USER) })

        return users
    }

    /**
     * 답변 주입
     */
    private fun injectAnswers(): List<AnswerEntity> {
        return (1 .. 50).map {
            AnswerEntity(text = "답변${it}번 입니다.", imageUrl = "")
        }.also { answerRepository.saveAll(it) }
    }

    private fun injectQuestionAnswers(
        users: List<UserEntity>,
        questions: List<QuestionEntity>,
        answers: List<AnswerEntity>,
    ) {
        // TODO
    }

    /**
     * DB 초기화
     */
    private fun resetDatabase() {
        entityManager.createNativeQuery("DELETE FROM notification").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM question_answer").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM answer").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM question_category").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM category").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM question").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM follow").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM user_visit").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM user_provider").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM user_role").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM withdraw_user").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate()

        entityManager.createNativeQuery("ALTER TABLE notification AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE question_answer AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE answer AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE question_category AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE question AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE follow AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE user_visit AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE user_provider AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE user_role AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE withdraw_user AUTO_INCREMENT = 1").executeUpdate()
        entityManager.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate()
    }
}