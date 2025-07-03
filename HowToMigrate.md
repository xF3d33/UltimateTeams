# How to migrate from v3.x to v4.x
###  Before starting, be sure you are on version 3.3 (otherwise update first)
### Also backup the plugin's folder, just to be sure

---

- Delete the old jar and all the files on the plugin's folder and upload the latest version
- if you are currently using SQLITE or H2 **DON'T** delete the database file, instead rename it from 
"UltimateTeamsData.db" to "LegacyUltimateTeamsData.db" (for H2 do the same but don't change the
file's extension)
- Start the server, change the new config like the old one and then restart the server
- if you wish to use cross-server be sure to change the server's name in the config, otherwise
warps and homes won't work
- now use "/teamadmin migrate set <parameter\> <value\>"
replacing "parameter" and "value" with the following:
  - if you're using SQLITE/H2 just run "/teamadmin migrate set DATABASE_TYPE SQLITE/H2" and skip the rest of this section
  - parameter: DATABASE_TYPE, value: MYSQL/MARIADB/POSTGRESQL
  - DATABASE_HOST, the current database host
  - DATABASE_PORT, the current database port
  - DATABASE_NAME, the current database name
  - DATABASE_USERNAME, the current database user
  - DATABASE_PASSWORD, the current database password
  - PLAYERS_TABLE, the current database player's table
  - TEAMS_TABLE, the current database teams table

if you are using a remote database be sure to use either another database or other table names, otherwise data will be corrupted

- now just use "/teamadmin migrate start" and wait till it finished. 
- restart the server and enjoy


warps and team homes might not be fully migrated depeding to your setup
