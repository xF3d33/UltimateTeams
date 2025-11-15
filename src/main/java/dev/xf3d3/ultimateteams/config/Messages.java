package dev.xf3d3.ultimateteams.config;

import lombok.Getter;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;

import java.util.List;

@YamlFile(header = """
        #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
        #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
        #                                   ----[UltimateTeams]----                                         #
        #                                     ----[By xF3d3]----                                            #
        #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
        #                                  ----[Plugin Messages File]----                                   #
        #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
        #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
        
        You can use MineDown: https://github.com/Phoenix616/MineDown?tab=readme-ov-file#syntax
        You can use \\n anywhere to go to a new line.
        """)
public class Messages {

    @YamlComment("You can also use Hex colors anywhere in the messages (#ffffffWHITE)\n\nTeam Creation Messages")
    @YamlKey("team.create.incorrect-usage")
    @Getter
    private String teamCreateIncorrectUsage = "&3use /team create <name>.";

    @YamlKey("team.create.name-too-short")
    @Getter
    private String teamNameTooShort = "&3Team name too short - minimum length is &6%CHARMIN% &3characters.";

    @YamlKey("team.create.name-too-long")
    @Getter
    private String teamNameTooLong = "&3Team name too long - maximum length is &6%CHARMAX% &3characters.";

    @YamlKey("team.create.successful")
    @Getter
    private String teamCreatedSuccessfully = "&3Team &6%TEAM% &3was Created!";

    @YamlKey("team.create.name-taken")
    @Getter
    private String teamNameAlreadyTaken = "&3Sorry but &6%TEAM% &3is already taken.\n&3Please choose another!";

    @YamlKey("team.create.name-banned")
    @Getter
    private String teamNameIsBanned = "&3Sorry but &6%TEAM% &3is a &cBANNED &3name!\n&3Please choose another!";

    @YamlKey("team.create.name-contains-space")
    @Getter
    private String teamNameContainsSpace = "&3Sorry but, since it contains a space, &6%TEAM% &3is a &cBANNED &3name!\n&3Please choose another!";

    @YamlKey("team.create.name-cannot-contain-colours")
    @Getter
    private String teamNameCannotContainColours = "&3Sorry, the team name cannot contain '&' or '#' characters.";

    @YamlKey("team.create.failed")
    @Getter
    private String teamCreationFailed = "&3Team &6%TEAM% &3was NOT created, please make sure you're not already in a team!";

    @YamlKey("team.create.broadcast-chat")
    @Getter
    private String teamCreatedBroadcastChat = "&6%TEAMOWNER%&3 Created a new team!\n&3The new team is called &6%TEAM%&3!";

    @YamlKey("team.create.broadcast-title-1")
    @Getter
    private String teamCreatedBroadcastTitle1 = "&6%TEAMOWNER%&3 Created a new team!";

    @YamlKey("team.create.broadcast-title-2")
    @Getter
    private String teamCreatedBroadcastTitle2 = "&3The new team is called &6%TEAM%&3!";

    @YamlComment("Team Rename")
    @YamlKey("team.rename.successful")
    @Getter
    private String teamNameChangeSuccessful = "&3Successfully changed team name to &6%TEAM%&3!";

    @YamlComment("Team Disbanded Messages")
    @YamlKey("team.disband.warning")
    @Getter
    private String teamDisbandWarning = "&3If you're sure you want to delete your team type: &6/team disband confirm&3. &cThis action cannot be undone!";

    @YamlKey("team.disband.successful")
    @Getter
    private String teamSuccessfullyDisbanded = "&3Team was disbanded!";

    @YamlKey("team.disband.failure")
    @Getter
    private String teamDisbandFailure = "&3Failed to disband team - Please make sure you're the owner!";

    @YamlComment("Team Transfer Ownership Messages")
    @YamlKey("team.transfer.successful")
    @Getter
    private String teamOwnershipTransferSuccessful = "&3You successfully transferred your team to &6%PLAYER%&3.";

    @YamlKey("team.transfer.new-owner-message")
    @Getter
    private String teamOwnershipTransferNewOwner = "&3You are now the owner of &6%TEAM%.";

    @YamlKey("team.transfer.failure-not-same-team")
    @Getter
    private String teamOwnershipTransferFailureNotSameTeam = "&3Failed to transfer team ownership as the player is not in your team.";

    @YamlKey("team.transfer.failure-owner-offline")
    @Getter
    private String teamOwnershipTransferFailureOwnerOffline = "&3Failed to transfer team ownership to &6%PLAYER%&3!\n&3They may be offline.";

    @YamlKey("team.transfer.failed-cannot-transfer-to-self")
    @Getter
    private String teamOwnershipTransferFailedCannotTransferToSelf = "&3Failed to transfer team ownership to &6%PLAYER%!\n&3The specified player cannot be yourself!";

    @YamlKey("team.transfer.failed-target-in-team")
    @Getter
    private String teamOwnershipTransferFailedTargetInTeam = "&3Failed to transfer team as the target is already in/owns a team!";

    @YamlKey("team.transfer.incorrect-usage")
    @Getter
    private String incorrectTeamTransferOwnershipCommandUsage = "&3Unrecognised argument please use &6/team transfer <player-name>&3.";

    @YamlComment("Team Invite Messages")
    @YamlKey("team.invite.no-valid-player")
    @Getter
    private String teamInviteNoValidPlayer = "&3Please specify a player to invite!";

    @YamlKey("team.invite.not-team-owner")
    @Getter
    private String teamInviteNotTeamOwner = "&3You must be a team owner to invite people!";

    @YamlKey("team.invite.self-error")
    @Getter
    private String teamInviteSelfError = "&3You can't invite yourself!";

