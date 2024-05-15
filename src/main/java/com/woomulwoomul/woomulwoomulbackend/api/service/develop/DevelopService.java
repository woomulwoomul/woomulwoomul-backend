package com.woomulwoomul.woomulwoomulbackend.api.service.develop;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DevelopService {

    @Value("${api.name}")
    private String serverName;

    /**
     * 서버명 조회
     * @retrun 서버명
     */
    public String getServerName() {
        return serverName;
    }
}
