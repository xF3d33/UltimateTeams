<center>
<img     src="https://cdn.modrinth.com/data/cached_images/d66d2a90b55e0718094cb76a914bb58b06695e67.png"  alt="Image Description" width="WIDTH">
</center>

<br>&nbsp;<br>

UltimateTeams is a light-weight teams plugin for Minecraft servers running Spigot and most of its forks, developed with optimization in mind!

UltimateTeams does offer the ability to disable friendly fire within your team and allies!

[bStats (400+ servers)](https://bstats.org/plugin/bukkit/UltimateTeams/18842)
<br>&nbsp;<br>

###  _**I'm currently adding support for cross-server. If you want to help me on this, write me a message on Discord (xF3d3) and i'll provide some test builds**_

<img     src="https://cdn.modrinth.com/data/cached_images/be59361ea48c350f68566f613ed176c19c266504.png"  alt="Image Description" width="WIDTH">
<br>&nbsp;<br>

UltimateTeams does support H2 (preferred over SQLite), SQLite, MySQL, MariaDB (preferred over MySQL) and PostgreSQL, check the config for further information.
### All database operations are executed asynchronously, to prevent any form of lag
<br>&nbsp;<br>

<img     src="https://cdn.modrinth.com/data/cached_images/163f5d441b2112c81ce92f2c288cec7bf049a8ac.png"  alt="Image Description" width="WIDTH">
<br>&nbsp;<br>

⭐ **Optimization**\
The whole plugin is developed with optimization in mind, and it has been tested with more than 100 active players.

⭐ **Home & Warps**\
Each team can have a home and one ore more warps (check the config). Teleportation can also be handled by HuskHomes

⭐ **Team Upgrades System** *(NEW in v1.0.3)*\
Teams can now purchase upgrades to increase their member and warp limits using economy integration (Vault)

⭐ **Co-Owner Rank** *(NEW in v1.0.3)*\
Introducing a new rank between Owner and Manager with invite/kick permissions but cannot disband the team

⭐ **Interactive Invites** *(NEW in v1.0.3)*\
Modern clickable accept/decline buttons for team invitations with hover text

⭐ **Bedrock Support** \
With floodgate correctly installed, bedrock players are automatically managed

⭐ **Team & Team allies chat**\
Players of each team and all its allies can talk in their dedicated channels

⭐ **Allies and Enemies**\
Other teams can be marked as allies or enemies

⭐ **PlaceholderAPI**\
Some placeholders are already available without requiring to download an external jar

⭐ **Admin commands**\
Staff members can force players to join a team or disband another

⭐ **GUI**\
Teams list can also be accessed in a paginated GUI with upgrade management
<br>&nbsp;<br>
<center>
<img     src="https://cdn.modrinth.com/data/cached_images/257f92ada5da2edf5e0513324f56f5190dff15d0.png"  alt="Image Description" width="400">
</center>
<br>&nbsp;<br>

## /team command
Aliases: `/team`

The `/team` command is the main command of the plugin, with `/team` you can do the following:
* `/team` - Opens Team Manager GUI (if in a team) or Team List GUI (if not in a team)
* `/team create <name>` - Creates A new team if not already in one
* `/team disband` - If you are the team owner, this will destroy your team
* `/team leave` - If you are in a team, this will remove you from it
* `/team invite <player>` - Will invite a player to your team if they are not already in one *(simplified in v1.0.3)*
* `/team join` - Will add you to a team that you have been invited too.
* `/team kick <player>` - Will kick a player from your team
* `/team info` - Will display information about your current team
* `/team list` - Will list all teams in the server
* `/team prefix <prefix>` - Will change the prefix for your team in chat
* `/team ally [add|remove] <team-name>` - Will either add or remove an allied team to yours
* `/team enemy [add|remove] <team-name>` - Will either add or remove an enemy team to yours
* `/team pvp` - Will toggle the friendly fire status for your team
* `/team [sethome|home]` - Will set a team home location or teleport you or you team members to this location.
* `/team [promote|demote] <player>` - Will promote/demote a team member to/from team manager.
* `/team permission [add|remove] <permission>` - Will add/remove a permission to make team managers use specific team commands.
* `/team coowner <player>` - Will promote a player to Co-Owner rank *(NEW in v1.0.3)*
* `/team coowner demote <player>` - Will demote a Co-Owner to Manager rank *(NEW in v1.0.3)*
* `/team upgrade` - View current team limits and upgrade costs *(NEW in v1.0.3)*
* `/team upgrade members` - Purchase an upgrade to increase max member limit *(NEW in v1.0.3)*
* `/team upgrade warps` - Purchase an upgrade to increase max warp limit *(NEW in v1.0.3)*

## /teamadmin command
Aliases: `/ta`

The `/teamadmin` command is purely for server admins only.

4 arguments are implemented which are:
* `/teamadmin reload` - This reloads the plugins `config.yml` & the `messages.yml` files from disk.
* `/teamadmin disband <team-name>` - This allows admins to delete any unauthorised teams.
* `/teamadmin about` - This give you an overview of the plugin's core information.

## /tc command
Aliases: /teamchat, /tchat, /tc

The `/tc` command is for the sole purpose of utilising the per team chat. The following syntax is accepted:

`/tc <message>` - This will send a message to only the members of YOUR team or the team you are in.

<br>&nbsp;<br>
<center>
<img     src="https://cdn.modrinth.com/data/cached_images/c3423f516516d259b157221d9852c28b84888063.png"  alt="Image Description" width="400">
</center>
<br>&nbsp;<br>


Player permissions
* `ultimateteams.player`
* `ultimateteams.team.gui`
* `ultimateteams.team.create`
* `ultimateteams.team.rename`
* `ultimateteams.team.warp`
* `ultimateteams.team.setwarp`
* `ultimateteams.team.delwarp`
* `ultimateteams.team.disband`
* `ultimateteams.team.invite.send`
* `ultimateteams.team.invite.accept`
* `ultimateteams.team.invite.deny`
* `ultimateteams.team.sethome`
* `ultimateteams.team.delhome`
* `ultimateteams.team.home`
* `ultimateteams.team.pvp`
* `ultimateteams.team.enemy.add`
* `ultimateteams.team.enemy.remove`
* `ultimateteams.team.ally.add`
* `ultimateteams.team.ally.remove`
* `ultimateteams.team.leave`
* `ultimateteams.team.kick`
* `ultimateteams.team.list`
* `ultimateteams.team.transfer`
* `ultimateteams.team.prefix`
* `ultimateteams.team.info`
* `ultimateteams.team.promote`
* `ultimateteams.team.demote`
* `ultimateteams.team.permissions.add`
* `ultimateteams.team.permissions.remove`
* `ultimateteams.team.coowner.promote` *(NEW in v1.0.3)*
* `ultimateteams.team.coowner.demote` *(NEW in v1.0.3)*
* `ultimateteams.team.upgrade.info` *(NEW in v1.0.3)*
* `ultimateteams.team.upgrade.members` *(NEW in v1.0.3)*
* `ultimateteams.team.upgrade.warps` *(NEW in v1.0.3)*
* `ultimateteams.allychat`
* `ultimateteams.teamchat`

Admin permissions:
* `ultimateteams.admin.about`
* `ultimateteams.admin.reload`
* `ultimateteams.admin.migrate`
* `ultimateteams.admin.team.disband`
* `ultimateteams.admin.team.join`
* `ultimateteams.bypass.pvp`
* `ultimateteams.bypass.homecooldown`
* `ultimateteams.bypass.chatcooldown`
* `ultimateteams.bypass.warpcooldown`
* `ultimateteams.chat.spy`

<br>&nbsp;<br>

<img     src="https://cdn.modrinth.com/data/cached_images/33a45de13c09e4853203944c4105cfb3105a50d9.png"  alt="Image Description" width="250">
<br>&nbsp;<br>

UltimateTeams exposes `8` external placeholders using `PlaceholderAPI` to enable the fetching of a players team name or the team prefix or if the team has friendly fire enabled or if the team has a home set.

The available placeholders are:
* `%ultimateteams_teamName%`
* `%ultimateteams_teamPrefix%`
* `%ultimateteams_friendlyFire%`
* `%ultimateteams_teamHomeSet%`
* `%ultimateteams_teamMembersSize%`
* `%ultimateteams_teamAllySize%`
* `%ultimateteams_teamEnemySize%`
* `%ultimateteams_isInTeam%`

To be able to use these The latest release of [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) MUST be installed!  Without it, you can't use these placeholders.

<br>&nbsp;<br>

## What's New in v1.0.3-dei2004

### 🏆 Team Upgrades System
- Teams can purchase upgrades to increase member and warp limits
- Default limits: 8 members, 2 warps (configurable)
- Economy integration via Vault (default cost: $1000 per upgrade)
- Commands: `/team upgrade members`, `/team upgrade warps`, `/team upgrade` (info)

### 👑 Co-Owner Rank
- New rank between Owner and Manager
- 4-tier hierarchy: Owner → Co-Owner → Manager → Member
- Co-Owners can invite/kick but cannot disband teams
- Commands: `/team coowner <player>`, `/team coowner demote <player>`

### ⚡ Simplified Commands
- `/team invite <player>` - Quick invite without typing "send"
- `/team` - Smart behavior: opens Team Manager or Team List based on status

### 🎯 Interactive Features
- Clickable accept/decline buttons for team invitations
- Hover text for better user experience
- Updated Team Manager GUI with upgrade button

### 🐛 Bug Fixes
- Fixed NullPointerException when Vault is not installed
- Improved error messages for missing economy plugin
- Enhanced validation for team limits

<br>&nbsp;<br>

## Requirements for v1.0.3+

For the upgrade system to work, you need:
- **Vault** plugin installed
- **Economy plugin** (EssentialsX, CMI, etc.)
- Enable `economy.enable: true` in config.yml

<br>&nbsp;<br>

## Please report any issue on GitHub.

###### This plugin was originally based on ClansLite by Loving11ish

## Thank you for using my plugin!
