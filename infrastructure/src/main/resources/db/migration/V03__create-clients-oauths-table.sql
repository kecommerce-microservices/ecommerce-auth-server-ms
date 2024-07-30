CREATE TABLE clients (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    client_id VARCHAR(255) UNIQUE,
    client_secret VARCHAR(255)
);

CREATE TABLE authentication_methods (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    authentication_method VARCHAR(255),
    client_id VARCHAR(38),
    CONSTRAINT fk_client_id_authentication_method FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE client_settings (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    require_authorization_consent BOOLEAN,
    require_proof_key BOOLEAN,
    client_id VARCHAR(38) UNIQUE,
    CONSTRAINT fk_client_id_client_settings FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE grant_types (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    grant_type VARCHAR(255),
    client_id VARCHAR(38),
    CONSTRAINT fk_client_id_grant_types FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE redirect_urls (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    client_id VARCHAR(38),
    CONSTRAINT fk_client_id_redirect_urls FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE scopes (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    scope VARCHAR(255),
    client_id VARCHAR(38),
    CONSTRAINT fk_client_id_scopes FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE token_settings (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    reuse_refresh_tokens BOOLEAN,
    access_token_ttl BIGINT,
    refresh_token_ttl BIGINT,
    client_id VARCHAR(38) UNIQUE,
    CONSTRAINT fk_client_id_token_settings FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE authorizations (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    registered_client_id VARCHAR(255) NOT NULL,
    principal_name VARCHAR(255) NOT NULL,
    authorization_grant_type VARCHAR(255) NOT NULL,
    authorized_scopes VARCHAR(1000),
    attributes TEXT,
    state VARCHAR(500)
);
CREATE INDEX idx_authorizations_state ON authorizations (state);

CREATE TABLE authorization_codes (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    authorization_id VARCHAR(255) NOT NULL,
    code_value TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata TEXT,
    CONSTRAINT fk_authorization_code_authorization
        FOREIGN KEY (authorization_id) REFERENCES authorizations (id)
);
CREATE INDEX idx_authorization_codes_code_value ON authorization_codes (code_value);

CREATE TABLE access_tokens (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    authorization_id VARCHAR(255) NOT NULL,
    token_value TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata TEXT,
    type VARCHAR(255),
    scopes VARCHAR(1000),
    CONSTRAINT fk_access_token_authorization
        FOREIGN KEY (authorization_id) REFERENCES authorizations (id)
);
CREATE INDEX idx_access_tokens_token_value ON access_tokens (token_value);

CREATE TABLE refresh_tokens (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    authorization_id VARCHAR(255) NOT NULL,
    refresh_value TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata TEXT,
    CONSTRAINT fk_refresh_token_authorization
        FOREIGN KEY (authorization_id) REFERENCES authorizations (id)
);
CREATE INDEX idx_refresh_tokens_refresh_value ON refresh_tokens (refresh_value);

CREATE TABLE oidc_id_tokens (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    authorization_id VARCHAR(255) NOT NULL,
    oidc_value TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata TEXT,
    claims TEXT,
    CONSTRAINT fk_oidc_id_token_authorization
        FOREIGN KEY (authorization_id) REFERENCES authorizations (id)
);
CREATE INDEX idx_oidc_id_tokens_oidc_value ON oidc_id_tokens (oidc_value);

CREATE TABLE user_codes (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    authorization_id VARCHAR(255) NOT NULL,
    user_code_value TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata TEXT,
    CONSTRAINT fk_user_code_authorization
        FOREIGN KEY (authorization_id) REFERENCES authorizations (id)
);
CREATE INDEX idx_user_codes_user_code_value ON user_codes (user_code_value);

CREATE TABLE device_codes (
    id VARCHAR(38) NOT NULL PRIMARY KEY,
    authorization_id VARCHAR(255) NOT NULL,
    device_code_value TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata TEXT,
    CONSTRAINT fk_device_code_authorization
        FOREIGN KEY (authorization_id) REFERENCES authorizations (id)
);
CREATE INDEX idx_device_codes_device_code_value ON device_codes (device_code_value);