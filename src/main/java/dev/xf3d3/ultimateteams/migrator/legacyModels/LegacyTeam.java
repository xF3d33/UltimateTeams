package dev.xf3d3.ultimateteams.migrator.legacyModels;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class LegacyTeam {
    public @Getter @Setter int id;

    @Expose
    private String teamFinalOwner;
    @Expose
    private String teamFinalName;
    @Expose
    private String teamPrefix;
    @Expose
    private ConcurrentHashMap<String, LegacyTeamWarp> teamWarps;
    @Expose
    private ArrayList<String> teamMembers;
    @Expose
    private ArrayList<String> teamAllies;
    @Expose
    private ArrayList<String> teamEnemies;
    @Expose
    private boolean friendlyFire;
    @Expose
    private String teamHomeWorld;
    @Expose
    @Nullable
    private Double teamHomeX;
    @Expose
    @Nullable
    private Double teamHomeY;
    @Expose
    @Nullable
    private Double teamHomeZ;
    @Expose
    @Nullable
    private Float teamHomeYaw;
    @Expose
    @Nullable
    private Float teamHomePitch;

    public String getTeamOwner(){
        return teamFinalOwner;
    }

    public String getTeamFinalName(){
        return teamFinalName;
    }

    public String getTeamPrefix(){
        return teamPrefix;
    }

    public Collection<LegacyTeamWarp> getTeamWarps() {
        return teamWarps.values();
    }

    public LegacyTeamWarp getTeamWarp(@NotNull String name){
        return teamWarps.get(name);
    }

    public ArrayList<String> getTeamMembers(){
        return teamMembers;
    }

    public ArrayList<String> getTeamAllies(){
        return teamAllies;
    }

    public ArrayList<String> getTeamEnemies(){
        return teamEnemies;
    }

    public boolean isFriendlyFireAllowed(){
        return friendlyFire;
    }


    @Nullable
    public String getTeamHomeWorld(){
        return teamHomeWorld;
    }

    public double getTeamHomeX() {
        return Objects.requireNonNullElseGet(teamHomeX, null);
    }


    public double getTeamHomeY(){
        return Objects.requireNonNullElseGet(teamHomeY, null);
    }

    public double getTeamHomeZ(){
        return Objects.requireNonNullElseGet(teamHomeZ, null);
    }

    public float getTeamHomeYaw(){
        return Objects.requireNonNullElseGet(teamHomeYaw, null);
    }

    public float getTeamHomePitch(){
        return Objects.requireNonNullElseGet(teamHomePitch, null);
    }
}