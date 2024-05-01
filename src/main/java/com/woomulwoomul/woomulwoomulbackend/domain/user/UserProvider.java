package com.woomulwoomul.woomulwoomulbackend.domain.user;

import com.woomulwoomul.woomulwoomulbackend.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_provider")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProvider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_provider_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProviderType providerType;

    @Column(nullable = false, length = 100)
    private String providerId;
}
