CREATE TABLE mail_tokens (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    token VARCHAR(34) NOT NULL UNIQUE,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    type VARCHAR(150) NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INT NOT NULL
);

CREATE INDEX idx_mail_tokens_email ON mail_tokens(email);
CREATE INDEX idx_mail_tokens_token ON mail_tokens(token);