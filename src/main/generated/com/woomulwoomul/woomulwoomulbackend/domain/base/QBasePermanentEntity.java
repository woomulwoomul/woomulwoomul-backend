package com.woomulwoomul.woomulwoomulbackend.domain.base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBasePermanentEntity is a Querydsl query type for BasePermanentEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBasePermanentEntity extends EntityPathBase<BasePermanentEntity> {

    private static final long serialVersionUID = -976699246L;

    public static final QBasePermanentEntity basePermanentEntity = new QBasePermanentEntity("basePermanentEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDateTime = _super.createDateTime;

    public final EnumPath<ServiceStatus> serviceStatus = createEnum("serviceStatus", ServiceStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDateTime = _super.updateDateTime;

    public QBasePermanentEntity(String variable) {
        super(BasePermanentEntity.class, forVariable(variable));
    }

    public QBasePermanentEntity(Path<? extends BasePermanentEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBasePermanentEntity(PathMetadata metadata) {
        super(BasePermanentEntity.class, metadata);
    }

}

