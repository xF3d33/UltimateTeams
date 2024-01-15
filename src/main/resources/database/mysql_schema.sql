# Set the storage engine
SET DEFAULT_STORAGE_ENGINE = INNODB;

# Enable foreign key constraints
SET FOREIGN_KEY_CHECKS = 1;

# Create the users table if it does not exist
CREATE TABLE IF NOT EXISTS `%user_table%`
(
    `id`          int         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `uuid`        char(36)    NOT NULL UNIQUE,
    `username`    varchar(16) NOT NULL,
    `isBedrock`   boolean     NOT NULL,
    `bedrockUUID` varchar(36),
    `canChatSpy`  boolean     NOT NULL,
    `data`        longblob    NOT NULL
) CHARACTER SET utf8
    COLLATE utf8_unicode_ci;

# Create the teams table if it does not exist
CREATE TABLE IF NOT EXISTS `%team_table%`
(
    `id`   int         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `uuid` varchar(36) NOT NULL UNIQUE,
    `name` varchar(16) NOT NULL,
    `data` longblob    NOT NULL
) CHARACTER SET utf8
    COLLATE utf8_unicode_ci;