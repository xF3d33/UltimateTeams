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

    @NotNull
    @ApiStatus.Internal
    public static Team create(@NotNull String name, @NotNull Player owner) {
        return Team.builder()
                .name(name)
                .members(Maps.newHashMap(Map.of(owner.getUniqueId(), 3)))
                .build();
    }

    public List<Player> getOnlineMembers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> getMembers().containsKey(player.getUniqueId())).collect(Collectors.toUnmodifiableList());
    }

    public void sendTeamMessage(@NotNull String message) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> getMembers().containsKey(player.getUniqueId()))
                .forEach(player -> player.sendPlainMessage(message));
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
}


