package com.woomulwoomul.woomulwoomulbackend.api.controller.develop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woomulwoomul.woomulwoomulbackend.api.service.develop.DevelopService;
import com.woomulwoomul.woomulwoomulbackend.config.auth.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.woomulwoomul.woomulwoomulbackend.common.response.SuccessCode.SERVER_OK;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(DevelopController.class)
@AutoConfigureMockMvc(addFilters = false)
class DevelopControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private DevelopService developService;

    @Test
    @DisplayName("헬스 체크를 하면 200을 반환한다")
    void givenValid_whenHealthCheck_thenReturn200() throws Exception {
        // given
        String serverName = "Woomulwoomul Test";

        when(developService.getServerName())
                .thenReturn(serverName);

        // when & then
        mockMvc.perform(
                get("/api/health")
        ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.code").value(SERVER_OK.name()),
                        jsonPath("$.message").value(serverName.concat(" ").concat(SERVER_OK.getMessage()))
                );
    }

}