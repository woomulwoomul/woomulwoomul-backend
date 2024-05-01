package com.woomulwoomul.woomulwoomulbackend.domain.user;

import com.woomulwoomul.woomulwoomulbackend.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_provider_id", nullable = false)
    private UserProvider userProvider;

    @Column(nullable = false, length = 15, unique = true)
    private String username;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private int visitCnt;
}