    @YamlKey("team.invite.invitee-not-found")
    @Getter
    private String teamInviteeNotFound = "&3Player &6%INVITED% &3was not found, make sure they are online!";

    @YamlKey("team.invite.invited-already-in-team")
    @Getter
    private String teamInviteInvitedAlreadyInTeam = "&3Player &6%INVITED% &3is already in a team!";

    @YamlKey("team.invite.max-size-reached")
    @Getter
    private String teamInviteMaxSizeReached = "&3You have reached the team members size limit &a%LIMIT%&3!";

    @YamlKey("team.invite.successful")
    @Getter
    private String teamInviteSuccessful = "&3You have invited &6%INVITED% &3to your team!";

    @YamlKey("team.invite.failed")
    @Getter
    private String teamInviteFailed = "&3Failed to send invite to &6%INVITED%&3, this player might already have an invitation!";

    @YamlKey("team.invite.invite-pending")
    @Getter
    private String teamInvitedPlayerInvitePending = "&3You have been invited to a team by &6%TEAMOWNER% &3- use /team invite accept";

    @YamlKey("team.invite.request")
    @Getter
    private String teamInviteRequest = "&6%PLAYER% &3would like you to invite them to your team.\n&3Use &6/team invite %PLAYER% &3to send the invite.";

    @YamlKey("team.invite.request-failed")
    @Getter
    private String teamInviteRequestFailed = "&3Failed to send request, check that the team owner is online!";

    @YamlKey("team.invite.request-sent-successfully")
    @Getter
    private String teamInviteSentSuccessfully = "&3You have successfully sent a request to %TEAMOWNER%";

    @YamlKey("team.invite.request-failed-own-team")
    @Getter
    private String teamInviteFailedOwnTeam = "&3Failed to send request, this is YOUR team!";

    @YamlKey("team.invite.request-failed-already-in-team")
    @Getter
    private String teamInviteFailedAlreadyInTeam = "&3Failed to send request, you are already in a team!";

    @YamlKey("team.invite.deny-failed-no-invite")
    @Getter
    private String teamInviteDenyFailedNoInvite = "&3Failed to deny invite - you don't have an active invite.";

    @YamlKey("team.invite.denied")
    @Getter
    private String teamInviteDenied = "&3Invite successfully denied.";

    @YamlKey("team.invite.denied-inviter")
    @Getter
    private String teamInviteDeniedInviter = "&3%PLAYER% denied your invite.";

    @YamlKey("team.invite.deny-fail")
    @Getter
    private String teamInviteDenyFail = "&3Failed to deny the invite.";

    @YamlKey("team.invite.received-message")
    @Getter
    private List<String> teamInviteInvitedMessage = List.of(
            "&7&m                                                    &r",
            "[Team Invitation](gold bold)",
            "[You've been invited to join](yellow) [%TEAM%](gold) [by](yellow) [%INVITER%](gold)",
            "",
            "[[✔ ACCEPT]](green bold run_command=/team invite accept hover=[Click to accept the invitation](green)) [[✔ DECLINE]](red bold run_command=/team invite deny hover=[Click to deny the invitation](red))",
            "&7&m                                                    &r"
    );

    @YamlKey("team.invite.error-invites-disabled")
    @Getter
    private String teamInviteFailInvitesOff = "[%NAME%](gold) [has invites disabled, you can't invite him!](dark_aqua)";


    @YamlComment("Team Join Messages")
    @YamlKey("team.join.successful")
    @Getter
    private String teamJoinSuccessful = "&3Successfully joined &6%TEAM%&3!";

    @YamlKey("team.join.failed")
    @Getter
    private String teamJoinFailed = "&3Failed to join &6%TEAM%&3";

    @YamlKey("team.join.failed-no-valid-team")
    @Getter
    private String teamJoinFailedNoValidTeam = "&3Failed to join a team - no team was found!";

    @YamlKey("team.join.failed-no-invite")
    @Getter
    private String teamJoinFailedNoInvite = "&3Failed to join a team - no invite was found!";

    @YamlKey("team.join.broadcast-chat")
    @Getter
    private String teamJoinBroadcastChat = "&3The player &6%PLAYER%&3 has joined your team!";

    @YamlComment("Team Leave Messages")
    @YamlKey("team.leave.failed-owner")
    @Getter
    private String failedTeamOwner = "&3You are the owner of a team, use &6/team disband&3.";

    @YamlKey("team.leave.successful")
    @Getter
    private String teamLeaveSuccessful = "&3You have left &6%TEAM%.";

    @YamlKey("team.leave.failed")
    @Getter
    private String teamLeaveFailed = "&3Failed to leave team, please try again later.";

    @YamlKey("team.leave.broadcast-chat")
    @Getter
    private String teamLeftBroadcastChat = "&3The player &6%PLAYER%&3 has left your team!";

    @YamlComment("Team Warp Messages")
    @YamlKey("team.warp.successful")
    @Getter
    private String teamWarpSuccessful = "&3Successfully added team warp &6%WARP_NAME%&3!";

    @YamlKey("team.warp.name-used")
    @Getter
    private String teamWarpNameUsed = "&3A warp with that name already exists!";

    @YamlKey("team.warp.limit-reached")
    @Getter
    private String teamWarpLimitReached = "&3You have reached the team warps limit!";

    @YamlKey("team.warp.deleted-successful")
    @Getter
    private String teamWarpDeletedSuccessful = "&3Successfully deleted team warp &6%WARP_NAME%&3!";

    @YamlKey("team.warp.not-found")
    @Getter
    private String teamWarpNotFound = "&3There is no warp with that name!";

    @YamlKey("team.warp.cooldown-start")
    @Getter
    private String teamWarpCooldownStart = "&3You will be teleported in %SECONDS%s!";

