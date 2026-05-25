ALTER TABLE users
    ADD COLUMN email_confirm_token      VARCHAR(64),
    ADD COLUMN email_confirm_expires_at TIMESTAMPTZ,
    ADD COLUMN email_confirmed_at       TIMESTAMPTZ;

ALTER TABLE lawyer_profiles
    ADD COLUMN onboarding_completed_at TIMESTAMPTZ;

ALTER TABLE verification_attempts
    ADD COLUMN validation_type VARCHAR(20) NOT NULL DEFAULT 'admin-manual';
