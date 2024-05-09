package com.woomulwoomul.woomulwoomulbackend.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.woomulwoomul.woomulwoomulbackend.domain.user.ProviderType.KAKAO;
import static com.woomulwoomul.woomulwoomulbackend.domain.user.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRoleRepositoryTest {

    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProviderRepository userProviderRepository;

    @DisplayName("회원 권한 조인 회원 전체 조회가 정상 작동한다")
    @Test
    void givenValid_whenFindAllFetchUser_thenReturn() {
        // given
        UserRole userRole = createAndSaveUserRole();

        // when
        List<UserRole> userRoles = userRoleRepository.findAllFetchUser(userRole.getUser().getId());

        // then
        assertThat(userRoles).containsExactly(userRole);
    }

    private UserRole createAndSaveUserRole() {
        UserProvider userProvider = userProviderRepository.save(UserProvider.builder()
                .provider(KAKAO)
                .providerId("1")
                .build());

        User user = userRepository.save(User.builder()
                .userProvider(userProvider)
                .username("tester")
                .imageUrl("")
                .build());

        return userRoleRepository.save(UserRole.builder()
                .user(user)
                .role(USER)
                .build());
    }
}