    @YamlKey("team.warp.teleported-successful")
    @Getter
    private String teamWarpTeleportedSuccessful = "&3Successfully teleported to the team warp!";

    @YamlKey("team.warp.no-warps")
    @Getter
    private String teamWarpEmpty = "[Your team has no warps!](red)";

    @YamlKey("team.warp.menu.header")
    @Getter
    private List<String> teamWarpMenuHeader = List.of(
            "[List of](dark_aqua) [%TEAM%](gold) [warps.](dark_aqua)",
            "[You can Hover each warp name to get details](dark_aqua)",
            ""
    );

    @YamlKey("team.warp.menu.warp")
    @Getter
    private String teamWarpMenuItem = "[[%WARP_NAME%]](#1055C9 hover=%WARP_INFO% run_command=/team warp %WARP_NAME%)";

    @YamlKey("team.warp.menu.warp-info")
    @Getter
    private List<String> teamWarpMenuItemInfo = List.of(
            "[Left click to TP to](gray) [%WARP_NAME%](gold) [position](gray)",
            "",
            "[Coordinates:](gray) [X:](gray)[%X%](dark_aqua) [Y:](gray)[%Y%](dark_aqua) [Z:](gray)[%Z%](dark_aqua)",
            "[World:](gray) [%WORLD_NAME%](dark_aqua) [Server:](gray) [%SERVER_NAME%](dark_aqua)"
    );

    @YamlComment("Team Kick Messages")
    @YamlKey("team.kick.successful")
    @Getter
    private String teamMemberKickSuccessful = "&3Player &6%KICKEDPLAYER%&3 was kicked from your team.";

    @YamlKey("team.kick.kicked-player-message")
    @Getter
    private String teamKickedPlayerMessage = "&3You were kicked from &6%TEAM%&3!";

    @YamlKey("team.kick.target-not-in-team")
    @Getter
    private String targetedPlayerIsNotInYourTeam = "&3Player &6%KICKEDPLAYER%&3 is not in your team.";

    @YamlKey("team.kick.player-not-found")
    @Getter
    private String couldNotFindSpecifiedPlayer = "&3Could not find player &6%KICKEDPLAYER%&3. They may be have not joined before.";

    @YamlKey("team.kick.must-be-owner")
    @Getter
    private String mustBeOwnerToKick = "&3You are not an owner of a team!";

    @YamlKey("team.kick.incorrect-usage")
    @Getter
    private String incorrectKickCommandUsage = "&3Unrecognised argument please use &6/team kick <member>&3.";

    @YamlKey("team.kick.failed-cannot-kick-yourself")
    @Getter
    private String failedCannotKickYourself = "&3You cannot kick yourself, use &6/team leave&3.";

    @YamlComment("Team Prefix Messages")
    @YamlKey("team.prefix.change-successful")
    @Getter
    private String teamPrefixChangeSuccessful = "&3Successfully changed team prefix to %TEAMPREFIX%&r&3!";

    @YamlKey("team.prefix.too-long")
    @Getter
    private String teamPrefixTooLong = "&3Team prefix too long - maximum length is &6%CHARMAX% &3characters.";

    @YamlKey("team.prefix.too-short")
    @Getter
    private String teamPrefixTooShort = "&3Team prefix too short - minimum length is &6%CHARMIN% &3characters.";

    @YamlKey("team.prefix.invalid-prefix")
    @Getter
    private String teamInvalidPrefix = "&3Please provide a new prefix. Use &b/team prefix <prefix>&3!";

    @YamlKey("team.prefix.color-hint")
    @Getter
    private String teamPrefixColorHint = "&7Tip: Use &e&& &7for color codes (e.g. &c&a for green) or &e# &7for hex colors (e.g. &#FF5555)";

    @YamlKey("team.prefix.already-taken")
    @Getter
    private String teamPrefixAlreadyTaken = "&3Sorry but &6%TEAMPREFIX% &3is already taken.\n&3Please choose another!";

    @YamlKey("team.prefix.is-banned")
    @Getter
    private String teamPrefixIsBanned = "&3Sorry but &6%TEAMPREFIX% &3is a &cBANNED &3name!\n&3Please choose another!";

    @YamlKey("team.prefix.must-be-owner")
    @Getter
    private String mustBeOwnerToChangePrefix = "&3You are not an owner of a team!";

    @YamlKey("team.prefix.cannot-contain-colours")
    @Getter
    private String teamTagCannotContainColours = "&3Sorry, the team prefix cannot contain '&' or '#' characters.";


    @YamlComment("Team MOTD Messages")
    @YamlKey("team.motd.cannot-contain-colours")
    @Getter
    private String teamMotdCannotContainColours = "&3Sorry, the team MOTD cannot contain '&' or '#' characters.";

    @YamlKey("team.motd.change-successful")
    @Getter
    private String teamMotdChangeSuccessful = "&3Successfully changed team MOTD to: %MOTD%&r&3!";

    @YamlKey("team.motd.not-valid")
    @Getter
    private String teamMotdNotValid = "&3The team MOTD contains invalid characters!";

    @YamlKey("team.motd.disabled")
    @Getter
    private String teamMotdDisabledSuccessful = "&3Successfully disabled the team MOTD.";

    @YamlKey("team.motd.not-set")
    @Getter
    private String teamMotdNotSet = "&3Team MOTD not set.";

    @YamlKey("team.motd.too-long")
    @Getter
    private String teamMotdTooLong = "&3Team MOTD too long - maximum length is &6%CHARMAX% &3characters.";

    @YamlKey("team.motd.too-short")
    @Getter
    private String teamMotdTooShort = "&3Team MOTD too short - minimum length is &6%CHARMIN% &3characters.";



