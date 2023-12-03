package dev.xf3d3.ultimateteams.team;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class TeamWarp {
    @Expose
    private String name;
    @Expose
    private String WarpWorld;
    @Expose
    private Double WarpX;
    @Expose
    private Double WarpY;
    @Expose
    private Double WarpZ;
    @Expose
    private Float WarpYaw;
    @Expose
    private Float WarpPitch;

    public TeamWarp(@NotNull String name, @NotNull String world, @NotNull Double x, @NotNull Double y, @NotNull Double z, @NotNull Float yaw, @NotNull Float pitch) {
        this.name = name;
        this.WarpWorld = world;
        this.WarpX = x;
        this.WarpY = y;
        this.WarpZ = z;
        this.WarpYaw = yaw;
        this.WarpPitch = pitch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWarpWorld(){
        return WarpWorld;
    }

    public void setWarpWorld(String warpWorld){
        this.WarpWorld = warpWorld;
    }

    public double getWarpX() {
        return WarpX;
    }

    public void setWarpX(double warpX){
        this.WarpX = warpX;
    }

    public double getWarpY(){
        return WarpY;
    }

    public void setWarpY(double warpY){
        this.WarpY = warpY;
    }

    public double getWarpZ(){
        return WarpZ;
    }

    public void setWarpZ(double warpZ){
        this.WarpZ = warpZ;
    }

    public float getWarpYaw(){
        return WarpYaw;
    }

    public void setWarpYaw(float warpYaw){
        this.WarpYaw = warpYaw;
    }

    public float getWarpPitch(){
        return WarpPitch;
    }

    public void setWarpPitch(float warpPitch){
        this.WarpPitch = warpPitch;
    }

    public Location getLocation() {
        return new Location(
                Bukkit.getWorld(this.WarpWorld),
                this.WarpX,
                this.WarpY,
                this.WarpZ,
                this.WarpYaw,
                this.WarpPitch
        );
    }
}
