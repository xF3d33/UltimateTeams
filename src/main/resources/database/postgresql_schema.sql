-- Create users table
CREATE TABLE IF NOT EXISTS users (
    uuid         CHAR(36)     PRIMARY KEY,
    username     VARCHAR(16)  NOT NULL,
    last_login   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    isBedrock    BOOLEAN      NOT NULL,
    bedrockUUID  VARCHAR(36),
    preferences  BYTEA        NOT NULL
    );

-- Create teams table
CREATE TABLE IF NOT EXISTS teams (
    id    SERIAL        PRIMARY KEY,
    name  VARCHAR(16)   NOT NULL,
    data  BYTEA         NOT NULL
    );