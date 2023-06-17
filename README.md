# UltimateTeams
UltimateTeams is a light-weight teams plugin for Minecraft servers running Spigot and most of its forks!

UltimateTeams does not support any grief prevention tools such as land claiming or securing containers within your team.

UltimateTeams DOES however offer the ability to disable friendly fire within your team!

## Database
UltimateTeams does support both SQLite and MySQL, check the config for further information

## /team command
Aliases: `/team` 
  
The `/team` command is the main command of the plugin, with `/team` you can do the following:
* `/team create <name>` - Creates A new team if not already in one
* `/team disband` - If you are the team owner, this will destroy your team
* `/team leave` - If you are in a team, this will remove you from it
* `/team invite <player>` - Will invite a player to your team if they are not already in one
* `/team join` - Will add you to a team that you have been invited too.
* `/team kick <player>` - Will kick a player from your team
* `/team info` - Will display information about your current team
* `/team list` - Will list all teams in the server
* `/team prefix <prefix>` - Will change the prefix for your team in chat
* `/team ally [add|remove] <team-name>` - Will either add or remove an allied team to yours
* `/team enemy [add|remove] <team-name>` - Will either add or remove an enemy team to yours
* `/team pvp` - Will toggle the friendly fire status for your team
* `/team [sethome|home]` - Will set a team home location or teleport you or you team members to this location.

## /teamadmin command
Aliases: `/ta`

The `/teamadmin` command is purely for server admins only. 

4 arguments are implemented which are: 
* `/teamadmin save` - which will save all current team info to the `teams.yml` data file.  
* `/teamadmin reload` - This reloads the plugins `config.yml` & the `messages.yml` files from disk.
* `/teamadmin disband <team-name>` - This allows admins to delete any unauthorised teams.
* `/teamadmin about` - This give you an overview of the plugin's core information.

## /tc command
Aliases: /teamchat, /tchat, /tc

The `/tc` command is for the sole purpose of utilising the per team chat. The following syntax is accepted:

`/tc <message>` - This will send a message to only the members of YOUR team or the team you are in.

## Permissions
UltimateTeams comes with `14` permissions:
* `ultimateteams.*`
* `ultimateteams.team`
* `ultimateteams.admin`
* `ultimateteams.update`
* `ultimateteams.bypass`
* `ultimateteams.bypass.*`
* `ultimateteams.bypass.homecooldown`
* `ultimateteams.bypass.chatcooldown`
* `ultimateteams.bypass.pvp`
* `ultimateteams.maxteamsize.group1`
* `ultimateteams.maxteamsize.group2`
* `ultimateteams.maxteamsize.group3`
* `ultimateteams.maxteamsize.group4`
* `ultimateteams.maxteamsize.group5`
* `ultimateteams.maxteamsize.group6`

`ultimateteams.*` is a permission to allow access to ALL functions regardless of operator status.

`ultimateteams.team` is by default given to everyone so they can all create, edit and manage a team.  

`ultimateteams.admin` is by default given to server operators.

`ultimateteams.update` is the permission node to allow a player to see in game notifications if there is a plugin update available.

`ultimateteams.bypass` is the permission node to allow a player to bypass all protections and cooldowns.

`ultimateteams.bypass.*` is the permission node to allow a player to bypass all protections and cooldowns.

`ultimateteams.bypass.homecooldown` is the permission node to allow a player to bypass the home command cooldown.

`ultimateteams.bypass.chatcooldown` - is the permission node to allow a player to bypass the team chat command cooldown.

`ultimateteams.bypass.pvp` is the permission node to allow a player to bypass the friendly fire protections.

`ultimateteams.maxteamsize.group1` is the permission node to allow only group 1 size of team.

`ultimateteams.maxteamsize.group2` is the permission node to allow only group 2 size of team.

`ultimateteams.maxteamsize.group3` is the permission node to allow only group 3 size of team.

`ultimateteams.maxteamsize.group4` is the permission node to allow only group 4 size of team.

`ultimateteams.maxteamsize.group5` is the permission node to allow only group 5 size of team.

`ultimateteams.maxteamsize.group6` is the permission node to allow only group 6 size of team.

## Config
The max team size (by default is 8), can be managed in the `plugins/UltimateTeams/config.yml` file.

The max team allies (by default is 4), can be managed in the `plugins/UltimateTeams/config.yml` file.

The max team enemies (by default is 2), can be managed in the `plugins/UltimateTeams/config.yml` file.


## PlaceholderAPI
UltimateTeams exposes `8` external placeholders using `PlaceholderAPI` to enable the fetching of a players team name or the team prefix or if the team has friendly fire enabled or if the team has a home set.

The four available placeholders are:
* `%ultimateteams_teamName%`
* `%ultimateteams_teamPrefix%`
* `%ultimateteams_friendlyFire%`
* `%ultimateteams_teamHomeSet%`
* `%ultimateteams_teamMembersSize%`
* `%ultimateteams_teamAllySize%`
* `%ultimateteams_teamEnemySize%`
* `%ultimateteams_playerPointBalance%`

To be able to use these The latest release of [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) MUST be installed!  Without it, you can't use these placeholders.

###Please report any issues in GitHub.

###Thank you for using my plugin!
