[![](https://jitpack.io/v/Loving11ish/CelestyTeams.svg)](https://jitpack.io/#Loving11ish/CelestyTeams)

# CelestyTeams
CelestyTeams is a light-weight teams plugin for Minecraft servers running Spigot and most of its forks!

CelestyTeams does not support any grief prevention tools such as land claiming or securing containers within your team.

CelestyTeams DOES however offer the ability to disable friendly fire within your team!

## /team command
Aliases: `/teams`, `/c`, `cl`  
  
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
* `/team ally [add|remove] <ally-owner>` - Will either add or remove an allied team to yours
* `/team enemy [add|remove] <ally-owner>` - Will either add or remove an enemy team to yours
* `/team pvp` - Will toggle the friendly fire status for your team
* `/team [sethome|home]` - Will set a team home location or teleport you or you team members to this location.

## /teamadmin command
Aliases: `/ca`, `cla`

The `/teamadmin` command is purely for server admins only. 

4 arguments are implemented which are: 
* `/teamadmin save` - which will save all current team info to the `teams.yml` data file.  
* `/teamadmin reload` - This reloads the plugins `config.yml` & the `messages.yml` files from disk.
* `/teamadmin disband <owner-name>` - This allows admins to delete any unauthorised teams.
* `/teamadmin about` - This give you an overview of the plugin's core information.

## /cc command
Aliases: /teamchat, /teamc, /cchat, /chat

The `/cc` command is for the sole purpose of utilising the per team chat. The following syntax is accepted:

`/cc <message>` - This will send a message to only the members of YOUR team or the team you are in.

## Permissions
CelestyTeams comes with `14` permissions:
* `celestyteams.*`
* `celestyteams.team`
* `celestyteams.admin`
* `celestyteams.update`
* `celestyteams.bypass`
* `celestyteams.bypass.*`
* `celestyteams.bypass.homecooldown`
* `celestyteams.bypass.chatcooldown`
* `celestyteams.bypass.pvp`
* `celestyteams.maxteamsize.group1`
* `celestyteams.maxteamsize.group2`
* `celestyteams.maxteamsize.group3`
* `celestyteams.maxteamsize.group4`
* `celestyteams.maxteamsize.group5`
* `celestyteams.maxteamsize.group6`

`celestyteams.*` is a permission to allow access to ALL functions regardless of operator status.

`celestyteams.team` is by default given to everyone so they can all create, edit and manage a team.  

`celestyteams.admin` is by default given to server operators.

`celestyteams.update` is the permission node to allow a player to see in game notifications if there is a plugin update available.

`celestyteams.bypass` is the permission node to allow a player to bypass all protections and cooldowns.

`celestyteams.bypass.*` is the permission node to allow a player to bypass all protections and cooldowns.

`celestyteams.bypass.homecooldown` is the permission node to allow a player to bypass the home command cooldown.

`celestyteams.bypass.chatcooldown` - is the permission node to allow a player to bypass the team chat command cooldown.

`celestyteams.bypass.pvp` is the permission node to allow a player to bypass the friendly fire protections.

`celestyteams.maxteamsize.group1` is the permission node to allow only group 1 size of team.

`celestyteams.maxteamsize.group2` is the permission node to allow only group 2 size of team.

`celestyteams.maxteamsize.group3` is the permission node to allow only group 3 size of team.

`celestyteams.maxteamsize.group4` is the permission node to allow only group 4 size of team.

`celestyteams.maxteamsize.group5` is the permission node to allow only group 5 size of team.

`celestyteams.maxteamsize.group6` is the permission node to allow only group 6 size of team.

## Config
The max team size (by default is 8), can be managed in the `plugins/CelestyTeams/config.yml` file.

The max team allies (by default is 4), can be managed in the `plugins/CelestyTeams/config.yml` file.

The max team enemies (by default is 2), can be managed in the `plugins/CelestyTeams/config.yml` file.

## Chat prefix
CelestyTeams exposes a variable of `{CLAN}` to use in Essentials Chat or similar.

## PlaceholderAPI
CelestyTeams exposes `8` external placeholders using `PlaceholderAPI` to enable the fetching of a players team name or the team prefix or if the team has friendly fire enabled or if the team has a home set.

The four available placeholders are:
* `%teamsLite_teamName%`
* `%teamsLite_teamPrefix%`
* `%teamsLite_friendlyFire%`
* `%teamsLite_teamHomeSet%`
* `%teamsLite_teamMembersSize%`
* `%teamsLite_teamAllySize%`
* `%teamsLite_teamEnemySize%`
* `%teamsLite_playerPointBalance%`

To be able to use these The latest release of [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) MUST be installed!  Without it, only the above `{CLAN}` will be available.

###Please report any issues in GitHub and feel free to join my [discord](https://discord.gg/crapticraft).

###Thank you for using my plugin!
