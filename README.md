<center>
<img     src="https://cdn.modrinth.com/data/cached_images/d66d2a90b55e0718094cb76a914bb58b06695e67.png"  alt="Image Description" width="WIDTH">
</center>

<br>&nbsp;<br>

UltimateTeams is a light-weight teams plugin for Minecraft servers running Spigot and most of its forks, with cross-server support, developed with optimization in mind!

UltimateTeams does offer the ability to disable friendly fire within your team and allies!

[bStats (800+ servers, 2000+ players)](https://bstats.org/plugin/bukkit/UltimateTeams/18842)
<br>&nbsp;<br>

## [Check the Images Gallery!](https://modrinth.com/plugin/ultimate-teams/gallery)



<img     src="https://cdn.modrinth.com/data/cached_images/be59361ea48c350f68566f613ed176c19c266504.png"  alt="Image Description" width="WIDTH">
<br>&nbsp;<br>

UltimateTeams does support H2 (preferred over SQLite), SQLite, MySQL, MariaDB (preferred over MySQL) and PostgreSQL, check the config for further information.
### All database operations are executed asynchronously, to prevent any form of lag
<br>&nbsp;<br>

<img     src="https://cdn.modrinth.com/data/cached_images/163f5d441b2112c81ce92f2c288cec7bf049a8ac.png"  alt="Image Description" width="WIDTH">
<br>&nbsp;<br>

⭐ **Works Cross-Server**\
The plugin will synchronize all data real-time between proxied servers using MySQL and a message broker (check the config).

⭐ **Optimization**\
The whole plugin is developed with optimization in mind, and it has been tested with more than 100 active players.

⭐ **Team Enderchests**\
Teams can have one or more shared enderchests with configurable slots. Admin can manage (add, remove, see, modify etc) them, and they are updated real-time between viewers to avoid dupe glitches.

⭐ **Home & Warps**\
Each team can have a home and one ore more warps (check the config). Teleportation can also be handled by HuskHomes. Max warps/members can also be dynamically modified with a permission

⭐ **GUI**\
Teams can be managed in a GUI. Team list can also be accessed in a paginated GUI.

⭐ **Economy**\
Economy can be enabled to support features like team creation cost, team join fee, team bank...

⭐ **Team & Allies chat**\
Players of each team and all its allies can talk in their dedicated channels

⭐ **Allies and Enemies**\
Other teams can be marked as allies or enemies

⭐ **PlaceholderAPI**\
Some placeholders are already available without requiring to download an external jar

⭐ **Admin commands**\
Staff members can force players to join a team or disband another

⭐ **LuckPerms contexts**\
The plugin provides context for LuckPerms such as "is-in-team"/"is-team-owner"

⭐ **HuskHomes support**\
The plugin will use its own teleportation handler (even between servers if cross-server is enabled), but it supports [HuskHomes](https://modrinth.com/plugin/huskhomes) for a more seamlessly integration

⭐ **Bedrock Support** \
With floodgate correctly installed, bedrock players are automatically managed


<br>&nbsp;<br>
<center>
<img     src="https://cdn.modrinth.com/data/cached_images/257f92ada5da2edf5e0513324f56f5190dff15d0.png"  alt="Image Description" width="400">
</center>
<br>&nbsp;<br>

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
* `/team [promote|demote] <player>` - Will promote/demote a team member to/from team manager.
* `/team permission [add|remove] <permission>` - Will add/remove a permission to make team managers use specific team commands.
* `/team deposit <amount>` - deposit money into the team bank
* `/team withdraw <amount>` - withdraw money from the team bank
* `/team fee [set/disable] [amount]` - see, disable, or set the team join fee
* `/invites <enable/disable>` - enable/disable invites from teams


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
* `ultimateteams.team.echest`
* `ultimateteams.team.permissions.add`
* `ultimateteams.team.permissions.remove`
* `ultimateteams.allychat`
* `ultimateteams.teamchat`
* `ultimateteams.team.echest`
* `ultimateteams.team.deposit`
* `ultimateteams.team.withdraw`
* `ultimateteams.team.invites.enable`
* `ultimateteams.team.invites.disable`
* `ultimateteams.team.fee.see`
* `ultimateteams.team.fee.set`
* `ultimateteams.team.fee.disable`

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
* `ultimateteams.admin.echest.add`
* `ultimateteams.admin.echest.remove`
* `ultimateteams.admin.echest.list`
* `ultimateteams.admin.echest.see`
* `ultimateteams.admin.echest.rollback`
* `ultimateteams.admin.echest.backup`
* `ultimateteams.admin.echest.remove`

<br>&nbsp;<br>

<img     src="https://cdn.modrinth.com/data/cached_images/33a45de13c09e4853203944c4105cfb3105a50d9.png"  alt="Image Description" width="250">
<br>&nbsp;<br>

The available placeholders are:
* `%ultimateteams_teamName%`
* `%ultimateteams_teamPrefix%`
* `%ultimateteams_friendlyFire%`
* `%ultimateteams_teamHomeSet%`
* `%ultimateteams_teamMembersSize%`
* `%ultimateteams_teamAllySize%`
* `%ultimateteams_teamEnemySize%`
* `%ultimateteams_isInTeam%`

To be able to use the placeholders, the latest release of [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) MUST be installed!

## Please report any issue on GitHub.

###### This plugin was originally based on ClansLite by Loving11ish

## Thank you for using my plugin!
