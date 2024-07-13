package com.woomulwoomul.woomulwoomulbackend.api.service.develop

import com.woomulwoomul.woomulwoomulbackend.common.constant.ExceptionCode.TESTER_NOT_FOUND
import com.woomulwoomul.woomulwoomulbackend.common.response.CustomException
import com.woomulwoomul.woomulwoomulbackend.config.auth.JwtProvider
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryEntity
import com.woomulwoomul.woomulwoomulbackend.domain.question.CategoryRepository
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
        val users = injectUsers()

        val categories = injectCategories(admin)
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
        }
        userRepository.saveAll(users)

        val userRoles = users.map { UserRoleEntity(user = it, role = Role.USER) }
        userRoleRepository.saveAll(userRoles)

        return users
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