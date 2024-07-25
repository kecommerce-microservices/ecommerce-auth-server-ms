CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(110) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    is_default BOOLEAN NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    version INT NOT NULL
);

CREATE INDEX idx_roles_default_not_deleted ON roles (is_default);

CREATE INDEX idx_roles_deleted_with_date ON roles (is_deleted, deleted_at);

-- this sql does not working with H2 database, i need remove h2 and use testcontainers with postgres
--CREATE INDEX idx_roles_default_not_deleted ON roles (is_default)
--WHERE is_default = TRUE AND is_deleted = FALSE;
--
--CREATE INDEX idx_roles_deleted_with_date ON roles (is_deleted, deleted_at)
--WHERE is_deleted = TRUE;