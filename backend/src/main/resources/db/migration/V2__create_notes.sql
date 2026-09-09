CREATE TABLE notes (
    id         TEXT PRIMARY KEY,
    user_id    TEXT NOT NULL REFERENCES users (id),
    title      TEXT NOT NULL,
    content    TEXT NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    version    BIGINT NOT NULL
);

CREATE INDEX idx_notes_user_id ON notes (user_id);
