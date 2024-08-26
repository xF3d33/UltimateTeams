-- Create the users table if it does not exist
CREATE TABLE IF NOT EXISTS user_table
(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    username VARCHAR(16) NOT NULL,
    isBedrock BOOLEAN NOT NULL,
    bedrockUUID VARCHAR(36),
    canChatSpy BOOLEAN NOT NULL
);

-- Create the teams table if it does not exist
CREATE TABLE IF NOT EXISTS team_table
(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    name VARCHAR(16) NOT NULL,
    data BLOB NOT NULL
);