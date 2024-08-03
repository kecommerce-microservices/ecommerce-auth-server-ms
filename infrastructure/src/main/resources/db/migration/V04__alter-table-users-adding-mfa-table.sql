CREATE TABLE users_mfa (
    id UUID PRIMARY KEY,
    mfa_enabled BOOLEAN NOT NULL,
    mfa_verified BOOLEAN NOT NULL,
    mfa_secret TEXT,
    device_name VARCHAR(255),
    device_verified BOOLEAN NOT NULL,
    mfa_type VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_until TIMESTAMP WITH TIME ZONE
);

ALTER TABLE users ADD COLUMN mfa_id UUID REFERENCES users_mfa(id);