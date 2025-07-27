-- Retrospektif özelliği için veritabanı tabloları

-- Retrospektif oturumları tablosu
CREATE TABLE retrospective_sessions (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    sprint_number INTEGER NOT NULL,
    template VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Retrospektif öğeleri tablosu
CREATE TABLE retrospective_items (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- POSITIVE, NEGATIVE, ACTION
    votes INTEGER NOT NULL DEFAULT 0,
    session_id BIGINT NOT NULL REFERENCES retrospective_sessions(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Retrospektif oyları tablosu
CREATE TABLE retrospective_votes (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES retrospective_items(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(item_id, user_id) -- Bir kullanıcı aynı item'a sadece bir kez oy verebilir
);

-- Aksiyon öğeleri tablosu
CREATE TABLE action_items (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    session_id BIGINT NOT NULL REFERENCES retrospective_sessions(id) ON DELETE CASCADE,
    assignee_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    priority VARCHAR(10) NOT NULL, -- HIGH, MEDIUM, LOW
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- İndeksler
CREATE INDEX idx_retrospective_sessions_team_id ON retrospective_sessions(team_id);
CREATE INDEX idx_retrospective_sessions_created_by ON retrospective_sessions(created_by);
CREATE INDEX idx_retrospective_sessions_sprint ON retrospective_sessions(team_id, sprint_number);

CREATE INDEX idx_retrospective_items_session_id ON retrospective_items(session_id);
CREATE INDEX idx_retrospective_items_author_id ON retrospective_items(author_id);
CREATE INDEX idx_retrospective_items_type ON retrospective_items(type);
CREATE INDEX idx_retrospective_items_votes ON retrospective_items(votes DESC);

CREATE INDEX idx_retrospective_votes_item_id ON retrospective_votes(item_id);
CREATE INDEX idx_retrospective_votes_user_id ON retrospective_votes(user_id);

CREATE INDEX idx_action_items_session_id ON action_items(session_id);
CREATE INDEX idx_action_items_assignee_id ON action_items(assignee_id);
CREATE INDEX idx_action_items_status ON action_items(status);
CREATE INDEX idx_action_items_priority ON action_items(priority);
