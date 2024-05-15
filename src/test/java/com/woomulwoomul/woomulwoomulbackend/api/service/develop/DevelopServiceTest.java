package com.woomulwoomul.woomulwoomulbackend.api.service.develop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DevelopServiceTest {

    @Autowired private DevelopService developService;

    @DisplayName("서버명 조회가 정상 작동한다")
    @Test
    void givenValid_whenGetServerName_thenReturn() {
        // given
        String serverName = "Woomulwoomul Test";

        // when
        String gotServerName = developService.getServerName();

        // then
        assertThat(gotServerName).isEqualTo(serverName);
    }

}