CREATE TABLE access_tokens (
    token      TEXT PRIMARY KEY,
    user_id    TEXT NOT NULL REFERENCES users (id),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE refresh_tokens (
    token      TEXT PRIMARY KEY,
    user_id    TEXT NOT NULL REFERENCES users (id),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_access_tokens_expires_at ON access_tokens (expires_at);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);