    @YamlComment("Team List Messages")
    @YamlKey("team.list.no-teams")
    @Getter
    private String noTeamsToList = "&3No teams found!\n&3Create one using &b/team create <name>&3!";

    @YamlKey("team.list.header")
    @Getter
    private String teamsListHeader = "&7----- &6TeamsList &7-----&r\n&3&lCurrent teams:&r\n";

    @YamlKey("team.list.footer")
    @Getter
    private String teamsListFooter = "&7-----";

    @YamlComment("Team Info Messages")
    @YamlKey("team.info.header")
    @Getter
    private String teamInfoHeader = "&7----- &6TeamInfo &7-----&r\n&3Name: %TEAM%&r\n&3Prefix: &7(%TEAMPREFIX%&r&7)&r";

    @YamlKey("team.info.owner-online")
    @Getter
    private String teamInfoOwnerOnline = "&3Owner: &a%OWNER%";

    @YamlKey("team.info.owner-offline")
    @Getter
    private String teamInfoOwnerOffline = "&3Owner: &c%OWNER%&7&o(offline)";

    @YamlKey("team.info.members-header")
    @Getter
    private String teamInfoMembersHeader = "&3Members: &3&o(%NUMBER%)";

    @YamlKey("team.info.managers-header")
    @Getter
    private String teamInfoManagersHeader = "&3Managers: &3&o(%NUMBER%)";

    @YamlKey("team.info.members-online")
    @Getter
    private String teamInfoMembersOnline = "&a%MEMBER%";

    @YamlKey("team.info.members-offline")
    @Getter
    private String teamInfoMembersOffline = "&c%MEMBER% &7&o(offline)";

    @YamlKey("team.info.allies-header")
    @Getter
    private String teamInfoAlliesHeader = "&3Allied Teams:";

    @YamlKey("team.info.ally-list-entry")
    @Getter
    private String teamAllyMembers = "&a%ALLYTEAM%";

    @YamlKey("team.info.ally-list-not-found")
    @Getter
    private String teamAllyMembersNotFound = "&aAlly not found";

    @YamlKey("team.info.enemies-header")
    @Getter
    private String teamInfoEnemiesHeader = "&3Enemy Teams:";

    @YamlKey("team.info.enemy-list-entry")
    @Getter
    private String teamEnemyMembers = "&c%ENEMYTEAM%";

    @YamlKey("team.info.enemy-list-not-found")
    @Getter
    private String teamEnemyMembersNotFound = "&aEnemy not found";

    @YamlKey("team.info.join-fee")
    @Getter
    private String teamInfoJoinFee = "&3Join Fee: &a%AMOUNT%";

    @YamlKey("team.info.motd")
    @Getter
    private String teamInfoMotd = "&3MOTD: &a%MOTD%";

    @YamlKey("team.info.pvp-enabled")
    @Getter
    private String teamPvpStatusEnabled = "&3Friendly Fire: &a&oENABLED";

    @YamlKey("team.info.pvp-disabled")
    @Getter
    private String teamPvpStatusDisabled = "&3Friendly Fire: &c&oDISABLED";

    @YamlKey("team.info.home-set")
    @Getter
    private String teamHomeSetTrue = "&3Home Set: &a&oTRUE";

    @YamlKey("team.info.home-not-set")
    @Getter
    private String teamHomeSetFalse = "&3Home Set: &c&oFALSE";

    @YamlKey("team.info.bank-amount")
    @Getter
    private String teamInfoBankAmount = "&3Bank Amount: &6%AMOUNT%";

    @YamlKey("team.info.footer")
    @Getter
    private String teamInfoFooter = "&7-----";

    @YamlKey("team.info.not-in-team")
    @Getter
    private String notInTeam = "&3You are not in a team!";

    @YamlComment("Team Ally Messages")
    @YamlKey("team.ally.added-successful")
    @Getter
    private String addedTeamToYourAllies = "&3You successfully added &6%ALLYTEAM% &3to your allies!";

    @YamlKey("team.ally.added-notification")
    @Getter
    private String teamAddedToOtherAllies = "&6%TEAMOWNER% &3has added your team to their allies!";

    @YamlKey("team.ally.failed-to-add")
    @Getter
    private String failedToAddTeamToAllies = "&3Unable to add &6%ALLYTEAM%&3! Make sure the owner is online!";

    @YamlKey("team.ally.failed-already-ally")
    @Getter
    private String failedTeamAlreadyYourAlly = "&3This team is already your ally!";

    @YamlKey("team.ally.failed-player-not-owner")
    @Getter
    private String failedPlayerNotTeamOwner = "&6%ALLYOWNER%&3 is not a team owner!";

    @YamlKey("team.ally.removed-successful")
    @Getter
    private String removedTeamFromYourAllies = "&3You Successfully removed &6%ALLYTEAM% &3from your allies!";

    @YamlKey("team.ally.removed-owner-notification")
    @Getter
    private String teamOwnerRemovedTeamFromAllies = "&3Your team owner has removed &6%TEAM% &3from your allies!";

    @YamlKey("team.ally.removed-other-notification")
    @Getter
    private String teamRemovedFromOtherAllies = "&3The team &6%TEAM% &3has removed your team from their allies!";

    @YamlKey("team.ally.failed-to-remove")
    @Getter
    private String failedToRemoveTeamFromAllies = "&3Unable to remove &6%ALLYOWNER%&3! Make sure they're your ally";

    @YamlKey("team.ally.add-owner-offline")
    @Getter
    private String allyTeamAddOwnerOffline = "&3Unable to add &6%ALLYTEAM%&3! Make sure the owner is online!";

    @YamlKey("team.ally.remove-owner-offline")
    @Getter
    private String allyTeamRemoveOwnerOffline = "&3Unable to remove &6%ALLYTEAM%&3! Make sure the owner is online!";

