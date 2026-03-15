ALTER TABLE users ADD COLUMN refresh_token_id VARCHAR(36);
CREATE UNIQUE INDEX idx_users_refresh_token_id ON users (refresh_token_id) WHERE refresh_token_id IS NOT NULL;
