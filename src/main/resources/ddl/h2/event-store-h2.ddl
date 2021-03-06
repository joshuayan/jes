-- schemaName & contentType are aliases. They will be replaced with actual types in runtime
CREATE SCHEMA IF NOT EXISTS schemaName;

-- noinspection SqlResolve

CREATE TABLE IF NOT EXISTS schemaName.event_store
(
    id   BIGSERIAL PRIMARY KEY,
    uuid UUID,
    data contentType NOT NULL
);

CREATE INDEX IF NOT EXISTS uuid_idx ON schemaName.event_store (uuid);