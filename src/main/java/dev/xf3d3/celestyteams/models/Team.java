package dev.xf3d3.celestyteams.models;

import com.google.gson.annotations.Expose;
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

    public Team(String teamOwner, String teamName) {
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

    public ArrayList<String> getClanMembers(){
        return teamMembers;
    }

    public void setTeamMembers(ArrayList<String> teamMembersList){
        teamMembers = teamMembersList;
    }

    public void addTeamMember(String teamMember){
        teamMembers.add(teamMember);
    }

    public Boolean removeClanMember(String teamMember){
        return teamMembers.remove(teamMember);
    }

    public ArrayList<String> getClanAllies(){
        return teamAllies;
    }

    public void addClanAlly(String ally){
        teamAllies.add(ally);
    }

    public void removeClanAlly(String allyUUID){
        teamAllies.remove(allyUUID);
    }

    public void setClanAllies(ArrayList<String> teamAlliesList){
        teamAllies = teamAlliesList;
    }

    public void addClanEnemy(String enemy){
        teamEnemies.add(enemy);
    }

    public void removeClanEnemy(String enemyUUID){
        teamEnemies.remove(enemyUUID);
    }

    public void setClanEnemies(ArrayList<String> teamEnemiesList){
        teamEnemies = teamEnemiesList;
    }

    public ArrayList<String> getClanEnemies(){
        return teamEnemies;
    }

    public boolean isFriendlyFireAllowed(){
        return friendlyFire;
    }

    public void setFriendlyFireAllowed(boolean friendlyFire){
        this.friendlyFire = friendlyFire;
    }

    public String getClanHomeWorld(){
        return teamHomeWorld;
    }

    public void setClanHomeWorld(String teamHomeWorld){
        this.teamHomeWorld = teamHomeWorld;
    }

    public double getClanHomeX(){
        return teamHomeX;
    }

    public void setClanHomeX(double teamHomeX){
        this.teamHomeX = teamHomeX;
    }

    public double getClanHomeY(){
        return teamHomeY;
    }

    public void setClanHomeY(double teamHomeY){
        this.teamHomeY = teamHomeY;
    }

    public double getClanHomeZ(){
        return teamHomeZ;
    }

    public void setClanHomeZ(double teamHomeZ){
        this.teamHomeZ = teamHomeZ;
    }

    public float getClanHomeYaw(){
        return teamHomeYaw;
    }

    public void setClanHomeYaw(float teamHomeYaw){
        this.teamHomeYaw = teamHomeYaw;
    }

    public float getClanHomePitch(){
        return teamHomePitch;
    }

    public void setClanHomePitch(float teamHomePitch){
        this.teamHomePitch = teamHomePitch;
    }
}
