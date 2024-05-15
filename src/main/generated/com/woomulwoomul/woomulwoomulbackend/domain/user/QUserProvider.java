package com.woomulwoomul.woomulwoomulbackend.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserProvider is a Querydsl query type for UserProvider
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserProvider extends EntityPathBase<UserProvider> {

    private static final long serialVersionUID = -690129168L;

    public static final QUserProvider userProvider = new QUserProvider("userProvider");

    public final com.woomulwoomul.woomulwoomulbackend.domain.base.QBaseEntity _super = new com.woomulwoomul.woomulwoomulbackend.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDateTime = _super.createDateTime;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<ProviderType> provider = createEnum("provider", ProviderType.class);

    public final StringPath providerId = createString("providerId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDateTime = _super.updateDateTime;

    public QUserProvider(String variable) {
        super(UserProvider.class, forVariable(variable));
    }

    public QUserProvider(Path<? extends UserProvider> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserProvider(PathMetadata metadata) {
        super(UserProvider.class, metadata);
    }

}

