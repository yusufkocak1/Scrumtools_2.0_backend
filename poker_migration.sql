-- Poker Sessions tablosu
CREATE TABLE IF NOT EXISTS poker_sessions (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL,
    story_title VARCHAR(255) NOT NULL,
    story_description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    final_estimate VARCHAR(10),
    created_by BIGINT NOT NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Poker Votes tablosu
CREATE TABLE IF NOT EXISTS poker_votes (
    id BIGSERIAL PRIMARY KEY,
    poker_session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    vote_value VARCHAR(10) NOT NULL,
    voted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_revealed BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (poker_session_id) REFERENCES poker_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(poker_session_id, user_id) -- Bir kullanıcı bir oturumda sadece bir oy verebilir
);

-- İndeksler
CREATE INDEX IF NOT EXISTS idx_poker_sessions_team_id ON poker_sessions(team_id);
CREATE INDEX IF NOT EXISTS idx_poker_sessions_status ON poker_sessions(status);
CREATE INDEX IF NOT EXISTS idx_poker_sessions_created_at ON poker_sessions(created_at);
CREATE INDEX IF NOT EXISTS idx_poker_votes_session_id ON poker_votes(poker_session_id);
CREATE INDEX IF NOT EXISTS idx_poker_votes_user_id ON poker_votes(user_id);
