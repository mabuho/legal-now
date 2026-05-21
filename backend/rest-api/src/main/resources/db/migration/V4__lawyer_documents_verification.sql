-- Phase 5a: lawyer documents, verification attempts, consultation_rate nullable

-- =====================================================================
-- lawyer_documents
-- =====================================================================
CREATE TABLE lawyer_documents (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lawyer_id     UUID NOT NULL REFERENCES lawyer_profiles(user_id) ON DELETE CASCADE,
    doc_type      VARCHAR(32) NOT NULL,
    file_path     VARCHAR(512) NOT NULL,
    file_name     VARCHAR(255) NOT NULL,
    content_type  VARCHAR(128),
    size_bytes    BIGINT,
    uploaded_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT lawyer_documents_doc_type_chk CHECK (doc_type IN ('cedula','titulo','otro'))
);

CREATE INDEX idx_lawyer_documents_lawyer ON lawyer_documents(lawyer_id);

-- =====================================================================
-- verification_attempts
-- =====================================================================
CREATE TABLE verification_attempts (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lawyer_id     UUID NOT NULL REFERENCES lawyer_profiles(user_id) ON DELETE CASCADE,
    status        VARCHAR(16) NOT NULL,
    notes         TEXT,
    reviewed_by   UUID REFERENCES users(id) ON DELETE SET NULL,
    requested_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reviewed_at   TIMESTAMPTZ,
    CONSTRAINT verification_attempts_status_chk CHECK (status IN ('pending','approved','rejected'))
);

CREATE INDEX idx_verification_attempts_lawyer ON verification_attempts(lawyer_id);
CREATE INDEX idx_verification_attempts_status ON verification_attempts(status);

-- =====================================================================
-- Make consultation_rate nullable (deferred to Phase 6 — payments)
-- =====================================================================
ALTER TABLE lawyer_profiles ALTER COLUMN consultation_rate DROP NOT NULL;
ALTER TABLE lawyer_profiles DROP CONSTRAINT IF EXISTS lawyer_profiles_rate_chk;
ALTER TABLE lawyer_profiles ADD CONSTRAINT lawyer_profiles_rate_chk CHECK (consultation_rate IS NULL OR consultation_rate >= 0);
