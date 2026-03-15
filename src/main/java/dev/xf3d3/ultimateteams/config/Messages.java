package dev.xf3d3.ultimateteams.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
@Getter
@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Messages {

    public static final String MESSAGES_HEADER = """
            #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            #                                   ----[UltimateTeams]----                                         #
            #                                     ----[By xF3d3]----                                            #
            #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            #                                  ----[Plugin Messages File]----                                   #
            #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            #~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
            
            You can use MineDown: https://github.com/Phoenix616/MineDown?tab=readme-ov-file#syntax (if a specific message isn't already in MineDown then you need to use the default color codes / hex)
            
            You can also use Hex colors anywhere in the messages (#ffffffthis message is white)
            
            You can use \\n anywhere to go to a new line.
            """;

    private TeamSettings team = new TeamSettings();
    private InvitesSettings invites = new InvitesSettings();
    private EconomySettings economy = new EconomySettings();
    private GeneralSettings general = new GeneralSettings();
    private AdminSettings admin = new AdminSettings();
    private CommandsSettings commands = new CommandsSettings();

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TeamSettings {

        private CreateSettings create = new CreateSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class CreateSettings {
            @Comment("Team Creation Messages")
            private String incorrectUsage = "[use /team create <name>.](dark_aqua)";
            private String nameTooShort = "[Team name too short - minimum length is](dark_aqua) [%CHARMIN%](gold) [characters.](dark_aqua)";
            private String nameTooLong = "[Team name too long - maximum length is](dark_aqua) [%CHARMAX%](gold) [characters.](dark_aqua)";
            private String successful = "[Team](dark_aqua) [%TEAM%](gold) [was Created!](dark_aqua)";
            private String nameTaken = "[Sorry but](dark_aqua) [%TEAM%](gold) [is already taken.](dark_aqua)\n[Please choose another!](dark_aqua)";
            private String nameBanned = "[Sorry but](dark_aqua) [%TEAM%](gold) [is a](dark_aqua) [BANNED](red) [name!](dark_aqua)\n[Please choose another!](dark_aqua)";
            private String nameContainsSpace = "[Sorry but, since it contains a space,](dark_aqua) [%TEAM%](gold) [is a](dark_aqua) [BANNED](red) [name!](dark_aqua)\n[Please choose another!](dark_aqua)";
            private String nameCannotContainColours = "[Sorry, the team name cannot contain '&' or '#' characters.](dark_aqua)";
            private String failed = "[Team](dark_aqua) [%TEAM%](gold) [was NOT created, please make sure you're not already in a team!](dark_aqua)";
            private String broadcastChat = "[%TEAMOWNER%](gold) [Created a new team!](dark_aqua)\n[The new team is called](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";
            private String broadcastTitle1 = "[%TEAMOWNER%](gold) [Created a new team!](dark_aqua)";
            private String broadcastTitle2 = "[The new team is called](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";
        }

        @Comment("Team Rename")
        private RenameSettings rename = new RenameSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class RenameSettings {
            private String successful = "[Successfully changed team name to](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";
        }

        @Comment("Team Disbanded Messages")
        private DisbandSettings disband = new DisbandSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class DisbandSettings {
            private String warning = "[If you're sure you want to delete your team type:](dark_aqua) [/team disband confirm](gold)[.](dark_aqua) [This action cannot be undone!](red)";
            private String successful = "[Team was disbanded!](dark_aqua)";
            private String failure = "[Failed to disband team - Please make sure you're the owner!](dark_aqua)";
        }

        @Comment("Team Transfer Ownership Messages")
        private TransferSettings transfer = new TransferSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class TransferSettings {
            private String successful = "[You successfully transferred your team to](dark_aqua) [%PLAYER%](gold)[.](dark_aqua)";
            private String newOwnerMessage = "[You are now the owner of](dark_aqua) [%TEAM%](gold)[.](dark_aqua)";
            private String failureNotSameTeam = "[Failed to transfer team ownership as the player is not in your team.](dark_aqua)";
            private String failureOwnerOffline = "[Failed to transfer team ownership to](dark_aqua) [%PLAYER%](gold)[!](dark_aqua)\n[They may be offline.](dark_aqua)";
            private String failedCannotTransferToSelf = "[Failed to transfer team ownership to](dark_aqua) [%PLAYER%](gold)[!](dark_aqua)\n[The specified player cannot be yourself!](dark_aqua)";
            private String failedTargetInTeam = "[Failed to transfer team as the target is already in/owns a team!](dark_aqua)";
            private String incorrectUsage = "[Unrecognised argument please use](dark_aqua) [/team transfer <player-name>](gold)[.](dark_aqua)";
        }

        @Comment("Team Invite Messages")
        private InviteSettings invite = new InviteSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class InviteSettings {
            private String noValidPlayer = "[Please specify a player to invite!](dark_aqua)";
            private String notTeamOwner = "[You must be a team owner to invite people!](dark_aqua)";
            private String selfError = "[You can't invite yourself!](dark_aqua)";
            private String feeWillExceedLimit = "[You can't invite more players as the join fee will exceed the team bank limit (](dark_aqua)[%CURRENCY%%LIMIT%](gold)[)!](dark_aqua)";
            private String cantJoinFeeWillExceedLimit = "[You can't accept the invite as the join fee will exceed the team bank limit (](dark_aqua)[%CURRENCY%%LIMIT%](gold)[)!](dark_aqua)";
            private String inviteeNotFound = "[Player](dark_aqua) [%INVITED%](gold) [was not found, make sure they are online!](dark_aqua)";
            private String invitedAlreadyInTeam = "[Player](dark_aqua) [%INVITED%](gold) [is already in a team!](dark_aqua)";
            private String maxSizeReached = "[You have reached the team members size limit](dark_aqua) [%LIMIT%](green)[!](dark_aqua)";
            private String successful = "[You have invited](dark_aqua) [%INVITED%](gold) [to your team!](dark_aqua)";
            private String failed = "[Failed to send invite to](dark_aqua) [%INVITED%](gold)[, this player might already have an invitation!](dark_aqua)";
            private String invitePending = "[You have been invited to a team by](dark_aqua) [%TEAMOWNER%](gold) [- use /team invite accept](dark_aqua)";
            private String request = "[%PLAYER%](gold) [would like you to invite them to your team.](dark_aqua)\n[Use](dark_aqua) [/team invite %PLAYER%](gold) [to send the invite.](dark_aqua)";
            private String requestFailed = "[Failed to send request, check that the team owner is online!](dark_aqua)";
            private String requestSentSuccessfully = "[You have successfully sent a request to %TEAMOWNER%](dark_aqua)";
            private String requestFailedOwnTeam = "[Failed to send request, this is YOUR team!](dark_aqua)";
            private String requestFailedAlreadyInTeam = "[Failed to send request, you are already in a team!](dark_aqua)";
            private String denyFailedNoInvite = "[Failed to deny invite - you don't have an active invite.](dark_aqua)";
            private String denied = "[Invite successfully denied.](dark_aqua)";
            private String deniedInviter = "[%PLAYER% denied your invite.](dark_aqua)";
            private String denyFail = "[Failed to deny the invite.](dark_aqua)";

            @Comment("You can remove the join fee line if you have it disabled")
            private List<String> receivedMessage = List.of(
                    "&7&m                                                    &r",
                    "[Team Invitation](gold bold)",
                    "[You've been invited to join](yellow) [%TEAM%](gold) [by](yellow) [%INVITER%](gold)",
                    "[Join fee:](yellow) [%FEE%](gold)[%CURRENCY_NAME%](green)",
                    "",
                    "[[✔ ACCEPT]](green bold run_command=/team invite accept hover=[Click to accept the invitation](green)) [[✔ DECLINE]](red bold run_command=/team invite deny hover=[Click to deny the invitation](red))",
                    "&7&m                                                    &r"
            );
            private String errorInvitesDisabled = "[%NAME%](gold) [has invites disabled, you can't invite him!](dark_aqua)";
        }

        @Comment("Team Join Messages")
        private JoinSettings join = new JoinSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class JoinSettings {
            private String successful = "[Successfully joined](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";
            private String failed = "[Failed to join](dark_aqua) [%TEAM%](gold)";
            private String failedNoValidTeam = "[Failed to join a team - no team was found!](dark_aqua)";
            private String failedNoInvite = "[Failed to join a team - no invite was found!](dark_aqua)";
            private String broadcastChat = "[The player](dark_aqua) [%PLAYER%](gold) [has joined your team!](dark_aqua)";
        }

        @Comment("Team Leave Messages")
        private LeaveSettings leave = new LeaveSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class LeaveSettings {
            private String failedOwner = "[You are the owner of a team, use](dark_aqua) [/team disband](gold)[.](dark_aqua)";
            private String successful = "[You have left](dark_aqua) [%TEAM%](gold)[.](dark_aqua)";
            private String failed = "[Failed to leave team, please try again later.](dark_aqua)";
            private String broadcastChat = "[The player](dark_aqua) [%PLAYER%](gold) [has left your team!](dark_aqua)";
        }

        @Comment("Team Warp Messages")
        private WarpSettings warp = new WarpSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class WarpSettings {
            private String successful = "[Successfully added team warp](dark_aqua) [%WARP_NAME%](gold)[!](dark_aqua)";
            private String nameUsed = "[A warp with that name already exists!](dark_aqua)";
            private String limitReached = "[You have reached the team warps limit!](dark_aqua)";
            private String deletedSuccessful = "[Successfully deleted team warp](dark_aqua) [%WARP_NAME%](gold)[!](dark_aqua)";
            private String notFound = "[There is no warp with that name!](dark_aqua)";
            private String cooldownStart = "[You will be teleported in %SECONDS%s!](dark_aqua)";
            private String teleportedSuccessful = "[Successfully teleported to the team warp!](dark_aqua)";
            private String noWarps = "[Your team has no warps!](red)";
            private MenuSettings menu = new MenuSettings();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class MenuSettings {
                private List<String> header = List.of(
                        "[List of](dark_aqua) [%TEAM%](gold) [warps.](dark_aqua)",
                        "[You can Hover each warp name to get details](dark_aqua)",
                        ""
                );
                private String warp = "[[%WARP_NAME%]](#1055C9 hover=%WARP_INFO% run_command=/team warp %WARP_NAME%)";
                private List<String> warpInfo = List.of(
                        "[Left click to TP to](gray) [%WARP_NAME%](gold) [position](gray)",
                        "",
                        "[Coordinates:](gray) [X:](gray)[%X%](dark_aqua) [Y:](gray)[%Y%](dark_aqua) [Z:](gray)[%Z%](dark_aqua)",
                        "[World:](gray) [%WORLD_NAME%](dark_aqua) [Server:](gray) [%SERVER_NAME%](dark_aqua)"
                );
            }
        }

        @Comment("Team Kick Messages")
        private KickSettings kick = new KickSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class KickSettings {
            private String successful = "[Player](dark_aqua) [%KICKEDPLAYER%](gold) [was kicked from your team.](dark_aqua)";
            private String kickedPlayerMessage = "[You were kicked from](dark_aqua) [%TEAM%](gold)[!](dark_aqua)";
            private String targetNotInTeam = "[Player](dark_aqua) [%KICKEDPLAYER%](gold) [is not in your team.](dark_aqua)";
            private String playerNotFound = "[Could not find player](dark_aqua) [%KICKEDPLAYER%](gold)[. They may be have not joined before.](dark_aqua)";
            private String mustBeOwner = "[You are not an owner of a team!](dark_aqua)";
            private String incorrectUsage = "[Unrecognised argument please use](dark_aqua) [/team kick <member>](gold)[.](dark_aqua)";
            private String failedCannotKickYourself = "[You cannot kick yourself, use](dark_aqua) [/team leave](gold)[.](dark_aqua)";
        }

        @Comment("Team Prefix Messages")
        private PrefixSettings prefix = new PrefixSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PrefixSettings {
            private String changeSuccessful = "[Successfully changed team prefix to ](dark_aqua)%TEAMPREFIX%[!](dark_aqua)";
            private String tooLong = "[Team prefix too long - maximum length is](dark_aqua) [%CHARMAX%](gold) [characters.](dark_aqua)";
            private String tooShort = "[Team prefix too short - minimum length is](dark_aqua) [%CHARMIN%](gold) [characters.](dark_aqua)";
            private String invalidPrefix = "[Please provide a new prefix. Use](dark_aqua) [/team prefix <prefix>](aqua)[!](dark_aqua)";
            private String colorHint = "[Tip: Use](gray) [&&](yellow) [for color codes (e.g. &c&a for green) or](gray) [#](yellow) [for hex colors (e.g. &#FF5555)](gray)";
            private String alreadyTaken = "[Sorry but](dark_aqua) [%TEAMPREFIX%](gold) [is already taken.](dark_aqua)\n[Please choose another!](dark_aqua)";
            private String isBanned = "[Sorry but](dark_aqua) [%TEAMPREFIX%](gold) [is a](dark_aqua) [BANNED](red) [name!](dark_aqua)\n[Please choose another!](dark_aqua)";
            private String mustBeOwner = "[You are not an owner of a team!](dark_aqua)";
            private String cannotContainColours = "[Sorry, the team prefix cannot contain '&' or '#' characters.](dark_aqua)";
        }

        @Comment("Team MOTD Messages")
        private MotdSettings motd = new MotdSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class MotdSettings {
            private String cannotContainColours = "[Sorry, the team MOTD cannot contain '&' or '#' characters.](dark_aqua)";
            private String changeSuccessful = "[Successfully changed team MOTD to:](dark_aqua) %MOTD%[!](dark_aqua)";
            private String notValid = "[The team MOTD contains invalid characters!](dark_aqua)";
            private String disabled = "[Successfully disabled the team MOTD.](dark_aqua)";
            private String notSet = "&3Team MOTD not set.";
            private String tooLong = "[Team MOTD too long - maximum length is](dark_aqua) [%CHARMAX%](gold) [characters.](dark_aqua)";
            private String tooShort = "[Team MOTD too short - minimum length is](dark_aqua) [%CHARMIN%](gold) [characters.](dark_aqua)";
        }

        @Comment("Team List Messages")
        private ListSettings list = new ListSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ListSettings {
            private String noTeams = "[No teams found!](dark_aqua)\n[Create one using](dark_aqua) [/team create <name>](aqua)[!](dark_aqua)";
            private String header = "[----- ](gray)[TeamsList](gold)[ -----](gray)\n[Current teams:](dark_aqua bold)\n";
            private String footer = "[-----](gray)";
        }

        @Comment("Team Info Messages")
        private InfoSettings info = new InfoSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class InfoSettings {
            private String header = "&7----- &6TeamInfo&7 -----\n&3Name: %TEAM%\n&3Prefix: &7(&r%TEAMPREFIX%&7)";
            private String ownerOnline = "&3Owner: &a%OWNER%";
            private String ownerOffline = "&3Owner: &c%OWNER% &7&o(offline)";
            private String membersHeader = "&3Members: &3&o(%NUMBER%)";
            private String managersHeader = "&3Managers: &3&o(%NUMBER%)";
            private String membersOnline = "&a%MEMBER%";
            private String membersOffline = "&c%MEMBER% &7&o(offline)";
            private String alliesHeader = "&3Allied Teams:";
            private String allyListEntry = "&a%ALLYTEAM%";
            private String allyListNotFound = "&aAlly not found";
            private String enemiesHeader = "&3Enemy Teams:";
            private String enemyListEntry = "&c%ENEMYTEAM%";
            private String enemyListNotFound = "&aEnemy not found";
            private String joinFee = "&3Join Fee: &a%AMOUNT%";
            private String motd = "&3MOTD: &a%MOTD%";
            private String pvpEnabled = "&3Friendly Fire: &a&oENABLED";
            private String pvpDisabled = "&3Friendly Fire: &c&oDISABLED";
            private String homeSet = "&3Home Set: &a&oTRUE";
            private String homeNotSet = "&3Home Set: &c&oFALSE";
            private String bankAmount = "&3Bank Amount: &6%AMOUNT%";
            private String footer = "&7-----";
            private String notInTeam = "&3You are not in a team!";
        }

        @Comment("Team Ally Messages")
        private AllySettings ally = new AllySettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class AllySettings {
            private String addedSuccessful = "[You successfully added](dark_aqua) [%ALLYTEAM%](gold) [to your allies!](dark_aqua)";
            private String addedNotification = "[%TEAMOWNER%](gold) [has added your team to their allies!](dark_aqua)";
            private String failedToAdd = "[Unable to add](dark_aqua) [%ALLYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";
            private String failedAlreadyAlly = "[This team is already your ally!](dark_aqua)";
            private String failedPlayerNotOwner = "[%ALLYOWNER%](gold) [is not a team owner!](dark_aqua)";
            private String removedSuccessful = "[You Successfully removed](dark_aqua) [%ALLYTEAM%](gold) [from your allies!](dark_aqua)";
            private String removedOwnerNotification = "[Your team owner has removed](dark_aqua) [%TEAM%](gold) [from your allies!](dark_aqua)";
            private String removedOtherNotification = "[The team](dark_aqua) [%TEAM%](gold) [has removed your team from their allies!](dark_aqua)";
            private String failedToRemove = "[Unable to remove](dark_aqua) [%ALLYOWNER%](gold)[! Make sure they're your ally](dark_aqua)";
            private String addOwnerOffline = "[Unable to add](dark_aqua) [%ALLYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";
            private String removeOwnerOffline = "[Unable to remove](dark_aqua) [%ALLYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";
            private String incorrectUsage = "[Unrecognised argument please use](dark_aqua) [/team ally [add|remove] <team-owner>](gold)[.](dark_aqua)";
            private String failedCannotAllySelf = "[You cannot be an ally with your](dark_aqua) [OWN](dark_aqua bold) [team!](dark_aqua)";
            private String maxAmountReached = "[You have reached the team allies amount limit](dark_aqua) [%LIMIT%](green)[!](dark_aqua)";
            private String failedCannotAllyEnemy = "[You cannot be an ally with an](dark_aqua) [ENEMY](red bold) [team!](dark_aqua)";
        }

        @Comment("Team Enemy Messages")
        private EnemySettings enemy = new EnemySettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class EnemySettings {
            private String addedSuccessful = "[You successfully added](dark_aqua) [%ENEMYTEAM%](gold) [to your enemies!](dark_aqua)";
            private String addedNotification = "[%TEAMOWNER%](gold) [has added your team to their enemies!](dark_aqua)";
            private String failedToAdd = "[Unable to add](dark_aqua) [%ENEMYOWNER%](gold)[! Make sure the owner is online!](dark_aqua)";
            private String failedAlreadyEnemy = "[This team is already your enemy!](dark_aqua)";
            private String failedPlayerNotOwner = "[%ENEMYOWNER%](gold) [is not a team owner!](dark_aqua)";
            private String removedSuccessful = "[You Successfully removed](dark_aqua) [%ENEMYTEAM%](gold) [from your enemies!](dark_aqua)";
            private String removedNotification = "[The team](dark_aqua) [%TEAMOWNER%](gold) [has removed your team from their enemies!](dark_aqua)";
            private String failedToRemove = "[Unable to remove](dark_aqua) [%ENEMYTEAM%](gold)[! Make sure they're your enemy!](dark_aqua)";
            private String addOwnerOffline = "[Unable to add](dark_aqua) [%ENEMYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";
            private String removeOwnerOffline = "[Unable to remove](dark_aqua) [%ENEMYTEAM%](gold)[! Make sure the owner is online!](dark_aqua)";
            private String incorrectUsage = "[Unrecognised argument please use](dark_aqua) [/team enemy [add|remove] <team-owner>](gold)[.](dark_aqua)";
            private String failedCannotEnemySelf = "[You cannot be an enemy with your](dark_aqua) [OWN](dark_aqua bold) [team!](dark_aqua)";
            private String maxAmountReached = "[You have reached the team enemies amount limit](dark_aqua) [%LIMIT%](green)[!](dark_aqua)";
            private String failedCannotEnemyAlly = "[You cannot be an enemy with an](dark_aqua) [ALLIED](green bold) [team!](dark_aqua)";
            private String warDeclaredTitle1 = "[YOUR TEAM IS NOW AT WAR WITH:](red bold)";
            private String warDeclaredTitle2 = "[%TEAMOWNER%'s](gold) [Team!](red)";
            private String peaceDeclaredTitle1 = "[YOUR TEAM IS NO LONGER AT WAR WITH:](green bold)";
            private String peaceDeclaredTitle2 = "[%TEAMOWNER%'s](gold) [Team!](green)";
            private String warReceivedTitle1 = "[YOUR TEAM IS NOW AT WAR WITH:](red bold)";
            private String warReceivedTitle2 = "[%TEAMOWNER%'s](gold) [Team!](red)";
            private String peaceReceivedTitle1 = "[YOUR TEAM IS NO LONGER AT WAR WITH:](green bold)";
            private String peaceReceivedTitle2 = "[%TEAMOWNER%'s](gold) [Team!](green)";
        }

        @Comment("Team Friendly Fire")
        private PvpSettings pvp = new PvpSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PvpSettings {
            private String enabled = "[You successfully](dark_aqua) [enabled](green) [friendly fire.](dark_aqua)\n[Your team members can now pvp each other!](dark_aqua)";
            private String disabled = "[You successfully](dark_aqua) [disabled](red) [friendly fire.](dark_aqua)\n[Your team members can no longer pvp each other!](dark_aqua)";
            private String failedNotInTeam = "[You need to be in a team first! Use](dark_aqua) [/team](gold) [for details how.](dark_aqua)";
            private String isDisabled = "[Your team has friendly fire disabled.](dark_aqua)";
            private String isDisabledForAllies = "[You can't hurt a member of an allied team!](dark_aqua)";
        }

        @Comment("Team Homes")
        private HomeSettings home = new HomeSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class HomeSettings {
            private String setSuccessful = "[You](dark_aqua) [Successfully](green) [set the team home to your current location!](dark_aqua)";
            private String deleteSuccessful = "[You](dark_aqua) [Successfully](green) [deleted the team home!](dark_aqua)";
            private String cooldownWait = "[Sorry, you can't use that again for another](dark_aqua) [%TIMELEFT%](gold) [seconds.](dark_aqua)";
            private String cooldownStart = "[You will be teleported in %SECONDS%s!](dark_aqua)";
            private String teleportSuccessful = "[You teleported to your team's home.](dark_aqua)";
            private String teleportMoved = "[The teleport has been cancelled because you moved!](dark_aqua)";
            private String teleportDamage = "[The teleport has been cancelled because you took damage!](dark_aqua)";
            private String noHomeSet = "[Your team does not have a home set!](dark_aqua)";
            private String failedNotInTeam = "[You need to be in a team first! Use](dark_aqua) [/team](gold) [for details how.](dark_aqua)";
        }

        @Comment("Team Chat")
        private ChatSettings chat = new ChatSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ChatSettings {
            private String failedNotInTeam = "[You need to be in a team first! Use](dark_aqua) [/team](gold) [for details how.](dark_aqua)";
            private String cooldownWait = "[Sorry, you can't use that again for another](dark_aqua) [%TIMELEFT%](gold) [seconds.](dark_aqua)";
            private String toggleOn = "[Toggled Team Chat](dark_aqua) [ON](green bold)[.](dark_aqua)";
            private String toggleOff = "[Toggled Team Chat](dark_aqua) [OFF](red bold)[.](dark_aqua)";
            private String spyToggleOn = "[Toggled Team Chat spy](dark_aqua) [ON](green bold)[.](dark_aqua)";
            private String spyToggleOff = "[Toggled Team Chat spy](dark_aqua) [OFF](red bold)[.](dark_aqua)";
        }

        private AllychatSettings allychat = new AllychatSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class AllychatSettings {
            private String incorrectUsage = "[UltimateTeams team chat usage:](gold)\n[/allychat <message>](dark_aqua)";
            private String toggleOn = "[Toggled Ally Chat](dark_aqua) [ON](green bold)[.](dark_aqua)";
            private String toggleOff = "[Toggled Ally Chat](dark_aqua) [OFF](red bold)[.](dark_aqua)";
        }

        @Comment("Team Managers")
        private ManagerSettings manager = new ManagerSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ManagerSettings {
            private String promoteSelfError = "[You can't promote yourself!](dark_aqua)";
            private String promoteSuccessful = "[Player](dark_aqua) [%PLAYER%](gold) [has been successfully promoted to team manager.](dark_aqua)";
            private String demoteSelfError = "[You can't demote yourself!](dark_aqua)";
            private String demoteSuccessful = "[Player](dark_aqua) [%PLAYER%](gold) [has been successfully demoted.](dark_aqua)";
        }

        @Comment("Team permissions")
        private PermissionSettings permission = new PermissionSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PermissionSettings {
            private String notFound = "[The permission %PERM% doesn't exist.](dark_aqua)";
            private String addedSuccessful = "[The permission](dark_aqua) [%PERM%](gold bold) [has been successfully added.](dark_aqua)";
            private String removedSuccessful = "[The permission](dark_aqua) [%PERM%](gold bold) [has been successfully removed.](dark_aqua)";
        }

        @Comment("Team Ender Chest Messages")
        private EchestSettings echest = new EchestSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class EchestSettings {
            private String opened = "[Opened team ender chest #%NUMBER%](dark_aqua)";
            private String notExist = "[Team ender chest #%NUMBER% does not exist!](red)";
        }

        @Comment("Fee")
        private FeeSettings fee = new FeeSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class FeeSettings {
            private String current = "[The fee to join your team is set to:](dark_aqua) [%AMOUNT%](gold)";
            private String disable = "[You have disabled the team fee!](dark_aqua)\n[new players will now join the team for free!](dark_aqua)";
            private String success = "[You have successfully set the team fee to:](dark_aqua) [%AMOUNT%](gold)";
            private String amountTooBig = "[The fee value exceeds the maximum limit, which is:](dark_aqua) [%AMOUNT%](gold)";
            private String cantJoinNotEnoughMoney = "[You can't join](dark_aqua) [%TEAM%](gold) [Because you don't have enough money to pay the join fee. The join fee is:](dark_aqua) [%AMOUNT%](gold)";
            private String feeDeposited = "[%PLAYER%](gold) [has deposited his join fee](dark_aqua) [(](dark_aqua)[%AMOUNT%](gold)[)](dark_aqua) [in the team bank](dark_aqua)";
        }
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class InvitesSettings {
        @Comment("Invites")
        private String invitesDisabled = "[Invites successfully disabled!](green)\n [Players can't invite you in their teams anymore.](dark_aqua)";
        private String invitesEnabled = "[Invites successfully enabled!](green)\n [Players can now invite you in their teams.](dark_aqua)";
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EconomySettings {
        @Comment("Economy")
        private String notEnoughMoney = "[You don't have enough money for this action. Required balance is:](dark_aqua) [%MONEY%](gold)";
        private String invalidAmount = "[%MONEY%](gold) [is not a valid amount](dark_aqua)";
        private String limitReached = "[You reached the bank limit](dark_aqua) [%CURRENCY%%LIMIT%](gold)[!](dark_aqua)";
        private String deposited = "[You have successfully deposited](dark_aqua) [%CURRENCY%%MONEY%](gold) [in the team bank](dark_aqua)";

        private WithdrawSettings withdraw = new WithdrawSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class WithdrawSettings {
            private String success = "[You have successfully withdrawn](dark_aqua) [%CURRENCY%%MONEY%](gold) [from the team bank](dark_aqua)";

            @Comment("You can use %MONEY% to display the amount in this message.")
            private String notEnoughFunds = "[The team bank does not have enough funds!](dark_aqua)";
        }
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GeneralSettings {
        @Comment("General Plugin Messages")
        private String noColourPermission = "[You don't have the required permission to use color codes](dark_aqua)";
        private String playerNotFound = "[The player doesn't exist.](dark_aqua)";
        private String mustBeOwner = "[You must be the team owner to do this.](dark_aqua)";
        private String teamNotFound = "[The team doesn't exist.](dark_aqua)";
        private String functionDisabled = "[Sorry, that has been disabled. :(](dark_aqua)";
        private String noPermission = "[You don't have permission to do that.](red)";
        private String pluginReloadBegin = "[UltimateTeams:](gold) [Beginning plugin reload...](green)";
        private String pluginReloadSuccessful = "[UltimateTeams:](gold) [The plugin has been successfully reloaded!](green)";
        private String incorrectCommandUsage = "[Unrecognised argument please use](dark_aqua) [/team](gold)[.](dark_aqua)";
        private String playerOnlyCommand = "[Sorry, that command can only be run by a player!](dark_red)";
        private String inviteWipeStarted = "[UltimateTeams:](gold) [Auto invite wipe task has started.](green)";
        private String inviteWipeComplete = "[UltimateTeams:](gold) [Cleared all outstanding team invites!](green)";
        private String inviteWipeFailed = "[UltimateTeams:](gold) [Failed to clear all outstanding team invites!](red)";
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AdminSettings {

        @Comment("Admin Disband Messages")
        private DisbandSettings disband = new DisbandSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class DisbandSettings {
            private String failure = "[Failed to disband team - make sure that the provided player is a team owner!](dark_aqua)";
            private String incorrectUsage = "[Unrecognised argument please use](dark_aqua) [/teamadmin disband <team-owner>](gold)[.](dark_aqua)";
        }

        @Comment("Admin Ender Chest Messages")
        private EchestSettings echest = new EchestSettings();

        @Getter
        @Configuration
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class EchestSettings {
            private String added = "[Added ender chest #%NUMBER% with %ROWS% rows to team %TEAM%](green)";
            private String removed = "[Removed ender chest #%NUMBER% from team %TEAM%](green)";
            private String rowsAdded = "[Added %ROWS% rows to chest #%NUMBER% (now %TOTAL% rows / %SLOTS% slots) for team %TEAM%](green)";
            private String pageAdded = "[Added %TYPE% page #%NUMBER% (%ROWS% rows / %SLOTS% slots) to team %TEAM%](green)";

            @Comment("Admin Ender Chest Rollback Messages")
            private BackupSettings backup = new BackupSettings();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class BackupSettings {
                private String listHeader = "[▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬](gold bold)";
                private String listTitle = "[Ender Chest #%CHEST% Backups](yellow bold)";
                private String listTeam = "[Team:](gray) [%TEAM%](white)";
                private String listEntry = "[#%NUMBER%](yellow) [-](gray) [%TIMESTAMP%](white) [(%TIMEAGO%)](gray)";
                private String listFooterAdmin = "[Use](gray) [/ta echest rollback <team> %CHEST% <backup#>](yellow) [to restore](gray)";
                private String listFooterForce = "[Use](gray) [/ta echest forcerollback <team> %CHEST% <backup#>](yellow) [to force restore](gray)";
                private String noBackups = "[No backups available for chest #%CHEST%](red)";
            }

            private RollbackSettings rollback = new RollbackSettings();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class RollbackSettings {
                private String success = "[✓ Successfully rolled back Ender Chest #%CHEST%!](green)";
                private String successAdmin = "[✓ Successfully rolled back Ender Chest #%CHEST% for team %TEAM%!](green)";
                private String restoredFrom = "[Restored from:](gray) [%TIMESTAMP%](white) [(%TIMEAGO%)](gray)";
                private String failed = "[✗ Failed to rollback Ender Chest #%CHEST%!](red)";
                private String invalidBackup = "[Backup #%NUMBER% does not exist! Only %TOTAL% backup(s) available.](red)";
                private String broadcast = "[[!]](yellow bold) [Team Ender Chest #%CHEST% has been rolled back by %PLAYER%](yellow)";
                private String adminBroadcast = "[[!]](red bold) [ADMIN ALERT:](red) [Team Ender Chest #%CHEST% has been rolled back by an administrator](yellow)";
                private String forceWarning = "[⚠ FORCE ROLLBACK - Team was not notified](red bold)";
            }

            private AllBackupSettings allBackup = new AllBackupSettings();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class AllBackupSettings {
                private String success = "[✓ Successfully backed up all %COUNT% ender chest(s) for team %TEAM%!](green)";
                private String noChests = "[Team %TEAM% has no ender chests to backup.](yellow)";
            }

            private RemoveRowSettings removeRow = new RemoveRowSettings();

            @Getter
            @Configuration
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class RemoveRowSettings {
                private String success = "[✓ Removed %ROWS% row(s) from chest #%CHEST% for team %TEAM%](green)";
                private String newSize = "[New size: %NEW_ROWS% rows (%SLOTS% slots)](gray)";
                private String warning = "[⚠ Warning: Items in removed slots will be lost!](red)";
                private String invalid = "[Cannot remove %ROWS% rows! Chest #%CHEST% only has %CURRENT_ROWS% rows.](red)";
                private String minimum = "[Minimum is 1 row. Use](yellow) [/ta removechest %TEAM% %CHEST%](gold) [to delete the entire chest.](yellow)";
            }
        }
    }

    @Getter
    @Configuration
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CommandsSettings {

        @Comment("/team Command Responses")
        private List<String> teamHelp = List.of(
                "&6UltimateTeams usage:",
                "&3/team create <name>",
                "&3/team disband",
                "&3/team invite accept/deny/send <player>",
                "&3/team leave",
                "&3/team kick <player>",
                "&3/team info",
                "&3/team list",
                "&3/team prefix <prefix>",
                "&3/team transfer <player-name>",
                "&3/team ally [add|remove] <team-owner>",
                "&3/team enemy [add|remove] <team-owner>",
                "&3/team pvp",
                "&3/team [sethome|delhome|home]",
                "&3/team promote <player>",
                "&3/team demote <player>",
                "&3/team permissions add <permission>",
                "&3/team permissions remove <permission>",
                "&3/team echest [number]",
                ""
        );

        @Comment("/teamadmin Command Responses")
        private List<String> adminHelp = List.of(
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
    }
}