-- schemaName is alias. It will be replaced with actual types in runtime
CREATE SCHEMA IF NOT EXISTS schemaName;

CREATE TABLE IF NOT EXISTS schemaName.locks
(
    id        BIGSERIAL PRIMARY KEY,
    key       VARCHAR     NOT NULL UNIQUE,
    locked_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX CONCURRENTLY IF NOT EXISTS locks_key_idx ON schemaName.locks (key);