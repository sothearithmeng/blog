-- Initial schema, matching com.kshrd.blog.entity.Task (columns from
-- com.kshrd.blog.common.entity.BaseEntity: id, created_at, updated_at).
-- Flyway is the single source of truth for the schema in production; Hibernate only validates
-- against it (spring.jpa.hibernate.ddl-auto: validate). To change the schema, add a new
-- versioned migration (V2__..., V3__...) rather than editing this file.

CREATE TABLE tasks (
    id              UUID PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     VARCHAR(2000),
    completed       BOOLEAN NOT NULL DEFAULT FALSE,
    reporter_email  VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL
);
