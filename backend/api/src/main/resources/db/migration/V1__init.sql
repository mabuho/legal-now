-- LegalNow initial schema
-- Phase 1 (MVP): users + lawyers + consultations + chat + payments + refresh tokens
-- Platform currency: MXN (single-currency phase 1)

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================================
-- users
-- =====================================================================
CREATE TABLE users (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email            VARCHAR(255) NOT NULL UNIQUE,
    password_hash    VARCHAR(255) NOT NULL,
    role             VARCHAR(16)  NOT NULL,
    name             VARCHAR(160) NOT NULL,
    avatar_url       VARCHAR(512),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT users_role_chk CHECK (role IN ('client', 'lawyer', 'admin'))
);

CREATE INDEX idx_users_role ON users(role);

-- =====================================================================
-- lawyer_profiles  (1:1 extension of users where role='lawyer')
-- =====================================================================
CREATE TABLE lawyer_profiles (
    user_id            UUID            PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    bar_id             VARCHAR(64),
    verified_at        TIMESTAMPTZ,
    bio                TEXT,
    consultation_rate  NUMERIC(10,2)   NOT NULL,
    languages          TEXT[]          NOT NULL DEFAULT '{}',
    created_at         TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT lawyer_profiles_rate_chk CHECK (consultation_rate >= 0)
);

CREATE INDEX idx_lawyer_profiles_verified_at ON lawyer_profiles(verified_at) WHERE verified_at IS NOT NULL;

-- =====================================================================
-- specializations (catalog)
-- =====================================================================
CREATE TABLE specializations (
    id    SMALLSERIAL  PRIMARY KEY,
    code  VARCHAR(64)  NOT NULL UNIQUE,
    name  VARCHAR(160) NOT NULL
);

-- =====================================================================
-- lawyer_specializations (M:N)
-- =====================================================================
CREATE TABLE lawyer_specializations (
    lawyer_id          UUID     NOT NULL REFERENCES lawyer_profiles(user_id) ON DELETE CASCADE,
    specialization_id  SMALLINT NOT NULL REFERENCES specializations(id) ON DELETE RESTRICT,
    PRIMARY KEY (lawyer_id, specialization_id)
);

CREATE INDEX idx_lawyer_specializations_spec ON lawyer_specializations(specialization_id);

-- =====================================================================
-- consultations
-- =====================================================================
CREATE TABLE consultations (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id       UUID         NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    lawyer_id       UUID         NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    status          VARCHAR(24)  NOT NULL DEFAULT 'pending',
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    scheduled_at    TIMESTAMPTZ,
    janus_room_id   BIGINT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT consultations_status_chk CHECK (
        status IN ('pending', 'accepted', 'rejected', 'scheduled', 'in_progress', 'completed', 'cancelled')
    ),
    CONSTRAINT consultations_client_lawyer_diff_chk CHECK (client_id <> lawyer_id)
);

CREATE INDEX idx_consultations_client ON consultations(client_id);
CREATE INDEX idx_consultations_lawyer ON consultations(lawyer_id);
CREATE INDEX idx_consultations_status ON consultations(status);
CREATE INDEX idx_consultations_scheduled_at ON consultations(scheduled_at);
CREATE UNIQUE INDEX uq_consultations_janus_room ON consultations(janus_room_id) WHERE janus_room_id IS NOT NULL;

-- =====================================================================
-- chat_sessions  (1:1 per consultation)
-- =====================================================================
CREATE TABLE chat_sessions (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    consultation_id    UUID         NOT NULL UNIQUE REFERENCES consultations(id) ON DELETE CASCADE,
    started_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    ended_at           TIMESTAMPTZ,
    recording_path     VARCHAR(512),
    transcript_path    VARCHAR(512),
    CONSTRAINT chat_sessions_time_chk CHECK (ended_at IS NULL OR ended_at >= started_at)
);

-- =====================================================================
-- chat_messages
-- =====================================================================
CREATE TABLE chat_messages (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id  UUID         NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    sender_id   UUID         NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    body        TEXT         NOT NULL,
    sent_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_messages_session_sent ON chat_messages(session_id, sent_at);
CREATE INDEX idx_chat_messages_sender ON chat_messages(sender_id);

-- =====================================================================
-- payments
-- =====================================================================
CREATE TABLE payments (
    id               UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    consultation_id  UUID            NOT NULL REFERENCES consultations(id) ON DELETE RESTRICT,
    provider         VARCHAR(24)     NOT NULL,
    amount           NUMERIC(10,2)   NOT NULL,
    currency         CHAR(3)         NOT NULL DEFAULT 'MXN',
    status           VARCHAR(24)     NOT NULL DEFAULT 'pending',
    provider_ref     VARCHAR(255),
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT payments_provider_chk CHECK (provider IN ('stripe', 'mercadopago')),
    CONSTRAINT payments_status_chk CHECK (
        status IN ('pending', 'authorized', 'captured', 'failed', 'refunded', 'cancelled')
    ),
    CONSTRAINT payments_amount_chk CHECK (amount >= 0)
);

CREATE INDEX idx_payments_consultation ON payments(consultation_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE UNIQUE INDEX uq_payments_provider_ref ON payments(provider, provider_ref) WHERE provider_ref IS NOT NULL;

-- =====================================================================
-- refresh_tokens
-- =====================================================================
CREATE TABLE refresh_tokens (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash   VARCHAR(255) NOT NULL UNIQUE,
    expires_at   TIMESTAMPTZ  NOT NULL,
    revoked_at   TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires ON refresh_tokens(expires_at) WHERE revoked_at IS NULL;

-- =====================================================================
-- updated_at trigger
-- =====================================================================
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_lawyer_profiles_updated_at
    BEFORE UPDATE ON lawyer_profiles
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_consultations_updated_at
    BEFORE UPDATE ON consultations
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
