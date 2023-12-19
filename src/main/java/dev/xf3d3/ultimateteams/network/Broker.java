package dev.xf3d3.ultimateteams.network;

import dev.xf3d3.ultimateteams.UltimateTeams;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public abstract class Broker {

    protected final UltimateTeams plugin;

    /**
     * Create a new broker
     *
     * @param plugin the HuskTowns plugin instance
     */
    protected Broker(@NotNull UltimateTeams plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle an inbound {@link Message}
     *
     * @param receiver The user who received the message, if a receiver exists
     * @param message  The message
     */
    protected void handle(@Nullable Player receiver, @NotNull Message message) {
        if (message.getSourceServer().equals(getServer())) {
            return;
        }
        switch (message.getType()) {
            case TOWN_DELETE -> message.getPayload().getInteger()
                    .flatMap(townId -> plugin.getTowns().stream().filter(town -> town.getId() == townId).findFirst())
                    .ifPresent(town -> plugin.runAsync(() -> {
                        plugin.getManager().sendTownMessage(town, plugin.getLocales()
                                .getLocale("town_deleted_notification", town.getName())
                                .map(MineDown::toComponent).orElse(Component.empty()));
                        plugin.getMapHook().ifPresent(mapHook -> mapHook.removeClaimMarkers(town));
                        plugin.removeTown(town);
                        plugin.getClaimWorlds().values().forEach(world -> {
                            if (world.removeTownClaims(town.getId()) > 0) {
                                plugin.getDatabase().updateClaimWorld(world);
                            }
                        });
                    }));
            case TOWN_DELETE_ALL_CLAIMS -> message.getPayload().getInteger()
                    .flatMap(townId -> plugin.getTowns().stream().filter(town -> town.getId() == townId).findFirst())
                    .ifPresent(town -> plugin.runAsync(() -> {
                        plugin.getManager().sendTownMessage(town, plugin.getLocales()
                                .getLocale("deleted_all_claims_notification", town.getName())
                                .map(MineDown::toComponent).orElse(Component.empty()));
                        plugin.getMapHook().ifPresent(mapHook -> mapHook.removeClaimMarkers(town));
                        plugin.getClaimWorlds().values().forEach(world -> world.removeTownClaims(town.getId()));
                    }));
            case TEAM_UPDATE -> plugin.runAsync(() -> message.getPayload().getInteger()
                    .flatMap(id -> plugin.getDatabase().getTown(id))
                    .ifPresentOrElse(
                            plugin::updateTown,
                            () -> plugin.log(Level.WARNING, "Failed to update town: Town not found")
                    ));
            case TOWN_INVITE_REQUEST -> {
                if (receiver == null) {
                    return;
                }
                message.getPayload().getInvite().ifPresentOrElse(
                        invite -> plugin.getManager().towns().handleInboundInvite(receiver, invite),
                        () -> plugin.log(Level.WARNING, "Invalid town invite request payload!")
                );
            }
            case TOWN_INVITE_REPLY -> message.getPayload().getBool().ifPresent(accepted -> {
                if (receiver == null) {
                    return;
                }
                final Optional<Member> member = plugin.getUserTown(receiver);
                if (member.isEmpty()) {
                    return;
                }
                final Member townMember = member.get();
                if (!accepted) {
                    plugin.getLocales().getLocale("invite_declined_by", message.getSender())
                            .ifPresent(receiver::sendMessage);
                    return;
                }
                plugin.getLocales().getLocale("user_joined_town", message.getSender(),
                                townMember.town().getName()).map(MineDown::toComponent)
                        .ifPresent(locale -> plugin.getManager().sendTownMessage(townMember.town(), locale));
            });
            case TOWN_CHAT_MESSAGE -> message.getPayload().getString()
                    .ifPresent(text -> plugin.getDatabase().getUser(message.getSender())
                            .flatMap(sender -> plugin.getUserTown(sender.user()))
                            .ifPresent(member -> plugin.getManager().towns().sendLocalChatMessage(text, member, plugin)));
            case REQUEST_USER_LIST -> {
                if (receiver == null) {
                    return;
                }
                Message.builder()
                        .type(Message.Type.USER_LIST)
                        .target(message.getSourceServer(), Message.TargetType.SERVER)
                        .payload(Payload.userList(plugin.getOnlineUsers().stream().map(online -> (User) online).toList()))
                        .build().send(this, receiver);
            }
            case USER_LIST -> message.getPayload()
                    .getUserList()
                    .ifPresent(players -> plugin.setUserList(message.getSourceServer(), players));
            case TOWN_LEVEL_UP, TOWN_TRANSFERRED, TOWN_RENAMED ->
                    message.getPayload().getInteger().flatMap(id -> plugin.getTowns().stream()
                            .filter(town -> town.getId() == id).findFirst()).ifPresent(town -> {
                        final Component locale = switch (message.getType()) {
                            case TOWN_LEVEL_UP -> plugin.getLocales().getLocale("town_levelled_up",
                                    Integer.toString(town.getLevel())).map(MineDown::toComponent).orElse(Component.empty());
                            case TOWN_RENAMED -> plugin.getLocales().getLocale("town_renamed",
                                    town.getName()).map(MineDown::toComponent).orElse(Component.empty());
                            case TOWN_TRANSFERRED -> plugin.getLocales().getLocale("town_transferred",
                                            town.getName(), plugin.getDatabase().getUser(town.getMayor())
                                                    .map(SavedUser::user).map(User::getUsername).orElse("?"))
                                    .map(MineDown::toComponent).orElse(Component.empty());
                            default -> Component.empty();
                        };
                        plugin.getManager().sendTownMessage(town, locale);
                    });
            case TOWN_DEMOTED, TOWN_PROMOTED, TOWN_EVICTED -> {
                if (receiver == null) {
                    return;
                }
                switch (message.getType()) {
                    case TOWN_DEMOTED -> plugin.getLocales().getLocale("demoted_you",
                            plugin.getRoles().fromWeight(message.getPayload().getInteger().orElse(-1))
                                    .map(Role::getName).orElse("?"),
                            message.getSender()).ifPresent(receiver::sendMessage);
                    case TOWN_PROMOTED -> plugin.getLocales().getLocale("promoted_you",
                            plugin.getRoles().fromWeight(message.getPayload().getInteger().orElse(-1))
                                    .map(Role::getName).orElse("?"),
                            message.getSender()).ifPresent(receiver::sendMessage);
                    case TOWN_EVICTED -> {
                        plugin.getLocales().getLocale("evicted_you",
                                plugin.getUserTown(receiver).map(Member::town).map(Town::getName).orElse("?"),
                                message.getSender()).ifPresent(receiver::sendMessage);
                        plugin.editUserPreferences(receiver, preferences -> preferences.setTownChatTalking(false));
                    }
                }
            }
            case TOWN_WAR_DECLARATION_SENT -> plugin.getManager().wars().ifPresent(manager -> message.getPayload()
                    .getDeclaration().ifPresent(declaration -> {
                        final Optional<Town> town = declaration.getAttackingTown(plugin);
                        final Optional<Town> defending = declaration.getDefendingTown(plugin);
                        if (town.isEmpty() || defending.isEmpty()) {
                            return;
                        }

                        // Add pending declaration
                        manager.getPendingDeclarations().add(declaration);

                        // Send notification
                        plugin.getLocales().getLocale("war_declaration_notification",
                                        town.get().getName(), defending.get().getName(),
                                        plugin.formatMoney(declaration.wager()),
                                        Long.toString(plugin.getSettings().getWarDeclarationExpiry()))
                                .ifPresent(t -> {
                                    plugin.getManager().sendTownMessage(town.get(), t.toComponent());
                                    plugin.getManager().sendTownMessage(defending.get(), t.toComponent());
                                });

                        // Send options to the defending town.
                        plugin.getLocales().getLocale("war_declaration_options", town.get().getName())
                                .ifPresent(l -> plugin.getManager().sendTownMessage(defending.get(), l.toComponent()));
                    }));
            case TOWN_WAR_DECLARATION_ACCEPTED -> plugin.getManager().wars().ifPresent(manager -> message.getPayload()
                    .getDeclaration().ifPresent(declaration -> {
                        manager.getPendingDeclarations().remove(declaration);
                        if (receiver == null) {
                            return;
                        }

                        final Optional<String> optionalServer = declaration.getWarServerName(plugin);
                        final Optional<Town> attacking = declaration.getAttackingTown(plugin);
                        final Optional<Town> defending = declaration.getDefendingTown(plugin);
                        if (optionalServer.isEmpty() || attacking.isEmpty() || defending.isEmpty()) {
                            return;
                        }

                        final String server = optionalServer.get();
                        if (plugin.getServerName().equalsIgnoreCase(server)) {
                            manager.startWar(
                                    receiver, attacking.get(), defending.get(), declaration.wager(),
                                    (startedWar) -> startedWar.teleportUsers(plugin)
                            );
                        }

                        plugin.getLocales().getLocale("war_declaration_accepted",
                                        attacking.get().getName(), defending.get().getName(),
                                        Long.toString(plugin.getSettings().getWarZoneRadius()))
                                .ifPresent(l -> {
                                    plugin.getManager().sendTownMessage(attacking.get(), l.toComponent());
                                    plugin.getManager().sendTownMessage(defending.get(), l.toComponent());
                                });
                    }));
            default -> plugin.log(Level.SEVERE, "Received unknown message type: " + message.getType());
        }
    }

    /**
     * Initialize the message broker
     *
     * @throws RuntimeException if the broker fails to initialize
     */
    public abstract void initialize() throws RuntimeException;

    /**
     * Send a message to the broker
     *
     * @param message the message to send
     * @param sender  the sender of the message
     */
    protected abstract void send(@NotNull Message message, @NotNull Player sender);

    /**
     * Move an {@link Player} to a new server on the proxy network
     *
     * @param user   the user to move
     * @param server the server to move the user to
     */
    public abstract void changeServer(@NotNull Player user, @NotNull String server);

    /**
     * Terminate the broker
     */
    public abstract void close();

    @NotNull
    protected String getSubChannelId() {
        final String version = plugin.getVersion().getMajor() + "." + plugin.getVersion().getMinor();
        return plugin.getKey(plugin.getSettings().getClusterId(), version).asString();
    }

    @NotNull
    protected String getServer() {
        return plugin.getServerName();
    }

    /**
     * Identifies types of message brokers
     */
    public enum Type {
        PLUGIN_MESSAGE("Plugin Messages"),
        REDIS("Redis");
        @NotNull
        private final String displayName;

        Type(@NotNull String displayName) {
            this.displayName = displayName;
        }

        @NotNull
        public String getDisplayName() {
            return displayName;
        }
    }

}