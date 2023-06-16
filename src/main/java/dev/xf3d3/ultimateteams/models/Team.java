package dev.xf3d3.ultimateteams.models;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Team {

    @Expose
    private String teamFinalOwner;
    @Expose
    private String teamFinalName;
    @Expose
    private String teamPrefix;
    @Expose
    @Nullable
    private ArrayList<String> teamMembers;
    @Expose
    @Nullable
    private ArrayList<String> teamAllies;
    @Expose
    @Nullable
    private ArrayList<String> teamEnemies;
    @Expose
    private boolean friendlyFire;
    @Expose
    @Nullable
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

    public Team(@NotNull String teamOwner, @NotNull String teamName) {
        teamFinalOwner = teamOwner;
        teamFinalName = teamName;
        teamPrefix = teamFinalName;
        teamMembers = new ArrayList<>();
        teamAllies = new ArrayList<>();
        teamEnemies = new ArrayList<>();
        friendlyFire = false;
        teamHomeWorld = null;
    }

    public String getTeamOwner(){
        return teamFinalOwner;
    }

    public String getTeamFinalName(){
        return teamFinalName;
    }

    public void setTeamFinalName(String newTeamFinalName){
        teamFinalName = newTeamFinalName;
    }

    public String getTeamPrefix(){
        return teamPrefix;
    }

    public void setTeamPrefix(String newTeamPrefix){
        teamPrefix = newTeamPrefix;
    }

    @Nullable
    public ArrayList<String> getTeamMembers(){
        return teamMembers;
    }

    public void setTeamMembers(@Nullable ArrayList<String> teamMembersList){
        teamMembers = teamMembersList;
    }

    public void addTeamMember(String teamMember){
        assert teamMembers != null;
        teamMembers.add(teamMember);
    }

    public Boolean removeTeamMember(String teamMember){
        assert teamMembers != null;
        return teamMembers.remove(teamMember);
    }

    @Nullable
    public ArrayList<String> getTeamAllies(){
        return teamAllies;
    }

    public void addTeamAlly(String ally){
        assert teamAllies != null;
        teamAllies.add(ally);
    }

    public void removeTeamAlly(String allyUUID){
        assert teamAllies != null;
        teamAllies.remove(allyUUID);
    }

    public void setTeamAllies(@Nullable ArrayList<String> teamAlliesList){
        teamAllies = teamAlliesList;
    }

    public void addTeamEnemy(String enemy){
        assert teamEnemies != null;
        teamEnemies.add(enemy);
    }

    public void removeTeamEnemy(String enemyUUID){
        assert teamEnemies != null;
        teamEnemies.remove(enemyUUID);
    }

    public void setTeamEnemies(@Nullable ArrayList<String> teamEnemiesList){
        teamEnemies = teamEnemiesList;
    }

    @Nullable
    public ArrayList<String> getTeamEnemies(){
        return teamEnemies;
    }

    public boolean isFriendlyFireAllowed(){
        return friendlyFire;
    }

    public void setFriendlyFireAllowed(boolean friendlyFire){
        this.friendlyFire = friendlyFire;
    }

    @Nullable
    public String getTeamHomeWorld(){
        return teamHomeWorld;
    }

    public void setTeamHomeWorld(@Nullable String teamHomeWorld){
        this.teamHomeWorld = teamHomeWorld;
    }

    public double getTeamHomeX() {
        return teamHomeX;
    }

    public void setTeamHomeX(double teamHomeX){
        this.teamHomeX = teamHomeX;
    }

    public double getTeamHomeY(){
        return teamHomeY;
    }

    public void setTeamHomeY(double teamHomeY){
        this.teamHomeY = teamHomeY;
    }

    public double getTeamHomeZ(){
        return teamHomeZ;
    }

    public void setTeamHomeZ(double teamHomeZ){
        this.teamHomeZ = teamHomeZ;
    }

    public float getTeamHomeYaw(){
        return teamHomeYaw;
    }

    public void setTeamHomeYaw(float teamHomeYaw){
        this.teamHomeYaw = teamHomeYaw;
    }

    public float getTeamHomePitch(){
        return teamHomePitch;
    }

    public void setTeamHomePitch(float teamHomePitch){
        this.teamHomePitch = teamHomePitch;
    }
}
