-- Team invite code migration
ALTER TABLE teams ADD COLUMN invite_code VARCHAR(8) UNIQUE;

-- Team member status migration
ALTER TABLE team_members ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Update existing members to ACTIVE status (they were already in the team)
UPDATE team_members SET status = 'ACTIVE' WHERE status = 'PENDING';

-- Create index for faster lookups
CREATE INDEX idx_teams_invite_code ON teams(invite_code);
CREATE INDEX idx_team_members_status ON team_members(status);
