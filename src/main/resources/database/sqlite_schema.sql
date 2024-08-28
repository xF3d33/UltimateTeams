CREATE TABLE IF NOT EXISTS `%user_table%`
(
    `id`          integer     NOT NULL PRIMARY KEY,
    `uuid`        char(36)    NOT NULL UNIQUE,
    `username`    varchar(16) NOT NULL,
    `isBedrock`   boolean     NOT NULL,
    `bedrockUUID` varchar(36),
    `canChatSpy`  boolean     NOT NULL
    );

CREATE TABLE IF NOT EXISTS `%team_table%`
(
    `id`   integer         NOT NULL PRIMARY KEY,
    `uuid` varchar(36)     NOT NULL UNIQUE,
    `name` varchar(16)     NOT NULL,
    `data` longblob        NOT NULL
    );