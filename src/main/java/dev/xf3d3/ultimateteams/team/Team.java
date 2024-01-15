package dev.xf3d3.ultimateteams.team;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class Team {
    private int id;
    @Expose
    private String teamFinalOwner;
    @Expose
    private String teamFinalName;
    @Expose
    private String teamPrefix;
    @Expose
    private ConcurrentHashMap<String, Warp> teamWarps;
    @Expose
    private ArrayList<String> teamMembers;
    @Expose
    private ArrayList<String> teamAllies;
    @Expose
    private ArrayList<String> teamEnemies;
    @Expose
    private boolean friendlyFire;
    @Expose
    @Nullable
    private Home home;

    public Team(@NotNull String teamOwner, @NotNull String teamName) {
        id = 0;
        teamFinalOwner = teamOwner;
        teamFinalName = teamName;
        teamPrefix = teamFinalName;
        teamMembers = new ArrayList<>();
        teamAllies = new ArrayList<>();
        teamEnemies = new ArrayList<>();
        teamWarps = new ConcurrentHashMap<>();
        friendlyFire = false;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setTeamOwner(String newOwner){
        this.teamFinalOwner = newOwner;
    }
    public String getTeamOwner(){
        return teamFinalOwner;
    }

    public String getName(){
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

    public Collection<Warp> getTeamWarps() {
        return teamWarps.values();
    }

    public Warp getTeamWarp(@NotNull String name){
        return teamWarps.get(name);
    }

    public void addTeamWarp(@NotNull Warp warp){
        teamWarps.put(warp.getName(), warp);
    }

    public void removeTeamWarp(@NotNull String name) {
        teamWarps.remove(name);
    }

    public ArrayList<String> getTeamMembers(){
        return teamMembers;
    }

    public void setTeamMembers(@Nullable ArrayList<String> teamMembersList){
        teamMembers = teamMembersList;
    }

    public void addTeamMember(String teamMember){
        teamMembers.add(teamMember);
    }

    public Boolean removeTeamMember(String teamMember) {
        return teamMembers.remove(teamMember);
    }

    public ArrayList<String> getTeamAllies(){
        return teamAllies;
    }

    public void addTeamAlly(String ally){
        teamAllies.add(ally);
    }

    public void removeTeamAlly(String allyUUID){
        teamAllies.remove(allyUUID);
    }

    public void setTeamAllies(@Nullable ArrayList<String> teamAlliesList){
        teamAllies = teamAlliesList;
    }

    public void addTeamEnemy(String enemy){
        teamEnemies.add(enemy);
    }

    public void removeTeamEnemy(String enemyUUID){
        teamEnemies.remove(enemyUUID);
    }

    public void setTeamEnemies(@Nullable ArrayList<String> teamEnemiesList){
        teamEnemies = teamEnemiesList;
    }

    public ArrayList<String> getTeamEnemies(){
        return teamEnemies;
    }

    public boolean isFriendlyFireAllowed(){
        return friendlyFire;
    }

    public void setFriendlyFireAllowed(boolean friendlyFire){
        this.friendlyFire = friendlyFire;
    }

    public Optional<Home> getHome() {
        return Optional.ofNullable(home);
    }

    public void setHome(@NotNull Home home) {
        this.home = home;
    }

    public void clearHome() {
        this.home = null;
    }
}