    @YamlKey("team.ally.incorrect-usage")
    @Getter
    private String incorrectTeamAllyCommandUsage = "&3Unrecognised argument please use &6/team ally [add|remove] <team-owner>&3.";

    @YamlKey("team.ally.failed-cannot-ally-self")
    @Getter
    private String failedCannotAllyYourOwnTeam = "&3You cannot be an ally with your &3&lOWN&r&3 team!";

    @YamlKey("team.ally.max-amount-reached")
    @Getter
    private String teamAllyMaxAmountReached = "&3You have reached the team allies amount limit &a%LIMIT%&3!";

    @YamlKey("team.ally.failed-cannot-ally-enemy")
    @Getter
    private String failedCannotAllyEnemyTeam = "&3You cannot be an ally with an &c&lENEMY&r &3team!";

    @YamlComment("Team Enemy Messages")
    @YamlKey("team.enemy.added-successful")
    @Getter
    private String addedTeamToYourEnemies = "&3You successfully added &6%ENEMYTEAM% &3to your enemies!";

    @YamlKey("team.enemy.added-notification")
    @Getter
    private String teamAddedToOtherEnemies = "&6%TEAMOWNER% &3has added your team to their enemies!";

    @YamlKey("team.enemy.failed-to-add")
    @Getter
    private String failedToAdTeamToEnemies = "&3Unable to add &6%ENEMYOWNER%&3! Make sure the owner is online!";

    @YamlKey("team.enemy.failed-already-enemy")
    @Getter
    private String failedTeamAlreadyYourEnemy = "&3This team is already your enemy!";

    @YamlKey("team.enemy.failed-player-not-owner")
    @Getter
    private String failedEnemyPlayerNotTeamOwner = "&6%ENEMYOWNER%&3 is not a team owner!";

    @YamlKey("team.enemy.removed-successful")
    @Getter
    private String removedTeamFromYourEnemies = "&3You Successfully removed &6%ENEMYTEAM% &3from your enemies!";

    @YamlKey("team.enemy.removed-notification")
    @Getter
    private String teamRemovedFromOtherEnemies = "&3The team &6%TEAMOWNER% &3has removed your team from their enemies!";

    @YamlKey("team.enemy.failed-to-remove")
    @Getter
    private String failedToRemoveTeamFromEnemies = "&3Unable to remove &6%ENEMYTEAM%&3! Make sure they're your enemy!";

    @YamlKey("team.enemy.add-owner-offline")
    @Getter
    private String enemyTeamAddOwnerOffline = "&3Unable to add &6%ENEMYTEAM%&3! Make sure the owner is online!";

    @YamlKey("team.enemy.remove-owner-offline")
    @Getter
    private String enemyTeamRemoveOwnerOffline = "&3Unable to remove &6%ENEMYTEAM%&3! Make sure the owner is online!";

    @YamlKey("team.enemy.incorrect-usage")
    @Getter
    private String incorrectTeamEnemyCommandUsage = "&3Unrecognised argument please use &6/team enemy [add|remove] <team-owner>&3.";

    @YamlKey("team.enemy.failed-cannot-enemy-self")
    @Getter
    private String failedCannotEnemyYourOwnTeam = "&3You cannot be an enemy with your &3&lOWN&r&3 team!";

    @YamlKey("team.enemy.max-amount-reached")
    @Getter
    private String teamEnemyMaxAmountReached = "&3You have reached the team enemies amount limit &a%LIMIT%&3!";

    @YamlKey("team.enemy.failed-cannot-enemy-ally")
    @Getter
    private String failedCannotEnemyAlliedTeam = "&3You cannot be an enemy with an &a&lALLIED&r &3team!";

    @YamlKey("team.enemy.war-declared-title-1")
    @Getter
    private String addedEnemyTeamToYourEnemiesTitle1 = "&c&lYOUR TEAM IS NOW AT WAR WITH:";

    @YamlKey("team.enemy.war-declared-title-2")
    @Getter
    private String addedEnemyTeamToYourEnemiesTitle2 = "&6%TEAMOWNER%'s &cTeam!";

    @YamlKey("team.enemy.peace-declared-title-1")
    @Getter
    private String removedEnemyTeamFromYourEnemiesTitle1 = "&a&lYOUR TEAM IS NO LONGER AT WAR WITH:";

    @YamlKey("team.enemy.peace-declared-title-2")
    @Getter
    private String removedEnemyTeamFromYourEnemiesTitle2 = "&6%TEAMOWNER%'s &aTeam!";

    @YamlKey("team.enemy.war-received-title-1")
    @Getter
    private String teamAddedToOtherEnemiesTitle1 = "&c&lYOUR TEAM IS NOW AT WAR WITH:";

    @YamlKey("team.enemy.war-received-title-2")
    @Getter
    private String teamAddedToOtherEnemiesTitle2 = "&6%TEAMOWNER%'s &cTeam!";

    @YamlKey("team.enemy.peace-received-title-1")
    @Getter
    private String teamRemovedFromOtherEnemiesTitle1 = "&a&lYOUR TEAM IS NO LONGER AT WAR WITH:";

    @YamlKey("team.enemy.peace-received-title-2")
    @Getter
    private String teamRemovedFromOtherEnemiesTitle2 = "&6%TEAMOWNER%'s &aTeam!";

    @YamlComment("Team Friendly Fire")
    @YamlKey("team.pvp.enabled")
    @Getter
    private String enabledFriendlyFire = "&3You successfully &aenabled &3friendly fire.\n&3Your team members can now pvp each other!";

    @YamlKey("team.pvp.disabled")
    @Getter
    private String disabledFriendlyFire = "&3You successfully &cdisabled &3friendly fire.\n&3Your team members can no longer pvp each other!";

