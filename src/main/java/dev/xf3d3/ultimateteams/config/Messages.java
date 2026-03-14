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
    private String teamCreateIncorrectUsage = "[use /team create <name>.](dark_aqua)";

    @YamlKey("team.create.name-too-short")
    @Getter
    private String teamNameTooShort = "[Team name too short - minimum length is](dark_aqua) [%CHARMIN%](gold) [characters.](dark_aqua)";

    @YamlKey("team.create.name-too-long")
    @Getter
    private String teamNameTooLong = "[Team name too long - maximum length is](dark_aqua) [%CHARMAX%](gold) [characters.](dark_aqua)";

    @YamlKey("team.create.successful")
    @Getter
    private String teamCreatedSuccessfully = "[Team](dark_aqua) [%TEAM%](gold) [was Created!](dark_aqua)";

    @YamlKey("team.create.name-taken")
    @Getter
    private String teamNameAlreadyTaken = "[Sorry but](dark_aqua) [%TEAM%](gold) [is already taken.](dark_aqua)\n[Please choose another!](dark_aqua)";

    @YamlKey("team.create.name-banned")
    @Getter
    private String teamNameIsBanned = "[Sorry but](dark_aqua) [%TEAM%](gold) [is a](dark_aqua) [BANNED](red) [name!](dark_aqua)\n[Please choose another!](dark_aqua)";

    @YamlKey("team.create.name-contains-space")
    @Getter
    private String teamNameContainsSpace = "[Sorry but, since it contains a space,](dark_aqua) [%TEAM%](gold) [is a](dark_aqua) [BANNED](red) [name!](dark_aqua)\n[Please choose another!](dark_aqua)";

    @YamlKey("team.create.name-cannot-contain-colours")
    @Getter
    private String teamNameCannotContainColours = "[Sorry, the team name cannot contain '&' or '#' characters.](dark_aqua)";

    @YamlKey("team.create.failed")
    @Getter
    private String teamCreationFailed = "[Team](dark_aqua) [%TEAM%](gold) [was NOT created, please make sure you're not already in a team!](dark_aqua)";

    @YamlKey("team.create.broadcast-chat")
    @Getter
    private String teamCreatedBroadcastChat = "[%TEAMOWNER%](gold) [Created a new team!](dark_aqua)\n[The new team is called](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";

    @YamlKey("team.create.broadcast-title-1")
    @Getter
    private String teamCreatedBroadcastTitle1 = "[%TEAMOWNER%](gold) [Created a new team!](dark_aqua)";

    @YamlKey("team.create.broadcast-title-2")
    @Getter
    private String teamCreatedBroadcastTitle2 = "[The new team is called](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";

    @YamlComment("Team Rename")
    @YamlKey("team.rename.successful")
    @Getter
    private String teamNameChangeSuccessful = "[Successfully changed team name to](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";

    @YamlComment("Team Disbanded Messages")
    @YamlKey("team.disband.warning")
    @Getter
    private String teamDisbandWarning = "[If you're sure you want to delete your team type:](dark_aqua) [/team disband confirm](gold)[.](dark_aqua) [This action cannot be undone!](red)";

    @YamlKey("team.disband.successful")
    @Getter
    private String teamSuccessfullyDisbanded = "[Team was disbanded!](dark_aqua)";

    @YamlKey("team.disband.failure")
    @Getter
    private String teamDisbandFailure = "[Failed to disband team - Please make sure you're the owner!](dark_aqua)";

    @YamlComment("Team Transfer Ownership Messages")
    @YamlKey("team.transfer.successful")
    @Getter
    private String teamOwnershipTransferSuccessful = "[You successfully transferred your team to](dark_aqua) [%PLAYER%](gold)[.](dark_aqua)";

    @YamlKey("team.transfer.new-owner-message")
    @Getter
    private String teamOwnershipTransferNewOwner = "[You are now the owner of](dark_aqua) [%TEAM%](gold)[.](dark_aqua)";

    @YamlKey("team.transfer.failure-not-same-team")
    @Getter
    private String teamOwnershipTransferFailureNotSameTeam = "[Failed to transfer team ownership as the player is not in your team.](dark_aqua)";

    @YamlKey("team.transfer.failure-owner-offline")
    @Getter
    private String teamOwnershipTransferFailureOwnerOffline = "[Failed to transfer team ownership to](dark_aqua) [%PLAYER%](gold)[!](dark_aqua)\n[They may be offline.](dark_aqua)";

    @YamlKey("team.transfer.failed-cannot-transfer-to-self")
    @Getter
    private String teamOwnershipTransferFailedCannotTransferToSelf = "[Failed to transfer team ownership to](dark_aqua) [%PLAYER%](gold)[!](dark_aqua)\n[The specified player cannot be yourself!](dark_aqua)";

    @YamlKey("team.transfer.failed-target-in-team")
    @Getter
    private String teamOwnershipTransferFailedTargetInTeam = "[Failed to transfer team as the target is already in/owns a team!](dark_aqua)";

    @YamlKey("team.transfer.incorrect-usage")
    @Getter
    private String incorrectTeamTransferOwnershipCommandUsage = "[Unrecognised argument please use](dark_aqua) [/team transfer <player-name>](gold)[.](dark_aqua)";

    @YamlComment("Team Invite Messages")
    @YamlKey("team.invite.no-valid-player")
    @Getter
    private String teamInviteNoValidPlayer = "[Please specify a player to invite!](dark_aqua)";

    @YamlKey("team.invite.not-team-owner")
    @Getter
    private String teamInviteNotTeamOwner = "[You must be a team owner to invite people!](dark_aqua)";

    @YamlKey("team.invite.self-error")
    @Getter
    private String teamInviteSelfError = "[You can't invite yourself!](dark_aqua)";

    @YamlKey("team.invite.invitee-not-found")
    @Getter
    private String teamInviteeNotFound = "[Player](dark_aqua) [%INVITED%](gold) [was not found, make sure they are online!](dark_aqua)";

    @YamlKey("team.invite.invited-already-in-team")
    @Getter
    private String teamInviteInvitedAlreadyInTeam = "[Player](dark_aqua) [%INVITED%](gold) [is already in a team!](dark_aqua)";

    @YamlKey("team.invite.max-size-reached")
    @Getter
    private String teamInviteMaxSizeReached = "[You have reached the team members size limit](dark_aqua) [%LIMIT%](green)[!](dark_aqua)";

    @YamlKey("team.invite.successful")
    @Getter
    private String teamInviteSuccessful = "[You have invited](dark_aqua) [%INVITED%](gold) [to your team!](dark_aqua)";

    @YamlKey("team.invite.failed")
    @Getter
    private String teamInviteFailed = "[Failed to send invite to](dark_aqua) [%INVITED%](gold)[, this player might already have an invitation!](dark_aqua)";

    @YamlKey("team.invite.invite-pending")
    @Getter
    private String teamInvitedPlayerInvitePending = "[You have been invited to a team by](dark_aqua) [%TEAMOWNER%](gold) [- use /team invite accept](dark_aqua)";

    @YamlKey("team.invite.request")
    @Getter
    private String teamInviteRequest = "[%PLAYER%](gold) [would like you to invite them to your team.](dark_aqua)\n[Use](dark_aqua) [/team invite %PLAYER%](gold) [to send the invite.](dark_aqua)";

    @YamlKey("team.invite.request-failed")
    @Getter
    private String teamInviteRequestFailed = "[Failed to send request, check that the team owner is online!](dark_aqua)";

    @YamlKey("team.invite.request-sent-successfully")
    @Getter
    private String teamInviteSentSuccessfully = "[You have successfully sent a request to %TEAMOWNER%](dark_aqua)";

    @YamlKey("team.invite.request-failed-own-team")
    @Getter
    private String teamInviteFailedOwnTeam = "[Failed to send request, this is YOUR team!](dark_aqua)";

    @YamlKey("team.invite.request-failed-already-in-team")
    @Getter
    private String teamInviteFailedAlreadyInTeam = "[Failed to send request, you are already in a team!](dark_aqua)";

    @YamlKey("team.invite.deny-failed-no-invite")
    @Getter
    private String teamInviteDenyFailedNoInvite = "[Failed to deny invite - you don't have an active invite.](dark_aqua)";

    @YamlKey("team.invite.denied")
    @Getter
    private String teamInviteDenied = "[Invite successfully denied.](dark_aqua)";

    @YamlKey("team.invite.denied-inviter")
    @Getter
    private String teamInviteDeniedInviter = "[%PLAYER% denied your invite.](dark_aqua)";

    @YamlKey("team.invite.deny-fail")
    @Getter
    private String teamInviteDenyFail = "[Failed to deny the invite.](dark_aqua)";

    @YamlKey("team.invite.received-message")
    @YamlComment("You can remove the join fee line if you have it disabled")
    @Getter
    private List<String> teamInviteInvitedMessage = List.of(
            "&7&m                                                    &r",
            "[Team Invitation](gold bold)",
            "[You've been invited to join](yellow) [%TEAM%](gold) [by](yellow) [%INVITER%](gold)",
            "[Join fee:](yellow) [%FEE%](gold)[%CURRENCY_NAME%](green)",
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
    private String teamJoinSuccessful = "[Successfully joined](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";

    @YamlKey("team.join.failed")
    @Getter
    private String teamJoinFailed = "[Failed to join](dark_aqua) [%TEAM%](gold)";

    @YamlKey("team.join.failed-no-valid-team")
    @Getter
    private String teamJoinFailedNoValidTeam = "[Failed to join a team - no team was found!](dark_aqua)";

    @YamlKey("team.join.failed-no-invite")
    @Getter
    private String teamJoinFailedNoInvite = "[Failed to join a team - no invite was found!](dark_aqua)";

    @YamlKey("team.join.broadcast-chat")
    @Getter
    private String teamJoinBroadcastChat = "[The player](dark_aqua) [%PLAYER%](gold) [has joined your team!](dark_aqua)";

    @YamlComment("Team Leave Messages")
    @YamlKey("team.leave.failed-owner")
    @Getter
    private String failedTeamOwner = "[You are the owner of a team, use](dark_aqua) [/team disband](gold)[.](dark_aqua)";

    @YamlKey("team.leave.successful")
    @Getter
    private String teamLeaveSuccessful = "[You have left](dark_aqua) [%TEAM%](gold)[.](dark_aqua)";

    @YamlKey("team.leave.failed")
    @Getter
    private String teamLeaveFailed = "[Failed to leave team, please try again later.](dark_aqua)";

    @YamlKey("team.leave.broadcast-chat")
    @Getter
    private String teamLeftBroadcastChat = "[The player](dark_aqua) [%PLAYER%](gold) [has left your team!](dark_aqua)";

    @YamlComment("Team Warp Messages")
    @YamlKey("team.warp.successful")
    @Getter
    private String teamWarpSuccessful = "[Successfully added team warp](dark_aqua) [%WARP_NAME%](gold)[!](dark_aqua)";

    @YamlKey("team.warp.name-used")
    @Getter
    private String teamWarpNameUsed = "[A warp with that name already exists!](dark_aqua)";

    @YamlKey("team.warp.limit-reached")
    @Getter
    private String teamWarpLimitReached = "[You have reached the team warps limit!](dark_aqua)";

    @YamlKey("team.warp.deleted-successful")
    @Getter
    private String teamWarpDeletedSuccessful = "[Successfully deleted team warp](dark_aqua) [%WARP_NAME%](gold)[!](dark_aqua)";

    @YamlKey("team.warp.not-found")
    @Getter
    private String teamWarpNotFound = "[There is no warp with that name!](dark_aqua)";

    @YamlKey("team.warp.cooldown-start")
    @Getter
    private String teamWarpCooldownStart = "[You will be teleported in %SECONDS%s!](dark_aqua)";

    @YamlKey("team.warp.teleported-successful")
    @Getter
    private String teamWarpTeleportedSuccessful = "[Successfully teleported to the team warp!](dark_aqua)";

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
    private String teamMemberKickSuccessful = "[Player](dark_aqua) [%KICKEDPLAYER%](gold) [was kicked from your team.](dark_aqua)";

    @YamlKey("team.kick.kicked-player-message")
    @Getter
    private String teamKickedPlayerMessage = "[You were kicked from](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";

    @YamlKey("team.kick.target-not-in-team")
    @Getter
    private String targetedPlayerIsNotInYourTeam = "[Player](dark_aqua) [%KICKEDPLAYER%](gold) [is not in your team.](dark_aqua)";

    @YamlKey("team.kick.player-not-found")
    @Getter
    private String couldNotFindSpecifiedPlayer = "[Could not find player](dark_aqua) [%KICKEDPLAYER%](gold)[. They may be have not joined before.](dark_aqua)";

    @YamlKey("team.kick.must-be-owner")
    @Getter
    private String mustBeOwnerToKick = "[You are not an owner of a team!](dark_aqua)";

    @YamlKey("team.kick.incorrect-usage")
    @Getter
    private String incorrectKickCommandUsage = "[Unrecognised argument please use](dark_aqua) [/team kick <member>](gold)[.](dark_aqua)";

    @YamlKey("team.kick.failed-cannot-kick-yourself")
    @Getter
    private String failedCannotKickYourself = "[You cannot kick yourself, use](dark_aqua) [/team leave](gold)[.](dark_aqua)";

    @YamlComment("Team Prefix Messages")
    @YamlKey("team.prefix.change-successful")
    @Getter
    private String teamPrefixChangeSuccessful = "[Successfully changed team prefix to ](dark_aqua)%TEAMPREFIX%[!]";

    @YamlKey("team.prefix.too-long")
    @Getter
    private String teamPrefixTooLong = "[Team prefix too long - maximum length is](dark_aqua) [%CHARMAX%](gold) [characters.](dark_aqua)";

    @YamlKey("team.prefix.too-short")
    @Getter
    private String teamPrefixTooShort = "[Team prefix too short - minimum length is](dark_aqua) [%CHARMIN%](gold) [characters.](dark_aqua)";

    @YamlKey("team.prefix.invalid-prefix")
    @Getter
    private String teamInvalidPrefix = "[Please provide a new prefix. Use](dark_aqua) [/team prefix <prefix>](aqua)[!](dark_aqua)";

    @YamlKey("team.prefix.color-hint")
    @Getter
    private String teamPrefixColorHint = "[Tip: Use](gray) [&&](yellow) [for color codes (e.g. &c&a for green) or](gray) [#](yellow) [for hex colors (e.g. &#FF5555)](gray)";

    @YamlKey("team.prefix.already-taken")
    @Getter
    private String teamPrefixAlreadyTaken = "[Sorry but](dark_aqua) [%TEAMPREFIX%](gold) [is already taken.](dark_aqua)\n[Please choose another!](dark_aqua)";

    @YamlKey("team.prefix.is-banned")
    @Getter
    private String teamPrefixIsBanned = "[Sorry but](dark_aqua) [%TEAMPREFIX%](gold) [is a](dark_aqua) [BANNED](red) [name!](dark_aqua)\n[Please choose another!](dark_aqua)";

    @YamlKey("team.prefix.must-be-owner")
    @Getter
    private String mustBeOwnerToChangePrefix = "[You are not an owner of a team!](dark_aqua)";

    @YamlKey("team.prefix.cannot-contain-colours")
    @Getter
    private String teamTagCannotContainColours = "[Sorry, the team prefix cannot contain '&' or '#' characters.](dark_aqua)";


    @YamlComment("Team MOTD Messages")
    @YamlKey("team.motd.cannot-contain-colours")
    @Getter
    private String teamMotdCannotContainColours = "[Sorry, the team MOTD cannot contain '&' or '#' characters.](dark_aqua)";

    @YamlKey("team.motd.change-successful")
    @Getter
    private String teamMotdChangeSuccessful = "[Successfully changed team MOTD to:](dark_aqua) %MOTD%[!](dark_aqua)";

    @YamlKey("team.motd.not-valid")
    @Getter
    private String teamMotdNotValid = "[The team MOTD contains invalid characters!](dark_aqua)";

    @YamlKey("team.motd.disabled")
    @Getter
    private String teamMotdDisabledSuccessful = "[Successfully disabled the team MOTD.](dark_aqua)";

    @YamlKey("team.motd.not-set")
    @Getter
    private String teamMotdNotSet = "[Team MOTD not set.](dark_aqua)";

    @YamlKey("team.motd.too-long")
    @Getter
    private String teamMotdTooLong = "[Team MOTD too long - maximum length is](dark_aqua) [%CHARMAX%](gold) [characters.](dark_aqua)";

    @YamlKey("team.motd.too-short")
    @Getter
    private String teamMotdTooShort = "[Team MOTD too short - minimum length is](dark_aqua) [%CHARMIN%](gold) [characters.](dark_aqua)";


    @YamlComment("Team List Messages")
    @YamlKey("team.list.no-teams")
    @Getter
    private String noTeamsToList = "[No teams found!](dark_aqua)\n[Create one using](dark_aqua) [/team create <name>](aqua)[!](dark_aqua)";

    @YamlKey("team.list.header")
    @Getter
    private String teamsListHeader = "[----- ](gray)[TeamsList](gold)[ -----](gray)\n[Current teams:](dark_aqua bold)\n";

    @YamlKey("team.list.footer")
    @Getter
    private String teamsListFooter = "[-----](gray)";

    @YamlComment("Team Info Messages")
    @YamlKey("team.info.header")
    @Getter
    private String teamInfoHeader = "[----- ](gray)[TeamInfo](gold)[ -----](gray)\n[Name: %TEAM%](dark_aqua)\n[Prefix:](dark_aqua) [(](gray)%TEAMPREFIX%[)](gray)";

    @YamlKey("team.info.owner-online")
    @Getter
    private String teamInfoOwnerOnline = "[Owner:](dark_aqua) [%OWNER%](green)";

    @YamlKey("team.info.owner-offline")
    @Getter
    private String teamInfoOwnerOffline = "[Owner:](dark_aqua) [%OWNER%](red) [(offline)](gray italic)";

    @YamlKey("team.info.members-header")
    @Getter
    private String teamInfoMembersHeader = "[Members:](dark_aqua) [(%NUMBER%)](dark_aqua italic)";

    @YamlKey("team.info.managers-header")
    @Getter
    private String teamInfoManagersHeader = "[Managers:](dark_aqua) [(%NUMBER%)](dark_aqua italic)";

    @YamlKey("team.info.members-online")
    @Getter
    private String teamInfoMembersOnline = "[%MEMBER%](green)";

    @YamlKey("team.info.members-offline")
    @Getter
    private String teamInfoMembersOffline = "[%MEMBER%](red) [(offline)](gray italic)";

    @YamlKey("team.info.allies-header")
    @Getter
    private String teamInfoAlliesHeader = "[Allied Teams:](dark_aqua)";

    @YamlKey("team.info.ally-list-entry")
    @Getter
    private String teamAllyMembers = "[%ALLYTEAM%](green)";

    @YamlKey("team.info.ally-list-not-found")
    @Getter
    private String teamAllyMembersNotFound = "[Ally not found](green)";

    @YamlKey("team.info.enemies-header")
    @Getter
    private String teamInfoEnemiesHeader = "[Enemy Teams:](dark_aqua)";

    @YamlKey("team.info.enemy-list-entry")
    @Getter
    private String teamEnemyMembers = "[%ENEMYTEAM%](red)";

    @YamlKey("team.info.enemy-list-not-found")
    @Getter
    private String teamEnemyMembersNotFound = "[Enemy not found](green)";

    @YamlKey("team.info.join-fee")
    @Getter
    private String teamInfoJoinFee = "[Join Fee:](dark_aqua) [%AMOUNT%](green)";

    @YamlKey("team.info.motd")
    @Getter
    private String teamInfoMotd = "[MOTD:](dark_aqua) [%MOTD%](green)";

    @YamlKey("team.info.pvp-enabled")
    @Getter
    private String teamPvpStatusEnabled = "[Friendly Fire:](dark_aqua) [ENABLED](green italic)";

    @YamlKey("team.info.pvp-disabled")
    @Getter
    private String teamPvpStatusDisabled = "[Friendly Fire:](dark_aqua) [DISABLED](red italic)";

    @YamlKey("team.info.home-set")
    @Getter
    private String teamHomeSetTrue = "[Home Set:](dark_aqua) [TRUE](green italic)";

    @YamlKey("team.info.home-not-set")
    @Getter
    private String teamHomeSetFalse = "[Home Set:](dark_aqua) [FALSE](red italic)";

    @YamlKey("team.info.bank-amount")
    @Getter
    private String teamInfoBankAmount = "[Bank Amount:](dark_aqua) [%AMOUNT%](gold)";

    @YamlKey("team.info.footer")
    @Getter
    private String teamInfoFooter = "[-----](gray)";

    @YamlKey("team.info.not-in-team")
    @Getter
    private String notInTeam = "[You are not in a team!](dark_aqua)";

    @YamlComment("Team Ally Messages")
    @YamlKey("team.ally.added-successful")
    @Getter
    private String addedTeamToYourAllies = "[You successfully added](dark_aqua) [%ALLYTEAM%](gold) [to your allies!](dark_aqua)";

    @YamlKey("team.ally.added-notification")
    @Getter
    private String teamAddedToOtherAllies = "[%TEAMOWNER%](gold) [has added your team to their allies!](dark_aqua)";

    @YamlKey("team.ally.failed-to-add")
    @Getter
    private String failedToAddTeamToAllies = "[Unable to add](dark_aqua) [%ALLYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";

    @YamlKey("team.ally.failed-already-ally")
    @Getter
    private String failedTeamAlreadyYourAlly = "[This team is already your ally!](dark_aqua)";

    @YamlKey("team.ally.failed-player-not-owner")
    @Getter
    private String failedPlayerNotTeamOwner = "[%ALLYOWNER%](gold) [is not a team owner!](dark_aqua)";

    @YamlKey("team.ally.removed-successful")
    @Getter
    private String removedTeamFromYourAllies = "[You Successfully removed](dark_aqua) [%ALLYTEAM%](gold) [from your allies!](dark_aqua)";

    @YamlKey("team.ally.removed-owner-notification")
    @Getter
    private String teamOwnerRemovedTeamFromAllies = "[Your team owner has removed](dark_aqua) [%TEAM%](gold) [from your allies!](dark_aqua)";

    @YamlKey("team.ally.removed-other-notification")
    @Getter
    private String teamRemovedFromOtherAllies = "[The team](dark_aqua) [%TEAM%](gold) [has removed your team from their allies!](dark_aqua)";

    @YamlKey("team.ally.failed-to-remove")
    @Getter
    private String failedToRemoveTeamFromAllies = "[Unable to remove](dark_aqua) [%ALLYOWNER%](gold)[! Make sure they're your ally](dark_aqua)";

    @YamlKey("team.ally.add-owner-offline")
    @Getter
    private String allyTeamAddOwnerOffline = "[Unable to add](dark_aqua) [%ALLYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";

    @YamlKey("team.ally.remove-owner-offline")
    @Getter
    private String allyTeamRemoveOwnerOffline = "[Unable to remove](dark_aqua) [%ALLYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";

    @YamlKey("team.ally.incorrect-usage")
    @Getter
    private String incorrectTeamAllyCommandUsage = "[Unrecognised argument please use](dark_aqua) [/team ally [add|remove] <team-owner>](gold)[.](dark_aqua)";

    @YamlKey("team.ally.failed-cannot-ally-self")
    @Getter
    private String failedCannotAllyYourOwnTeam = "[You cannot be an ally with your](dark_aqua) [OWN](dark_aqua bold) [team!](dark_aqua)";

    @YamlKey("team.ally.max-amount-reached")
    @Getter
    private String teamAllyMaxAmountReached = "[You have reached the team allies amount limit](dark_aqua) [%LIMIT%](green)[!](dark_aqua)";

    @YamlKey("team.ally.failed-cannot-ally-enemy")
    @Getter
    private String failedCannotAllyEnemyTeam = "[You cannot be an ally with an](dark_aqua) [ENEMY](red bold) [team!](dark_aqua)";

    @YamlComment("Team Enemy Messages")
    @YamlKey("team.enemy.added-successful")
    @Getter
    private String addedTeamToYourEnemies = "[You successfully added](dark_aqua) [%ENEMYTEAM%](gold) [to your enemies!](dark_aqua)";

    @YamlKey("team.enemy.added-notification")
    @Getter
    private String teamAddedToOtherEnemies = "[%TEAMOWNER%](gold) [has added your team to their enemies!](dark_aqua)";

    @YamlKey("team.enemy.failed-to-add")
    @Getter
    private String failedToAdTeamToEnemies = "[Unable to add](dark_aqua) [%ENEMYOWNER%](gold)[! Make sure the owner is online!](dark_aqua)";

    @YamlKey("team.enemy.failed-already-enemy")
    @Getter
    private String failedTeamAlreadyYourEnemy = "[This team is already your enemy!](dark_aqua)";

    @YamlKey("team.enemy.failed-player-not-owner")
    @Getter
    private String failedEnemyPlayerNotTeamOwner = "[%ENEMYOWNER%](gold) [is not a team owner!](dark_aqua)";

    @YamlKey("team.enemy.removed-successful")
    @Getter
    private String removedTeamFromYourEnemies = "[You Successfully removed](dark_aqua) [%ENEMYTEAM%](gold) [from your enemies!](dark_aqua)";

    @YamlKey("team.enemy.removed-notification")
    @Getter
    private String teamRemovedFromOtherEnemies = "[The team](dark_aqua) [%TEAMOWNER%](gold) [has removed your team from their enemies!](dark_aqua)";

    @YamlKey("team.enemy.failed-to-remove")
    @Getter
    private String failedToRemoveTeamFromEnemies = "[Unable to remove](dark_aqua) [%ENEMYTEAM%](gold)[! Make sure they're your enemy!](dark_aqua)";

    @YamlKey("team.enemy.add-owner-offline")
    @Getter
    private String enemyTeamAddOwnerOffline = "[Unable to add](dark_aqua) [%ENEMYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";

    @YamlKey("team.enemy.remove-owner-offline")
    @Getter
    private String enemyTeamRemoveOwnerOffline = "[Unable to remove](dark_aqua) [%ENEMYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";

    @YamlKey("team.enemy.incorrect-usage")
    @Getter
    private String incorrectTeamEnemyCommandUsage = "[Unrecognised argument please use](dark_aqua) [/team enemy [add|remove] <team-owner>](gold)[.](dark_aqua)";

    @YamlKey("team.enemy.failed-cannot-enemy-self")
    @Getter
    private String failedCannotEnemyYourOwnTeam = "[You cannot be an enemy with your](dark_aqua) [OWN](dark_aqua bold) [team!](dark_aqua)";

    @YamlKey("team.enemy.max-amount-reached")
    @Getter
    private String teamEnemyMaxAmountReached = "[You have reached the team enemies amount limit](dark_aqua) [%LIMIT%](green)[!](dark_aqua)";

    @YamlKey("team.enemy.failed-cannot-enemy-ally")
    @Getter
    private String failedCannotEnemyAlliedTeam = "[You cannot be an enemy with an](dark_aqua) [ALLIED](green bold) [team!](dark_aqua)";

    @YamlKey("team.enemy.war-declared-title-1")
    @Getter
    private String addedEnemyTeamToYourEnemiesTitle1 = "[YOUR TEAM IS NOW AT WAR WITH:](red bold)";

    @YamlKey("team.enemy.war-declared-title-2")
    @Getter
    private String addedEnemyTeamToYourEnemiesTitle2 = "[%TEAMOWNER%'s](gold) [Team!](red)";

    @YamlKey("team.enemy.peace-declared-title-1")
    @Getter
    private String removedEnemyTeamFromYourEnemiesTitle1 = "[YOUR TEAM IS NO LONGER AT WAR WITH:](green bold)";

    @YamlKey("team.enemy.peace-declared-title-2")
    @Getter
    private String removedEnemyTeamFromYourEnemiesTitle2 = "[%TEAMOWNER%'s](gold) [Team!](green)";

    @YamlKey("team.enemy.war-received-title-1")
    @Getter
    private String teamAddedToOtherEnemiesTitle1 = "[YOUR TEAM IS NOW AT WAR WITH:](red bold)";

    @YamlKey("team.enemy.war-received-title-2")
    @Getter
    private String teamAddedToOtherEnemiesTitle2 = "[%TEAMOWNER%'s](gold) [Team!](red)";

    @YamlKey("team.enemy.peace-received-title-1")
    @Getter
    private String teamRemovedFromOtherEnemiesTitle1 = "[YOUR TEAM IS NO LONGER AT WAR WITH:](green bold)";

    @YamlKey("team.enemy.peace-received-title-2")
    @Getter
    private String teamRemovedFromOtherEnemiesTitle2 = "[%TEAMOWNER%'s](gold) [Team!](green)";

    @YamlComment("Team Friendly Fire")
    @YamlKey("team.pvp.enabled")
    @Getter
    private String enabledFriendlyFire = "[You successfully](dark_aqua) [enabled](green) [friendly fire.](dark_aqua)\n[Your team members can now pvp each other!](dark_aqua)";

    @YamlKey("team.pvp.disabled")
    @Getter
    private String disabledFriendlyFire = "[You successfully](dark_aqua) [disabled](red) [friendly fire.](dark_aqua)\n[Your team members can no longer pvp each other!](dark_aqua)";

    @YamlKey("team.pvp.failed-not-in-team")
    @Getter
    private String failedNotInTeam = "[You need to be in a team first! Use](dark_aqua) [/team](gold) [for details how.](dark_aqua)";

    @YamlKey("team.pvp.is-disabled")
    @Getter
    private String friendlyFireIsDisabled = "[Your team has friendly fire disabled.](dark_aqua)";

    @YamlKey("team.pvp.is-disabled-for-allies")
    @Getter
    private String friendlyFireIsDisabledForAllies = "[You can't hurt a member of an allied team!](dark_aqua)";

    @YamlComment("Team Homes")
    @YamlKey("team.home.set-successful")
    @Getter
    private String successfullySetTeamHome = "[You](dark_aqua) [Successfully](green) [set the team home to your current location!](dark_aqua)";

    @YamlKey("team.home.delete-successful")
    @Getter
    private String successfullyDeletedTeamHome = "[You](dark_aqua) [Successfully](green) [deleted the team home!](dark_aqua)";

    @YamlKey("team.home.cooldown-wait")
    @Getter
    private String homeCoolDownTimerWait = "[Sorry, you can't use that again for another](dark_aqua) [%TIMELEFT%](gold) [seconds.](dark_aqua)";

    @YamlKey("team.home.cooldown-start")
    @Getter
    private String teamHomeCooldownStart = "[You will be teleported in %SECONDS%s!](dark_aqua)";

    @YamlKey("team.home.teleport-successful")
    @Getter
    private String successfullyTeleportedToHome = "[You teleported to your team's home.](dark_aqua)";

    @YamlKey("team.home.teleport-moved")
    @Getter
    private String teleportCancelledMoved = "[The teleport has been cancelled because you moved!](dark_aqua)";

    @YamlKey("team.home.teleport-damage")
    @Getter
    private String teleportCancelledDamage = "[The teleport has been cancelled because you took damage!](dark_aqua)";

    @YamlKey("team.home.no-home-set")
    @Getter
    private String failedNoHomeSet = "[Your team does not have a home set!](dark_aqua)";

    @YamlKey("team.home.failed-not-in-team")
    @Getter
    private String failedTpNotInTeam = "[You need to be in a team first! Use](dark_aqua) [/team](gold) [for details how.](dark_aqua)";

    @YamlComment("Team Chat")
    @YamlKey("team.chat.failed-not-in-team")
    @Getter
    private String failedMustBeInTeam = "[You need to be in a team first! Use](dark_aqua) [/team](gold) [for details how.](dark_aqua)";

    @YamlKey("team.chat.cooldown-wait")
    @Getter
    private String chatCoolDownTimerWait = "[Sorry, you can't use that again for another](dark_aqua) [%TIMELEFT%](gold) [seconds.](dark_aqua)";

    @YamlKey("team.chat.toggle-on")
    @Getter
    private String chatToggleOn = "[Toggled Team Chat](dark_aqua) [ON](green bold)[.](dark_aqua)";

    @YamlKey("team.chat.toggle-off")
    @Getter
    private String chatToggleOff = "[Toggled Team Chat](dark_aqua) [OFF](red bold)[.](dark_aqua)";

    @YamlKey("team.chat.spy-toggle-on")
    @Getter
    private String chatspyToggleOn = "[Toggled Team Chat spy](dark_aqua) [ON](green bold)[.](dark_aqua)";

    @YamlKey("team.chat.spy-toggle-off")
    @Getter
    private String chatspyToggleOff = "[Toggled Team Chat spy](dark_aqua) [OFF](red bold)[.](dark_aqua)";

    @YamlKey("team.allychat.incorrect-usage")
    @Getter
    private String allychatIncorrectUsage = "[UltimateTeams team chat usage:](gold)\n[/allychat <message>](dark_aqua)";

    @YamlKey("team.allychat.toggle-on")
    @Getter
    private String allyChatToggleOn = "[Toggled Ally Chat](dark_aqua) [ON](green bold)[.](dark_aqua)";

    @YamlKey("team.allychat.toggle-off")
    @Getter
    private String allyChatToggleOff = "[Toggled Ally Chat](dark_aqua) [OFF](red bold)[.](dark_aqua)";

    @YamlComment("Team Managers")
    @YamlKey("team.manager.promote-self-error")
    @Getter
    private String teamPromoteSelfError = "[You can't promote yourself!](dark_aqua)";

    @YamlKey("team.manager.promote-successful")
    @Getter
    private String teamPromoteSuccessful = "[Player](dark_aqua) [%PLAYER%](gold) [has been successfully promoted to team manager.](dark_aqua)";

    @YamlKey("team.manager.demote-self-error")
    @Getter
    private String teamDemoteSelfError = "[You can't demote yourself!](dark_aqua)";

    @YamlKey("team.manager.demote-successful")
    @Getter
    private String teamDemoteSuccessful = "[Player](dark_aqua) [%PLAYER%](gold) [has been successfully demoted.](dark_aqua)";

    @YamlComment("Team permissions")
    @YamlKey("team.permission.not-found")
    @Getter
    private String permissionNotFound = "[The permission %PERM% doesn't exist.](dark_aqua)";

    @YamlKey("team.permission.added-successful")
    @Getter
    private String teamPermissionAddedSuccessful = "[The permission](dark_aqua) [%PERM%](gold bold) [has been successfully added.](dark_aqua)";

    @YamlKey("team.permission.removed-successful")
    @Getter
    private String teamPermissionRemovedSuccessful = "[The permission](dark_aqua) [%PERM%](gold bold) [has been successfully removed.](dark_aqua)";

    @YamlComment("Team Ender Chest Messages")
    @YamlKey("team.echest.opened")
    @Getter
    private String teamEchestOpened = "[Opened team ender chest #%NUMBER%](dark_aqua)";

    @YamlKey("team.echest.not-exist")
    @Getter
    private String teamEchestNotExist = "[Team ender chest #%NUMBER% does not exist!](red)";


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
    private String notEnoughMoney = "[You don't have enough money for this action. Required balance is:](dark_aqua) [%MONEY%](gold)";

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
    private String useColoursMissingPermission = "[You don't have the required permission to use color codes](dark_aqua)";

    @YamlKey("general.player-not-found")
    @Getter
    private String playerNotFound = "[The player doesn't exist.](dark_aqua)";

    @YamlKey("general.must-be-owner")
    @Getter
    private String teamMustBeOwner = "[You must be the team owner to do this.](dark_aqua)";

    @YamlKey("general.team-not-found")
    @Getter
    private String teamNotFound = "[The team doesn't exist.](dark_aqua)";

    @YamlKey("general.function-disabled")
    @Getter
    private String functionDisabled = "[Sorry, that has been disabled. :(](dark_aqua)";

    @YamlKey("general.no-permission")
    @Getter
    private String noPermission = "[You don't have permission to do that.](red)";

    @YamlKey("general.plugin-reload-begin")
    @Getter
    private String pluginReloadBegin = "[UltimateTeams:](gold) [Beginning plugin reload...](green)";

    @YamlKey("general.plugin-reload-successful")
    @Getter
    private String pluginReloadSuccessful = "[UltimateTeams:](gold) [The plugin has been successfully reloaded!](green)";

    @YamlKey("general.incorrect-command-usage")
    @Getter
    private String incorrectCommandUsage = "[Unrecognised argument please use](dark_aqua) [/team](gold)[.](dark_aqua)";

    @YamlKey("general.player-only-command")
    @Getter
    private String playerOnlyCommand = "[Sorry, that command can only be run by a player!](dark_red)";

    @YamlKey("general.invite-wipe-started")
    @Getter
    private String autoInviteWipeStarted = "[UltimateTeams:](gold) [Auto invite wipe task has started.](green)";

    @YamlKey("general.invite-wipe-complete")
    @Getter
    private String autoInviteWipeComplete = "[UltimateTeams:](gold) [Cleared all outstanding team invites!](green)";

    @YamlKey("general.invite-wipe-failed")
    @Getter
    private String inviteWipeFailed = "[UltimateTeams:](gold) [Failed to clear all outstanding team invites!](red)";

    @YamlComment("Admin Disband Messages")
    @YamlKey("admin.disband.failure")
    @Getter
    private String teamAdminDisbandFailure = "[Failed to disband team - make sure that the provided player is a team owner!](dark_aqua)";

    @YamlKey("admin.disband.incorrect-usage")
    @Getter
    private String incorrectDisbandCommandUsage = "[Unrecognised argument please use](dark_aqua) [/teamadmin disband <team-owner>](gold)[.](dark_aqua)";

    @YamlComment("Admin Ender Chest Messages")
    @YamlKey("admin.echest.added")
    @Getter
    private String teamEchestAdded = "[Added ender chest #%NUMBER% with %ROWS% rows to team %TEAM%](green)";

    @YamlKey("admin.echest.removed")
    @Getter
    private String teamEchestRemoved = "[Removed ender chest #%NUMBER% from team %TEAM%](green)";

    @YamlKey("admin.echest.rows-added")
    @Getter
    private String teamEchestRowsAdded = "[Added %ROWS% rows to chest #%NUMBER% (now %TOTAL% rows / %SLOTS% slots) for team %TEAM%](green)";

    @YamlKey("admin.echest.page-added")
    @Getter
    private String teamEchestPageAdded = "[Added %TYPE% page #%NUMBER% (%ROWS% rows / %SLOTS% slots) to team %TEAM%](green)";

    @YamlComment("Admin Ender Chest Rollback Messages")
    @YamlKey("admin.echest.backup.list-header")
    @Getter
    private String teamEchestBackupListHeader = "[▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬](gold bold)";

    @YamlKey("admin.echest.backup.list-title")
    @Getter
    private String teamEchestBackupListTitle = "[Ender Chest #%CHEST% Backups](yellow bold)";

    @YamlKey("admin.echest.backup.list-team")
    @Getter
    private String teamEchestBackupListTeam = "[Team:](gray) [%TEAM%](white)";

    @YamlKey("admin.echest.backup.list-entry")
    @Getter
    private String teamEchestBackupListEntry = "[#%NUMBER%](yellow) [-](gray) [%TIMESTAMP%](white) [(%TIMEAGO%)](gray)";

    @YamlKey("admin.echest.backup.list-footer-admin")
    @Getter
    private String teamEchestBackupListFooterAdmin = "[Use](gray) [/ta echest rollback <team> %CHEST% <backup#>](yellow) [to restore](gray)";

    @YamlKey("admin.echest.backup.list-footer-force")
    @Getter
    private String teamEchestBackupListFooterForce = "[Use](gray) [/ta echest forcerollback <team> %CHEST% <backup#>](yellow) [to force restore](gray)";

    @YamlKey("admin.echest.backup.no-backups")
    @Getter
    private String teamEchestNoBackups = "[No backups available for chest #%CHEST%](red)";

    @YamlKey("admin.echest.rollback.success")
    @Getter
    private String teamEchestRollbackSuccess = "[✓ Successfully rolled back Ender Chest #%CHEST%!](green)";

    @YamlKey("admin.echest.rollback.success-admin")
    @Getter
    private String teamEchestRollbackSuccessAdmin = "[✓ Successfully rolled back Ender Chest #%CHEST% for team %TEAM%!](green)";

    @YamlKey("admin.echest.rollback.restored-from")
    @Getter
    private String teamEchestRollbackRestoredFrom = "[Restored from:](gray) [%TIMESTAMP%](white) [(%TIMEAGO%)](gray)";

    @YamlKey("admin.echest.rollback.failed")
    @Getter
    private String teamEchestRollbackFailed = "[✗ Failed to rollback Ender Chest #%CHEST%!](red)";

    @YamlKey("admin.echest.rollback.invalid-backup")
    @Getter
    private String teamEchestRollbackInvalidBackup = "[Backup #%NUMBER% does not exist! Only %TOTAL% backup(s) available.](red)";

    @YamlKey("admin.echest.rollback.broadcast")
    @Getter
    private String teamEchestRollbackBroadcast = "[[!]](yellow bold) [Team Ender Chest #%CHEST% has been rolled back by %PLAYER%](yellow)";

    @YamlKey("admin.echest.rollback.admin-broadcast")
    @Getter
    private String teamEchestRollbackAdminBroadcast = "[[!]](red bold) [ADMIN ALERT:](red) [Team Ender Chest #%CHEST% has been rolled back by an administrator](yellow)";

    @YamlKey("admin.echest.rollback.force-warning")
    @Getter
    private String teamEchestRollbackForceWarning = "[⚠ FORCE ROLLBACK - Team was not notified](red bold)";

    @YamlKey("admin.echest.all-backup.success")
    @Getter
    private String teamEchestAllBackupSuccess = "[✓ Successfully backed up all %COUNT% ender chest(s) for team %TEAM%!](green)";

    @YamlKey("admin.echest.all-backup.no-chests")
    @Getter
    private String teamEchestAllBackupNoChests = "[Team %TEAM% has no ender chests to backup.](yellow)";

    @YamlKey("admin.echest.remove-row.success")
    @Getter
    private String teamEchestRemoveRowSuccess = "[✓ Removed %ROWS% row(s) from chest #%CHEST% for team %TEAM%](green)";

    @YamlKey("admin.echest.remove-row.new-size")
    @Getter
    private String teamEchestRemoveRowNewSize = "[New size: %NEW_ROWS% rows (%SLOTS% slots)](gray)";

    @YamlKey("admin.echest.remove-row.warning")
    @Getter
    private String teamEchestRemoveRowWarning = "[⚠ Warning: Items in removed slots will be lost!](red)";

    @YamlKey("admin.echest.remove-row.invalid")
    @Getter
    private String teamEchestRemoveRowInvalid = "[Cannot remove %ROWS% rows! Chest #%CHEST% only has %CURRENT_ROWS% rows.](red)";

    @YamlKey("admin.echest.remove-row.minimum")
    @Getter
    private String teamEchestRemoveRowMinimum = "[Minimum is 1 row. Use](yellow) [/ta removechest %TEAM% %CHEST%](gold) [to delete the entire chest.](yellow)";

    @YamlComment("/team Command Responses")
    @YamlKey("commands.team-help")
    @Getter
    private List<String> teamCommandIncorrectUsage = List.of(
            "[UltimateTeams usage:](gold)",
            "[/team create <name>](dark_aqua)",
            "[/team disband](dark_aqua)",
            "[/team invite accept/deny/send <player>](dark_aqua)",
            "[/team leave](dark_aqua)",
            "[/team kick <player>](dark_aqua)",
            "[/team info](dark_aqua)",
            "[/team list](dark_aqua)",
            "[/team prefix <prefix>](dark_aqua)",
            "[/team transfer <player-name>](dark_aqua)",
            "[/team ally [add|remove] <team-owner>](dark_aqua)",
            "[/team enemy [add|remove] <team-owner>](dark_aqua)",
            "[/team pvp](dark_aqua)",
            "[/team [sethome|delhome|home]](dark_aqua)",
            "[/team promote <player>](dark_aqua)",
            "[/team demote <player>](dark_aqua)",
            "[/team permissions add <permission>](dark_aqua)",
            "[/team permissions remove <permission>](dark_aqua)",
            "[/team echest [number]](dark_aqua)",
            ""
    );

    @YamlComment("/teamadmin Command Responses")
    @YamlKey("commands.admin-help")
    @Getter
    private List<String> teamAdminCommandIncorrectUsage = List.of(
            "[UltimateTeams Admin usage:](gold)",
            "[/teamadmin save](dark_aqua)",
            "[/teamadmin reload](dark_aqua)",
            "[/teamadmin disband <team-owner>](dark_aqua)",
            "[/teamadmin about](dark_aqua)",
            "[/teamadmin see <team-name> <chest-number>](dark_aqua)",
            "[/teamadmin addechest <team-name> <rows|chest|doublechest>](dark_aqua)",
            "[/teamadmin removeechest <team-name> <chest-number>](dark_aqua)",
            "[/teamadmin listechests <team-name>](dark_aqua)",
            "[/teamadmin echest backups <team-name> <chest-number>](dark_aqua)",
            "[/teamadmin echest rollback <team-name> <chest-number> <backup#>](dark_aqua)",
            "[/teamadmin echest forcerollback <team-name> <chest-number> <backup#>](dark_aqua)",
            "[/teamadmin echest allbackup <team-name>](dark_aqua)",
            "[/teamadmin removerow <team-name> <chest-number> <rows>](dark_aqua)",
            "[/teamadmin removechest <team-name> <chest-number>](dark_aqua)"
    );


    @SuppressWarnings("unused")
    private Messages() {
    }
}