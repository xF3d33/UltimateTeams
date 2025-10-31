package dev.xf3d3.ultimateteams.models;

import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import dev.xf3d3.ultimateteams.UltimateTeams;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Team {
    @Getter @Setter
    @Builder.Default
    public int id = 0;

    @Expose
    @Getter @Setter
    private String name;

    @Expose
    @Builder.Default
    @Nullable
    @Getter @Setter
    private String prefix = null;

    @Getter
    @Expose
    @Builder.Default
    private Map<String, TeamWarp> warps = Maps.newHashMap();

    @Expose
    @Getter
    @Builder.Default
    private Map<UUID, Integer> members = Maps.newHashMap();

    @Expose
    @Builder.Default
    private Map<Integer, Relation> relations = Maps.newHashMap();

    @Expose
    @Builder.Default
    @Getter @Setter
    private boolean friendlyFire = false;

    @Nullable
    @Expose
    @Builder.Default
    @Getter @Setter
    private TeamHome home = null;

    @Expose
    @Builder.Default
    @Setter
    private EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);

    @Expose
    @Getter
    @Builder.Default
    private Map<Integer, TeamEnderChest> enderChests = Maps.newHashMap();

    @NotNull
    @ApiStatus.Internal
    public static Team create(@NotNull String name, @NotNull Player owner, @NotNull Boolean friendlyFire) {
        // Create default ender chest with 3 rows (27 slots)
        TeamEnderChest defaultChest = TeamEnderChest.builder()
                .chestNumber(1)
                .rows(3)
                .serializedContents("")
                .build();
        
        Map<Integer, TeamEnderChest> defaultChests = Maps.newHashMap();
        defaultChests.put(1, defaultChest);
        
        return Team.builder()
                .name(name)
                .friendlyFire(friendlyFire)
                .members(Maps.newHashMap(Map.of(owner.getUniqueId(), 3)))
                .enderChests(defaultChests)
                .build();
    }

    public List<Player> getOnlineMembers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> getMembers().containsKey(player.getUniqueId())).collect(Collectors.toUnmodifiableList());
    }

    public void sendTeamMessage(@NotNull String message) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> getMembers().containsKey(player.getUniqueId()))
                .forEach(player -> player.sendMessage(message));
    }

    public Optional<TeamWarp> getTeamWarp(@NotNull String name){
        return Optional.ofNullable(warps.get(name));
    }

    public void addTeamWarp(@NotNull TeamWarp warp){
        warps.put(warp.getName(), warp);
    }

    public void removeTeamWarp(@NotNull String name) {
        warps.remove(name);
    }

    @NotNull
    public UUID getOwner() {
        return members.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("Team \"" + getName() + "\" has no owner"));
    }

    public void addMember(@NotNull UUID uuid, @Nullable Integer weight) {
        this.members.put(uuid, Objects.requireNonNullElse(weight, 1));
    }

    public void removeMember(@NotNull UUID uuid) throws IllegalArgumentException {
        if (getOwner().equals(uuid)) {
            throw new IllegalArgumentException("Cannot remove the mayor of the team \"" + getName() + "\"");
        }

        this.members.remove(uuid);
    }

    @NotNull
    public Map<Integer, Relation> getRelations() {
        return relations == null ? relations = new HashMap<>() : relations;
    }

    @NotNull
    public Map<Team, Relation> getRelations(@NotNull UltimateTeams plugin) {
        return getRelations().entrySet().stream()
                .filter(e -> plugin.getTeamStorageUtil().findTeam(e.getKey()).isPresent())
                .collect(Collectors.toMap(
                        e -> plugin.getTeamStorageUtil().findTeam(e.getKey()).orElse(null),
                        Map.Entry::getValue
                ));
    }

    @NotNull
    public Relation getRelationWith(@NotNull Team otherTeam) {
        return relations.getOrDefault(otherTeam.getId(), Relation.NEUTRAL);
    }

    public void setRelationWith(@NotNull Team otherTeam, @NotNull Relation relation) {
        relations.remove(otherTeam.getId());
        relations.put(otherTeam.getId(), relation);
    }

    public void removeRelationWith(@NotNull Team otherTeam) {
        relations.remove(otherTeam.getId());
    }

    @NotNull
    public Set<Permission> getPermissions() {
        return permissions == null ? permissions = EnumSet.noneOf(Permission.class) : EnumSet.copyOf(permissions);
    }

    public void addPermission(Permission perm) {
        permissions.add(perm);
    }

    public void removePermission(Permission perm) {
        permissions.remove(perm);
    }

    public boolean hasPermission(Permission perm) {
        return permissions.contains(perm);
    }

    public boolean isFriendlyFireAllowed(){
        return friendlyFire;
    }

    public boolean areRelationsBilateral(@NotNull Team otherTeam, @NotNull Relation relation) {
        if (otherTeam.equals(this)) {
            return true;
        }
        return getRelationWith(otherTeam) == relation && otherTeam.getRelationWith(this) == relation;
    }



    public enum Relation {
        ALLY,

        NEUTRAL,

        ENEMY;

        /**
         * Parse a {@link Relation} from a string name
         *
         * @param string the string to parse
         * @return the parsed {@link Relation} wrapped in an {@link Optional}, if any was found
         */
        public static Optional<Relation> parse(@NotNull String string) {
            return Arrays.stream(values())
                    .filter(operation -> operation.name().equalsIgnoreCase(string))
                    .findFirst();
        }
    }

    public enum Permission {
        INVITE,
        KICK,
        PVP,
        RELATIONS,
        WARPS,
        RENAME,
        PREFIX,
        HOME,
        PROMOTE;

        /**
         * Parse a {@link Permission} from a string name
         *
         * @param string the string to parse
         * @return the parsed {@link Permission} wrapped in an {@link Optional}, if any was found
         */
        public static Optional<Permission> parse(@NotNull String string) {
            return Arrays.stream(values())
                    .filter(operation -> operation.name().equalsIgnoreCase(string))
                    .findFirst();
        }
    }

    // ========== Team Ender Chest Methods ==========

    /**
     * Get a team ender chest by number
     * @param chestNumber The chest number (1, 2, 3, etc.)
     * @return Optional containing the TeamEnderChest if it exists
     */
    public Optional<TeamEnderChest> getEnderChest(int chestNumber) {
        return Optional.ofNullable(enderChests.get(chestNumber));
    }

    /**
     * Add or update a team ender chest
     * @param chest The TeamEnderChest to add/update
     */
    public void setEnderChest(@NotNull TeamEnderChest chest) {
        enderChests.put(chest.getChestNumber(), chest);
    }

    /**
     * Remove a team ender chest
     * @param chestNumber The chest number to remove
     */
    public void removeEnderChest(int chestNumber) {
        enderChests.remove(chestNumber);
    }

    /**
     * Get the number of ender chests this team has
     * @return The count of ender chests
     */
    public int getEnderChestCount() {
        return enderChests.size();
    }

    /**
     * Check if the team has a specific ender chest
     * @param chestNumber The chest number to check
     * @return true if the chest exists
     */
    public boolean hasEnderChest(int chestNumber) {
        return enderChests.containsKey(chestNumber);
    }
}