    @YamlKey("team.pvp.failed-not-in-team")
    @Getter
    private String failedNotInTeam = "&3You need to be in a team first! Use &6/team &3for details how.";

    @YamlKey("team.pvp.is-disabled")
    @Getter
    private String friendlyFireIsDisabled = "&3Your team has friendly fire disabled.";

    @YamlKey("team.pvp.is-disabled-for-allies")
    @Getter
    private String friendlyFireIsDisabledForAllies = "&3You can't hurt a member of an allied team!";

    @YamlComment("Team Homes")
    @YamlKey("team.home.set-successful")
    @Getter
    private String successfullySetTeamHome = "&3You &aSuccessfully &3set the team home to your current location!";

    @YamlKey("team.home.delete-successful")
    @Getter
    private String successfullyDeletedTeamHome = "&3You &aSuccessfully &3deleted the team home!";

    @YamlKey("team.home.cooldown-wait")
    @Getter
    private String homeCoolDownTimerWait = "&3Sorry, you can't use that again for another &6%TIMELEFT%&r &3seconds.";

    @YamlKey("team.home.cooldown-start")
    @Getter
    private String teamHomeCooldownStart = "&3You will be teleported in %SECONDS%s!";

    @YamlKey("team.home.teleport-successful")
    @Getter
    private String successfullyTeleportedToHome = "&3You teleported to your team's home.";

    @YamlKey("team.home.no-home-set")
    @Getter
    private String failedNoHomeSet = "&3Your team does not have a home set!";

    @YamlKey("team.home.failed-not-in-team")
    @Getter
    private String failedTpNotInTeam = "&3You need to be in a team first! Use &6/team &3for details how.";

    @YamlComment("Team Chat")
    @YamlKey("team.chat.failed-not-in-team")
    @Getter
    private String failedMustBeInTeam = "&3You need to be in a team first! Use &6/team &3for details how.";

    @YamlKey("team.chat.cooldown-wait")
    @Getter
    private String chatCoolDownTimerWait = "&3Sorry, you can't use that again for another &6%TIMELEFT%&r &3seconds.";

    @YamlKey("team.chat.toggle-on")
    @Getter
    private String chatToggleOn = "&3Toggled Team Chat &a&lON&3.";

    @YamlKey("team.chat.toggle-off")
    @Getter
    private String chatToggleOff = "&3Toggled Team Chat &c&lOFF&3.";

    @YamlKey("team.chat.spy-toggle-on")
    @Getter
    private String chatspyToggleOn = "&3Toggled Team Chat spy &a&lON&3.";

    @YamlKey("team.chat.spy-toggle-off")
    @Getter
    private String chatspyToggleOff = "&3Toggled Team Chat spy &c&lOFF&3.";

    @YamlKey("team.allychat.incorrect-usage")
    @Getter
    private String allychatIncorrectUsage = "&6UltimateTeams team chat usage:&3\n/allychat <message>";

    @YamlKey("team.allychat.toggle-on")
    @Getter
    private String allyChatToggleOn = "&3Toggled Ally Chat &a&lON&3.";

    @YamlKey("team.allychat.toggle-off")
    @Getter
    private String allyChatToggleOff = "&3Toggled Ally Chat &c&lOFF&3.";

    @YamlComment("Team Managers")
    @YamlKey("team.manager.promote-self-error")
    @Getter
    private String teamPromoteSelfError = "&3You can't promote yourself!";

    @YamlKey("team.manager.promote-successful")
    @Getter
    private String teamPromoteSuccessful = "&3Player &6%PLAYER%&3 has been successfully promoted to team manager.";

    @YamlKey("team.manager.demote-self-error")
    @Getter
    private String teamDemoteSelfError = "&3You can't demote yourself!";

    @YamlKey("team.manager.demote-successful")
    @Getter
    private String teamDemoteSuccessful = "&3Player &6%PLAYER%&3 has been successfully demoted.";

    @YamlComment("Team permissions")
    @YamlKey("team.permission.not-found")
    @Getter
    private String permissionNotFound = "&3The permission %PERM% doesn't exist.";

    @YamlKey("team.permission.added-successful")
    @Getter
    private String teamPermissionAddedSuccessful = "&3The permission &6&l%PERM% &3has been successfully added.";

    @YamlKey("team.permission.removed-successful")
    @Getter
    private String teamPermissionRemovedSuccessful = "&3The permission &6&l%PERM% &3has been successfully removed.";

    @YamlComment("Team Ender Chest Messages")
    @YamlKey("team.echest.opened")
    @Getter
    private String teamEchestOpened = "&3Opened team ender chest #%NUMBER%";

    @YamlKey("team.echest.not-exist")
    @Getter
    private String teamEchestNotExist = "&cTeam ender chest #%NUMBER% does not exist!";


    @YamlComment("Fee")
    @YamlKey("team.fee.current")
    @Getter
    private String teamFeeCurrent = "[The fee to join your team is set to:](dark_aqua) [%AMOUNT%](gold)";

    @YamlKey("team.fee.disable")
    @Getter
    private String teamFeeDisable = "[You have disabled the team fee!](dark_aqua)\n[new players will now join the team for free!](dark_aqua)";

    @YamlKey("team.fee.success")
    @Getter
    private String teamFeeSet = "[You have successfully set the team fee to:](dark_aqua) [%AMOUNT%](gold)";

    @YamlKey("team.fee.amount-too-big")
    @Getter
    private String teamFeeTooBig = "[The fee value exceeds the maximum limit, which is:](dark_aqua) [%AMOUNT%](gold)";

    @YamlKey("team.fee.cant-join-not-enough-money")
    @Getter
    private String teamFeeCantJoin = "[You can't join](dark_aqua) [%TEAM%](gold) [Because you don't have enough money to pay the join fee. The join fee is:](dark_aqua) [%AMOUNT%](gold)";

