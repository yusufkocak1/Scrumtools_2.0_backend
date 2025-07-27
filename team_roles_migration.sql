-- TeamMember çoklu rol desteği için veritabanı migration

-- Mevcut team_members tablosundan role kolonunu kaldır (eğer varsa)
-- Bu işlem veri kaybına neden olabilir, production'da dikkatli olunmalı
ALTER TABLE team_members DROP COLUMN IF EXISTS role;

-- TeamMember rolleri için yeni tablo oluştur
CREATE TABLE team_member_roles (
    team_member_id BIGINT NOT NULL REFERENCES team_members(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (team_member_id, role)
);

-- İndeks ekle
CREATE INDEX idx_team_member_roles_team_member_id ON team_member_roles(team_member_id);
CREATE INDEX idx_team_member_roles_role ON team_member_roles(role);

-- Mevcut team_members verilerini korumak için
-- Tüm mevcut üyelere default MEMBER rolü ata
INSERT INTO team_member_roles (team_member_id, role)
SELECT id, 'MEMBER' FROM team_members
WHERE NOT EXISTS (
    SELECT 1 FROM team_member_roles tmr WHERE tmr.team_member_id = team_members.id
);

-- Takım yaratıcılarına ADMIN rolü ekle
INSERT INTO team_member_roles (team_member_id, role)
SELECT tm.id, 'ADMIN'
FROM team_members tm
JOIN teams t ON tm.team_id = t.id
WHERE tm.user_id = t.created_by
AND NOT EXISTS (
    SELECT 1 FROM team_member_roles tmr
    WHERE tmr.team_member_id = tm.id AND tmr.role = 'ADMIN'
);
