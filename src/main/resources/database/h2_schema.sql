-- Users table
CREATE TABLE IF NOT EXISTS %user_table% (
    uuid         CHAR(36)     NOT NULL PRIMARY KEY,
    username     VARCHAR(16)  NOT NULL,
    last_login   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    isBedrock    BOOLEAN      NOT NULL,
    bedrockUUID  VARCHAR(36),
    preferences  BLOB         NOT NULL
    );

-- Teams table
CREATE TABLE IF NOT EXISTS %team_table% (
    id    INT          PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(16)  NOT NULL,
    data  BLOB         NOT NULL
    );