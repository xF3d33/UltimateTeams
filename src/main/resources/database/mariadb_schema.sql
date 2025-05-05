# Set the storage engine
SET DEFAULT_STORAGE_ENGINE = INNODB;

# Enable foreign key constraints
SET FOREIGN_KEY_CHECKS = 1;

# Create the users table if it does not exist
CREATE TABLE IF NOT EXISTS `%user_table%`
(
    `uuid`        char(36)      NOT NULL UNIQUE PRIMARY KEY,
    `username`    varchar(16)   NOT NULL,
    `last_login`  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `isBedrock`   boolean       NOT NULL,
    `bedrockUUID` varchar(36),
    `preferences` longblob      NOT NULL
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;



# Create the teams table if it does not exist
CREATE TABLE IF NOT EXISTS `%team_table%`
(
    `id`   int                  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(16)          NOT NULL,
    `data` longblob             NOT NULL
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;