    @YamlKey("team.fee.fee-deposited")
    @Getter
    private String teamFeeDeposited = "[%PLAYER%](gold) [has deposited his join fee](dark_aqua) [(](dark_aqua)[%AMOUNT%](gold)[)](dark_aqua) [in the team bank](dark_aqua)";


    @YamlComment("Invites")
    @YamlKey("invites.invites-disabled")
    @Getter
    private String invitesDisabled = "[Invites successfully disabled!](green)\n [Players can't invite you in their teams anymore.](dark_aqua)";

    @YamlKey("invites.invites-enabled")
    @Getter
    private String invitesEnabled = "[Invites successfully enabled!](green)\n [Players can now invite you in their teams.](dark_aqua)";


    @YamlComment("Economy")
    @YamlKey("economy.not-enough-money")
    @Getter
    private String notEnoughMoney = "&3You don't have enough money for this action. Required balance is: &6%MONEY%";

    @YamlKey("economy.invalid-amount")
    @Getter
    private String economyInvalidAmount = "[%MONEY%](gold) [is not a valid amount](dark_aqua)";

    @YamlKey("economy.deposited")
    @Getter
    private String moneyDeposited = "[You have successfully deposited](dark_aqua) [%CURRENCY%%MONEY%](gold) [in the team bank](dark_aqua)";

    @YamlKey("economy.withdraw.success")
    @Getter
    private String moneyWithdrawn = "[You have successfully withdrawn](dark_aqua) [%CURRENCY%%MONEY%](gold) [from the team bank](dark_aqua)";

    @YamlComment("You can use %MONEY% to display the amount in this message.")
    @YamlKey("economy.withdraw.not-enough-funds")
    @Getter
    private String moneyWithdrawNotEnoughFunds = "[The team bank does not have enough funds!](dark_aqua)";


    @YamlComment("General Plugin Messages")
    @YamlKey("general.no-colour-permission")
    @Getter
    private String useColoursMissingPermission = "&3You don't have the required permission to use color codes";

    @YamlKey("general.player-not-found")
    @Getter
    private String playerNotFound = "&3The player doesn't exist.";

    @YamlKey("general.must-be-owner")
    @Getter
    private String teamMustBeOwner = "&3You must be the team owner to do this.";

    @YamlKey("general.team-not-found")
    @Getter
    private String teamNotFound = "&3The team doesn't exist.";

    @YamlKey("general.function-disabled")
    @Getter
    private String functionDisabled = "&3Sorry, that has been disabled. :(";

    @YamlKey("general.no-permission")
    @Getter
    private String noPermission = "&cYou don't have permission to do that.";

    @YamlKey("general.plugin-reload-begin")
    @Getter
    private String pluginReloadBegin = "&6UltimateTeams: &aBeginning plugin reload...";

    @YamlKey("general.plugin-reload-successful")
    @Getter
    private String pluginReloadSuccessful = "&6UltimateTeams: &aThe plugin has been successfully reloaded!";

    @YamlKey("general.incorrect-command-usage")
    @Getter
    private String incorrectCommandUsage = "&3Unrecognised argument please use &6/team&3.";

    @YamlKey("general.player-only-command")
    @Getter
    private String playerOnlyCommand = "&4Sorry, that command can only be run by a player!";

    @YamlKey("general.invite-wipe-started")
    @Getter
    private String autoInviteWipeStarted = "&6UltimateTeams: &aAuto invite wipe task has started.";

    @YamlKey("general.invite-wipe-complete")
    @Getter
    private String autoInviteWipeComplete = "&6UltimateTeams: &aCleared all outstanding team invites!";

    @YamlKey("general.invite-wipe-failed")
    @Getter
    private String inviteWipeFailed = "&6UltimateTeams: &cFailed to clear all outstanding team invites!";

    @YamlComment("Admin Disband Messages")
    @YamlKey("admin.disband.failure")
    @Getter
    private String teamAdminDisbandFailure = "&3Failed to disband team - make sure that the provided player is a team owner!";

    @YamlKey("admin.disband.incorrect-usage")
    @Getter
    private String incorrectDisbandCommandUsage = "&3Unrecognised argument please use &6/teamadmin disband <team-owner>&3.";

    @YamlComment("Admin Ender Chest Messages")
    @YamlKey("admin.echest.added")
    @Getter
    private String teamEchestAdded = "&aAdded ender chest #%NUMBER% with %ROWS% rows to team %TEAM%";

    @YamlKey("admin.echest.removed")
    @Getter
    private String teamEchestRemoved = "&aRemoved ender chest #%NUMBER% from team %TEAM%";

    @YamlKey("admin.echest.rows-added")
    @Getter
    private String teamEchestRowsAdded = "&aAdded %ROWS% rows to chest #%NUMBER% (now %TOTAL% rows / %SLOTS% slots) for team %TEAM%";

    @YamlKey("admin.echest.page-added")
    @Getter
    private String teamEchestPageAdded = "&aAdded %TYPE% page #%NUMBER% (%ROWS% rows / %SLOTS% slots) to team %TEAM%";

    @YamlComment("Admin Ender Chest Rollback Messages")
    @YamlKey("admin.echest.backup.list-header")
    @Getter
    private String teamEchestBackupListHeader = "&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";

    @YamlKey("admin.echest.backup.list-title")
    @Getter
    private String teamEchestBackupListTitle = "&e&lEnder Chest #%CHEST% Backups";

    @YamlKey("admin.echest.backup.list-team")
    @Getter
    private String teamEchestBackupListTeam = "&7Team: &f%TEAM%";

    @YamlKey("admin.echest.backup.list-entry")
    @Getter
    private String teamEchestBackupListEntry = "&e#%NUMBER% &7- &f%TIMESTAMP% &7(%TIMEAGO%)";

    @YamlKey("admin.echest.backup.list-footer-admin")
    @Getter
    private String teamEchestBackupListFooterAdmin = "&7Use &e/ta echest rollback <team> %CHEST% <backup#> &7to restore";

    @YamlKey("admin.echest.backup.list-footer-force")
    @Getter
    private String teamEchestBackupListFooterForce = "&7Use &e/ta echest forcerollback <team> %CHEST% <backup#> &7to force restore";

    @YamlKey("admin.echest.backup.no-backups")
    @Getter
    private String teamEchestNoBackups = "&cNo backups available for chest #%CHEST%";

    @YamlKey("admin.echest.rollback.success")
    @Getter
    private String teamEchestRollbackSuccess = "&a✓ Successfully rolled back Ender Chest #%CHEST%!";

    @YamlKey("admin.echest.rollback.success-admin")
    @Getter
    private String teamEchestRollbackSuccessAdmin = "&a✓ Successfully rolled back Ender Chest #%CHEST% for team %TEAM%!";

    @YamlKey("admin.echest.rollback.restored-from")
    @Getter
    private String teamEchestRollbackRestoredFrom = "&7Restored from: &f%TIMESTAMP% &7(%TIMEAGO%)";

    @YamlKey("admin.echest.rollback.failed")
    @Getter
    private String teamEchestRollbackFailed = "&c✗ Failed to rollback Ender Chest #%CHEST%!";

    @YamlKey("admin.echest.rollback.invalid-backup")
    @Getter
    private String teamEchestRollbackInvalidBackup = "&cBackup #%NUMBER% does not exist! Only %TOTAL% backup(s) available.";

    @YamlKey("admin.echest.rollback.broadcast")
    @Getter
    private String teamEchestRollbackBroadcast = "&e&l[!] &eTeam Ender Chest #%CHEST% has been rolled back by %PLAYER%";

    @YamlKey("admin.echest.rollback.admin-broadcast")
    @Getter
    private String teamEchestRollbackAdminBroadcast = "&c&l[!] &cADMIN ALERT: &eTeam Ender Chest #%CHEST% has been rolled back by an administrator";

    @YamlKey("admin.echest.rollback.force-warning")
    @Getter
    private String teamEchestRollbackForceWarning = "&c&l⚠ FORCE ROLLBACK - Team was not notified";

    @YamlKey("admin.echest.all-backup.success")
    @Getter
    private String teamEchestAllBackupSuccess = "&a✓ Successfully backed up all %COUNT% ender chest(s) for team %TEAM%!";

    @YamlKey("admin.echest.all-backup.no-chests")
    @Getter
    private String teamEchestAllBackupNoChests = "&eTeam %TEAM% has no ender chests to backup.";

    @YamlKey("admin.echest.remove-row.success")
    @Getter
    private String teamEchestRemoveRowSuccess = "&a✓ Removed %ROWS% row(s) from chest #%CHEST% for team %TEAM%";

    @YamlKey("admin.echest.remove-row.new-size")
    @Getter
    private String teamEchestRemoveRowNewSize = "&7New size: %NEW_ROWS% rows (%SLOTS% slots)";

    @YamlKey("admin.echest.remove-row.warning")
    @Getter
    private String teamEchestRemoveRowWarning = "&c⚠ Warning: Items in removed slots will be lost!";

    @YamlKey("admin.echest.remove-row.invalid")
    @Getter
    private String teamEchestRemoveRowInvalid = "&cCannot remove %ROWS% rows! Chest #%CHEST% only has %CURRENT_ROWS% rows.";

    @YamlKey("admin.echest.remove-row.minimum")
    @Getter
    private String teamEchestRemoveRowMinimum = "&eMinimum is 1 row. Use &6/ta removechest %TEAM% %CHEST% &eto delete the entire chest.";

    @YamlComment("/team Command Responses")
    @YamlKey("commands.team-help")
    @Getter
    private List<String> teamCommandIncorrectUsage = List.of(
            "&6UltimateTeams usage:&3",
            "/team create <name>",
            "/team disband",
            "/team invite accept/deny/send <player>",
            "/team leave",
            "/team kick <player>",
            "/team info",
            "/team list",
            "/team prefix <prefix>",
            "/team transfer <player-name>",
            "/team ally [add|remove] <team-owner>",
            "/team enemy [add|remove] <team-owner>",
            "/team pvp",
            "/team [sethome|delhome|home]",
            "/team promote <player>",
            "/team demote <player>",
            "/team permissions add <permission>",
            "/team permissions remove <permission>",
            "/team echest [number]",
            ""
    );

    @YamlComment("/teamadmin Command Responses")
    @YamlKey("commands.admin-help")
    @Getter
    private List<String> teamAdminCommandIncorrectUsage = List.of(
            "&6UltimateTeams Admin usage:&3",
            "/teamadmin save",
            "/teamadmin reload",
            "/teamadmin disband <team-owner>",
            "/teamadmin about",
            "/teamadmin see <team-name> <chest-number>",
            "/teamadmin addechest <team-name> <rows|chest|doublechest>",
            "/teamadmin removeechest <team-name> <chest-number>",
            "/teamadmin listechests <team-name>",
            "/teamadmin echest backups <team-name> <chest-number>",
            "/teamadmin echest rollback <team-name> <chest-number> <backup#>",
            "/teamadmin echest forcerollback <team-name> <chest-number> <backup#>",
            "/teamadmin echest allbackup <team-name>",
            "/teamadmin removerow <team-name> <chest-number> <rows>",
            "/teamadmin removechest <team-name> <chest-number>"
    );


    @SuppressWarnings("unused")
    private Messages() {
    }
}